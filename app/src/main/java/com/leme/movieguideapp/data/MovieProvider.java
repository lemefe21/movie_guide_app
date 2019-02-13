package com.leme.movieguideapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 10;
    public static final int CODE_MOVIE_WITH_ID = 11;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.leme.movieguideapp/movie/ */
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);

        /*
         * This URI would look something like content://com.leme.movieguideapp/movie/1
         * The "/#" signifies to the UriMatcher that if PATH_MOVIE is followed by ANY number,
         * that it should return the CODE_MOVIE_WITH_ID code
         */
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        movieDbHelper = new MovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (uriMatcher.match(uri)) {

            case CODE_MOVIE_WITH_ID: {

                String lastId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{lastId};

                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.leme.movieguideapp/movie
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
            case CODE_MOVIE: {

                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
        
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
