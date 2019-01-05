package com.makemusiccount.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;

public class FirstActivity extends AppCompatActivity {

    VideoView videoview;

    TextView tvSignIn, tvSignUp;

    Activity context;

    private void setBackground() {
        videoview = findViewById(R.id.videoview);
        final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnCompletionListener(mp -> {
            mp.reset();
            videoview.setVideoURI(uri);
            videoview.start();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setBackground();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        context = this;

        if (Util.getUserId(context) != null) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }

        initComp();

        tvSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("page", "sign_in");
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("page", "sign_up");
            startActivity(intent);
        });
    }

    private void initComp() {
        tvSignIn = findViewById(R.id.tvSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    @Override
    public void onDestroy() {
        if (videoview != null) {
            videoview.stopPlayback();
        }
        super.onDestroy();
    }
}
