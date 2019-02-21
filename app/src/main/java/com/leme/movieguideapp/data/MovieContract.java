package com.leme.movieguideapp.data;

import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.leme.movieguideapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for MovieGuide.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that MovieGuide
     * can handle. For instance,
     *
     *     content://com.leme.movieguideapp/movie/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.leme.movieguideapp/test/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "test". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_VOTE_COUNT        = "vote_count";
        public static final String COLUMN_MOVIE_ID          = "movie_id";
        public static final String COLUMN_VIDEO             = "video";
        public static final String COLUMN_VOTE_AVERAGE      = "vote_average";
        public static final String COLUMN_TITLE             = "title";
        public static final String COLUMN_POPULARITY        = "popularity";
        public static final String COLUMN_POSTER_PATH       = "poster_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_BACKDROP_PATH     = "backdrop_path";
        public static final String COLUMN_ADULT             = "adult";
        public static final String COLUMN_OVERVIEW          = "overview";
        public static final String COLUMN_RELEASE_DATE      = "release_date";
        public static final String COLUMN_FAVORITE          = "favorite";
        public static final String COLUMN_SEARCH_TYPE       = "search_type";

        /*
         * The columns of data that we are interested in displaying within our MainActivity's list of
         * weather data.
         */
        public static final String[] MOVIES_PROJECTION = {
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_VIDEO,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_POPULARITY,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
                MovieContract.MovieEntry.COLUMN_ADULT,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_FAVORITE,
                MovieContract.MovieEntry.COLUMN_SEARCH_TYPE
        };

        /*
         * We store the indices of the values in the array of Strings above to more quickly be able to
         * access the data from our query. If the order of the Strings above changes, these indices
         * must be adjusted to match the order of the Strings.
         */
        public static final int INDEX_MOVIE_VOTE_COUNT = 0;
        public static final int INDEX_MOVIE_ID = 1;
        public static final int INDEX_MOVIE_VIDEO = 2;
        public static final int INDEX_MOVIE_VOTE_AVERAGE = 3;
        public static final int INDEX_MOVIE_TITLE = 4;
        public static final int INDEX_MOVIE_POPULARITY = 5;
        public static final int INDEX_MOVIE_POSTER_PATH = 6;
        public static final int INDEX_MOVIE_ORIGINAL_LANGUAGE = 7;
        public static final int INDEX_MOVIE_BACKDROP_PATH = 8;
        public static final int INDEX_MOVIE_ADULT = 9;
        public static final int INDEX_MOVIE_OVERVIEW = 10;
        public static final int INDEX_MOVIE_RELEASE_DATE = 11;
        public static final int INDEX_MOVIE_FAVORITE = 12;
        public static final int INDEX_MOVIE_SEARCH_TYPE = 13;

        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

        public static String getSqlSelectForSearchType(String searchType) {
            return MovieEntry.COLUMN_SEARCH_TYPE + " = '" + searchType + "'";
        }

        public static String getSqlSelectForFavoritedAndTypeMovies(String searchType) {
            return MovieEntry.COLUMN_FAVORITE + " = '" + 1 + "' AND " +
                    MovieEntry.COLUMN_SEARCH_TYPE + " = '" + searchType + "'";
        }

        public static String getSqlSelectForFavoritedMovies() {
            return MovieEntry.COLUMN_FAVORITE + " = '" + 1 + "'";
        }

    }

}
