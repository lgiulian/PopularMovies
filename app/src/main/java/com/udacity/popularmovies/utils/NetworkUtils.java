/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by iulian on 2/20/2018.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private final static String TMDB_API_BASE_URL = "http://api.themoviedb.org/3";
    private final static String TMDB_API_MOVIE_URL = TMDB_API_BASE_URL + "/movie";
    public final static String TMDB_API_MOVIE_POPULAR_URL = TMDB_API_MOVIE_URL + "/popular";
    public final static String TMDB_API_MOVIE_TOP_RATED_URL = TMDB_API_MOVIE_URL + "/top_rated";
    public final static String TMDB_API_MOVIE_DISCOVER_URL = TMDB_API_BASE_URL + "/discover/movie";
    public final static String TMDB_API_MOVIE_VIDEOS_PATH = "videos";
    public final static String TMDB_API_MOVIE_REVIEWS_PATH = "reviews";
    private final static String LANGUAGE_PARAM = "language";
    private final static String language = "en-US";
    private final static String PAGE_PARAM = "page";
    private final static int page = 1;

    private final static String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p";
    private final static String TMDB_POSTER_SIZE = "/w185"; // "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    public final static String TMDB_POSTER_URL = TMDB_POSTER_BASE_URL + TMDB_POSTER_SIZE;
    public final static String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private final static String API_KEY = BuildConfig.API_KEY;

    /**
     * Builds the URL used to communicate with TMDB using the specified endpoint
     */
    public static URL buildUrl(String endpoint) {
        Uri builtUri = Uri.parse(endpoint).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "built url: " + url);
        return url;
    }

    public static URL buildTrailersUrl(int movieId) {
        Uri builtUri = Uri.parse(TMDB_API_MOVIE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(TMDB_API_MOVIE_VIDEOS_PATH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "built url: " + url);
        return url;
    }

    public static URL buildReviewsUrl(int movieId) {
        Uri builtUri = Uri.parse(TMDB_API_MOVIE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(TMDB_API_MOVIE_REVIEWS_PATH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "built url: " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
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
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnected(Context context) {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @NonNull
    public static String getTrailerUrl(String trailerKey) {
        return NetworkUtils.YOUTUBE_URL + trailerKey;
    }

}
