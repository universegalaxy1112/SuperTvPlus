package com.uni.julio.superplus.viewmodel;


import android.app.ProgressDialog;

public interface AccountDetailsViewModelContract {

    interface View extends Lifecycle.View {
        void onCloseSessionSelected();
        void onCloseSessionNoInternet();
        void onError();
        void onCheckForUpdateCompleted(boolean z, String str);

        void onDownloadUpdateCompleted(String str);

        void onDownloadUpdateError(int i);
    }
    interface ViewModel extends Lifecycle.ViewModel {
        void showAccountDetails();
        void checkForUpdate(android.view.View view);

        void downloadUpdate(String str, ProgressDialog progressDialog);
    }
}