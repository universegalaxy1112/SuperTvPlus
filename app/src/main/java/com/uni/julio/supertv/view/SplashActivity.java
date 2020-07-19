package com.uni.julio.supertv.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.uni.julio.supertv.LiveTvApplication;
import com.uni.julio.supertv.R;
import com.uni.julio.supertv.listeners.DialogListener;
import com.uni.julio.supertv.utils.Connectivity;
import com.uni.julio.supertv.utils.DataManager;
import com.uni.julio.supertv.utils.Device;
import com.uni.julio.supertv.utils.Dialogs;
import com.uni.julio.supertv.utils.networing.HttpRequest;
import com.uni.julio.supertv.viewmodel.Lifecycle;
import com.uni.julio.supertv.viewmodel.SplashViewModel;
import com.uni.julio.supertv.viewmodel.SplashViewModelContract;
import java.io.File;

public class SplashActivity extends BaseActivity implements SplashViewModelContract.View {
    private boolean isInit = false;
    private SplashViewModel splashViewModel;
    boolean denyAll=false;
    public ProgressDialog downloadProgress;
    private String updateLocation;
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
                showErrorMessage("Estimado "+ LiveTvApplication.getUser().getName()+", su cuenta a sido desactivada, porfavor comunicate con tu vendedor.", errorFound);
                break;
        }
    }

    public void showErrorMessage(String message, final String error_found) {

        if(Connectivity.isConnected()) {
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
        }
        else {
            noInternetConnection(new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closeApp();
                }
            });
        }

    }

    public void closeApp(){
        finishAffinity();
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Device.setHDMIStatus();
        HttpRequest.getInstance().checkCertificate();
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
        if(!isInit){
            if (getPermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                requestStoragePermission();
            } else{
                splashViewModel.checkForUpdate();
                isInit = true;
            }
        }
    }

    @Override
    public void onCheckForUpdateCompleted(boolean hasNewVersion, String location) {
        this.updateLocation = location;
        if (hasNewVersion) {
            try{
                Dialogs.showTwoButtonsDialog( getActivity(),R.string.download , R.string.cancel, R.string.new_version_available,  new DialogListener() {
                    public void onAccept() {
                       if (Connectivity.isConnected()) {
                            downloadUpdate(updateLocation);
                        } else {
                            goToNoConnectionError();
                        }
                    }
                    public void onCancel() {
                        splashViewModel.login();
                    }

                    @Override
                    public void onDismiss() {
                        splashViewModel.login();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            splashViewModel.login();
        }
    }
    private void downloadUpdate(String location) {
        if (Connectivity.isConnected()) {
            downloadProgress = new ProgressDialog(getActivity(),ProgressDialog.THEME_HOLO_LIGHT);
            downloadProgress.setProgressStyle(1);
            downloadProgress.setMessage("Downloading");
            downloadProgress.setIndeterminate(false);
            downloadProgress.setCancelable(false);
            downloadProgress.show();
            splashViewModel.downloadUpdate(location, this.downloadProgress);
            return;
        }
        goToNoConnectionError();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);

        if (requestCode != 4168) {
            return;
        }
        if (getPermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            splashViewModel.checkForUpdate();
            isInit = true;
        } else {
            finishActivity();
        }
    }
    public int getPermissionStatus(String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(getActivity(), androidPermissionName) == 0) {
            return 0;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), androidPermissionName) || !DataManager.getInstance().getBoolean("storagePermissionRequested", false)) {
            return 1;
        }
        return 2;
    }

    public void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < 23 || getPermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return;
        }
        this.denyAll = false;
        int accept = R.string.accept;
        int message = R.string.permission_storage;
        if (getPermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE") == 2) {
            this.denyAll = true;
            accept = R.string.config;
            message = R.string.permission_storage_config;
        }
        Dialogs.showTwoButtonsDialog( this, accept,  R.string.cancel, message,  new DialogListener() {
            @TargetApi(23)
            public void onAccept() {
                if (!denyAll) {
                    DataManager.getInstance().saveData("storagePermissionRequested", Boolean.TRUE);
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                    return;
                }
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(intent, 4168);
            }

            public void onCancel() {
                finishActivity();
            }

            @Override
            public void onDismiss() {
                finishActivity();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 1) {
            finishActivity();
        }
        if (getPermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            splashViewModel.checkForUpdate();
            isInit = true;
        } else {
            finishActivity();
        }
    }
    public void goToNoConnectionError() {
        noInternetConnection(new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchActivity(LoginActivity.class);
                getActivity().finish();
            }
        });
    }
    @Override
    public void onDownloadUpdateCompleted(String location) {
        this.downloadProgress.dismiss();
        try
        {
            File file = new File(location);
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri downloaded_apk = getFileUri(this, file);
                intent = new Intent(Intent.ACTION_VIEW).setDataAndType(downloaded_apk,
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                intent = new Intent("android.intent.action.INSTALL_PACKAGE");
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.putExtra("android.intent.extra.RETURN_RESULT", false);
            intent.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", getPackageName());
            finishActivity();
            startActivity(intent);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context,
                "com.uni.julio.supertv.fileprovider"
                , file);
    }
    @Override
    public void onDownloadUpdateError(int error) {
        downloadProgress.dismiss();

        if (error == 1) {
            Dialogs.showOneButtonDialog(getActivity(), R.string.verify_unknown_sources, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    splashViewModel.login();
                }
            });
            return;
        }
        Dialogs.showOneButtonDialog(getActivity(), R.string.new_version_generic_error_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                splashViewModel.login();
            }
        });
    }

    @Override
    public void onLoginCompleted(boolean success) {
        if(success){
            launchActivity(MainActivity.class);
            finishActivity();
        }
        else{
            if(Connectivity.isConnected()){
                launchActivity(LoginActivity.class);
                finishActivity();
            }
            else{

                noInternetConnection(new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivity();
                    }
                });
            }
         }
    }

}
