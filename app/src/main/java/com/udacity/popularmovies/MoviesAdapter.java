package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.List;

/**
 * Created by iulian on 2/23/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> mMoviesData;
    private final Context mContext;
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMoviesData.get(position);
        String posterUrl = NetworkUtils.TMDB_POSTER_URL + movie.getPosterPath();
        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.mPosterImage);
    }

    @Override
    public int getItemCount() {
        return mMoviesData != null? mMoviesData.size(): 0;
    }

    public void setMovieData(List<Movie> movies) {
        mMoviesData = movies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mPosterImage = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(mMoviesData.get(position));
        }
    }
}
