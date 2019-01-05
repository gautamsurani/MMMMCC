package com.makemusiccount.android.activity;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;

public class SettingsActivity extends AppCompatActivity {

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        context = this;

        SeekBar songSeekBar = findViewById(R.id.songSeekBar);
        SeekBar pianoSeekBar = findViewById(R.id.pianoSeekBar);
        ImageView ivClose = findViewById(R.id.ivClose);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            songSeekBar.setProgress(Integer.parseInt(Util.getSongSound(context)), true);
            pianoSeekBar.setProgress(Integer.parseInt(Util.getPianoSound(context)), true);
        } else {
            songSeekBar.setProgress(Integer.parseInt(Util.getSongSound(context)));
            pianoSeekBar.setProgress(Integer.parseInt(Util.getPianoSound(context)));
        }

        ivClose.setOnClickListener(view -> onBackPressed());

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                AppPreference.setPreference(context, AppPersistence.keys.SongSound, String.valueOf(progressChangedValue));
            }
        });

        pianoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppPreference.setPreference(context, AppPersistence.keys.PianoSound, String.valueOf(progressChangedValue));
            }
        });

    }
}
