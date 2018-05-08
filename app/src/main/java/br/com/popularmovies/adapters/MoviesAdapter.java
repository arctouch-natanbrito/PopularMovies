package br.com.popularmovies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.popularmovies.R;
import br.com.popularmovies.model.Movies;
import br.com.popularmovies.model.MoviesCatalog;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> implements Filterable {

    List<Movies> moviesList;
    List<Movies> mFilteredList;
    Context context;
    private boolean isLoadingAdded = false;
    private final OnClickListener onClickListener;


    public interface OnClickListener {
        void onItemClick(Movies movies);
    }

    public MoviesAdapter(List<Movies> moviesList, Context context, OnClickListener onClickListener) {
        this.moviesList = moviesList;
        this.mFilteredList = moviesList;
        this.context = context;
        this.onClickListener = onClickListener;
    }


    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_layout, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesViewHolder holder, int position) {
        holder.bind(mFilteredList.get(position), onClickListener);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = moviesList;
                } else {

                    ArrayList<Movies> filteredList = new ArrayList<>();

                    for (Movies movie : moviesList) {

                        if (movie.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(movie);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Movies>) filterResults.values;
            }
        };
    }

    public static class MoviesViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_vote;
        ImageView iv_poster;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            tv_vote = (TextView) itemView.findViewById(R.id.tv_vote);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            iv_poster = (ImageView) itemView.findViewById(R.id.iv_poster);
        }

        public void bind(final Movies movies, final OnClickListener clickListener) {
            Picasso.with(itemView.getContext()).load(movies.getPoster_path()).into(iv_poster);
            tv_title.setText(movies.title);
            tv_vote.setText(movies.vote_average);
            Double rating = Double.parseDouble(movies.vote_average);
            if (rating >= 5 && rating < 6)
                tv_vote.setTextColor(Color.YELLOW);
            else if (rating >= 6 && rating <= 10)
                tv_vote.setTextColor(Color.GREEN);
            else
                tv_vote.setTextColor(Color.RED);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(movies);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }
}
