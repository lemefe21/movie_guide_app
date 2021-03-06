package com.leme.movieguideapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leme.movieguideapp.R;
import com.leme.movieguideapp.data.MovieContract;
import com.leme.movieguideapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.MovieItemAdapterViewHolder>{

    private final MovieItemAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private Cursor cursor;

    public interface MovieItemAdapterOnClickHandler {
        void onClick(int movieId);
    }

    public MovieItemAdapter(Context context, MovieItemAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieItemAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, viewGroup, false);
        view.setFocusable(true);

        return new MovieItemAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieItemAdapterViewHolder movieItemAdapterViewHolder, int position) {

        cursor.moveToPosition(position);

        movieItemAdapterViewHolder.mMovieNameTextView.setText(cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_TITLE));

        String poster_path = cursor.getString(MovieContract.MovieEntry.INDEX_MOVIE_POSTER_PATH);
        Picasso.with(mContext)
                .load(NetworkUtils.getBaseImageURL() + poster_path)
                .error(R.drawable.poster_default)
                .into(movieItemAdapterViewHolder.mMoviePosterImageView);

    }

    @Override
    public int getItemCount() {
        if(cursor == null) return 0;
        return cursor.getCount();
    }

    public void swapCursor(Cursor data) {
        cursor = data;
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
            cursor.moveToPosition(adapterPosition);
            int movieIdClicked = cursor.getInt(MovieContract.MovieEntry.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieIdClicked);
        }

    }
}
