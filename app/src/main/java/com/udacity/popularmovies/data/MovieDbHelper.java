package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.udacity.popularmovies.data.MovieContract.FavoriteEntry;

/**
 * Created by iulian on 3/2/2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private final static int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +

                        FavoriteEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteEntry.COLUMN_MOVIE_ID     + " INTEGER NOT NULL, "                 +
                        FavoriteEntry.COLUMN_TITLE        + " TEXT NOT NULL, "                    +
                        FavoriteEntry.COLUMN_OVERVIEW     + " TEXT, "                    +
                        FavoriteEntry.COLUMN_POSTER_PATH  + " TEXT, "                    +
                        FavoriteEntry.COLUMN_RELEASE_DATE + " INTEGER, "                    +
                        FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL, "                    +
                        " UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
