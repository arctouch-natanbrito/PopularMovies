package br.com.popularmovies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.popularmovies.R;
import br.com.popularmovies.model.MovieTrailler;
import br.com.popularmovies.model.Movies;
import okhttp3.internal.Util;

public class MoviesTrailerAdapter extends RecyclerView.Adapter<MoviesTrailerAdapter.MoviesViewHolder>{

    List<MovieTrailler> moviesTrailerList;
    Context context;

    public MoviesTrailerAdapter(List<MovieTrailler> moviesTrailerList, Context context) {
        this.moviesTrailerList = moviesTrailerList;
        this.context = context;
    }


    @Override
    public MoviesTrailerAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer_layout, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesTrailerAdapter.MoviesViewHolder holder, int position) {
        holder.bind(moviesTrailerList.get(position));
    }

    public static class MoviesViewHolder extends RecyclerView.ViewHolder {

        WebView webView;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.wb);
        }

        public void bind(final MovieTrailler movies) {

            String video = "<html><body><iframe width=\"400\" height=\"240\" " +
                    "src=\"https://www.youtube.com/embed/"+movies.getKey()+"\" frameborder=\"0\"allowfullscreen/>" +
                    "</body></html>";
            WebSettings webSettings = webView.getSettings ();
            webSettings.setJavaScriptEnabled(true);
            webView.loadData(video, "text/html", "utf-8");

        }

    }

    @Override
    public int getItemCount() {
        return moviesTrailerList.size();
    }
}
