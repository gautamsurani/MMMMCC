package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.adapter.LeaderBoardAdapter;
import com.makemusiccount.android.model.LeaderList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    RecyclerView recyclerView;

    List<LeaderList> leaderLists = new ArrayList<>();

    LeaderBoardAdapter leaderBoardAdapter;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    ProgressBar progress_bar;

    PianoView pv;

    String UserId = "", resMessage = "", resCode = "";

    ImageView ivCup3, ivCup2, ivCup1;

    CircleImageView ivUser3, ivUser2, ivUser1;

    TextView tvName3, tvScore3, tvName2, tvScore2, tvName1, tvScore1, tvDate, tvPage;

    GridLayoutManager mLayoutManager;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLoading = true;

    int page = 0;

    String number1, image1, name1, point1, number2, image2, name2, point2, number3, image3, name3, point3;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        context = getActivity();

        global = new Global(context);

        initComp(view);

        view1.setVisibility(View.GONE);
        rlPiano.setVisibility(View.GONE);
        pv.setVisibility(View.GONE);

        UserId = Util.getUserId(context);

        mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        leaderBoardAdapter = new LeaderBoardAdapter(context, leaderLists);
        recyclerView.setAdapter(leaderBoardAdapter);

        Util.setDate(tvDate);

        tvPage.setText("Leaderboard");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(@NonNull RecyclerView rvBookingList, int dx, int dy) {
                super.onScrolled(rvBookingList, dx, dy);

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (IsLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && rvBookingList.getChildAt(rvBookingList.getChildCount() - 1).getBottom() <= rvBookingList.getHeight()) {
                        page++;
                        new GetLeader().execute();
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetLeader().execute();
        } else {
            retryInternet("leader");
        }

        return view;
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("leader")) {
                    new GetLeader().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetLeader extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                }
            } else {
                progress_bar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_LEADER_LIST + UserId
                    + "&app_type=" + "Android"
                    + "&pagecode=" + page;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("leaderboard_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        LeaderList leaderList = new LeaderList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        leaderList.setName(jsonObjectList.getString("name"));
                                        leaderList.setPoint(jsonObjectList.getString("point"));
                                        leaderList.setImage(jsonObjectList.getString("image"));
                                        leaderList.setAccount_type(jsonObjectList.getString("account_type"));
                                        leaderLists.add(leaderList);
                                    }
                                }
                            }
                        }
                        number1 = jsonObjectList.getString("number1");
                        image1 = jsonObjectList.getString("image1");
                        name1 = jsonObjectList.getString("name1");
                        point1 = jsonObjectList.getString("point1");
                        number2 = jsonObjectList.getString("number2");
                        image2 = jsonObjectList.getString("image2");
                        name2 = jsonObjectList.getString("name2");
                        point2 = jsonObjectList.getString("point2");
                        number3 = jsonObjectList.getString("number3");
                        image3 = jsonObjectList.getString("image3");
                        name3 = jsonObjectList.getString("name3");
                        point3 = jsonObjectList.getString("point3");
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
            progress_bar.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {
                leaderBoardAdapter.notifyDataSetChanged();
                if (page == 0) {

                    Glide.with(context)
                            .load(number1)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivCup1);

                    Glide.with(context)
                            .load(image1)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivUser1);

                    tvName1.setText(name1);

                    tvScore1.setText(point1);

                    Glide.with(context)
                            .load(number2)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivCup2);

                    Glide.with(context)
                            .load(image2)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivUser2);

                    tvName2.setText(name2);

                    tvScore2.setText(point2);


                    Glide.with(context)
                            .load(number3)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivCup3);

                    Glide.with(context)
                            .load(image3)
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivUser3);

                    tvName3.setText(name3);

                    tvScore3.setText(point3);
                }
            } else {
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ivHelp.setVisibility(View.GONE);
    }

    ImageView ivHelp;

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);

        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        pv = context.findViewById(R.id.pv);

        ivHelp = context.findViewById(R.id.ivHelp);

        ivCup3 = view.findViewById(R.id.ivCup3);
        ivCup2 = view.findViewById(R.id.ivCup2);
        ivCup1 = view.findViewById(R.id.ivCup1);
        tvPage = view.findViewById(R.id.tvPage);

        ivUser3 = view.findViewById(R.id.ivUser3);
        ivUser2 = view.findViewById(R.id.ivUser2);
        ivUser1 = view.findViewById(R.id.ivUser1);

        tvName3 = view.findViewById(R.id.tvName3);
        tvName2 = view.findViewById(R.id.tvName2);
        tvName1 = view.findViewById(R.id.tvName1);
        tvScore3 = view.findViewById(R.id.tvScore3);
        tvScore2 = view.findViewById(R.id.tvScore2);
        tvScore1 = view.findViewById(R.id.tvScore1);

        tvDate = view.findViewById(R.id.tvDate);
        progress_bar = view.findViewById(R.id.progress_bar);
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