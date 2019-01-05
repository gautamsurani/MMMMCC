package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import org.json.JSONObject;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class MeetTheFounderActivity extends AppCompatActivity {

    TextView tvPage;

    ProgressDialog progressDialog;

    Activity context;

    String resMessage = "", resCode = "", image = "", say = "", name = "", poss = "",
            sec_heading = "", sec_text = "", web = "";

    ImageView ivImage;

    TextView tvSay, tvName, tvPoss, tvHeading, tvDesc;

    Button btnHowItWork, btnSite;

    Global global;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_meet_the_founder);

        context = this;

        global = new Global(context);

        initComp();

        initToolbar();

        tvPage.setText("Meet the founder");

        if (global.isNetworkAvailable()) {
            new MeetTheFounder().execute();
        } else {
            global.retryInternet("meet_the_founder");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("meet_the_founder")) {
                    new MeetTheFounder().execute();
                }
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class MeetTheFounder extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_Meet_The_Founder;
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
                        image = jsonObjectList.getString("image");
                        say = jsonObjectList.getString("say");
                        name = jsonObjectList.getString("name");
                        poss = jsonObjectList.getString("poss");
                        sec_heading = jsonObjectList.getString("sec_heading");
                        sec_text = jsonObjectList.getString("sec_text");
                        web = jsonObjectList.getString("web");
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
                Glide.with(context)
                        .load(image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivImage);
                tvSay.setText(say);
                tvName.setText(name);
                tvPoss.setText(poss);
                tvHeading.setText(sec_heading);
                tvDesc.setText(sec_text.replace("\\n", "\n"));
                btnSite.setOnClickListener(view -> {
                    if (!web.isEmpty()) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(web));
                        startActivity(i);
                    }
                });
                btnHowItWork.setOnClickListener(view -> {
                    Intent intent = new Intent(context, HowItWorkActivity.class);
                    startActivity(intent);
                });
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        tvPage = findViewById(R.id.tvPage);
        ivImage = findViewById(R.id.ivImage);
        tvSay = findViewById(R.id.tvSay);
        tvName = findViewById(R.id.tvName);
        tvPoss = findViewById(R.id.tvPoss);
        tvHeading = findViewById(R.id.tvHeading);
        tvDesc = findViewById(R.id.tvDesc);
        btnHowItWork = findViewById(R.id.btnHowItWork);
        btnSite = findViewById(R.id.btnSite);
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
