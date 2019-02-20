package com.leme.movieguideapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.models.VideoResult;

import java.util.List;

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosAdapterViewHolder> {

    private final MovieVideoAdapterOnClickHandler mClickHandle;
    private Context mContext;
    private List<VideoResult> mVideoResult;

    public MovieVideosAdapter(Context context, MovieVideoAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandle = clickHandler;
    }

    public interface MovieVideoAdapterOnClickHandler {
        void onClick(int movieId);
    }

    @NonNull
    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //TODO onCreateViewHolder

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapterViewHolder movieVideosAdapterViewHolder, int i) {

        //TODO onBindViewHolder

    }

    @Override
    public int getItemCount() {
        if(mVideoResult == null) return 0;
        return mVideoResult.size();
    }

    public class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //view
        //view

        public MovieVideosAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //view
            //view

        }

        @Override
        public void onClick(View v) {
            /*int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int movieIdClicked = cursor.getInt(MovieContract.MovieEntry.INDEX_MOVIE_ID);
            //Movie movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movieIdClicked);*/
        }
    }

}
