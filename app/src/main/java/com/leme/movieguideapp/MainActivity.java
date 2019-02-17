package com.leme.movieguideapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
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
import android.widget.Toast;

import com.leme.movieguideapp.adapters.MovieItemAdapter;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.MoviesResult;
import com.leme.movieguideapp.sync.MovieSyncUtils;

public class MainActivity extends AppCompatActivity implements MovieItemAdapter.MovieItemAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SEARCH_TYPE_PREFERENCES = "search_type_preferences";
    private static final String TAG = "MoviesApp_Main";
    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    public static final String FAVORITES_MOVIES = "favorites";
    public static final String ARGS_FAVORITES_MOVIES = "1";
    private static final String STATE_RESULT = "state_list_movie";
    private static final String GRID_STATE_RESULT = "grid_state";
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
    private static Bundle activityStateBundle;
    private String lastTypeSelected;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "onCreate");

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mImageNoInternet = findViewById(R.id.iv_image_no_internet);

        int posterWidth = 500;
        int numberOfColumns = calculateBestSpanCount(posterWidth);
        Log.v(TAG, "numberOfColumns to gridLayout: " + numberOfColumns);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);


        mRecyclerView.setLayoutManager(layoutManager);
        mMovieItemAdapter = new MovieItemAdapter(this,this);
        mRecyclerView.setAdapter(mMovieItemAdapter);

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
        //Bundle bundleForLoader = createBundleToLoader(POPULAR_MOVIES);
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */

        preferences = getSharedPreferences(SEARCH_TYPE_PREFERENCES, MODE_PRIVATE);
        lastTypeSelected = preferences.getString(SEARCH_TYPE, POPULAR_MOVIES);

        showLoading();

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, createBundleToLoader(lastTypeSelected), callbacks);

        MovieSyncUtils.initialized(this, POPULAR_MOVIES, isOnline());

    }

    private void setSearchTypePreference(SharedPreferences preferences, String type) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SEARCH_TYPE, type);
        editor.commit();
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
        Uri uriForIdClicked = MovieContract.MovieEntry.buildMovieUriWithId(movieIdClicked);
        intent.setData(uriForIdClicked);
        intent.putExtra("id", movieIdClicked);
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
                showLoading();
                setSearchTypePreference(preferences, POPULAR_MOVIES);
                MovieSyncUtils.startImmediateSync(this, POPULAR_MOVIES);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, createBundleToLoader(POPULAR_MOVIES), MainActivity.this);
                return true;

            case R.id.action_top_rated:
                showLoading();
                setSearchTypePreference(preferences, TOP_RATED_MOVIES);
                MovieSyncUtils.startImmediateSync(this, TOP_RATED_MOVIES);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, createBundleToLoader(TOP_RATED_MOVIES), MainActivity.this);
                return true;

            case R.id.action_favorites:
                showLoading();
                setSearchTypePreference(preferences, FAVORITES_MOVIES);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, createBundleToLoader(FAVORITES_MOVIES), MainActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putParcelable(STATE_RESULT, result);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.v(TAG, "onRestoreInstanceState");
        result = savedInstanceState.getParcelable(STATE_RESULT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        activityStateBundle = new Bundle();
        Parcelable state = mRecyclerView.getLayoutManager().onSaveInstanceState();
        activityStateBundle.putParcelable(GRID_STATE_RESULT, state);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        if(activityStateBundle != null) {

            Log.v(TAG, "onResume - onRestoreInstanceState of grid");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(activityStateBundle.getParcelable(GRID_STATE_RESULT));

        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable final Bundle loaderArgs) {

        Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;


        switch (loaderId) {

            case MOVIE_LOADER_ID:

                if(!preferences.getString(SEARCH_TYPE, POPULAR_MOVIES).equals(FAVORITES_MOVIES)) {
                    String type = (String) loaderArgs.get(SEARCH_TYPE);
                    String[] selectionArguments = new String[]{type};
                    return new CursorLoader(this, movieQueryUri, MovieContract.MovieEntry.MOVIES_PROJECTION,
                            MovieContract.MovieEntry.COLUMN_SEARCH_TYPE + " = ? ",
                            selectionArguments , null);
                } else {
                    String[] selectionArguments = new String[]{ARGS_FAVORITES_MOVIES};
                    return new CursorLoader(this, movieQueryUri, MovieContract.MovieEntry.MOVIES_PROJECTION,
                            MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ",
                            selectionArguments , null);
                }


            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

    }

    /**
     * Called when a previously created loader has finished its load.
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Log.v(TAG, "onLoadFinished");

        mMovieItemAdapter.swapCursor(data);

        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        } else if(activityStateBundle == null) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        isConnected = isOnline();
        if(data.getCount() == 0 && !isOnline()) {
            showErrorMessage(isConnected);
        }

        if(data.getCount() == 0 && preferences.getString(SEARCH_TYPE, POPULAR_MOVIES).equals(FAVORITES_MOVIES)){
            Toast.makeText(this, getString(R.string.no_favorited_movies), Toast.LENGTH_LONG).show();
        }

        if(data.getCount() != 0) {
            showMovieDataView();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
        mMovieItemAdapter.swapCursor(null);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}