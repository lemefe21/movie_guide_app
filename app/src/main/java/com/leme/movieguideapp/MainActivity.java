package com.leme.movieguideapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.leme.easymoviesapp.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

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

        mResponseTest = findViewById(R.id.tv_response_test);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        //exemple Picasso Framework
        //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        loadMovieData();

    }

    private void loadMovieData() {
        showMovieDataView();
        new MovieAPITask().execute();
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        //mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        //mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
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

                //desserializacao
                //List<Movie> listMoviesResponse = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

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
                mResponseTest.setText(jsonResponse);
                //Toast.makeText(MainActivity.this, jsonResponse, Toast.LENGTH_SHORT).show();

                //String[] weatherData << List movies
                //mForecastAdapter.setWeatherData(weatherData);

            } else {
                showErrorMessage();
            }
            super.onPostExecute(jsonResponse);
        }
    }
}