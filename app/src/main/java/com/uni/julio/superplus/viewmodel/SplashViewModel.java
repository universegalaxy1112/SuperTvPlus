package com.uni.julio.superplus.viewmodel;

import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.uni.julio.superplus.LiveTvApplication;
import com.uni.julio.superplus.listeners.StringRequestListener;
import com.uni.julio.superplus.model.User;
import com.uni.julio.superplus.utils.DataManager;
import com.uni.julio.superplus.utils.Device;
import com.uni.julio.superplus.utils.networing.NetManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashViewModel implements SplashViewModelContract.ViewModel, StringRequestListener {

    //    public boolean isConnected;
    private NetManager netManager;
    private SplashViewModelContract.View viewCallback;
    private User user;

    public SplashViewModel(SplashViewModelContract.View splash) {
        this.viewCallback = splash;
        netManager = NetManager.getInstance();
    }

    @Override
    public void onViewResumed() {

    }

    @Override
    public void onViewAttached(@NonNull Lifecycle.View viewCallback) {
        //set the callback to the fragment (using the BaseFragment class)
        this.viewCallback = (SplashViewModelContract.View) viewCallback;
    }

    @Override
    public void onViewDetached() {
        this.viewCallback = null;
    }

    @Override
    public void login() {
        String usr = "";
        String password = "";
        String id = "";
        String theUser = DataManager.getInstance().getString("theUser", "");

        if (!TextUtils.isEmpty(theUser)) {
            user = new Gson().fromJson(theUser, User.class);
            LiveTvApplication.user = user;
            usr = user.getName();
            password = user.getPassword();
            id = user.getDeviceId();
        }
        if (TextUtils.isEmpty(usr) || TextUtils.isEmpty(password) || TextUtils.isEmpty(id)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewCallback.onLoginCompleted(false);
                }
            }, 2000);
        } else {
            netManager.performLoginCode(usr, password, id, this);
        }
    }

    @Override
    public void onCompleted(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject jsonObject = new JSONObject(response);


                if (jsonObject.has("status") && "1".equals(jsonObject.getString("status"))) {
                    String userAgent = jsonObject.getString("user-agent");
                    if (!jsonObject.isNull("pin")) {
                        DataManager.getInstance().saveData("adultsPassword", jsonObject.getString("pin"));
                    }
                    if (!TextUtils.isEmpty(userAgent)) {
                        user.setUser_agent(userAgent);
                        user.setExpiration_date(jsonObject.getString("expire_date"));
                        user.setDevice(Device.getModel() + " - " + Device.getFW());
                        user.setVersion(Device.getVersion());
                        user.setAdultos(jsonObject.getInt("adultos"));
                        user.setDeviceId(Device.getIdentifier());
                        DataManager.getInstance().saveData("theUser", new Gson().toJson(user));
                        DataManager.getInstance().saveData("device_num", jsonObject.getString("device_num"));
                        viewCallback.onLoginCompleted(true);
                        return;
                    }
                } else {
                    String errorFound = jsonObject.getString("error_found");
                    viewCallback.onLoginError(errorFound);
                    return;
                }

            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        //DataManager.getInstance().saveData("theUser","");
        viewCallback.onLoginCompleted(false);
    }

    @Override
    public void onError() {
        viewCallback.onLoginCompleted(false);
    }



}