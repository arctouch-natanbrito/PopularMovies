package br.com.popularmovies.model;

import java.util.List;

public class MoviesCatalog {
    List<Movies> results;
    int page,total_pages,total_results;

    public List<Movies> getResults() {
        return results;
    }

    public int getPage() {
        return page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }
}
