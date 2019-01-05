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
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

public class VideoViewActivity extends AppCompatActivity {

    VideoView videoView;

    ProgressDialog progressDialog;

    TextView tvSkip;

    Activity context;

    String videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_view);

        context = this;

        videoURL = AppConstant.Help_Video;

        videoView = findViewById(R.id.videoView);

        tvSkip = findViewById(R.id.tvSkip);

        progressDialog = new ProgressDialog(context);

        if (Util.getIntroVideo(context) != null) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        } else {
            Util.setIntroVideo(context, "isShow");
            try {
                progressDialog = ProgressDialog.show(VideoViewActivity.this, "", "Buffering video...", true);
                progressDialog.setCancelable(true);
                PlayVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvSkip.setOnClickListener(view -> {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            });
        }
    }

    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(VideoViewActivity.this);

            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videoURL);

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