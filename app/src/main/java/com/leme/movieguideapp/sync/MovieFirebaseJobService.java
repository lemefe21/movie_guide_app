package com.leme.movieguideapp.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.leme.movieguideapp.MainActivity;

public class MovieFirebaseJobService extends JobService {

    private static final String TAG = "MoviesApp_JobService";
    private AsyncTask<Void, Void, Void> fetchMovieTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        fetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                String searchType = MainActivity.POPULAR_MOVIES;
                Log.v(TAG, "syncMovie searchType: " + searchType);

                Context context = getApplicationContext();
                MovieSyncTask.syncMovie(context, searchType);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        if(fetchMovieTask != null) {
            fetchMovieTask.cancel(true);
        }

        return false;
    }

}
