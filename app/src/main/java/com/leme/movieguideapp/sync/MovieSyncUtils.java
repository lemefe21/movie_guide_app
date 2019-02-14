package com.leme.movieguideapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.leme.movieguideapp.MainActivity;
import com.leme.movieguideapp.data.MovieContract;

public class MovieSyncUtils {

    private static boolean initialized;
    private static final String TAG = "MoviesApp_MovieSyncUtil";

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialized(final Context context, final String searchType) {

        if(initialized) return;

        initialized = true;

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                /* URI for every row of movie data in our movie table*/
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                Log.v(TAG, "doInBackground searchType: " + searchType);

                /*
                 * Since this query is going to be used only as a check to see if we have any
                 * data (rather than to display data), we just need to PROJECT the ID of each
                 * row. In our queries where we display data, we need to PROJECT more columns
                 * to determine what weather details need to be displayed.
                 */

                String[] projectionColumns = {MovieContract.MovieEntry._ID};
                String sqlSelectForSearchType = MovieContract.MovieEntry.getSqlSelectForSearchType(searchType);

                /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projectionColumns,
                        sqlSelectForSearchType,
                        null,
                        null);

                Log.v(TAG, "cursor.getCount(): " + cursor.getCount());

                if(cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context, searchType);
                }

                cursor.close();

                return null;
            }

        }.execute();

    }

    public static void startImmediateSync(final Context context, String searchType) {

        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        intentToSyncImmediately.putExtra(MainActivity.SEARCH_TYPE, searchType);
        context.startService(intentToSyncImmediately);

    }

}
