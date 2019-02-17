package com.leme.movieguideapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leme.movieguideapp.MainActivity;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.utilities.MovieUtils;
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
     * @param searchType
     */
    synchronized public static void syncMovie(Context context, String searchType) {

        Log.v(TAG, "syncMovie searchType: " + searchType);

        try {

            URL moviesRequestUrl = NetworkUtils.buildUrl(searchType);

            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

            Log.v(TAG, "syncMovie json: " + jsonMoviesResponse);

            ContentValues[] movieValues = OpenMovieJSONUtils.getListMoviesContentValuesFromJSON(jsonMoviesResponse, searchType);

            if(movieValues != null && movieValues.length != 0) {

                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver movieContentResolver = context.getContentResolver();

                movieValues = MovieUtils.updateValuesWithFavoritedMovies(movieContentResolver, movieValues, searchType);

                //delete the old data and insert the new
                //movieContentResolver.delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

                String[] selectionArguments = new String[]{searchType};
                int delete = movieContentResolver.delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_SEARCH_TYPE + " = ? ",
                        selectionArguments);

                //Insert our new movie data into MovieGuide's ContentProvider
                movieContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
