package com.uni.julio.superplus.listeners;


import com.uni.julio.superplus.model.MovieCategory;
import com.uni.julio.superplus.model.VideoStream;

import java.util.List;

public interface LoadMoviesForCategoryResponseListener extends BaseResponseListener {
    void onMoviesForCategoryCompleted(MovieCategory movieCategory, List<VideoStream> movies, int offset);
    void onMoviesForCategoryCompletedError(MovieCategory movieCategory);
}
