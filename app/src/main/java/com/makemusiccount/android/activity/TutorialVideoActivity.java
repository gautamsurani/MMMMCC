package com.makemusiccount.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.makemusiccount.android.R;

public class TutorialVideoActivity extends AppCompatActivity {

    VideoView videoView;

    ProgressDialog progressDialog;

    TextView tvBack;

    Activity context;

    String videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_video);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoURL = bundle.getString("videoURL", "");
        }

        context = this;

        videoView = findViewById(R.id.videoView);
        tvBack = findViewById(R.id.tvBack);

        progressDialog = ProgressDialog.show(TutorialVideoActivity.this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);

        PlayVideo();

        tvBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("screenType", "tutorial_question");
        startActivity(intent);
        finish();
    }

    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(context);

            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videoURL);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(mp -> {
                dismissProgressDialog();
                videoView.start();
            });
        } catch (Exception e) {
            dismissProgressDialog();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }
    }

    @Override
    public void onDestroy() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}