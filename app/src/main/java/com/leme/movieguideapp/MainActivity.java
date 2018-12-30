package com.leme.movieguideapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieItemAdapter.MovieItemAdapterOnClickHandler {

    private static final String TAG = "MoviesApp";
    private RecyclerView mRecyclerView;
    private MovieItemAdapter mMovieItemAdapter;
    private TextView mErrorMessageDisplay;
    private TextView mResponseTest;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO tirar - teste
        mResponseTest = findViewById(R.id.tv_response_test);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_movies);

        //TODO tirar - teste
        //exemple Picasso Framework
        //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mMovieItemAdapter = new MovieItemAdapter(this,this);
        mRecyclerView.setAdapter(mMovieItemAdapter);

        loadMovieData();

    }

    private void loadMovieData() {
        showMovieDataView();
        new MovieAPITask().execute();
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
        Toast.makeText(this, movieClicked.getTitle(), Toast.LENGTH_LONG).show();
    }

    public class MovieAPITask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            URL moviesRequestUrl = NetworkUtils.buildUrl();

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
                Log.v(TAG, "onPostExecute: " + listMovies);
                Movie movie = listMovies.get(0);
                Log.v(TAG, "onPostExecute - First Movie name: " + movie.getTitle());

                mMovieItemAdapter.setMovieData(listMovies);

            } else {
                showErrorMessage();
            }
            super.onPostExecute(jsonResponse);
        }
    }
}