package com.uni.julio.supertv.listeners;


import com.uni.julio.supertv.model.MovieCategory;
import com.uni.julio.supertv.model.VideoStream;

import java.util.List;

public interface LoadMoviesForCategoryResponseListener extends BaseResponseListener {
    void onMoviesForCategoryCompleted(MovieCategory movieCategory, List<VideoStream> movies, int offset);
    void onMoviesForCategoryCompletedError(MovieCategory movieCategory);
}
