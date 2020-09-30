package com.uni.julio.superplus.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.uni.julio.superplus.LiveTvApplication;
import com.uni.julio.superplus.R;
import com.uni.julio.superplus.utils.Connectivity;
import com.uni.julio.superplus.utils.Device;
import com.uni.julio.superplus.utils.Dialogs;
import com.uni.julio.superplus.viewmodel.Lifecycle;
import com.uni.julio.superplus.viewmodel.SplashViewModel;
import com.uni.julio.superplus.viewmodel.SplashViewModelContract;


public class SplashActivity extends BaseActivity implements SplashViewModelContract.View {
    private boolean isInit = false;
    private SplashViewModel splashViewModel;
    boolean denyAll = false;

    protected Lifecycle.ViewModel getViewModel() {
        return splashViewModel;
    }

    @Override
    protected Lifecycle.View getLifecycleView() {
        return this;
    }

    @Override
    public void onLoginError(String errorFound) {
        switch (errorFound) {
            case "103":
            case "104":
                showErrorMessage(getString(R.string.login_error_change_device), errorFound);
                break;
            case "105":
                showErrorMessage(getString(R.string.login_error_usr_pss_incorrect), errorFound);
                break;
            case "106":
                showErrorMessage(getString(R.string.login_error_device_not_registered), errorFound);
                break;
            case "107":
                showErrorMessage(getString(R.string.login_error_expired), errorFound);
                break;
            case "108": {
                showErrorMessage(getString(R.string.login_error_change_account).replace("{ID}", Device.getIdentifier()), errorFound);
            }
            break;
            case "109": {
                showErrorMessage(getString(R.string.login_error_demo), errorFound);
            }
            break;
            case "110": {
                showErrorMessage(getString(R.string.ip_limitation), errorFound);
            }
            break;
            default:
                showErrorMessage("Estimado " + LiveTvApplication.getUser().getName() + ", su cuenta a sido desactivada, porfavor comunicate con tu vendedor.", errorFound);
                break;
        }
    }

    public void showErrorMessage(String message, final String error_found) {

        if (Connectivity.isConnected()) {
            Dialogs.showOneButtonDialog(this, getString(R.string.attention), message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (error_found) {
                        case "103":
                        case "104":
                        case "105":
                        case "106":
                        case "107":
                        case "108":
                        case "109":
                            launchActivity(LoginActivity.class);
                            finishActivity();
                            break;
                        default:
                            closeApp();
                    }
                    //onLoginCompleted(false);
                }
            });
        } else {
            noInternetConnection(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closeApp();
                }
            });
        }

    }

    public void closeApp() {
        finishAffinity();
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Device.setHDMIStatus();
       // HttpRequest.getInstance().check();
        splashViewModel = new SplashViewModel(this);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this.getBaseContext(), "Can not go back!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInit) {
            splashViewModel.login();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);

    }


    @Override
    public void onLoginCompleted(boolean success) {
        if (success) {
            launchActivity(MainActivity.class);
            finishActivity();
        } else {
            if (Connectivity.isConnected()) {
                launchActivity(LoginActivity.class);
                finishActivity();
            } else {

                noInternetConnection(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivity();
                    }
                });
            }
        }
    }

}
