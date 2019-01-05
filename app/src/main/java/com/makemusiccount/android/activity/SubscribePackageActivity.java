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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.SubscribeAdapter;
import com.makemusiccount.android.model.PackageList;
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

public class SubscribePackageActivity extends AppCompatActivity {

    RecyclerView rvPackage;

    Activity context;

    List<PackageList> packageLists = new ArrayList<>();

    SubscribeAdapter subscribeAdapter;

    ImageView ivBack;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "", sub_title, sub_point_1, sub_point_2, sub_point_3, notes, main_title;

    TextView tvHeading, tvSubHeading, tvPoint1, tvPoint2, tvPoint3, tvNote, tvContinue;

    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_subscribe_package);

        context = this;

        global = new Global(context);

        initComp();

        if (Util.getUserId(context) == null) {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        } else {
            if (Util.getUserId(context).isEmpty()) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        }

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvPackage.setLayoutManager(mLayoutManager);
        rvPackage.setHasFixedSize(true);
        subscribeAdapter = new SubscribeAdapter(context, packageLists);
        rvPackage.setAdapter(subscribeAdapter);

        ivBack.setOnClickListener(view -> onBackPressed());

        if (global.isNetworkAvailable()) {
            new GetPackage().execute();
        } else {
            global.retryInternet("package");
        }

        tvContinue.setOnClickListener(view -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("package", packageLists.get(subscribeAdapter.getSelectedPosition()));
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("package")) {
                    new GetPackage().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPackage extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_PACKAGE_LIST + Util.getUserId(context)
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("package_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        PackageList packageList = new PackageList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        packageList.setPackID(jsonObjectList.getString("packID"));
                                        packageList.setName(jsonObjectList.getString("name"));
                                        packageList.setPlan_price_info(jsonObjectList.getString("plan_price_info"));
                                        packageList.setPackage_desc(jsonObjectList.getString("package_desc"));
                                        packageList.setPoint1(jsonObjectList.getString("point1"));
                                        packageList.setPoint2(jsonObjectList.getString("point2"));
                                        packageList.setPoint3(jsonObjectList.getString("point3"));
                                        packageList.setPoint4(jsonObjectList.getString("point4"));
                                        packageList.setPrice(jsonObjectList.getString("price"));
                                        packageLists.add(packageList);
                                    }
                                }
                            }

                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("package_info");
                            {
                                JSONObject jsonObjectList = jsonArray1.getJSONObject(0);
                                main_title = jsonObjectList.getString("main_title");
                                sub_title = jsonObjectList.getString("sub_title");
                                sub_point_1 = jsonObjectList.getString("sub_point_1");
                                sub_point_2 = jsonObjectList.getString("sub_point_2");
                                sub_point_3 = jsonObjectList.getString("sub_point_3");
                                notes = jsonObjectList.getString("notes");
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
                subscribeAdapter.notifyDataSetChanged();
                tvHeading.setText(main_title);
                tvSubHeading.setText(sub_title);
                tvPoint1.setText(sub_point_1);
                tvPoint2.setText(sub_point_2);
                tvPoint3.setText(sub_point_3);
                tvNote.setText(notes);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        rvPackage = findViewById(R.id.rvPackage);
        ivBack = findViewById(R.id.ivBack);
        tvContinue = findViewById(R.id.tvContinue);
        tvHeading = findViewById(R.id.tvHeading);
        tvSubHeading = findViewById(R.id.tvSubHeading);
        tvPoint1 = findViewById(R.id.tvPoint1);
        tvPoint2 = findViewById(R.id.tvPoint2);
        tvPoint3 = findViewById(R.id.tvPoint3);
        tvNote = findViewById(R.id.tvNote);
        progressDialog = new ProgressDialog(context);
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