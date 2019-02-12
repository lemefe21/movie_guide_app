package com.leme.movieguideapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.MoviesResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieItemAdapter.MovieItemAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MoviesApp";
    private static final String MOVIE_CLICKED = "movie_clicked";
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String STATE_RESULT = "state_list_movie";
    public static final String SEARCH_TYPE = "searchType";
    private static final int MOVIE_LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private MovieItemAdapter mMovieItemAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ImageView mImageNoInternet;
    private MoviesResult result;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mImageNoInternet = findViewById(R.id.iv_image_no_internet);

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = MOVIE_LOADER_ID;

        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mMovieItemAdapter = new MovieItemAdapter(this,this);
        mRecyclerView.setAdapter(mMovieItemAdapter);

        showMovieDataView();

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String. (implements LoaderCallbacks<String>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<String> callbacks = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = createBundleToLoader(POPULAR_MOVIES);
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundleForLoader, callbacks);

    }

    private Bundle createBundleToLoader(String searchType) {
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TYPE, searchType);
        return bundle;
    }


    private void showMovieDataView() {
        mImageNoInternet.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(boolean isConnected) {
        if(!isConnected){
            mImageNoInternet.setVisibility(View.VISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        } else {
            mImageNoInternet.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Movie movieClicked) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_CLICKED, movieClicked);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        Log.v(TAG, "onOptionsItemSelected: " + itemId);
        switch (itemId){
            case R.id.action_popular:
                invalidateData();
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, createBundleToLoader(POPULAR_MOVIES), this);
                return true;

            case R.id.action_top_rated:
                invalidateData();
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, createBundleToLoader(TOP_RATED_MOVIES), this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        outState.putParcelable(STATE_RESULT, result);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        result = savedInstanceState.getParcelable(STATE_RESULT);

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable final Bundle loaderArgs) {

        /**
         * Within onCreateLoader, return a new AsyncTaskLoader that looks a lot like the existing MovieAPITask.
         */
        return new AsyncTaskLoader<String>(this) {

            String moviesData = null;

            /**
             * Cache the weather data in a member variable and deliver it in onStartLoading.
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if(moviesData != null) {
                    Log.v(TAG, "onStartLoading: moviesData is not null");
                    deliverResult(moviesData);
                } else {
                    Log.v(TAG, "onStartLoading: moviesData is null >> forceLoad");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load the JSON data
             * @return Movie data from JSON request, null if an error occurs.
             */
            @Nullable
            @Override
            public String loadInBackground() {

                String searchType = loaderArgs.getString(SEARCH_TYPE);
                Log.v(TAG, "doInBackground - searchType: " + searchType);

                URL moviesRequestUrl = NetworkUtils.buildUrl(searchType);

                isConnected = isOnline();
                Log.v(TAG, "isConnected: " + isConnected);
                if(isOnline()) {
                    try {

                        String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                        Log.v(TAG, "doInBackground: " + jsonMoviesResponse);

                        return jsonMoviesResponse;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    return null;
                }

            }

            /**
             * Sends the result of the load to the registered listener.
             */
            public boolean isOnline() {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            @Override
            public void deliverResult(@Nullable String data) {
                Log.v(TAG, "deliverResult: " + data);
                moviesData = data;
                super.deliverResult(data);
            }

        };

    }

    /**
     * Called when a previously created loader has finished its load.
     * @param loader The Loader that has finished.
     * @param jsonResponse The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String jsonResponse) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        Log.v(TAG, "onLoadFinished: " + jsonResponse);

        if (jsonResponse != null) {

            showMovieDataView();

            List<Movie> listMovies = OpenMovieJSONUtils.getListMoviesFromJSON(MainActivity.this, jsonResponse);
            result = new MoviesResult(listMovies);
            Log.v(TAG, "onLoadFinished: " + listMovies);
            Movie movie = listMovies.get(0);
            Log.v(TAG, "onLoadFinished - First Movie name: " + movie.getTitle());

            mMovieItemAdapter.setMovieData(result.getResults());

        } else {
            showErrorMessage(isConnected);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    /**
     * This method is used when we are resetting data or change search type, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mMovieItemAdapter.setMovieData(null);
    }

}