package com.leme.movieguideapp.helpers;

import android.database.Cursor;

import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.utilities.MovieUtils;

public class MovieHelper {


    public static Movie getMovieByCursor(Cursor cursor) {

        Movie movie = new Movie();
        movie.setVote_count(cursor.getInt(MovieContract.MovieEntry.INDEX_MOVIE_VOTE_COUNT));
        movie.setId(cursor.getInt(MovieContract.MovieEntry.INDEX_MOVIE_ID));
        movie.setVideo(MovieUtils.checkIfMovieIsVideo(cursor));
        movie.setVote_average(cursor.getDouble(MovieContract.MovieEntry.INDEX_MOVIE_VOTE_AVERAGE));
        movie.setTitle(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_TITLE));
        movie.setPopularity(cursor.getDouble(MovieContract.MovieEntry.INDEX_MOVIE_POPULARITY));
        movie.setPoster_path(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_POSTER_PATH));
        movie.setOriginal_language(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_ORIGINAL_LANGUAGE));
        movie.setBackdrop_path(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_BACKDROP_PATH));
        movie.setAdult(MovieUtils.checkIfMovieIsAdult(cursor));
        movie.setOverview(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_OVERVIEW));
        movie.setRelease_date(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_RELEASE_DATE));
        movie.setFavorite(MovieUtils.checkIfMovieIsFavorite(cursor));
        movie.setSearchType(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_SEARCH_TYPE));

        return movie;

    }
}
