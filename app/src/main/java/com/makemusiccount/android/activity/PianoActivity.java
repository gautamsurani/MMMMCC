package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.RecordPianoKeyList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.CountDownAnimation;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.PlaySound;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class PianoActivity extends AppCompatActivity implements OnPianoListener,
        OnLoadAudioListener, OnPianoAutoPlayListener {

    Activity context;
    ImageView ivPlay, ivPlay1;
    ProgressDialog progressDialog;
    String resMessage = "", resCode = "", song_name = "", song_file = "", song_id = "",
            screen = "", image = "", artist = "";
    List<RecordPianoKeyList> recordPianoKeyLists = new ArrayList<>();
    TextView tvName, tvArtist;
    ProgressBar pbCounter;
    MediaPlayer mediaPlayer, mediaPlayer1;
    LinearLayout rlCountdown;
    Chronometer chronometer;
    PianoView pianoView;
    boolean autoPlay = false, exit = false;
    List<AutoPlayEntity> autoPlayEntities = new ArrayList<>();
    AudioManager audio;
    TextView tvCounter;
    CountDownAnimation countDownAnimation;
    String song_file_path = "";
    ImageView ivImage;
    LinearLayout tvNext;
    Global global;

    @Override
    protected void onResume() {
        super.onResume();
        pianoView.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piano);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        chronometer.setText("00 min : 00 sec");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            song_name = bundle.getString("song_name", "");
            song_id = bundle.getString("song_id", "");
            screen = bundle.getString("screen", "");
            tvName.setText(song_name);
        }

        if (screen.equals("record")) {
            tvNext.setVisibility(View.GONE);
        } else {
            tvNext.setVisibility(View.VISIBLE);
        }

        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setSoundPollMaxStream(100);
        pianoView.setLoadAudioListener(this);

        tvNext.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("screenType", "test_question");
            startActivity(intent);
            context.finish();
            context.overridePendingTransition(0, 0);
        });

        countDownAnimation = new CountDownAnimation(tvCounter, 5);
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f,
                1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);
        countDownAnimation.setCountDownListener(animation -> {
            rlCountdown.setVisibility(View.GONE);
            startMusic(song_file_path);
        });

        if (global.isNetworkAvailable()) {
            new AutoPlayKey().execute();
        } else {
            global.retryInternet("auto_play_key");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("auto_play_key")) {
                    new AutoPlayKey().execute();
                }
            }
        }
    }

    @Override
    public void loadPianoAudioStart() {

    }

    @Override
    public void loadPianoAudioFinish() {

    }

    @Override
    public void loadPianoAudioError(Exception e) {
        Toast.makeText(getApplicationContext(), "loadPianoMusicError", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioProgress(int progress) {

    }

    @Override
    public void onPianoAutoPlayStart() {
        autoPlay = true;
    }

    @Override
    public void onPianoAutoPlayEnd() {
        autoPlay = false;
    }

    @Override
    public void onPianoInitFinish() {

    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup) {

    }

    @SuppressLint("StaticFieldLeak")
    private class AutoPlayKey extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rlCountdown.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_AUTO_PLAY_KEY + Util.getUserId(context)
                    + "&songsID=" + song_id
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String api = restClient.getResponse();
                if (api != null && api.length() != 0) {
                    jsonObjectList = new JSONObject(api);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        song_file = jsonObjectList.getString("song_file");
                        artist = jsonObjectList.getString("artist");
                        image = jsonObjectList.getString("image");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("key_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        RecordPianoKeyList recordPianoKeyList = new RecordPianoKeyList();
                                        recordPianoKeyList.setName(jsonObjectList.getString("value"));
                                        recordPianoKeyList.setPosition(jsonObjectList.getString("key_value"));
                                        recordPianoKeyList.setWaiting_time(jsonObjectList.getString("type"));
                                        recordPianoKeyLists.add(recordPianoKeyList);
                                        autoPlayEntities.add(Util.getAutoPlayObject
                                                (Integer.parseInt(jsonObjectList.getString("key_value")),
                                                        Long.parseLong(jsonObjectList.getString("type"))));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            tvArtist.setText(artist);
            Glide.with(context)
                    .load(image)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivImage);
            setClickOn();
            if (resCode.equalsIgnoreCase("0")) {
                String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(downloadAudioPath + File.separator + "voices");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdir();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
                new DownloadFile().execute(song_file, downloadAudioPath);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPianoDisable() {
        pianoView.setEnabled(false);
    }

    private void setPianoEnable() {
        pianoView.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
        stopMusic();
    }

    @SuppressLint("SetTextI18n")
    private void stopMusic() {
        ivPlay1.setTag("PLAY");
        ivPlay1.setImageResource(R.drawable.play_song);
        ivPlay1.setVisibility(View.VISIBLE);
        ivPlay.setTag("PLAY");
        ivPlay.setImageResource(R.drawable.play);
        stopTime();
        if (autoPlay) {
            pianoView.stopAutoPlay();
        }
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    setPianoEnable();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mediaPlayer1 != null) {
                if (mediaPlayer1.isPlaying()) {
                    mediaPlayer1.stop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setClickOn() {
        ivPlay.setOnClickListener(view -> {
            if (ivPlay.getTag().toString().equals("PLAY")) {
                String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(downloadAudioPath + File.separator + "voices");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdir();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
                File file = new File(downloadAudioPath);
                if (file.exists()) {
                    startMusic(downloadAudioPath);
                }
            } else {
                stopMusic();
            }
        });

        ivPlay1.setOnClickListener(view -> {
            if (ivPlay1.getTag().toString().equals("PLAY")) {
                ivPlay1.setTag("STOP");
                ivPlay1.setImageResource(R.drawable.stop_song);
                String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(downloadAudioPath + File.separator + "voices");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdir();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
                File file = new File(downloadAudioPath);
                if (file.exists()) {
                    startSong(downloadAudioPath);
                }
            } else {
                stopMusic();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL urls = new URL(url[0]);
                URLConnection connection = urls.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(urls.openStream());
                OutputStream output = new FileOutputStream(url[1]);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return url[1];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            song_file_path = s;
            pbCounter.setVisibility(View.GONE);
            tvCounter.setVisibility(View.VISIBLE);
            countDownAnimation.start();
            PlaySound.play(context, R.raw.countdown);
        }
    }

    Runnable runnable;

    Handler handler = new Handler();

    private void startSong(String s) {
        Uri myUri = Uri.parse(s);
        mediaPlayer1 = new MediaPlayer();
        Float maxVolume = Float.parseFloat(Util.getSongSound(context));
        float volume = (float) (maxVolume/100.00);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer1.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
        });
        mediaPlayer1.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.release();
            ivPlay1.setTag("PLAY");
            ivPlay1.setImageResource(R.drawable.play_song);
            setPianoEnable();
        });
        try {
            mediaPlayer1.setDataSource(context, myUri);
            mediaPlayer1.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startMusic(String downloadAudioPath) {
        ivPlay.setTag("STOP");
        ivPlay.setImageResource(R.drawable.pause);
        setPianoDisable();
        Uri myUri = Uri.parse(downloadAudioPath);
        mediaPlayer = new MediaPlayer();
        Float maxVolume = Float.parseFloat(Util.getSongSound(context));
        float volume = (float) (maxVolume/100.00);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            startTime();
            pianoView.autoPlay(autoPlayEntities, "song");
            mediaPlayer.start();
            exit = true;
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.release();
            stopMusic();
        });

        try {
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void startTime() {
        chronometer.setOnChronometerTickListener(chronometer -> {
            CharSequence text = chronometer.getText();
            if (text.length() == 5) {
                String a[] = text.toString().split(":");
                String min = a[0] + " min : ";
                String sec = a[1] + " sec";
                chronometer.setText(min + sec);
            } else if (text.length() == 7) {
                String a[] = text.toString().split(":");
                String min = a[1] + " min : ";
                String sec = a[2] + " sec";
                chronometer.setText(min + sec);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        chronometer.start();
    }

    @SuppressLint("SetTextI18n")
    private void stopTime() {
        chronometer.stop();
        chronometer.setText("00 min : 00 sec");
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        pianoView = findViewById(R.id.pianoView);
        ivPlay = findViewById(R.id.ivPlay);
        tvName = findViewById(R.id.tvName);
        tvArtist = findViewById(R.id.tvArtist);
        tvNext = findViewById(R.id.tvNext);
        ivImage = findViewById(R.id.ivImage);
        ivPlay1 = findViewById(R.id.ivPlay1);
        rlCountdown = findViewById(R.id.rlCountdown);
        tvCounter = findViewById(R.id.tvCounter);
        pbCounter = findViewById(R.id.pbCounter);
        chronometer = findViewById(R.id.chronometer);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        TextView tvDate = findViewById(R.id.tvDate);
        Util.setDate(tvDate);
        TextView tvPage = findViewById(R.id.tvPage);
        tvPage.setText("Play Along");
        TextView lblDashboard = findViewById(R.id.lblDashboard);
        lblDashboard.setText("Song");
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            if (screen.equals("record")) {
                super.onBackPressed();
                finish();
                overridePendingTransition(0, 0);
            } else {
                goToSongList();
            }
        }
    }

    private long exitTime = 0;

    private void goToSongList() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_to_go_song_list, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("screenType", "Songs");
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}