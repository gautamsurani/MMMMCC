package com.makemusiccount.android.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SubscribePackageActivity;
import com.makemusiccount.android.activity.TutorialVideoActivity;
import com.makemusiccount.android.adapter.TutorialCategoryAdapter;
import com.makemusiccount.android.model.TutorialCategoryList;
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

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialCategoryFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    PianoView pv;

    TextView tvError;

    RecyclerView recyclerView;

    List<TutorialCategoryList> tutorialCategoryLists = new ArrayList<>();

    TutorialCategoryAdapter tutorialCategoryAdapter;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "", subscription_msg = "", subscription_img = "",
            badge_title = "", badge_msg = "", badge_img = "";

    ImageView ivBack, ivNext;

    int ThisVisibleItemCount = 0;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLoading = true;

    ProgressBar pbLoading;

    int page = 0;

    LinearLayoutManager linearLayoutManager;

    String search_text = "";

    ImageView ivNotification, ivHelp;

    @Override
    public void onResume() {
        super.onResume();
        ivHelp.setVisibility(View.GONE);
        ivNotification.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        ivHelp.setVisibility(View.VISIBLE);
        ivNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        super.onCreateOptionsMenu(menu, inflater);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 0;
                search_text = query;
                new GetSongList().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            page = 0;
            if (!search_text.isEmpty()) {
                search_text = "";
                new GetSongList().execute();
            }
            return false;
        });
    }

    public TutorialCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tutorial_category, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        assert context != null;

        UserId = Util.getUserId(context);

        if (UserId == null) {
            Util.loginDialog(context, "You need to sign in or sign up to play songs");
        }

        global = new Global(context);

        initComp(view);

        view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);
        pv.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        tutorialCategoryAdapter = new TutorialCategoryAdapter(context, tutorialCategoryLists);
        recyclerView.setAdapter(tutorialCategoryAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ThisVisibleItemCount = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (IsLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                            recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                        //page++;
                        //new GetSongList().execute();
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetSongList().execute();
        } else {
            retryInternet("song_list");
        }

        tutorialCategoryAdapter.setOnItemClickListener((position, view, which) -> {
            if (which == 3) {
                MainActivity.tutorialCategoryId = tutorialCategoryLists.get(position).getID();
                MainActivity.tutorialCategoryName = tutorialCategoryLists.get(position).getName();
                if (!tutorialCategoryLists.get(position).getVideo().isEmpty()) {
                    Intent intent = new Intent(context, TutorialVideoActivity.class);
                    intent.putExtra("videoURL", tutorialCategoryLists.get(position).getVideo());
                    startActivity(intent);
                } else {
                    Fragment fragment = new TutorialEquationFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    MainActivity.isHome = 6;
                }
            }
        });

        ivBack.setOnClickListener(view -> {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
                recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        ivNext.setOnClickListener(view -> {
            if (tutorialCategoryLists.size() != 0) {
                if (linearLayoutManager.findFirstVisibleItemPosition() < (tutorialCategoryLists.size() - 1)) {
                    recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                } else {
                    recyclerView.smoothScrollToPosition(tutorialCategoryLists.size() - 1);
                }
            }
        });

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
                if (extraValue.equalsIgnoreCase("song_list")) {
                    new GetSongList().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                tutorialCategoryLists.clear();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                }
            } else {
                pbLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Tutorials_Category + UserId
                    + "&search=" + search_text;

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
                        subscription_msg = jsonObjectList.getString("subscription_msg");
                        subscription_img = jsonObjectList.getString("subscription_img");
                        badge_title = jsonObjectList.getString("badge_title");
                        badge_msg = jsonObjectList.getString("badge_msg");
                        badge_img = jsonObjectList.getString("badge_img");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("tutorial_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TutorialCategoryList tutorialCategoryList = new TutorialCategoryList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        tutorialCategoryList.setID(jsonObjectList.getString("ID"));
                                        tutorialCategoryList.setName(jsonObjectList.getString("name"));
                                        tutorialCategoryList.setImage(jsonObjectList.getString("image"));
                                        tutorialCategoryList.setStatus(jsonObjectList.getString("status"));
                                        tutorialCategoryList.setVideo(jsonObjectList.getString("video"));
                                        tutorialCategoryList.setComplete_status(jsonObjectList.getString("complete_status"));
                                        tutorialCategoryLists.add(tutorialCategoryList);
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
                tvError.setVisibility(View.GONE);
                IsLoading = true;
                tutorialCategoryAdapter.notifyDataSetChanged();
                if (!badge_msg.isEmpty()) {
                    openBadgesPopup();
                } else {
                    if (!subscription_msg.isEmpty()) {
                        openPopup();
                    }
                }
            } else {
                IsLoading = false;
                if (page == 0) {
                    tvError.setText(resMessage);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void openBadgesPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_badges_detail, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnOk = alert_layout.findViewById(R.id.btnOk);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(badge_msg);
        tvTitle.setText(badge_title);

        btnOk.setOnClickListener(v -> dialog.dismiss());
        Glide.with(context)
                .load(badge_img)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivIcon);

        dialog.setOnDismissListener(dialogInterface -> {
            if (global.isNetworkAvailable()) {
                page = 0;
                new GetSongList().execute();
            } else {
                Toast.makeText(context, "No internet available!!!", Toast.LENGTH_SHORT).show();
            }
        });

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

    AlertDialog dialog;

    private void openPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(subscription_msg);
        tvTitle.setText(subscription_msg);

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            MainActivity.isHome = 0;
        });

        btnSubscribe.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            MainActivity.isHome = 0;
        });

        Glide.with(context)
                .load(subscription_img)
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

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        pv = context.findViewById(R.id.pv);
        tvError = view.findViewById(R.id.tvError);
        ivNotification = context.findViewById(R.id.ivNotification);
        ivHelp = context.findViewById(R.id.ivHelp);
        ivBack = view.findViewById(R.id.ivBack);
        ivNext = view.findViewById(R.id.ivNext);
        pbLoading = view.findViewById(R.id.pbLoading);
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
