package br.com.popularmovies.connection;

import java.util.List;

import br.com.popularmovies.model.MoviesCatalog;
import br.com.popularmovies.model.TraillerCatalog;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndpointInterface {

    public static final String BASE_URL="https://api.themoviedb.org/3/";

    @GET("discover/movie")
    Call<MoviesCatalog> moviesList(@Query("api_key") String key,
                                   @Query("language") String language,
                                   @Query("include_video") boolean hasVideo,
                                   @Query("page") int page);

    @GET("movie/{id}/videos")
    Call<TraillerCatalog> traillerCatalog(@Path("id") String id,
                                              @Query("api_key") String key,
                                              @Query("language") String language);
}
