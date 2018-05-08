package br.com.popularmovies.model;

public class Movies {

    public String title,overview,poster_path,vote_average,id;
    public static final String IMAGE_PATH="https://image.tmdb.org/t/p/w500/";


    public Movies(String title, String overview, String poster_path, String vote_average) {
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return IMAGE_PATH+poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }
}
