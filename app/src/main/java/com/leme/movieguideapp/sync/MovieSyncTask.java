package com.leme.movieguideapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leme.movieguideapp.MainActivity;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.leme.movieguideapp.utilities.OpenMovieJSONUtils;

import java.io.Serializable;
import java.net.URL;

public class MovieSyncTask {

    private static final String TAG = "MoviesApp_MovieSyncTask";

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new movie information into our ContentProvider. Will notify the user that new
     * movie has been loaded if the user hasn't been notified of the movie within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     * @param intent
     */
    synchronized public static void syncMovie(Context context, Intent intent) {

        String searchTypeExtra = (String) intent.getSerializableExtra(MainActivity.SEARCH_TYPE);
        Log.v(TAG, "syncMovie searchTypeExtra: " + searchTypeExtra);

        try {

            URL moviesRequestUrl = NetworkUtils.buildUrl(searchTypeExtra);

            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

            Log.v(TAG, "syncMovie json: " + jsonMoviesResponse);

            ContentValues[] movieValues = OpenMovieJSONUtils.getListMoviesContentValuesFromJSON(jsonMoviesResponse, searchTypeExtra);

            if(movieValues != null && movieValues.length != 0) {

                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver movieContentResolver = context.getContentResolver();

                //delete the old data and insert the new
                movieContentResolver.delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

                //Insert our new movie data into MovieGuide's ContentProvider
                movieContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
