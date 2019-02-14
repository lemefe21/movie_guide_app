package com.leme.movieguideapp.utilities;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.MoviesResult;
import com.leme.movieguideapp.data.MovieContract.*;

import java.util.List;

public class OpenMovieJSONUtils {

    private static Gson gson = new Gson();

    public static List<Movie> getListMoviesFromJSON(String movieJSON) {

        MoviesResult results = gson.fromJson(movieJSON, MoviesResult.class);

        return results.getResults();
    }

    public static ContentValues[] getListMoviesContentValuesFromJSON(String movieJSON, String searchType) {

        MoviesResult results = gson.fromJson(movieJSON, MoviesResult.class);

        ContentValues[] movieContentValues = new ContentValues[results.getResults().size()];
        List<Movie> movies = results.getResults();

        for (int i = 0; i < movies.size(); i++) {

            Movie movie  = movies.get(i);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_VIDEO, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_TITLE, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_POPULARITY, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_ADULT, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_SEARCH_TYPE, searchType);

            movieContentValues[i] = movieValues;

        }

        return movieContentValues;

    }

}
