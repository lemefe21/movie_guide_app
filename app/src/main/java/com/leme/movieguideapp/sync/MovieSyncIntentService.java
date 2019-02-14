package com.leme.movieguideapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieSyncTask.syncMovie(this, intent);
    }

}
