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

import android.util.Log;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by iulian on 2/20/2018.
 */

public class TmDbJsonUtils {
    private final static String TAG = TmDbJsonUtils.class.getSimpleName();

    /**
     * Parse movies from json
     * @param json the json content
     * @return the list of movies parsed from json
     */
    public static List<Movie> getMoviesFromJsonResponse(String json) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject main = new JSONObject(json);
            JSONArray moviesArray = main.getJSONArray("results");
            if (moviesArray != null) {
                for (int i = 0; i < moviesArray.length(); i++) {
                    Movie movie = getMovieFromJsonObject(moviesArray.getJSONObject(i));
                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private static Movie getMovieFromJsonObject(JSONObject movieJsonObject) throws ParseException {
//          "vote_count": 5776,
//          "id": 211672,
//          "video": false,
//          "vote_average": 6.4,
//          "title": "Minions",
//          "popularity": 513.970362,
//          "poster_path": "/q0R4crx2SehcEEQEkYObktdeFy.jpg",
//          "original_language": "en",
//          "original_title": "Minions",
//          "genre_ids": [
//          10751,
//          16,
//          12,
//          35
//          ],
//          "backdrop_path": "/qLmdjn2fv0FV2Mh4NBzMArdA0Uu.jpg",
//          "adult": false,
//          "overview": "Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.",
//          "release_date": "2015-06-17"
        Movie movie = new Movie();
        movie.setId(movieJsonObject.optInt("id"));
        movie.setOverview(movieJsonObject.optString("overview"));
        movie.setPosterPath(movieJsonObject.optString("poster_path"));
        movie.setTitle(movieJsonObject.optString("title"));
        movie.setVoteAverage(movieJsonObject.optDouble("vote_average"));
        Date releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(movieJsonObject.optString("release_date"));
        movie.setReleaseDate(releaseDate);

        Log.v(TAG, movie.toString());

        return movie;
    }

    /**
     * Parse trailers from json
     * @param json the json content
     * @return the list of trailers parsed from json
     */
    public static List<Trailer> getTrailersFromJsonResponse(String json) {
        List<Trailer> trailers = new ArrayList<>();
        try {
            JSONObject main = new JSONObject(json);
            JSONArray trailersArray = main.getJSONArray("results");
            if (trailersArray != null) {
                for (int i = 0; i < trailersArray.length(); i++) {
                    Trailer trailer = getTrailerFromJsonObject(trailersArray.getJSONObject(i));
                    trailers.add(trailer);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    private static Trailer getTrailerFromJsonObject(JSONObject trailerJsonObject) {
//        {
//            "id": "5a96a6b6c3a36859f70092fd",
//                "iso_639_1": "en",
//                "iso_3166_1": "US",
//                "key": "Jit3YhGx5pU",
//                "name": "Official Red Band Trailer",
//                "site": "YouTube",
//                "size": 1080,
//                "type": "Trailer"
//        },
        Trailer trailer = new Trailer();
        trailer.setId(trailerJsonObject.optString("id"));
        trailer.setKey(trailerJsonObject.optString("key"));
        trailer.setName(trailerJsonObject.optString("name"));

        Log.v(TAG, trailer.toString());
        return trailer;
    }

    /**
     * Parse trailers from json
     * @param json the json content
     * @return the list of trailers parsed from json
     */
    public static List<Review> getReviewsFromJsonResponse(String json) {
        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject main = new JSONObject(json);
            JSONArray reviewsArray = main.getJSONArray("results");
            if (reviewsArray != null) {
                for (int i = 0; i < reviewsArray.length(); i++) {
                    Review trailer = getReviewFromJsonObject(reviewsArray.getJSONObject(i));
                    reviews.add(trailer);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private static Review getReviewFromJsonObject(JSONObject reviewJsonObject) {
//    {
//        "id": "5a73f1a6925141059a016d77",
//            "author": "Gimly",
//            "content": "Pretty damn funny given the incredibly grim subject matter, but that's not a shock when you take into consideration that _Three Billboards Outside Ebbing, Missouri_ is directed by Martin McDonagh, who once again comes away with a win, which makes him, in my opinion at least, three for three as a director.\r\n\r\n_Final rating:★★★½ - I really liked it. Would strongly recommend you give it your time._",
//            "url": "https://www.themoviedb.org/review/5a73f1a6925141059a016d77"
//    }
        Review review = new Review();
        review.setId(reviewJsonObject.optString("id"));
        review.setAuthor(reviewJsonObject.optString("author"));
        review.setContent(reviewJsonObject.optString("content"));
        review.setUrl(reviewJsonObject.optString("url"));

        Log.v(TAG, review.toString());
        return review;
    }
}
