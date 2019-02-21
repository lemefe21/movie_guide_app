package com.leme.movieguideapp.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.leme.movieguideapp.data.MovieContract.*;

public class MovieUtils {

    private static final String TAG = "MoviesApp_MovieUtils";

    public static boolean checkIfMovieIsVideo(Cursor data) {
        return (data.getInt(MovieEntry.INDEX_MOVIE_VIDEO) != 0);
    }

    public static boolean checkIfMovieIsAdult(Cursor data) {
        return (data.getInt(MovieEntry.INDEX_MOVIE_ADULT) != 0);
    }

    public static boolean checkIfMovieIsFavorite(Cursor data) {
        return (data.getInt(MovieEntry.INDEX_MOVIE_FAVORITE) != 0);
    }

    public static ContentValues[] updateValuesWithFavoritedMovies(ContentResolver movieContentResolver, ContentValues[] movieValues, String searchType) {

        String sqlSelectForFavoritedMovies = MovieEntry.getSqlSelectForFavoritedMovies();
        Log.v(TAG, "doInBackground sqlSelectForFavoritedMovies: " + sqlSelectForFavoritedMovies);

        /* Here, we perform the query to check to see if we have any weather data */
        Cursor cursor = getContentResolverQueryForFavorites(movieContentResolver, sqlSelectForFavoritedMovies);

        int count = cursor.getCount();
        int length = movieValues.length;

        Log.v(TAG, "query with favorited cursor.getCount(): " + cursor.getCount());
        Log.v(TAG, "query with favorited movieValues.length: " + movieValues.length);

        if (cursor != null && cursor.moveToFirst()) {
            /* We have valid data, continue...*/

            for (int c = 0; c < cursor.getCount(); c++) {

                int idFavorited = cursor.getInt(MovieEntry.INDEX_MOVIE_ID);

                for (int v = 0; v < movieValues.length; v++) {

                    int valueIdMovie = (int) movieValues[v].get(MovieEntry.COLUMN_MOVIE_ID);

                    if(idFavorited == valueIdMovie) {

                        ContentValues values = new ContentValues();
                        values.put(MovieEntry.COLUMN_VOTE_COUNT, cursor.getInt(MovieEntry.INDEX_MOVIE_VOTE_COUNT));
                        values.put(MovieEntry.COLUMN_MOVIE_ID, cursor.getInt(MovieEntry.INDEX_MOVIE_ID));
                        values.put(MovieEntry.COLUMN_VIDEO, cursor.getInt(MovieEntry.INDEX_MOVIE_VIDEO));
                        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, cursor.getDouble(MovieEntry.INDEX_MOVIE_VOTE_AVERAGE));
                        values.put(MovieEntry.COLUMN_TITLE, cursor.getString(MovieEntry.INDEX_MOVIE_TITLE));
                        values.put(MovieEntry.COLUMN_POPULARITY, cursor.getDouble(MovieEntry.INDEX_MOVIE_POPULARITY));
                        values.put(MovieEntry.COLUMN_POSTER_PATH, cursor.getString(MovieEntry.INDEX_MOVIE_POSTER_PATH));
                        values.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, cursor.getString(MovieEntry.INDEX_MOVIE_ORIGINAL_LANGUAGE));
                        values.put(MovieEntry.COLUMN_BACKDROP_PATH, cursor.getString(MovieEntry.INDEX_MOVIE_BACKDROP_PATH));
                        values.put(MovieEntry.COLUMN_ADULT, cursor.getInt(MovieEntry.INDEX_MOVIE_ADULT));
                        values.put(MovieEntry.COLUMN_OVERVIEW, cursor.getString(MovieEntry.INDEX_MOVIE_OVERVIEW));
                        values.put(MovieEntry.COLUMN_RELEASE_DATE, cursor.getString(MovieEntry.INDEX_MOVIE_RELEASE_DATE));
                        values.put(MovieEntry.COLUMN_FAVORITE, cursor.getInt(MovieEntry.INDEX_MOVIE_FAVORITE));
                        values.put(MovieEntry.COLUMN_SEARCH_TYPE, cursor.getString(MovieEntry.INDEX_MOVIE_SEARCH_TYPE));
                        movieValues[v] = values;

                        break;
                    }

                }

                cursor.moveToNext();
            }
        }

        cursor.close();

        return movieValues;
    }

    private static Cursor getContentResolverQueryForFavorites(ContentResolver movieContentResolver, String sqlSelectForFavoritedMovies) {
        return movieContentResolver.query(
                MovieEntry.CONTENT_URI,
                MovieEntry.MOVIES_PROJECTION,
                sqlSelectForFavoritedMovies,
                null,
                null);
    }

    public static int calculateBestSpanCount(Display display) {
        int posterWidth = 500;
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }
}
