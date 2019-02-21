package com.leme.movieguideapp.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.leme.movieguideapp.MovieDetailActivity;
import com.leme.movieguideapp.models.ReviewResult;
import com.leme.movieguideapp.models.VideoResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MovieVideoTask extends AsyncTask<Integer, Void, List<VideoResult>> {

    private static final String TAG = "MoviesApp_ReviewTask";
    private MovieDetailActivity movieDetailActivity;
    private boolean isConnected;

    public MovieVideoTask(MovieDetailActivity activity){
        movieDetailActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        movieDetailActivity.startVideoLoading();
    }

    @Override
    protected List<VideoResult> doInBackground(Integer... params) {

        Integer movieId = params[0];
        Log.v(TAG, "doInBackground - movieId: " + movieId);

        URL urlVideos = NetworkUtils.buildUrlVideos(movieId);

        isConnected = isOnline();
        Log.v(TAG, "isConnected: " + isConnected);
        if(isConnected) {
            try {

                String jsonResponseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(urlVideos);
                Log.v(TAG, "doInBackground: " + jsonResponseFromHttpUrl);

                List<VideoResult> listVideoResultFromJSON = OpenMovieJSONUtils.getListVideoResultFromJSON(jsonResponseFromHttpUrl);

                return listVideoResultFromJSON;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<VideoResult> videoResults) {
        super.onPostExecute(videoResults);

        if(videoResults != null && videoResults.size() > 0 && isConnected) {

            movieDetailActivity.showVideoLoadingResults(videoResults);

        } else {

            movieDetailActivity.showEmptyVideoResults(isConnected);

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) movieDetailActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
