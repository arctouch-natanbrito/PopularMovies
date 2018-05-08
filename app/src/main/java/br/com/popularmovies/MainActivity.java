package br.com.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.popularmovies.adapters.MoviesAdapter;
import br.com.popularmovies.connection.RestApiKey;
import br.com.popularmovies.interfaces.ServerResponseConnector;
import br.com.popularmovies.model.Movies;
import br.com.popularmovies.model.MoviesCatalog;

public class MainActivity extends AppCompatActivity implements ServerResponseConnector {

    private RecyclerView rv;
    private GridLayoutManager layoutManager;
    MoviesAdapter adapter;
    private int currentPage = 1;
    private int TOTAL_PAGES = 5;
    List<Movies> moviesList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    RestApiKey task;
    boolean isLoading = true;
    int totalPages;
    int previous_total = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        rv = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new GridLayoutManager(this, 2);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        task = new RestApiKey();
        loadData();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previous_total) {
                            isLoading = false;
                            previous_total = totalItemCount;
                        }
                    }


                    if (!isLoading && (totalItemCount-visibleItemCount) <= (lastVisible+10)) {
                        if(currentPage <= TOTAL_PAGES)
                            loadData();
                        isLoading = true;
                        currentPage += 1;
                    }
                }
            }
        });

    }

    public void loadData() {
        new RetrieveData().execute();

    }

    public class RetrieveData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            conn();
            if (currentPage <= TOTAL_PAGES)
                task.moviesList(currentPage);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            if (currentPage == 1)
                rv.setAdapter(adapter);
            else
                adapter.notifyItemRangeChanged(adapter.getItemCount(), moviesList.size() - 1);
        }
    }


    private void conn() {
        task.setServerResponseConnector(this);
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
            if (responseData instanceof MoviesCatalog) {
                MoviesCatalog catalog = (MoviesCatalog) responseData;
                totalPages = catalog.getTotal_pages();
                if (currentPage == catalog.getPage() && currentPage <= TOTAL_PAGES) {
                    for (final Movies movie : catalog.getResults()) {
                        moviesList.add(movie);
                    }
                }

                if (moviesList != null && moviesList.size() > 0) {
                    adapter = new MoviesAdapter(moviesList, this, new MoviesAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(Movies movies) {
                            Intent i = new Intent(MainActivity.this, DetailActivity.class);
                            i.putExtra("title", movies.getTitle());
                            i.putExtra("poster", movies.getPoster_path());
                            i.putExtra("overview", movies.getOverview());
                            i.putExtra("id",movies.getId());
                            startActivity(i);
                        }
                    });
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
