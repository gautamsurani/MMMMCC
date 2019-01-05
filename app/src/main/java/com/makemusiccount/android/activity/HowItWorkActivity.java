package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class HowItWorkActivity extends AppCompatActivity {

    Activity context;

    TextView tvPage, tvHeading1, tvHeading2, tvHeading3, tvHeading4, tvDesc1, tvDesc2, tvDesc3, tvDesc4;

    ImageView iv1, iv2, iv3, iv4;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "";

    Global global;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_how_it_work);

        context = this;

        global = new Global(context);

        initComp();

        initToolbar();

        tvPage.setText("How it work");

        if (global.isNetworkAvailable()) {
            new HowItWork().execute();
        } else {
            global.retryInternet("how_it_work");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("how_it_work")) {
                    new HowItWork().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class HowItWork extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_How_It_Work;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);

            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("how_it_works");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        switch (i) {
                                            case 0:
                                                runOnUiThread(() -> {
                                                    try {
                                                        Glide.with(context)
                                                                .load(jsonObjectList.getString("image"))
                                                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .into(iv1);
                                                        tvHeading1.setText(jsonObjectList.getString("title"));
                                                        tvDesc1.setText(jsonObjectList.getString("text"));
                                                        iv1.setColorFilter(Color.parseColor(jsonObjectList.getString("color")));
                                                        tvHeading1.setTextColor(Color.parseColor(jsonObjectList.getString("color")));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            case 1:
                                                runOnUiThread(() -> {
                                                    try {
                                                        Glide.with(context)
                                                                .load(jsonObjectList.getString("image"))
                                                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .into(iv2);
                                                        tvHeading2.setText(jsonObjectList.getString("title"));
                                                        tvDesc2.setText(jsonObjectList.getString("text"));
                                                        iv2.setColorFilter(Color.parseColor(jsonObjectList.getString("color")));
                                                        tvHeading2.setTextColor(Color.parseColor(jsonObjectList.getString("color")));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            case 2:
                                                runOnUiThread(() -> {
                                                    try {
                                                        Glide.with(context)
                                                                .load(jsonObjectList.getString("image"))
                                                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .into(iv3);
                                                        tvHeading3.setText(jsonObjectList.getString("title"));
                                                        tvDesc3.setText(jsonObjectList.getString("text"));
                                                        iv3.setColorFilter(Color.parseColor(jsonObjectList.getString("color")));
                                                        tvHeading3.setTextColor(Color.parseColor(jsonObjectList.getString("color")));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            case 3:
                                                runOnUiThread(() -> {
                                                    try {
                                                        Glide.with(context)
                                                                .load(jsonObjectList.getString("image"))
                                                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .into(iv4);
                                                        tvHeading4.setText(jsonObjectList.getString("title"));
                                                        tvDesc4.setText(jsonObjectList.getString("text"));
                                                        iv4.setColorFilter(Color.parseColor(jsonObjectList.getString("color")));
                                                        tvHeading4.setTextColor(Color.parseColor(jsonObjectList.getString("color")));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                                break;
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
            }
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        tvPage = findViewById(R.id.tvPage);

        tvHeading1 = findViewById(R.id.tvHeading1);
        tvHeading2 = findViewById(R.id.tvHeading2);
        tvHeading3 = findViewById(R.id.tvHeading3);
        tvHeading4 = findViewById(R.id.tvHeading4);

        tvDesc1 = findViewById(R.id.tvDesc1);
        tvDesc2 = findViewById(R.id.tvDesc2);
        tvDesc3 = findViewById(R.id.tvDesc3);
        tvDesc4 = findViewById(R.id.tvDesc4);

        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        TextView tvDate = findViewById(R.id.tvDate);
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        Util.setDate(tvDate);
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
