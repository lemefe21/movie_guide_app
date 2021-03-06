package com.leme.movieguideapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.leme.movieguideapp.data.MovieContract.*;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                MovieEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL, "                              +
                MovieEntry.COLUMN_MOVIE_ID          + " INTEGER NOT NULL, "                              +
                MovieEntry.COLUMN_VIDEO             + " INTEGER DEFAULT 0 NOT NULL, "                    +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, "                                 +
                MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_POPULARITY        + " REAL NOT NULL, "                                 +
                MovieEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_BACKDROP_PATH     + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_ADULT             + " INTEGER DEFAULT 0 NOT NULL, "                    +
                MovieEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, "                                 +
                MovieEntry.COLUMN_FAVORITE          + " INTEGER DEFAULT 0 NOT NULL, "                    +
                MovieEntry.COLUMN_SEARCH_TYPE       + " TEXT NOT NULL, "                                 +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
