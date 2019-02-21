package com.leme.movieguideapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.leme.movieguideapp.MainActivity;

public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String searchTypeExtra = (String) intent.getSerializableExtra(MainActivity.SEARCH_TYPE);
        MovieSyncTask.syncMovie(this, searchTypeExtra);
    }

}
