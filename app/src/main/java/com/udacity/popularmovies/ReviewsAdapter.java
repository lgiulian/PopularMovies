package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.model.Review;

import java.util.List;

/**
 * Created by iulian on 2/23/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> mReviewsData;
    private Context mContext;

    public ReviewsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviewsData.get(position);
        holder.mReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewsData != null? mReviewsData.size(): 0;
    }

    public void setReviewData(List<Review> reviews) {
        mReviewsData = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewContent = itemView.findViewById(R.id.review_content);
        }
    }
}
