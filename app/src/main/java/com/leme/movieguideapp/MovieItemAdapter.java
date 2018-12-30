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
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.MovieItemAdapterViewHolder>{

    private List<Movie> mMovieList;
    private final MovieItemAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public interface MovieItemAdapterOnClickHandler {
        void onClick(Movie movieClicked);
    }

    public MovieItemAdapter(Context context, MovieItemAdapterOnClickHandler clickHandler) {
        mContext = context;
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
        String poster_path = movie.getPoster_path();
        movieItemAdapterViewHolder.mMovieNameTextView.setText(movie.getTitle());
        Picasso.with(mContext)
                .load(NetworkUtils.getBaseImageURL() + poster_path)
                .error(R.drawable.poster_default)
                .into(movieItemAdapterViewHolder.mMoviePosterImageView);

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
