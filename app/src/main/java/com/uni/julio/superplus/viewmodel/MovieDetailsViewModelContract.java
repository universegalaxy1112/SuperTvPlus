package com.uni.julio.superplus.viewmodel;

import com.uni.julio.superplus.databinding.ActivityOneseasonDetailBinding;
import com.uni.julio.superplus.model.CastDevice;
import com.uni.julio.superplus.model.Movie;

public interface MovieDetailsViewModelContract {
     interface View extends Lifecycle.View {

        void onPlaySelected(Movie movie, int fromStart);
        void finishActivity();
        void onDeviceLoaded(CastDevice castDevice);
        void showMovieDetails(Movie movie ,int maincategory);

     }

    //this will have methods that the activity/fragment will call from the ViewModel
    interface ViewModel extends Lifecycle.ViewModel {
         void showMovieDetails(Movie movie, ActivityOneseasonDetailBinding movieDetailsBinding,   int mainCategoryId);
    }
}