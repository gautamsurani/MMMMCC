package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;

import org.json.JSONObject;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.isValidEmail;

public class ForgetActivity extends AppCompatActivity {

    EditText etEmail;

    TextView tvSubmit, tvBack;

    Activity context;

    Global global;

    String Email = "";

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "";

    VideoView videoview;

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
        setContentView(R.layout.activity_forget);

        context = this;

        global = new Global(context);

        initComp();

        tvSubmit.setOnClickListener(view -> {
            Email = etEmail.getText().toString().trim();
            Email = Email.replaceAll(" ", "");
            if (Email.equals("")) {
                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(Email)) {
                Toast.makeText(context, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new Forget().execute();
                } else {
                    global.retryInternet("forget");
                }
            }
        });

        tvBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("forget")) {
                    new Forget().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Forget extends AsyncTask<String, Void, String> {

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
            String strLogin = AppConstant.API_FORGET + Email
                    + "&app_type=" + "Android";

            String strTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strTrim", strTrim);
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                Log.e("APIString", APIString);

                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
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
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        etEmail = findViewById(R.id.etEmail);
        tvSubmit = findViewById(R.id.tvSubmit);
        tvBack = findViewById(R.id.tvBack);
    }

    @Override
    public void onDestroy() {
        if (videoview != null) {
            videoview.stopPlayback();
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
