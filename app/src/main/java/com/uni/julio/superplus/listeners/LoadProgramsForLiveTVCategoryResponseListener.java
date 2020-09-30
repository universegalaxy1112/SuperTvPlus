package com.uni.julio.superplus.listeners;


import com.uni.julio.superplus.model.LiveTVCategory;

public interface LoadProgramsForLiveTVCategoryResponseListener extends BaseResponseListener {
    void onProgramsForLiveTVCategoriesCompleted();
    void onProgramsForLiveTVCategoryCompleted(LiveTVCategory liveTVCategory);
    void onProgramsForLiveTVCategoryError(LiveTVCategory liveTVCategory);
}
