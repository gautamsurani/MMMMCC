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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.NotificationAdapter;
import com.makemusiccount.android.model.NotificationList;
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

public class NotificationActivity extends AppCompatActivity {

    Activity context;

    RecyclerView rvNotification;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "", UserId = "";

    int page = 0;

    Global global;

    List<NotificationList> notificationLists = new ArrayList<>();

    NotificationAdapter notificationAdapter;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLoading = true;

    ProgressBar pbLoading;

    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_notification);

        context = this;

        global = new Global(context);

        initComp();

        UserId = Util.getUserId(context);

        if (global.isNetworkAvailable()) {
            new GetNotificationList().execute();
        } else {
            global.retryInternet("notification");
        }

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvNotification.setLayoutManager(mLayoutManager);
        rvNotification.setHasFixedSize(true);
        notificationAdapter = new NotificationAdapter(context, notificationLists);
        rvNotification.setAdapter(notificationAdapter);

        ivBack.setOnClickListener(view -> onBackPressed());

        rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (IsLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                            recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                        page++;
                        if (global.isNetworkAvailable()) {
                            new GetNotificationList().execute();
                        } else {
                            global.retryInternet("notification");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("notification")) {
                    new GetNotificationList().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNotificationList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            } else {
                pbLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_NOTIFICATION_LIST + UserId + "&pagecode=" + page;
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("data_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        NotificationList notificationList = new NotificationList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        notificationList.setOfferID(jsonObjectList.getString("offerID"));
                                        notificationList.setButtonID(jsonObjectList.getString("buttonID"));
                                        notificationList.setType(jsonObjectList.getString("type"));
                                        notificationList.setImage(jsonObjectList.getString("image"));
                                        notificationList.setSub_cat(jsonObjectList.getString("sub_cat"));
                                        notificationList.setButton_name(jsonObjectList.getString("button_name"));
                                        notificationList.setTitle(jsonObjectList.getString("title"));
                                        notificationList.setMessage(jsonObjectList.getString("message"));
                                        notificationList.setIcon(jsonObjectList.getString("icon"));
                                        notificationList.setDate(jsonObjectList.getString("date"));
                                        notificationLists.add(notificationList);
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
            pbLoading.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {
                notificationAdapter.notifyDataSetChanged();
            } else {
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
                IsLoading = false;
            }
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        rvNotification = findViewById(R.id.rvNotification);
        pbLoading = findViewById(R.id.pbLoading);
        ivBack = findViewById(R.id.ivBack);
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