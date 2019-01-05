package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.MyRoundImageView;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class MyAccountActivity extends AppCompatActivity {

    LinearLayout llProfile, llPassword, llMembership;

    Activity context;

    MyRoundImageView ivPImg, ivPoImg, ivSLImg;

    CircleImageView ivImage;

    TextView tvName, tvType, tvPCount, tvProgress, tvPoCount, tvPoint, tvPSLCount, tvSL, tvPage;

    ProgressDialog progressDialog;

    String resMessage, resCode;

    String account_type, userID, name, phone, email, image, progress_image, progress, progress_sup,
            point_image, point, point_sup,
            level_image, level, level_sup, total_comp_songs;

    LinearLayout llPoints, llBadges, llProgress;

    ImageView ivDashboard;

    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_account);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        tvPage.setText("My Profile");

        llMembership.setOnClickListener(view -> {
            startActivity(new Intent(context, MembershipActivity.class));
            overridePendingTransition(0, 0);
        });

        llProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChangeProfileActivity.class);
            intent.putExtra("page", "profile");
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        llPassword.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChangeProfileActivity.class);
            intent.putExtra("page", "password");
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        llPoints.setOnClickListener(view -> {
            Intent intent = new Intent(context, PointHistoryActivity.class);
            startActivity(intent);
        });

        llBadges.setOnClickListener(view -> {
            Intent intent = new Intent(context, BadgesActivity.class);
            startActivity(intent);
        });

        llProgress.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProgressActivity.class);
            startActivity(intent);
        });

        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        if (global.isNetworkAvailable()) {
            new GetUserDATA().execute();
        } else {
            global.retryInternet("user_data");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("user_data")) {
                    new GetUserDATA().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserDATA extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_USER_DATA + Util.getUserId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("profile_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                    userID = jsonObjectList.getString("userID");
                                    account_type = jsonObjectList.getString("account_type");
                                    name = jsonObjectList.getString("name");
                                    phone = jsonObjectList.getString("phone");
                                    email = jsonObjectList.getString("email");
                                    image = jsonObjectList.getString("image");
                                    progress_image = jsonObjectList.getString("progress_image");
                                    progress = jsonObjectList.getString("progress");
                                    progress_sup = jsonObjectList.getString("progress_sup");
                                    point_image = jsonObjectList.getString("point_image");
                                    point = jsonObjectList.getString("point");
                                    point_sup = jsonObjectList.getString("point_sup");
                                    level_image = jsonObjectList.getString("level_image");
                                    level = jsonObjectList.getString("level");
                                    level_sup = jsonObjectList.getString("level_sup");
                                    total_comp_songs = jsonObjectList.getString("total_comp_songs");
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
                tvName.setText(name);
                tvType.setText(account_type);
                if (!progress_sup.isEmpty()) {
                    tvPCount.setText(progress_sup);
                } else {
                    tvPCount.setVisibility(View.GONE);
                }
                tvProgress.setText(progress);
                tvPoCount.setText(point_sup);
                tvPoint.setText(point);
                if (!level_sup.isEmpty()) {
                    tvPSLCount.setText(level_sup);
                } else {
                    tvPSLCount.setVisibility(View.GONE);
                }
                tvSL.setText(level);

                Glide.with(context)
                        .load(progress_image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.app_logo)
                        .into(ivPImg);

                Glide.with(context)
                        .load(point_image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.app_logo)
                        .into(ivPoImg);

                Glide.with(context)
                        .load(level_image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.app_logo)
                        .into(ivSLImg);

                Glide.with(context)
                        .load(image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.app_logo)
                        .into(ivImage);

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        TextView tvDate = findViewById(R.id.tvDate);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Util.setDate(tvDate);
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        ivDashboard = findViewById(R.id.ivDashboard);
        llBadges = findViewById(R.id.llBadges);
        llProgress = findViewById(R.id.llProgress);
        tvPage = findViewById(R.id.tvPage);
        llProfile = findViewById(R.id.llProfile);
        llPassword = findViewById(R.id.llPassword);
        llMembership = findViewById(R.id.llMembership);
        llPoints = findViewById(R.id.llPoints);
        ivPImg = findViewById(R.id.ivPImg);
        ivPoImg = findViewById(R.id.ivPoImg);
        ivSLImg = findViewById(R.id.ivSLImg);
        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvPCount = findViewById(R.id.tvPCount);
        tvProgress = findViewById(R.id.tvProgress);
        tvPoCount = findViewById(R.id.tvPoCount);
        tvPoint = findViewById(R.id.tvPoint);
        tvPSLCount = findViewById(R.id.tvPSLCount);
        tvSL = findViewById(R.id.tvSL);
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
