package com.makemusiccount.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;

public class SplashActivity extends AppCompatActivity {

    private int SPLASH_TIME_OUT = 2000;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        context = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.getUserId(context) == null) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, "Guest");
                    startActivity(new Intent(context, MainActivity.class));
                } else {
                    startActivity(new Intent(context, MainActivity.class));
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
