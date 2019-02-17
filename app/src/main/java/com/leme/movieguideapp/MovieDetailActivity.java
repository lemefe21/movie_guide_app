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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leme.movieguideapp.adapters.MovieReviewsAdapter;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.helpers.MovieHelper;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.ReviewResult;
import com.leme.movieguideapp.tasks.MovieReviewTask;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MoviesApp_Details";
    private static final String MOVIE_CLICKED = "movie_clicked";
    public static final String SEARCH_TYPE = "searchType";
    private static Movie movie;

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseData;
    private TextView btnFavorite;
    private TextView mStatusReview;
    private ProgressBar mLoadingReview;
    private RecyclerView mRecyclerViewReviews;
    private MovieReviewsAdapter mMovieReviewsAdapter;
    private boolean isFavorited;
    private int idMovieClicked;
    private String typeDetail;
    private Cursor cursor;
    private Uri uri;

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
        btnFavorite = findViewById(R.id.btn_favorite_movie);
        mLoadingReview = findViewById(R.id.pb_loading_reviews);
        mRecyclerViewReviews = findViewById(R.id.recyclerview_reviews_movies);
        mStatusReview = findViewById(R.id.tv_detail_movie_review);

        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            idMovieClicked = intent.getIntExtra("id", 0);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewReviews.setLayoutManager(linearLayoutManager);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this);
        mRecyclerViewReviews.setAdapter(mMovieReviewsAdapter);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovieReviews();
        loadMovieTrailersLinks();
    }

    private void loadMovieReviews() {
        new MovieReviewTask(this).execute(idMovieClicked);
    }

    private void loadMovieTrailersLinks() {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

    public void startReviewLoading() {
        mLoadingReview.setVisibility(View.VISIBLE);
        mRecyclerViewReviews.setVisibility(View.INVISIBLE);
    }

    public void showReviewLoadingResults(List<ReviewResult> reviewResult) {
        mLoadingReview.setVisibility(View.GONE);
        mStatusReview.setVisibility(View.GONE);
        mRecyclerViewReviews.setVisibility(View.VISIBLE);
        mMovieReviewsAdapter.setReviewData(reviewResult);
    }

    public void showEmptyReviewResults(boolean isConnected) {
        if(!isConnected) {
            mLoadingReview.setVisibility(View.GONE);
            mStatusReview.setVisibility(View.VISIBLE);
            mStatusReview.setText(getString(R.string.tv_no_internet));
            mRecyclerViewReviews.setVisibility(View.GONE);
        } else {
            mLoadingReview.setVisibility(View.GONE);
            mStatusReview.setVisibility(View.VISIBLE);
            mStatusReview.setText(getString(R.string.no_review));
            mRecyclerViewReviews.setVisibility(View.GONE);
        }
    }
}
