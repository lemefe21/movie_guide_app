package com.leme.movieguideapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_CLICKED = "movie_clicked";
    private static Movie movie;
    private static final String TAG = "MoviesApp_Details";

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseData;
    private Uri uri;

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] DETAIL_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_OVERVIEW = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_RELEASE_DATE = 5;

    private static final int ID_DETAIL_LOADER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPoster = findViewById(R.id.iv_detail_movie_poster);
        mTitle = findViewById(R.id.tv_detail_movie_title);
        mOverview = findViewById(R.id.tv_detail_movie_overview);
        mVoteAverage = findViewById(R.id.tv_detail_movie_vote_average);
        mReleaseData = findViewById(R.id.tv_detail_movie_release_date);

        /*Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MOVIE_CLICKED)){
            movie = intent.getParcelableExtra(MOVIE_CLICKED);

            bindMovieDetails(movie);

        }*/

        uri = getIntent().getData();
        if (uri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }

    private void bindMovieDetails(Cursor data) {

        Log.v(TAG, "bindMovieDetails - cursor data:" + data.getString(INDEX_MOVIE_ID));

        Picasso.with(this)
                .load(NetworkUtils.getBaseImageURL() + data.getString(INDEX_MOVIE_POSTER_PATH))
                .error(R.drawable.poster_default)
                .into(mPoster);

        mTitle.setText(data.getString(INDEX_MOVIE_TITLE));
        mOverview.setText(data.getString(INDEX_MOVIE_OVERVIEW));
        mVoteAverage.setText(String.valueOf(data.getString(INDEX_MOVIE_VOTE_AVERAGE)));
        mReleaseData.setText(data.getString(INDEX_MOVIE_RELEASE_DATE));

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {

        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        uri, DETAIL_MOVIES_PROJECTION, null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        bindMovieDetails(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
