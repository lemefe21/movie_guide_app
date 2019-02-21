package com.leme.movieguideapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.leme.movieguideapp.MainActivity;
import com.leme.movieguideapp.data.MovieContract;

import java.util.concurrent.TimeUnit;

public class MovieSyncUtils {

    private static boolean initialized;
    private static final String TAG = "MoviesApp_MovieSyncUtil";

    /*
     * Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
     * writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */
    private static final int SYNC_INTERVAL_HOURS = 4;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 4;

    /*
     * for test
    private static final int SYNC_INTERVAL_MINUTES = 2;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS;
    */

    private static final String MOVIE_SYNC_TAG = "movie-sync";

    /**
     * Schedules a repeating sync of Sunshine's weather data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(final Context context) {

        Log.v(TAG, "scheduleFirebaseJobDispatcherSync - " + SYNC_INTERVAL_SECONDS + " seconds");

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(MOVIE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS, SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMovieJob);

    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     * @param online
     */
    synchronized public static void initialized(final Context context, final String searchType, boolean online) {

        if(initialized) return;
        if(online) {
            initialized = true;
        }

        scheduleFirebaseJobDispatcherSync(context);

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

                //String[] projectionColumns = {MovieContract.MovieEntry._ID};
                String sqlSelectForSearchType = MovieContract.MovieEntry.getSqlSelectForSearchType(searchType);
                Log.v(TAG, "doInBackground sqlSelectForSearchType: " + sqlSelectForSearchType);

                String[] selectionArguments = new String[]{searchType};

                /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        MovieContract.MovieEntry.MOVIES_PROJECTION,
                        MovieContract.MovieEntry.COLUMN_SEARCH_TYPE + " = ? ",
                        selectionArguments,
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
