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
            movie.setSearchType(searchType);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVote_count());
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            movieValues.put(MovieEntry.COLUMN_VIDEO, movie.isVideo());
            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
            movieValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
            movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginal_language());
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
            movieValues.put(MovieEntry.COLUMN_ADULT, movie.isAdult());
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
            movieValues.put(MovieEntry.COLUMN_FAVORITE, movie.isFavorite());
            movieValues.put(MovieEntry.COLUMN_SEARCH_TYPE, movie.getSearchType());

            movieContentValues[i] = movieValues;

        }

        return movieContentValues;

    }

}
