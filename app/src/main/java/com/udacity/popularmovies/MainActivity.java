package com.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;
import com.udacity.popularmovies.utils.TmDbJsonUtils;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int RECYCLERVIEW_SPANCOUNT = 2;
    private static final int MOVIES_LOADER_ID = 1;
    public static final String MOVIE_EXTRA_KEY = "MOVIE_EXTRA_KEY";

    private RecyclerView mMoviesView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageTv;
    private ProgressBar mLoadingIndicatorPb;

    enum Order {MOST_POPULAR, HIGHEST_RATED}

    private Order mCurrentOrder = Order.MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTv = findViewById(R.id.tv_error_message);
        mLoadingIndicatorPb = findViewById(R.id.pb_loading_indicator);

        mMoviesView = findViewById(R.id.rv_movies);
        mMoviesView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, RECYCLERVIEW_SPANCOUNT);
        mMoviesView.setLayoutManager(layoutManager);

        mMoviesAdapter = new MoviesAdapter(this, this);

        mMoviesView.setAdapter(mMoviesAdapter);

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            private List<Movie> mMovies = null;

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                List<Movie> movies = null;

                try {
                    String url;
                    if (mCurrentOrder == Order.MOST_POPULAR) {
                        url = NetworkUtils.TMDB_API_MOVIE_POPULAR_URL;
                    } else if (mCurrentOrder == Order.HIGHEST_RATED) {
                        url = NetworkUtils.TMDB_API_MOVIE_TOP_RATED_URL;
                    } else {
                        url = NetworkUtils.TMDB_API_MOVIE_POPULAR_URL;
                    }
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(url));
                    movies = TmDbJsonUtils.getMoviesFromJsonResponse(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return movies;
            }

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.isConnected(MainActivity.this)) {
                    if (mMovies != null) {
                        deliverResult(mMovies);
                    } else {
                        mLoadingIndicatorPb.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicatorPb.setVisibility(View.INVISIBLE);
        mMoviesAdapter.setMovieData(data);
        if (data == null) {
            showErrorMessage();
        } else {
            showMoviesList();
        }
    }

    private void showMoviesList() {
        mMoviesView.setVisibility(View.VISIBLE);
        mErrorMessageTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mMoviesView.setVisibility(View.INVISIBLE);
        mErrorMessageTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_highest_rated:
                mCurrentOrder = Order.HIGHEST_RATED;
                mMoviesAdapter.setMovieData(null);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                return true;
            case R.id.action_most_popular:
                mCurrentOrder = Order.MOST_POPULAR;
                mMoviesAdapter.setMovieData(null);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MOVIE_EXTRA_KEY, movie);
        startActivity(intent);
    }
}
