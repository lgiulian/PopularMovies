package com.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by iulian on 3/2/2018.
 */

public class MovieProvider extends ContentProvider {

    private static final int CODE_FAVORITE = 100;
    private static final int CODE_FAVORITE_WITH_ID = 101;

    /*
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that will match each URI to corresponded codes defined above.
     *
     * @return A UriMatcher that correctly matches the code constants
     */
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.udacity.popularmovies/favorite/ */
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, CODE_FAVORITE);
        /* This URI would look something like content://com.udacity.popularmovies/favorite/123 */
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                cursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVORITE_WITH_ID:
                String[] selectionArguments = new String[] {uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in PopularMovies.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnedUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                long id = mOpenHelper.getWritableDatabase().insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id != -1) {
                    returnedUri = ContentUris.withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert the row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedRows;
        if(TextUtils.isEmpty(selection)) {
            selection = "1";
        }
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                deletedRows = mOpenHelper.getReadableDatabase().delete(MovieContract.FavoriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in PopularMovies");
    }
}
