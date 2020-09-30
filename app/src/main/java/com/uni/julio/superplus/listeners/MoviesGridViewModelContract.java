package com.uni.julio.superplus.listeners;

import com.uni.julio.superplus.helper.TVRecyclerView;
import com.uni.julio.superplus.viewmodel.Lifecycle;

public interface MoviesGridViewModelContract {

    interface View extends Lifecycle.View {

    }

    interface ViewModel extends Lifecycle.ViewModel {
        void showMovieList(TVRecyclerView moviesGridRV, int mainCategoryPosition, int movieCategoryPosition);
     }
}