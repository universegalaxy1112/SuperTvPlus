package com.uni.julio.supertv.viewmodel;


public interface SplashViewModelContract {

    interface View extends Lifecycle.View {
        void onLoginCompleted(boolean success);
        void onLoginError(String errorFound);
    }

    //this will have methods that the activity/fragment will call from the ViewModel
    interface ViewModel extends Lifecycle.ViewModel {
        void login();
    }
}