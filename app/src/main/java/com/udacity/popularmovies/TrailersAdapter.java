package com.udacity.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by iulian on 2/23/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    private List<Trailer> mTrailersData;
    private final TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerKey);
    }

    public TrailersAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailersData.get(position);
        holder.mTrailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailersData != null? mTrailersData.size(): 0;
    }

    public void setTrailerData(List<Trailer> trailers) {
        mTrailersData = trailers;
        notifyDataSetChanged();
    }

    public List<Trailer> getTrailersData() {
        return mTrailersData;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerName = itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(mTrailersData.get(position).getKey());
        }
    }
}
