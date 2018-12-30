package com.leme.movieguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String MOVIE_CLICKED = "movie_clicked";
    private static Movie movie;

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPoster = findViewById(R.id.iv_detail_movie_poster);
        mTitle = findViewById(R.id.tv_detail_movie_title);
        mOverview = findViewById(R.id.tv_detail_movie_overview);
        mVoteAverage = findViewById(R.id.tv_detail_movie_vote_average);
        mReleaseData = findViewById(R.id.tv_detail_movie_release_date);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MOVIE_CLICKED)){
            movie = intent.getParcelableExtra(MOVIE_CLICKED);

            bindMovieDetails(movie);

        }

    }

    private void bindMovieDetails(Movie movie) {

        Picasso.with(this)
                .load(NetworkUtils.getBaseImageURL() + movie.getPoster_path())
                .error(R.drawable.poster_default)
                .into(mPoster);

        mTitle.setText(movie.getTitle());
        mOverview.setText(movie.getOverview());
        mVoteAverage.setText(String.valueOf(movie.getVote_average()));
        mReleaseData.setText(movie.getRelease_date());

    }
}
