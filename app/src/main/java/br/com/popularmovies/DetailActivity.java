package br.com.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.popularmovies.adapters.MoviesTrailerAdapter;
import br.com.popularmovies.connection.RestApiKey;
import br.com.popularmovies.interfaces.ServerResponseConnector;
import br.com.popularmovies.model.MovieTrailler;
import br.com.popularmovies.model.Movies;
import br.com.popularmovies.model.TraillerCatalog;

public class DetailActivity extends AppCompatActivity implements ServerResponseConnector {

    String title, poster, overview, id;
    TextView tvOverview;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView imageView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RestApiKey task;
    MoviesTrailerAdapter adapter;
    List<MovieTrailler> movieTraillerList = new ArrayList<>();
    String youtubeTrailer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        Intent i = getIntent();
        task = new RestApiKey();

        if(i.getStringExtra("title")!=null || !i.getStringExtra("title").isEmpty()) {
            title = i.getStringExtra("title");
            collapsingToolbarLayout.setTitle(title);
        }

        if(i.getStringExtra("poster")!=null || !i.getStringExtra("poster").isEmpty()) {
            poster = i.getStringExtra("poster");
            Picasso.with(this).load(poster).into(imageView);
        }

        if(i.getStringExtra("overview")!=null || !i.getStringExtra("overview").isEmpty()) {
            overview = i.getStringExtra("overview");
            tvOverview.setText(overview);
        }

        if(i.getStringExtra("id")!=null || !i.getStringExtra("id").isEmpty()) {
            id = i.getStringExtra("id");
        }
        new RetrieveData().execute();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvOverview = (TextView) findViewById(R.id.tv_overview);
        imageView = (ImageView) findViewById(R.id.img);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ct);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void conn() {
        task.setServerResponseConnector(this);
    }

    public class RetrieveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            conn();
            task.Trailer(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(adapter);

        }
    }


    @Override
    public void onConnectionResult(int statusCode, Object responseData) {

        if (statusCode == RestApiKey.NO_CONNECTION_ERROR) {
            Toast.makeText(this, "O seu dispositivo não está conectado a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (statusCode == RestApiKey.GENERIC_RESPONSE_CODE_ERROR
                || statusCode == RestApiKey.NOT_FOUND
                || statusCode == RestApiKey.FORBIDDEN) {
            Toast.makeText(this, "Ocorreu um erro inesperado", Toast.LENGTH_SHORT).show();
            return;
        }
        if (statusCode == 200) {
            if (responseData instanceof TraillerCatalog) {
                TraillerCatalog catalog = (TraillerCatalog) responseData;

                for(MovieTrailler trailer: catalog.getResults()) {
                    movieTraillerList.add(trailer);
                }

                if(movieTraillerList != null && movieTraillerList.size() > 0){
                    adapter = new MoviesTrailerAdapter(movieTraillerList, this);
                }
            }
        }

    }
}
