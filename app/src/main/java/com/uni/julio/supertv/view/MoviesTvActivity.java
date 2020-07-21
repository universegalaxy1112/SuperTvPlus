package com.uni.julio.supertv.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.uni.julio.supertv.LiveTvApplication;
import com.uni.julio.supertv.R;
import com.uni.julio.supertv.utils.Tracking;

public class MoviesTvActivity extends FragmentActivity {
    MoviesMenuTVFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_tv);
        LiveTvApplication.appContext = this;
        fragment = new MoviesMenuTVFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, fragment).commit();
    }

    @Override

    public void onResume() {
        super.onResume();
        Tracking.getInstance().enableTrack(true);
        Tracking.getInstance().enableSleep(false);
        Tracking.getInstance().setAction(getClass().getSimpleName());
        Tracking.getInstance().track();
        LiveTvApplication.appContext = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        Tracking.getInstance().enableSleep(true);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Tracking.getInstance().getSleep()) {
                    Tracking.getInstance().setAction("Sleeping");
                    Tracking.getInstance().track();
                    Tracking.getInstance().enableSleep(false);
                    Tracking.getInstance().enableTrack(false);
                }
            }
        }, 1000);
        Context appCompatActivity = LiveTvApplication.appContext;
        if (this.equals(appCompatActivity))
            LiveTvApplication.appContext = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        if(keyCode==KeyEvent.KEYCODE_MEDIA_FAST_FORWARD||keyCode==KeyEvent.KEYCODE_FORWARD||keyCode==272||keyCode==274||keyCode==KeyEvent.KEYCODE_BUTTON_R1||keyCode==KeyEvent.KEYCODE_BUTTON_R2||keyCode==KeyEvent.KEYCODE_RIGHT_BRACKET ){

            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE){

            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_MEDIA_REWIND){

            return true;
        }

        return false;
    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Log.e("", "Longpress detected");
            Toast.makeText(getBaseContext(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();

        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
