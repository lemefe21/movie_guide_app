package com.leme.movieguideapp.utilities;

import android.database.Cursor;

import com.leme.movieguideapp.data.MovieContract;

public class MovieUtils {

    public static boolean checkIfMovieIsFavorite(Cursor data) {
        return (data.getInt(MovieContract.MovieEntry.INDEX_MOVIE_FAVORITE) != 0);
    }

}
