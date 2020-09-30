package com.uni.julio.superplus.viewmodel;

import com.uni.julio.superplus.databinding.ActivityLivetvnewBinding;
import com.uni.julio.superplus.model.LiveProgram;

public interface LiveTVViewModelContract {

    //this will have methods that the ViewModel will call from the Activity/Fragment to update it's view
    interface View extends Lifecycle.View {//}, LoadMoviesForCategoryResponseListener {
        void onProgramAccepted(LiveProgram liveProgram);//it needs the view to share the elements in the animation
    }

    //this will have methods that the activity/fragment will call from the ViewModel
    interface ViewModel extends Lifecycle.ViewModel {
        void showProgramList(ActivityLivetvnewBinding activityLiveBinding);
    }
}