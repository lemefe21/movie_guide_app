package com.leme.movieguideapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leme.movieguideapp.R;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.VideoResult;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosAdapterViewHolder> {

    private final MovieVideoAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private List<VideoResult> mVideoResult;

    public MovieVideosAdapter(Context context, MovieVideoAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    public interface MovieVideoAdapterOnClickHandler {
        void onClick(String movieKey);
    }

    @NonNull
    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_video, viewGroup, false);
        return new MovieVideosAdapter.MovieVideosAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapterViewHolder movieVideosAdapterViewHolder, int position) {

        VideoResult videoResult = mVideoResult.get(position);
        String videoResultKey = videoResult.getKey();
        String thumbnailURL = NetworkUtils.getBaseImageThumbnailURL(videoResultKey);

        Picasso.with(mContext)
                .load(thumbnailURL)
                .error(R.drawable.thumbnail_default)
                .into(movieVideosAdapterViewHolder.mThumbnailVideo);

    }

    @Override
    public int getItemCount() {
        if(mVideoResult == null) return 0;
        return mVideoResult.size();
    }

    public void setVideoData(List<VideoResult> videoResults) {
        mVideoResult = videoResults;
        notifyDataSetChanged();
    }

    public class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mThumbnailVideo;

        public MovieVideosAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnailVideo = itemView.findViewById(R.id.im_thumbnail_video);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            VideoResult videoResult = mVideoResult.get(adapterPosition);
            mClickHandler.onClick(videoResult.getKey());
        }
    }

}
