package com.uni.julio.superplus.listeners;

import com.uni.julio.superplus.model.MainCategory;
import com.uni.julio.superplus.model.MovieCategory;

import java.util.List;

public interface LoadSubCategoriesResponseListener extends BaseResponseListener {
    void onSubCategoriesLoaded(MainCategory mainCategory, List<MovieCategory> movieCategories);
    void onSubCategoriesLoadedError();
}
