package com.leme.movieguideapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.leme.movieguideapp.models.VideoResult;

import java.util.List;

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.MovieVideosAdapterViewHolder> {

    private Context mContext;
    private List<VideoResult> mVideoResult;

    public MovieVideoAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapterViewHolder movieVideosAdapterViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if(mVideoResult == null) return 0;
        return mVideoResult.size();
    }

    public class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder {

        //view
        //view

        public MovieVideosAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //view
            //view

        }
    }

}
