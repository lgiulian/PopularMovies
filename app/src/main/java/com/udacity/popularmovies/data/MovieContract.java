package com.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by iulian on 3/2/2018.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    /* Inner class that defines the table contents of the movie table */
    public static final class FavoriteEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Movie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote-average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";

        /**
         * Builds a URI that adds the ID to the end of the favorite movie content URI path.
         * This is used to query details about a single favorite movie entry by id.
         *
         * @param id The favorite id
         * @return Uri to query details about a single favorite entry
         */
        public static Uri buildFavoriteUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}