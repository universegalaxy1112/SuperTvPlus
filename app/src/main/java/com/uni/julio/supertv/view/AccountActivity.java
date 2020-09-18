package com.uni.julio.supertv.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.uni.julio.supertv.LiveTvApplication;
import com.uni.julio.supertv.R;
import com.uni.julio.supertv.databinding.ActivityAccountBinding;
import com.uni.julio.supertv.listeners.DialogListener;
import com.uni.julio.supertv.utils.Connectivity;
import com.uni.julio.supertv.utils.DataManager;
import com.uni.julio.supertv.utils.Device;
import com.uni.julio.supertv.utils.Dialogs;
import com.uni.julio.supertv.viewmodel.AccountDetailsViewModel;
import com.uni.julio.supertv.viewmodel.AccountDetailsViewModelContract;
import com.uni.julio.supertv.viewmodel.Lifecycle;

import java.io.File;
import java.util.Objects;

public class AccountActivity extends BaseActivity implements AccountDetailsViewModelContract.View {
    private AccountDetailsViewModel accountDetailsViewModel;
    private ActivityAccountBinding activityAccountBinding;
    private String updateLocation = null;
    public ProgressDialog downloadProgress;

    @Override
    protected Lifecycle.ViewModel getViewModel() {
        return accountDetailsViewModel;
    }
    @Override
    protected Lifecycle.View getLifecycleView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        Toolbar toolbar = activityAccountBinding.toolbar;
        toolbar.setTitle("Mi Cuenta");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if(Device.treatAsBox){
             (activityAccountBinding.Appbarlayout).setVisibility(View.GONE);
        }

        accountDetailsViewModel = new AccountDetailsViewModel(getActivity(), activityAccountBinding);
        activityAccountBinding.setAccountDetailsVM(accountDetailsViewModel);
        accountDetailsViewModel.showAccountDetails();
        TextView textView = activityAccountBinding.testspeed;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(SpeedTestActivity.class);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCloseSessionSelected() {
        DataManager.getInstance().saveData("theUser","");
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishActivity();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           finishActivity();
            return true;
        }
        return false;
    }
    @Override
    public void onCloseSessionNoInternet() {
        Dialogs.showOneButtonDialog(getActivity(), R.string.no_connection_title,  R.string.close_session_no_internet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    @Override
    public void onError() {
        finishActivity();
    }

    @Override
    public void onCheckForUpdateCompleted(boolean z, final String str) {
        this.updateLocation = str;
        if (z) {
            Resources res = getResources();
            Dialogs.showTwoButtonsDialog((Activity) getActivity(), (int) R.string.download, (int) R.string.cancel, getResources().getString(R.string.new_version_available), (DialogListener) new DialogListener() {
                public void onAccept() {
                    if (Connectivity.isConnected()) {
                        downloadProgress = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
                        downloadProgress.setProgressStyle(1);
                        downloadProgress.setMessage("Downloading");
                        downloadProgress.setIndeterminate(false);
                        downloadProgress.setCancelable(false);
                        downloadProgress.show();
                        accountDetailsViewModel.downloadUpdate(str, downloadProgress);
                    } else {
                        AccountActivity.this.goToNoConnectionError();
                    }
                }

                public void onCancel() {

                }

                @Override
                public void onDismiss() {

                }
            });

        } else {
            //prompt no update
        }
    }
    public void goToNoConnectionError() {
        noInternetConnection(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

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
                Uri downloaded_apk = getFileUri(getActivity(), file);
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
            intent.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", getActivity().getPackageName());
            startActivity(intent);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context,
                "com.livetv.android.fileprovider"
                , file);
    }

    @Override
    public void onDownloadUpdateError(int error) {
        this.downloadProgress.dismiss();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AccountActivity.this.getActivity().finish();
            }
        };
        if (error == 1) {
            Dialogs.showOneButtonDialog((Activity) getActivity(), (int) R.string.verify_unknown_sources, listener);
            return;
        }
        Dialogs.showOneButtonDialog((Activity) getActivity(), (int) R.string.new_version_generic_error_message, listener);
    }
}
