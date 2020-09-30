package com.uni.julio.superplus.viewmodel;

import android.app.ProgressDialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableBoolean;

import com.uni.julio.superplus.LiveTvApplication;
import com.uni.julio.superplus.R;
import com.uni.julio.superplus.databinding.ActivityAccountBinding;
import com.uni.julio.superplus.listeners.DialogListener;
import com.uni.julio.superplus.listeners.DownloaderListener;
import com.uni.julio.superplus.listeners.StringRequestListener;
import com.uni.julio.superplus.model.User;
import com.uni.julio.superplus.utils.Connectivity;
import com.uni.julio.superplus.utils.Device;
import com.uni.julio.superplus.utils.Dialogs;
import com.uni.julio.superplus.utils.networing.Downloader;
import com.uni.julio.superplus.utils.networing.NetManager;
import com.uni.julio.superplus.utils.networing.WebConfig;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AccountDetailsViewModel implements AccountDetailsViewModelContract.ViewModel {

    private final AppCompatActivity mActivity;
    private AccountDetailsViewModelContract.View viewCallback;
    public ObservableBoolean isLoading;
    private ObservableBoolean isTV;
    private List<String> modelList = new ArrayList<>();
    private ActivityAccountBinding activityAccountBinding;

    public AccountDetailsViewModel(AppCompatActivity activity, ActivityAccountBinding activityAccountBinding) {
        this.activityAccountBinding = activityAccountBinding;
        isLoading = new ObservableBoolean(false);

        getModels();
        mActivity = activity;
    }

    @Override
    public void onViewResumed() {

    }

    @Override
    public void onViewAttached(@NonNull Lifecycle.View viewCallback) {
        //set the callback to the fragment (using the BaseFragment class)
        this.viewCallback = (AccountDetailsViewModelContract.View) viewCallback;
    }

    @Override
    public void onViewDetached() {
        this.viewCallback = null;
    }

    public void onCloseSession(View view) {
        if (Device.canTreatAsBox()) {
            Dialogs.showTwoButtonsDialog(this.mActivity, R.string.accept, (R.string.cancel), R.string.end_session_message, new DialogListener() {
                public void onAccept() {
                    AccountDetailsViewModel.this.onCloseSession();
                }

                public void onCancel() {
                }

                @Override
                public void onDismiss() {

                }
            });
        } else {
            onCloseSession();
        }
    }

    private void getModels() {
        if (Connectivity.isConnected()) {
            this.isLoading.set(true);
            final User user = LiveTvApplication.getUser();
            if (user != null) {
                String url = WebConfig.getMessage.replace("{USER}", user.getName());
                NetManager.getInstance().makeStringRequest(url, new StringRequestListener() {
                    public void onCompleted(String response) {
                        AccountDetailsViewModel.this.isLoading.set(false);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < 3; i++) {
                                if (jsonArray.length() > i) {
                                    modelList.add(jsonArray.getString(i));
                                    if (i != 0 && user.getDevice().contains(jsonArray.getString(i))) {
                                        String temp = modelList.get(0);
                                        modelList.set(0, modelList.get(i));
                                        modelList.set(i, temp);
                                    }
                                } else modelList.add("Not Registered");
                            }
                            activityAccountBinding.device0.setText(modelList.get(0));
                            activityAccountBinding.device1.setText(modelList.get(1));
                            activityAccountBinding.device2.setText(modelList.get(2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //JSONArray jsonArray = new JSONArray(response)
                    }

                    public void onError() {
                        AccountDetailsViewModel.this.isLoading.set(false);
                    }
                });
                return;
            }
            this.viewCallback.onCloseSessionNoInternet();
        }
    }

    public void onCloseSession() {
        if (Connectivity.isConnected()) {
            this.isLoading.set(true);
            if (LiveTvApplication.getUser() != null) {
                String url = WebConfig.removeUserURL.replace("{USER}", LiveTvApplication.getUser().getName()).replace("{DEVICE_ID}", LiveTvApplication.getUser().getDeviceId());
                NetManager.getInstance().makeStringRequest(url, new StringRequestListener() {
                    public void onCompleted(String response) {
                        if (response.toLowerCase().contains("success")) {
                            AccountDetailsViewModel.this.viewCallback.onCloseSessionSelected();
                        }
                    }

                    public void onError() {
                        AccountDetailsViewModel.this.isLoading.set(false);
                    }
                });
                return;
            }
            return;
        }
        this.viewCallback.onCloseSessionNoInternet();

    }

    @Override
    public void showAccountDetails() {
        if (LiveTvApplication.getUser() != null) {
            this.activityAccountBinding.setUser(LiveTvApplication.getUser());
        } else {
            viewCallback.onError();
        }
    }

    @Override
    public void checkForUpdate(View view) {
        /*NetManager.getInstance().performCheckForUpdate(new StringRequestListener() {
            @Override
            public void onCompleted(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("android_version")) {
                            if (!Device.getVersionInstalled().replaceAll("\\.", "").equals(jsonObject.getString("android_version"))) {
                                AccountDetailsViewModel.this.viewCallback.onCheckForUpdateCompleted(true, jsonObject.getString("link_android") + "/android" + jsonObject.getString("android_version") + ".apk");
                                return;
                            }
                            AccountDetailsViewModel.this.viewCallback.onCheckForUpdateCompleted(false, null);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }
        });*/
    }

    @Override
    public void downloadUpdate(String str, ProgressDialog progressDialog) {
        Downloader.getInstance().performDownload(str, progressDialog, new DownloaderListener() {
            @Override
            public void onDownloadComplete(String str) {
                AccountDetailsViewModel.this.viewCallback.onDownloadUpdateCompleted(str);
            }

            @Override
            public void onDownloadError(int i) {

            }
        });
    }
}