package com.udacity.popularmovies;

import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.databinding.ActivityDetailBinding;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private final DateFormat dateFormat_ddMMMMYYYY = new SimpleDateFormat("dd MMMM YYYY");

    private ActivityDetailBinding mBinding;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(MainActivity.MOVIE_EXTRA_KEY)) {
            mMovie = extras.getParcelable(MainActivity.MOVIE_EXTRA_KEY);
            if (mMovie != null) {
                Log.d(TAG, mMovie.toString());
            }
        }

        populateUi();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        }
    }
}
