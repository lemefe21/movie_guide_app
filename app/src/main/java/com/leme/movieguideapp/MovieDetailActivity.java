package com.leme.movieguideapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.utilities.MovieUtils;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_CLICKED = "movie_clicked";
    private static Movie movie;
    private static final String TAG = "MoviesApp_Details";
    public static final String SEARCH_TYPE = "searchType";

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseData;
    private TextView btnFavorite;
    private boolean isFavorited;
    private Uri uri;
    private Cursor cursor;
    private String typeDetail;

    private static final int ID_DETAIL_LOADER = 3;
    //private static final int ID_FAVORITE_LOADER = 4;
    //private static final int ID_VIDEOS_LOADER = 5;
    //private static final int ID_REVIEWS_LOADER = 6;
    //private static final String FAVORITE_VALUE = "favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPoster = findViewById(R.id.iv_detail_movie_poster);
        mTitle = findViewById(R.id.tv_detail_movie_title);
        mOverview = findViewById(R.id.tv_detail_movie_overview);
        mVoteAverage = findViewById(R.id.tv_detail_movie_vote_average);
        mReleaseData = findViewById(R.id.tv_detail_movie_release_date);
        btnFavorite = findViewById(R.id.btn_favorite_movie);

        uri = getIntent().getData();
        if (uri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeFavoriteStatus();

            }
        });

    }

    private void changeFavoriteStatus() {
        Log.v(TAG, "onClick - cursor data:" + movie.isFavorite());
        Log.v(TAG, "Favorited this movie: " + movie.isFavorite());

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, (!isFavorited ? 1 : 0));

        int countRowsUpdated = getContentResolver().update(uri,
                movieValues,
                null,
                null);

        if(countRowsUpdated > 0) {
            Log.v(TAG, "Favorite movie: " + countRowsUpdated + " rows update");
        }

    }

    private void setFavoriteTextView() {
        if(isFavorited) {
            btnFavorite.setText("Favorite: Yes");
        } else {
            btnFavorite.setText("Favorite: No");
        }
    }

    private void bindMovieDetails(Cursor data) {

        movie = MovieHelper.getMovieByCursor(data);

        Log.v(TAG, "bindMovieDetails - cursor data:" + movie.getId());

        Picasso.with(this)
                .load(NetworkUtils.getBaseImageURL() + movie.getPoster_path())
                .error(R.drawable.poster_default)
                .into(mPoster);

        typeDetail = movie.getSearchType();
        mTitle.setText(movie.getTitle());
        mOverview.setText(movie.getOverview());
        mVoteAverage.setText(String.valueOf(movie.getVote_average()));
        mReleaseData.setText(movie.getRelease_date());
        isFavorited = movie.isFavorite();
        setFavoriteTextView();

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {

        Log.v(TAG, "onCreateLoader - loader ID:" + loaderId);
        switch (loaderId) {

            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        uri, MovieContract.MovieEntry.MOVIES_PROJECTION, null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Log.v(TAG, "onLoadFinished - loader ID:" + loader.getId());

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

        loadMovieReviews();

        loadMovieTrailersLinks();

    }

    private void loadMovieReviews() {
    }

    private void loadMovieTrailersLinks() {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

}
