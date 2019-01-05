package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ProgressActivity extends AppCompatActivity {
    private CustomGauge gaugeSong, gaugeQue;
    TextView textSong, tvSongHeading, tvHeadingQue, textQue, tvSongNote, tvQueNote;
    ProgressDialog progressDialog;
    String resMessage, resCode;
    Activity context;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_progress);

        context = this;

        global = new Global(context);

        initToolbar();

        progressDialog = new ProgressDialog(context);
        gaugeSong = findViewById(R.id.gaugeSong);
        textSong = findViewById(R.id.textSong);
        gaugeQue = findViewById(R.id.gaugeQue);
        textQue = findViewById(R.id.textQue);

        tvSongHeading = findViewById(R.id.tvSongHeading);
        tvHeadingQue = findViewById(R.id.tvHeadingQue);
        tvSongNote = findViewById(R.id.tvSongNote);
        tvQueNote = findViewById(R.id.tvQueNote);

        if (global.isNetworkAvailable()) {
            new GetProgress().execute();
        } else {
            global.retryInternet("progress");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("progress")) {
                    new GetProgress().execute();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ImageView ivNotification = findViewById(R.id.ivNotification);
        TextView tvPage = findViewById(R.id.tvPage);
        tvPage.setText("Progress");
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        TextView tvDate = findViewById(R.id.tvDate);
        Util.setDate(tvDate);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetProgress extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String progress = "0", label = "", note = "", progress1 = "0", label1 = "", note1 = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_GET_PROGRESS + Util.getUserId(context);
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("progress");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        if (i == 0) {
                                            progress = jsonObjectList.getString("per");
                                            label = jsonObjectList.getString("label");
                                            note = jsonObjectList.getString("note");
                                        } else if (i == 1) {
                                            progress1 = jsonObjectList.getString("per");
                                            label1 = jsonObjectList.getString("label");
                                            note1 = jsonObjectList.getString("note");
                                        }
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
            if (!resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            } else {
                tvSongHeading.setText(label);
                tvSongNote.setText(note);
                new Thread() {
                    @SuppressLint("SetTextI18n")
                    public void run() {
                        for (int i = 0; i < Integer.parseInt(progress); i++) {
                            try {
                                int finalI = i;
                                runOnUiThread(() -> {
                                    gaugeSong.setValue(finalI);
                                    textSong.setText(finalI + " %");
                                });
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                tvHeadingQue.setText(label1);
                tvQueNote.setText(note1);
                new Thread() {
                    @SuppressLint("SetTextI18n")
                    public void run() {
                        for (int i = 0; i < Integer.parseInt(progress1); i++) {
                            try {
                                int finalI = i;
                                runOnUiThread(() -> {
                                    gaugeQue.setValue(finalI);
                                    textQue.setText(finalI + " %");
                                });
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}