package com.uni.julio.superplus.viewmodel;

import com.uni.julio.superplus.helper.TVRecyclerView;
import com.uni.julio.superplus.model.MainCategory;

public interface MainCategoriesMenuViewModelContract {
     interface View extends Lifecycle.View {
         void onMainCategorySelected(MainCategory mainCategory);
        void onAccountPressed();
    }
     interface ViewModel extends Lifecycle.ViewModel {
        void showMainCategories(TVRecyclerView mainCategoriesRV);
     }
}