package com.leme.movieguideapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.MoviesResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieItemAdapter.MovieItemAdapterOnClickHandler {

    private static final String TAG = "MoviesApp";
    private static final String MOVIE_CLICKED = "movie_clicked";
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String STATE_RESULT = "state_list_movie";
    private RecyclerView mRecyclerView;
    private MovieItemAdapter mMovieItemAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviesResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_movies);

        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mMovieItemAdapter = new MovieItemAdapter(this,this);
        mRecyclerView.setAdapter(mMovieItemAdapter);

        loadMovieData(POPULAR_MOVIES);

    }

    private void loadMovieData(String searchType) {
        showMovieDataView();
        new MovieAPITask().execute(searchType);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
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
        switch (itemId){
            case R.id.action_popular:
                loadMovieData(POPULAR_MOVIES);
                return true;

            case R.id.action_top_rated:
                loadMovieData(TOP_RATED_MOVIES);
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

    public class MovieAPITask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String searchType = params[0];
            Log.v(TAG, "doInBackground - searchType: " + searchType);

            URL moviesRequestUrl = NetworkUtils.buildUrl(searchType);

            try {

                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                Log.v(TAG, "doInBackground: " + jsonMoviesResponse);

                return jsonMoviesResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.v(TAG, "onPostExecute: " + jsonResponse);

            if (jsonResponse != null) {
                showMovieDataView();

                //TODO tirar comentário - desserializacao
                List<Movie> listMovies = OpenMovieJSONUtils.getListMoviesFromJSON(MainActivity.this, jsonResponse);
                result = new MoviesResult(listMovies);
                Log.v(TAG, "onPostExecute: " + listMovies);
                Movie movie = listMovies.get(0);
                Log.v(TAG, "onPostExecute - First Movie name: " + movie.getTitle());

                mMovieItemAdapter.setMovieData(result.getResults());

            } else {
                showErrorMessage();
            }
            super.onPostExecute(jsonResponse);
        }
    }
}