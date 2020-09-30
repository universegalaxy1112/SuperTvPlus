package com.uni.julio.superplus.viewmodel;
import com.uni.julio.superplus.helper.TVRecyclerView;

public interface MoviesMenuViewModelContract {

    interface View extends Lifecycle.View {
        void onShowAsGridSelected(Integer position);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void showMovieLists(TVRecyclerView categoriesRecyclerview, int mainCategoryPosition);
    }
}