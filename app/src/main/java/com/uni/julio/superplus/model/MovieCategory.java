package com.uni.julio.superplus.model;

import java.util.ArrayList;
import java.util.List;

public class MovieCategory extends BaseCategory {


    private List<VideoStream> movieList;
    private boolean isLoading = false;
    private boolean isLoaded = false;
    private boolean hasErrorLoading = false;
    private boolean categoryDisplayed = false;

    public MovieCategory() {
        movieList = new ArrayList<>();
    }

    public List<VideoStream> getMovieList() { return movieList; }
    public void setMovieList(List<VideoStream> list) { movieList = list; }
    public void addMovies(List<VideoStream> list) {
        movieList.addAll(list);
    }
    public boolean isLoading() { return isLoading; }
    public void setLoading(boolean loading) { isLoading = loading; }

    public boolean isLoaded() { return isLoaded; }
    public void setLoaded(boolean loaded) { isLoaded = loaded; }

    public boolean hasErrorLoading() { return hasErrorLoading; }
    public void setErrorLoading(boolean errorLoading) { hasErrorLoading = errorLoading; }
    public boolean isCategoryDisplayed() {
        return categoryDisplayed;
    }
    public void setCategoryDisplayed(boolean categoryDisplayed) {
        this.categoryDisplayed = categoryDisplayed;
    }
}
