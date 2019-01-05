package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.PointHistoryAdapter;
import com.makemusiccount.android.model.PointModel;
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

public class PointHistoryActivity extends AppCompatActivity {

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    LinearLayoutManager mLayoutManager;

    RecyclerView rvPointHistory;

    int pagecode = 0;

    boolean IsLAstLoading = true;

    ProgressBar progressBar1;

    PointHistoryAdapter pointHistoryAdapter;

    List<PointModel> pointModels = new ArrayList<>();

    TextView tvTotalPoints, tvPage, tvNoFound;

    String resMessage = "", resCode = "", point = "";

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_point_history);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        tvPage.setText("Point History");

        if (global.isNetworkAvailable()) {
            new GetWalletHistoryDetails().execute();
        } else {
            global.retryInternet("point_history");
        }

        mLayoutManager = new LinearLayoutManager(context);
        rvPointHistory.setLayoutManager(mLayoutManager);
        rvPointHistory.setHasFixedSize(true);
        pointHistoryAdapter = new PointHistoryAdapter(context, pointModels);
        rvPointHistory.setAdapter(pointHistoryAdapter);

        rvPointHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            progressBar1.setVisibility(View.VISIBLE);
                            pagecode++;
                            new GetWalletHistoryDetails().execute();
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
                if (extraValue.equalsIgnoreCase("point_history")) {
                    new GetWalletHistoryDetails().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWalletHistoryDetails extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pagecode == 0) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_POINT_HISTORY + Util.getUserId(context)
                    + "&pagecode=" + pagecode;

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
                        point = jsonObjectList.getString("point");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("transction_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        PointModel pointModel = new PointModel();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        pointModel.setPoints(jsonObjectList.getString("points"));
                                        pointModel.setRemark(jsonObjectList.getString("remark"));
                                        pointModel.setSymbol(jsonObjectList.getString("symbol"));
                                        pointModel.setType(jsonObjectList.getString("type"));
                                        pointModel.setDate(jsonObjectList.getString("date"));
                                        pointModels.add(pointModel);
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            IsLAstLoading = true;
            tvNoFound.setVisibility(View.GONE);
            progressBar1.setVisibility(View.GONE);
            if (resCode.equals("0")) {
                tvTotalPoints.setText(point);
                pointHistoryAdapter.notifyDataSetChanged();
            } else {
                if (pointModels.size() == 0) {
                    tvNoFound.setVisibility(View.VISIBLE);
                    tvNoFound.setText(resMessage);
                }
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
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ImageView ivNotification = findViewById(R.id.ivNotification);
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

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        rvPointHistory = findViewById(R.id.rvPointHistory);
        progressBar1 = findViewById(R.id.progressBar1);
        tvPage = findViewById(R.id.tvPage);
        tvNoFound = findViewById(R.id.tvNoFound);
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