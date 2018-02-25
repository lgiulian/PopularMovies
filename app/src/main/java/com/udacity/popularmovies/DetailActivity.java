package com.udacity.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private DateFormat dateFormat_ddMMMMYYYY = new SimpleDateFormat("dd MMMM YYYY");

    private Movie mMovie;
    private ImageView mPosterImgView;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mPlotSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(MainActivity.MOVIE_EXTRA_KEY)) {
            mMovie = extras.getParcelable(MainActivity.MOVIE_EXTRA_KEY);
            Log.d(TAG, mMovie.toString());
        }

        mPosterImgView = findViewById(R.id.iv_poster);
        mTitle = findViewById(R.id.tv_movie_title);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mVoteAverage = findViewById(R.id.tv_vote_average);
        mPlotSynopsis = findViewById(R.id.tv_plot_synopsis);
        populateUi();
    }

    private void populateUi() {
        if (mMovie != null) {
            String posterUrl = NetworkUtils.TMDB_POSTER_URL + mMovie.getPosterPath();
            Picasso.with(this)
                    .load(posterUrl)
                    .into(mPosterImgView);
            mTitle.setText(mMovie.getTitle());
            mReleaseDate.setText(dateFormat_ddMMMMYYYY.format(mMovie.getReleaseDate()));
            mVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
            mPlotSynopsis.setText(mMovie.getOverview());
        }
    }
}
