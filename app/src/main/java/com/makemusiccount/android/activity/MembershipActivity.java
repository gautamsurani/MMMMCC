package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.PackageHistoryAdapter;
import com.makemusiccount.android.model.HistoryPackageList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class MembershipActivity extends AppCompatActivity {

    LinearLayout llProfile, llPassword;

    Activity context;

    RecyclerView rvPackage;

    Global global;

    String resMessage, resCode;

    ProgressDialog progressDialog;

    TextView tvPage, tvMsg;

    List<HistoryPackageList> historyPackageLists = new ArrayList<>();

    PackageHistoryAdapter packageHistoryAdapter;

    ImageView ivImg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_membership);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        tvPage.setText("My Profile");

        llProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChangeProfileActivity.class);
            intent.putExtra("page", "profile");
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        llPassword.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChangeProfileActivity.class);
            intent.putExtra("page", "password");
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvPackage.setLayoutManager(mLayoutManager);
        rvPackage.setHasFixedSize(true);
        packageHistoryAdapter = new PackageHistoryAdapter(context, historyPackageLists);
        rvPackage.setAdapter(packageHistoryAdapter);

        if (global.isNetworkAvailable()) {
            new GetPackageHistory().execute();
        } else {
           global.retryInternet("package_history");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("package_history")) {
                    new GetPackageHistory().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPackageHistory extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_PACKAGE_History + Util.getUserId(context)
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_package_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HistoryPackageList historyPackageList = new HistoryPackageList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        historyPackageList.setOrderID(jsonObjectList.getString("orderID"));
                                        historyPackageList.setPack_name(jsonObjectList.getString("pack_name"));
                                        historyPackageList.setTotal_amount(jsonObjectList.getString("total_amount"));
                                        historyPackageList.setPackage_desc(jsonObjectList.getString("package_desc"));
                                        historyPackageList.setPayment_date(jsonObjectList.getString("payment_date"));
                                        historyPackageList.setStart_date(jsonObjectList.getString("start_date"));
                                        historyPackageList.setEnd_date(jsonObjectList.getString("end_date"));
                                        historyPackageLists.add(historyPackageList);
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
                ivImg.setVisibility(View.GONE);
                tvMsg.setVisibility(View.GONE);
                tvMsg.setText("");
                rvPackage.setVisibility(View.VISIBLE);
                packageHistoryAdapter.notifyDataSetChanged();
            } else {
                rvPackage.setVisibility(View.GONE);
                ivImg.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(resMessage);
            }
        }
    }

    private void initComp() {
        llProfile = findViewById(R.id.llProfile);
        llPassword = findViewById(R.id.llPassword);
        rvPackage = findViewById(R.id.rvPackage);
        tvPage = findViewById(R.id.tvPage);
        tvMsg = findViewById(R.id.tvMsg);
        ivImg = findViewById(R.id.ivImg);
        progressDialog = new ProgressDialog(context);
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
    public void onBackPressed() {
        startActivity(new Intent(context, ChangeProfileActivity.class));
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