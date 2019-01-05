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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.AllSong;
import com.makemusiccount.android.model.RecordPianoKeyList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class RecordPianoActivity extends AppCompatActivity
        implements OnPianoListener, OnLoadAudioListener, OnPianoAutoPlayListener {

    private PianoView pianoView;

    Activity context;

    ProgressDialog progressDialog;

    Button btnSubmit, btnCancel;

    Global global;

    String UserId = "", resMessage = "", resCode = "";

    List<AllSong> allSongs = new ArrayList<>();

    List<String> songName = new ArrayList<>();

    Spinner spinner;

    String keys_list, selectedSongId = "", selectedSongName = "", selectedSongPath = "";

    Button btnPlayAlong;

    AudioManager audio;

    Chronometer chronometer;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record_piano);

        context = this;

        UserId = Util.getUserId(context);

        global = new Global(context);

        initToolbar();

        initComp();

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        chronometer.setText("00 min : 00 sec");

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

        btnSubmit.setOnClickListener(view -> {
            if (btnSubmit.getText().toString().equals("Start")) {
                downloadSong();
            } else {
                stopRecording();
            }
        });

        btnCancel.setOnClickListener(view -> cancelRecording());

        btnPlayAlong.setOnClickListener(view -> {
            Intent intent = new Intent(context, PianoActivity.class);
            intent.putExtra("screen", "record");
            intent.putExtra("song_name", selectedSongName);
            intent.putExtra("song_id", selectedSongId);
            startActivity(intent);
            context.overridePendingTransition(0, 0);
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSongId = allSongs.get(i).getID();
                selectedSongName = allSongs.get(i).getName();
                selectedSongPath = allSongs.get(i).getSong_file();
                cancelRecording();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setLoadAudioListener(this);

        if (global.isNetworkAvailable()) {
            new GetSongList().execute();
        } else {
            global.retryInternet("song_list");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("song_list")) {
                    new GetSongList().execute();
                } else if (extraValue.equalsIgnoreCase("create_song")) {
                    new CreateSong().execute();
                }
            }
        }
    }

    private void downloadSong() {
        String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(downloadAudioPath + File.separator + "voices");
        if (!audioVoice.exists()) {
            boolean is = audioVoice.mkdir();
            if (is) {
                Log.d("Create dir : --- ", "ok");
            }
        }
        String filename = Util.extractFilename(selectedSongPath);
        downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
        File file = new File(downloadAudioPath);
        if (file.exists()) {
            startSong(downloadAudioPath);
        } else {
            if (global.isNetworkAvailable()) {
                new DownloadFile().execute(selectedSongPath, downloadAudioPath);
            }
        }
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
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Song downloading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            startSong(s);
        }
    }

    MediaPlayer mediaPlayer;

    private void startSong(String downloadAudioPath) {
        Uri myUri = Uri.parse(downloadAudioPath);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            btnCancel.setVisibility(View.VISIBLE);
            startRecoding();
            mediaPlayer.start();
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
        });
        try {
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopSong() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void startRecoding() {
        btnSubmit.setText("Submit");
        lastTime = sdf.format(Calendar.getInstance().getTime());
        chronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        chronometer.start();
    }

    @SuppressLint("SetTextI18n")
    private void cancelRecording() {
        recordPianoKeyLists.clear();
        chronometer.stop();
        btnCancel.setVisibility(View.GONE);
        chronometer.setText("00 min : 00 sec");
        btnSubmit.setText("Start");
        stopSong();
    }

    @SuppressLint("SetTextI18n")
    private void stopRecording() {
        chronometer.stop();
        stopSong();
        btnCancel.setVisibility(View.GONE);
        chronometer.setText("00 min : 00 sec");
        btnSubmit.setText("Start");
        if (recordPianoKeyLists.size() != 0) {
            keys_list = getRecordedKey();
            if (global.isNetworkAvailable()) {
                new CreateSong().execute();
            } else {
                global.retryInternet("create_song");
            }
        } else {
            Toast.makeText(context, "Record the song on the piano", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRecordedKey() {
        try {
            JSONArray keys = new JSONArray();
            for (int i = 0; i < recordPianoKeyLists.size(); i++) {
                JSONObject key = new JSONObject();
                key.put("name", recordPianoKeyLists.get(i).getName());
                key.put("position", recordPianoKeyLists.get(i).getPosition());
                key.put("waiting_time", recordPianoKeyLists.get(i).getWaiting_time());
                keys.put(key);
            }
            JSONObject recordedKeys = new JSONObject();
            recordedKeys.put("recorded_keys", keys);
            return recordedKeys.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    }

    @Override
    public void loadPianoAudioProgress(int progress) {

    }

    @Override
    public void onPianoAutoPlayStart() {

    }

    @Override
    public void onPianoAutoPlayEnd() {

    }

    @Override
    public void onPianoInitFinish() {

    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup) {
        play(Util.getPianoPosition(type, group, positionOfGroup));
    }

    @SuppressLint("StaticFieldLeak")
    private class CreateSong extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_Song_Record;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("userID", UserId);
                    restClient.AddParam("songDetails", keys_list);
                    restClient.AddParam("songsID", selectedSongId);
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String api = restClient.getResponse();
                Log.e("API", api);
                if (api != null && api.length() != 0) {
                    jsonObjectList = new JSONObject(api);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            recordPianoKeyLists.clear();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_SONG_ALL + UserId;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String API = restClient.getResponse();
                Log.e("API", API);

                if (API != null && API.length() != 0) {
                    jsonObjectList = new JSONObject(API);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AllSong allSong = new AllSong();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        allSong.setID(jsonObjectList.getString("ID"));
                                        allSong.setName(jsonObjectList.getString("name"));
                                        allSong.setSong_file(jsonObjectList.getString("song_file"));
                                        allSongs.add(allSong);
                                        songName.add(jsonObjectList.getString("name"));
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
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, songName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    List<RecordPianoKeyList> recordPianoKeyLists = new ArrayList<>();

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss.SSS");

    String lastTime;

    private void play(int position) {
        RecordPianoKeyList recordPianoKeyList = new RecordPianoKeyList();
        recordPianoKeyList.setPosition(String.valueOf(position));
        recordPianoKeyList.setName("");
        try {
            Date Date1 = sdf.parse(lastTime);
            Date Date2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
            long millie = Date2.getTime() - Date1.getTime();
            recordPianoKeyList.setWaiting_time(String.valueOf(millie));
            lastTime = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        recordPianoKeyLists.add(recordPianoKeyList);
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        pianoView = findViewById(R.id.pianoView);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        spinner = findViewById(R.id.spinner);
        chronometer = findViewById(R.id.chronometer);
        btnPlayAlong = findViewById(R.id.btnPlayAlong);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        stopSong();
        super.onPause();
    }

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
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
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