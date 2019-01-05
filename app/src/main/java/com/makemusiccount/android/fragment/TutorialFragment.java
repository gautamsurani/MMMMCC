package com.makemusiccount.android.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.VideoTutorialActivity;
import com.makemusiccount.android.adapter.TutorialAdapter;
import com.makemusiccount.android.model.TutorialList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class TutorialFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    PianoView pv;

    RecyclerView recyclerView;

    List<TutorialList> tutorialLists = new ArrayList<>();

    TutorialAdapter tutorialAdapter;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "";

    public TutorialFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        context = getActivity();

        global = new Global(context);

        initComp(view);

        view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);
        pv.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        tutorialAdapter = new TutorialAdapter(context, tutorialLists);
        recyclerView.setAdapter(tutorialAdapter);

        tutorialAdapter.setOnItemClickListener((position, view, i) -> {
            if (i == 3) {
                Intent intent = new Intent(context, VideoTutorialActivity.class);
                intent.putExtra("videoURL", tutorialLists.get(position).getUrl());
                startActivity(intent);
            }
        });

        if (global.isNetworkAvailable()) {
            new GetTutorialList().execute();
        } else {
            retryInternet("tutorial_list");
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("tutorial_list")) {
                    new GetTutorialList().execute();
                }
            }
        }
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTutorialList extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_TUTORIALS + UserId
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("tutorial_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TutorialList tutorialList = new TutorialList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        tutorialList.setID(jsonObjectList.getString("ID"));
                                        tutorialList.setName(jsonObjectList.getString("name"));
                                        tutorialList.setUrl(jsonObjectList.getString("url"));
                                        tutorialList.setImage(jsonObjectList.getString("image"));
                                        tutorialList.setStatus(jsonObjectList.getString("status"));
                                        tutorialLists.add(tutorialList);
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
                tutorialAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ImageView ivHelp;

    @Override
    public void onResume() {
        super.onResume();
        ivHelp.setVisibility(View.GONE);
    }

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        pv = context.findViewById(R.id.pv);
        ivHelp = context.findViewById(R.id.ivHelp);
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