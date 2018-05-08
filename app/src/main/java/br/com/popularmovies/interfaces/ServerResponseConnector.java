package br.com.popularmovies.interfaces;

public interface ServerResponseConnector {

    void onConnectionResult(int statusCode, Object responseData);
}
