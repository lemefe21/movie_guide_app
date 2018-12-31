package com.leme.movieguideapp.utilities;

import android.net.Uri;
import android.util.Log;

import com.leme.movieguideapp.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = "MoviesApp";
    private static final String API_KEY = BuildConfig.API_KEY_THE_MOVIE;
    private static final String LANGUAGE = "en-US";
    private static final String PAGE = "1";
    private static final String MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_IMAGES_URL = "http://image.tmdb.org/t/p/w500//";
    private static final String API_KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";

    public static URL buildUrl(String searchType) {
        Uri builtUri = Uri.parse(MOVIES_URL + searchType).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, PAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built The Movies URL: " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getBaseImageURL() {
        return NetworkUtils.BASE_IMAGES_URL;
    }
}
