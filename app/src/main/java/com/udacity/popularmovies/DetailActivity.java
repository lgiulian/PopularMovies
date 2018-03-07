package com.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.MovieContract;
import com.udacity.popularmovies.databinding.ActivityDetailBinding;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;
import com.udacity.popularmovies.utils.NetworkUtils;
import com.udacity.popularmovies.utils.TmDbJsonUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List> {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int TRAILERS_LOADER_ID = 1;
    private static final int REVIEWS_LOADER_ID = 2;
    private final DateFormat dateFormat_ddMMMMYYYY = new SimpleDateFormat("dd MMMM YYYY");

    private ActivityDetailBinding mBinding;
    private Movie mMovie;

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(MainActivity.MOVIE_EXTRA_KEY)) {
            mMovie = extras.getParcelable(MainActivity.MOVIE_EXTRA_KEY);
            if (mMovie != null) {
                Log.d(TAG, mMovie.toString());
            }
        }

        setupTrailersList();
        setupReviewsList();

        mBinding.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processFavoriteClicked((ToggleButton) v);
            }
        });

        populateUi();
    }

    private void setupTrailersList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvTrailers.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mBinding.rvTrailers.getContext(),
                layoutManager.getOrientation()
        );
        mBinding.rvTrailers.addItemDecoration(dividerItemDecoration);

        mTrailersAdapter = new TrailersAdapter(this, this);
        mBinding.rvTrailers.setAdapter(mTrailersAdapter);

        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
    }

    private void setupReviewsList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvReviews.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mBinding.rvReviews.getContext(),
                layoutManager.getOrientation()
        );
        mBinding.rvReviews.addItemDecoration(dividerItemDecoration);

        mReviewsAdapter = new ReviewsAdapter(this);
        mBinding.rvReviews.setAdapter(mReviewsAdapter);

        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_share_trailer:
                shareFirstTrailerUrl();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    private void populateUi() {
        if (mMovie != null) {
            String posterUrl = NetworkUtils.TMDB_POSTER_URL + mMovie.getPosterPath();
            Picasso.with(this)
                    .load(posterUrl)
                    .into(mBinding.ivPoster);
            mBinding.tvMovieTitle.setText(mMovie.getTitle());
            mBinding.tvReleaseDate.setText(dateFormat_ddMMMMYYYY.format(mMovie.getReleaseDate()));
            mBinding.tvVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
            mBinding.tvPlotSynopsis.setText(mMovie.getOverview());
            Cursor cursor = getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                    new String[] {MovieContract.FavoriteEntry.COLUMN_MOVIE_ID},
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                    new String[] {String.valueOf(mMovie.getId())},
                    null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {

                    mBinding.favoriteButton.setChecked(true);
                } else {
                    mBinding.favoriteButton.setChecked(false);
                }
                cursor.close();
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return getTrailersTask();
            case REVIEWS_LOADER_ID:
                return getReviewsTask();
            default:
                throw new RuntimeException("Loader unknown");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, List data) {
        switch (loader.getId()) {
            case TRAILERS_LOADER_ID:
                mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
                mTrailersAdapter.setTrailerData(data);
                if (data == null) {
                    showErrorMessage();
                } else {
                    showMoviesList();
                }
                break;
            case REVIEWS_LOADER_ID:
                mBinding.pbLoadingIndicatorReview.setVisibility(View.INVISIBLE);
                mReviewsAdapter.setReviewData(data);
                if (data == null) {
                    showReviewsErrorMessage();
                } else {
                    showReviewsList();
                }
                break;
        }
    }

    private void showMoviesList() {
        mBinding.rvTrailers.setVisibility(View.VISIBLE);
        mBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mBinding.rvTrailers.setVisibility(View.INVISIBLE);
        mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showReviewsList() {
        mBinding.rvReviews.setVisibility(View.VISIBLE);
        mBinding.tvReviewErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showReviewsErrorMessage() {
        mBinding.rvReviews.setVisibility(View.INVISIBLE);
        mBinding.tvReviewErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(String trailerKey) {
        String trailerUrl = NetworkUtils.getTrailerUrl(trailerKey);
        Log.d(TAG, trailerUrl);
        openWebPage(trailerUrl);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void processFavoriteClicked(ToggleButton favBtn) {
        if (favBtn.isChecked()) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
            values.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            values.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate().getTime());
            values.put(MovieContract.FavoriteEntry.COLUMN_TITLE, mMovie.getTitle());
            values.put(MovieContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
        } else {
            getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(mMovie.getId())});
        }
    }

    private AsyncTaskLoader<List<Trailer>> getTrailersTask() {
        return new AsyncTaskLoader<List<Trailer>>(this) {
            private List<Trailer> mTrailers;

            @Nullable
            @Override
            public List<Trailer> loadInBackground() {
                List<Trailer> trailers = null;
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildTrailersUrl(mMovie.getId()));
                    trailers = TmDbJsonUtils.getTrailersFromJsonResponse(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return trailers;
            }

            @Override
            public void deliverResult(@Nullable List<Trailer> data) {
                mTrailers = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.isConnected(DetailActivity.this)) {
                    if (mTrailers != null) {
                        deliverResult(mTrailers);
                    } else {
                        mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private AsyncTaskLoader<List<Review>> getReviewsTask() {
        return new AsyncTaskLoader<List<Review>>(this) {
            private List<Review> mReviews;

            @Nullable
            @Override
            public List<Review> loadInBackground() {
                List<Review> reviews = null;
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildReviewsUrl(mMovie.getId()));
                    reviews = TmDbJsonUtils.getReviewsFromJsonResponse(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return reviews;
            }

            @Override
            public void deliverResult(@Nullable List<Review> data) {
                mReviews = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.isConnected(DetailActivity.this)) {
                    if (mReviews != null) {
                        deliverResult(mReviews);
                    } else {
                        mBinding.pbLoadingIndicatorReview.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void shareFirstTrailerUrl() {
        List<Trailer> trailers = mTrailersAdapter.getTrailersData();
        if (trailers != null && trailers.size() > 0) {
            String mimeType = "text/plain";
            Trailer trailer = trailers.get(0);
            String title = trailer.getName();
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle(title)
                    .setText(NetworkUtils.getTrailerUrl(trailer.getKey()))
                    .startChooser();
        }
    }
}
