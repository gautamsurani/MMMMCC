package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.BadgesAdapter;
import com.makemusiccount.android.model.BadgesList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class BadgesActivity extends AppCompatActivity {

    Activity context;

    Global global;

    RecyclerView rvBadges;

    ProgressBar pbLoading;

    List<BadgesList> badgesLists = new ArrayList<>();

    BadgesAdapter badgesAdapter;

    ProgressDialog progressDialog;

    int page = 0;

    String resMessage = "", resCode = "";

    boolean IsLoading = true;

    TextView tvPage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_badges);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        tvPage.setText("Badges");

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 4);
        rvBadges.setLayoutManager(mLayoutManager);
        rvBadges.setHasFixedSize(true);
        badgesAdapter = new BadgesAdapter(context, badgesLists);
        rvBadges.setAdapter(badgesAdapter);

        badgesAdapter.setOnItemClickListener((position, view, i) -> openPopup(position));

        if (global.isNetworkAvailable()) {
            new GetBatchesList().execute();
        } else {
            global.retryInternet("batches");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("batches")) {
                    new GetBatchesList().execute();
                }
            }
        }
    }

    AlertDialog dialog;

    @SuppressLint("SetTextI18n")
    private void openPopup(int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_badges, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnOk = alert_layout.findViewById(R.id.btnOk);
        TextView tvTerms = alert_layout.findViewById(R.id.tvTerms);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(badgesLists.get(position).getMessage());
        tvTitle.setText(badgesLists.get(position).getName());
        tvTerms.setText(badgesLists.get(position).getTerms());

        if (badgesLists.get(position).getMessage().isEmpty()) {
            tvMsg.setVisibility(View.GONE);
        }

        btnOk.setOnClickListener(v -> dialog.dismiss());
        Glide.with(context)
                .load(badgesLists.get(position).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivIcon);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(380, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBatchesList extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_Batches_LIST + Util.getUserId(context) + "&pagecode=" + page;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.e("API", strAPITrim);
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("batches_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        BadgesList badgesList = new BadgesList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        badgesList.setID(jsonObjectList.getString("ID"));
                                        badgesList.setName(jsonObjectList.getString("name"));
                                        badgesList.setImage(jsonObjectList.getString("image"));
                                        badgesList.setStatus(jsonObjectList.getString("status"));
                                        badgesList.setTerms(jsonObjectList.getString("terms"));
                                        badgesList.setMessage(jsonObjectList.getString("message"));
                                        badgesLists.add(badgesList);
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
                badgesAdapter.notifyDataSetChanged();
            } else {
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
                IsLoading = false;
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
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

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        rvBadges = findViewById(R.id.rvBadges);
        tvPage = findViewById(R.id.tvPage);
        pbLoading = findViewById(R.id.progressBar1);
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