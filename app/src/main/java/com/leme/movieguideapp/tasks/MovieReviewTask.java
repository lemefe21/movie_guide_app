package com.leme.movieguideapp.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.leme.movieguideapp.MovieDetailActivity;
import com.leme.movieguideapp.models.ReviewResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.net.URL;
import java.util.List;

public class MovieReviewTask extends AsyncTask<Integer, Void, List<ReviewResult>> {

    private static final String TAG = "MoviesApp_ReviewTask";
    private MovieDetailActivity movieDetailActivity;
    private boolean isConnected;

    public MovieReviewTask(MovieDetailActivity activity){
        movieDetailActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        movieDetailActivity.startReviewLoading();
    }

    @Override
    protected List<ReviewResult> doInBackground(Integer... params) {

        Integer movieId = params[0];
        Log.v(TAG, "doInBackground - movieId: " + movieId);

        URL urlReviews = NetworkUtils.buildUrlReviews(movieId);

        isConnected = isOnline();
        Log.v(TAG, "isConnected: " + isConnected);
        if(isConnected) {
            try {

                String jsonResponseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(urlReviews);
                Log.v(TAG, "doInBackground: " + jsonResponseFromHttpUrl);

                List<ReviewResult> listReviewResultFromJSON = OpenMovieJSONUtils.getListReviewResultFromJSON(jsonResponseFromHttpUrl);

                return listReviewResultFromJSON;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<ReviewResult> reviewResult) {
        super.onPostExecute(reviewResult);

        if(reviewResult != null && reviewResult.size() > 0 && isConnected) {

            movieDetailActivity.showReviewLoadingResults(reviewResult);

        } else {

            movieDetailActivity.showEmptyReviewResults(isConnected);

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) movieDetailActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
