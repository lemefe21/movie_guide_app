package com.leme.movieguideapp.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.leme.movieguideapp.MainActivity;

public class MovieFirebaseJobService extends JobService {

    private static final String TAG = "MoviesApp_JobService";
    private AsyncTask<Void, Void, Void> fetchMovieTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        fetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                String searchType = MainActivity.POPULAR_MOVIES;
                Log.v(TAG, "MovieFirebaseJobService doInBackground: " + searchType);

                Context context = getApplicationContext();
                MovieSyncTask.syncMovie(context, searchType);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.v(TAG, "MovieFirebaseJobService onPostExecute");
                jobFinished(jobParameters, false);
            }

        }.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        Log.v(TAG, "MovieFirebaseJobService onStopJob");
        if(fetchMovieTask != null) {
            fetchMovieTask.cancel(true);
        }

        return false;
    }

}
