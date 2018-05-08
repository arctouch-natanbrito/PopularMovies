package br.com.popularmovies.connection;

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import br.com.popularmovies.interfaces.ServerResponseConnector;
import br.com.popularmovies.model.Movies;
import br.com.popularmovies.model.MoviesCatalog;
import br.com.popularmovies.model.TraillerCatalog;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiKey {

    String baseUrl = EndpointInterface.BASE_URL;
    Retrofit retrofit;
    ServerResponseConnector connector;
    EndpointInterface endpointInterface;
    OkHttpClient okHttpClient;
    public static final String API_KEY="ADD YOUR KEY";
    public static final String BR_PORTUGUESE = "pt-BR";
    public static final String POPULARITY = "popularity.desc";
    public static int GENERIC_RESPONSE_CODE_ERROR = 500;
    public static int NO_CONNECTION_ERROR = -2;
    public static int UNAUTHORIZED = 401;
    public static int FORBIDDEN = 403;
    public static int NOT_FOUND = 404;

    public void configure() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        endpointInterface = retrofit.create(EndpointInterface.class);
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient != null)
            return okHttpClient;

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                return chain.proceed(request);
            }
        });

        clientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        clientBuilder.connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS);

        okHttpClient = clientBuilder.build();

        return okHttpClient;
    }

    public void setServerResponseConnector(ServerResponseConnector connector) {
        this.connector = connector;
    }

    public void Trailer(String id) {
        configure();
        try {
            retrofit2.Response<TraillerCatalog> trailerResponse = endpointInterface.traillerCatalog(id,API_KEY, BR_PORTUGUESE).execute();
            if (trailerResponse.code() > 300) {
                Converter<ResponseBody, Movies> responseBodyMoviesConverter = retrofit.responseBodyConverter(Movies.class, new Annotation[0]);
                Movies error = responseBodyMoviesConverter.convert(trailerResponse.errorBody());
                connector.onConnectionResult(trailerResponse.code(), error);
            } else {
                connector.onConnectionResult(trailerResponse.code(), trailerResponse.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moviesList(int currentPage) {

        configure();

        try {
            retrofit2.Response<MoviesCatalog> moviesCatalogResponse = endpointInterface.moviesList(API_KEY, BR_PORTUGUESE, true, currentPage).execute();
            Log.d("TESTE", "moviesList: " + moviesCatalogResponse);
            if (moviesCatalogResponse.code() > 300) {
                Converter<ResponseBody, Movies> responseBodyMoviesConverter = retrofit.responseBodyConverter(Movies.class, new Annotation[0]);
                Movies error = responseBodyMoviesConverter.convert(moviesCatalogResponse.errorBody());
                connector.onConnectionResult(moviesCatalogResponse.code(), error);
            } else {
                connector.onConnectionResult(moviesCatalogResponse.code(), moviesCatalogResponse.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
