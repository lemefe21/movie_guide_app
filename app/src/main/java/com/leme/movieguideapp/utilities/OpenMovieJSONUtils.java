package com.leme.movieguideapp.utilities;

import android.content.Context;

import com.google.gson.Gson;
import com.leme.movieguideapp.models.Movie;
import com.leme.movieguideapp.models.MoviesResult;

import java.util.List;

public class OpenMovieJSONUtils {

    private static Gson gson = new Gson();

    public static List<Movie> getListMoviesFromJSON(Context context, String movieJSON) {

        List<Movie> movies = null;

        MoviesResult results = gson.fromJson(movieJSON, MoviesResult.class);

        return results.getResults();
    }

}
