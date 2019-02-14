package com.leme.movieguideapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

            case CODE_MOVIE_WITH_ID:

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
            case CODE_MOVIE:
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    /**
     * Handles requests to insert a set of new rows. In MovieGuide, we are only going to be
     * inserting multiple rows of data at a time from a request. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *	     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case CODE_MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;

                try {

                    for(ContentValues value : values) {

                        long dataInserts = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if(dataInserts != -1) {
                            rowsInserted++;
                        }

                    }

                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);

        }

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing insert in MovieGuide.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("We are not implementing insert in MovieGuide. Use bulkInsert instead");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if(selection == null) {
            selection = "1";
        }

        switch (uriMatcher.match(uri)) {

            case CODE_MOVIE:

                numRowsDeleted = movieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if(numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
