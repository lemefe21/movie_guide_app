package com.leme.movieguideapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leme.movieguideapp.models.Movie;

import java.util.List;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.MovieItemAdapterViewHolder>{

    private List<Movie> mMovieList;
    private final MovieItemAdapterOnClickHandler mClickHandler;

    public interface MovieItemAdapterOnClickHandler {
        void onClick(Movie movieClicked);
    }

    public MovieItemAdapter(MovieItemAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieItemAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieItemAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieItemAdapterViewHolder movieItemAdapterViewHolder, int position) {

        Movie movie = mMovieList.get(position);
        movieItemAdapterViewHolder.mMovieNameTextView.setText(movie.getTitle());

    }

    @Override
    public int getItemCount() {
        if(mMovieList == null) return 0;
        return mMovieList.size();
    }

    public void setMovieData(List<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public class MovieItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mMovieNameTextView;
        public final ImageView mMoviePosterImageView;

        public MovieItemAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mMovieNameTextView = itemView.findViewById(R.id.tv_movie_name);
            mMoviePosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

}
