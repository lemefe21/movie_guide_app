package com.leme.movieguideapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.MoviesResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieItemAdapter.MovieItemAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MoviesApp_Main";
    private static final String MOVIE_CLICKED = "movie_clicked";
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String STATE_RESULT = "state_list_movie";
    public static final String SEARCH_TYPE = "searchType";
    private static final int MOVIE_LOADER_ID = 1;
    private int mPosition = RecyclerView.NO_POSITION;
    private RecyclerView mRecyclerView;
    private MovieItemAdapter mMovieItemAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ImageView mImageNoInternet;
    private MoviesResult result;
    private boolean isConnected;

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "onCreate");

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mImageNoInternet = findViewById(R.id.iv_image_no_internet);

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = MOVIE_LOADER_ID;

        int posterWidth = 500;
        int numberOfColumns = calculateBestSpanCount(posterWidth);
        Log.v(TAG, "numberOfColumns to gridLayout: " + numberOfColumns);
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
        LoaderManager.LoaderCallbacks<Cursor> callbacks = MainActivity.this;

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

        showLoading();

    }

    private Bundle createBundleToLoader(String searchType) {
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TYPE, searchType);
        return bundle;
    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
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
    public void onClick(int movieIdClicked) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Uri uriForIdClicked = MovieContract.MovieEntry.buildWeatherUriWithId(movieIdClicked);
        intent.setData(uriForIdClicked);
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
        super.onSaveInstanceState(outState, outPersistentState);
        Log.v(TAG, "onSaveInstanceState");

        outState.putParcelable(STATE_RESULT, result);

        //test to save state of grid
        //gridState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        //outState.putParcelable(GRID_STATE_RESULT, gridState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.v(TAG, "onRestoreInstanceState");

        result = savedInstanceState.getParcelable(STATE_RESULT);

        //test to save state of grid
        /*if(savedInstanceState != null) {
            gridState = savedInstanceState.getParcelable(GRID_STATE_RESULT);
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        /*gridState = new Bundle();
        Parcelable state = mRecyclerView.getLayoutManager().onSaveInstanceState();
        gridState.putParcelable(GRID_STATE_RESULT, state);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        /*if(gridState != null) {
            Log.v(TAG, "onResume - onRestoreInstanceState of grid");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(gridState.getParcelable(GRID_STATE_RESULT));
        }*/

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable final Bundle loaderArgs) {

        switch (loaderId) {

            case MOVIE_LOADER_ID:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                return new CursorLoader(this, movieQueryUri, MAIN_MOVIES_PROJECTION, null, null , null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

        /**
         * Within onCreateLoader, return a new AsyncTaskLoader that looks a lot like the existing MovieAPITask.
         */
        /*return new AsyncTaskLoader<String>(this) {

            String moviesData = null;

            *//**
             * Cache the weather data in a member variable and deliver it in onStartLoading.
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             *//*
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

            *//**
             * This is the method of the AsyncTaskLoader that will load the JSON data
             * @return Movie data from JSON request, null if an error occurs.
             *//*
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

            *//**
             * Sends the result of the load to the registered listener.
             *//*
            public boolean isOnline() {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            }

            *//**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             *//*
            @Override
            public void deliverResult(@Nullable String data) {
                Log.v(TAG, "deliverResult: " + data);
                moviesData = data;
                super.deliverResult(data);
            }

        };*/

    }

    /**
     * Called when a previously created loader has finished its load.
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        Log.v(TAG, "onLoadFinished");

        mMovieItemAdapter.swapCursor(data);

        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }

        mRecyclerView.smoothScrollToPosition(mPosition);

        if(data.getCount() != 0) {
            showMovieDataView();
        }

        /*if (jsonResponse != null) {

            showMovieDataView();

            List<Movie> listMovies = OpenMovieJSONUtils.getListMoviesFromJSON(MainActivity.this, jsonResponse);
            result = new MoviesResult(listMovies);
            Movie movie = listMovies.get(0);
            Log.v(TAG, "onLoadFinished - First Movie name: " + movie.getTitle() + ", ID: " + movie.getId());

            mMovieItemAdapter.setMovieData(result.getResults());

        } else {
            showErrorMessage(isConnected);
        }*/

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
        mMovieItemAdapter.swapCursor(null);
    }

    /**
     * This method is used when we are resetting data or change search type, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        //mMovieItemAdapter.setMovieData(null);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

}