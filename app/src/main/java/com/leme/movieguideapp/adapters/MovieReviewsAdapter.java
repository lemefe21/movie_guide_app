package com.leme.movieguideapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leme.movieguideapp.R;
import com.leme.movieguideapp.models.ReviewResult;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {

    private Context mContext;
    private List<ReviewResult> mReviewResult;

    public MovieReviewsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_review, viewGroup, false);

        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsAdapterViewHolder viewHolder, int position) {

        ReviewResult reviewResult = mReviewResult.get(position);

        viewHolder.mAuthor.setText(reviewResult.getAuthor());
        viewHolder.mContent.setText(reviewResult.getContent());

    }

    @Override
    public int getItemCount() {
        if(mReviewResult == null) return 0;
        return mReviewResult.size();
    }

    public void setReviewData(List<ReviewResult> reviewResult) {
        mReviewResult = reviewResult;
        notifyDataSetChanged();
    }

    public class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mAuthor;
        public final TextView mContent;

        public MovieReviewsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mAuthor = itemView.findViewById(R.id.tv_movie_detail_author);
            mContent = itemView.findViewById(R.id.tv_movie_detail_content_review);

        }

    }

}
