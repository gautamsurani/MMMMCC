package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.makemusiccount.android.adapter.SongAdapter;
import com.makemusiccount.android.model.SongList;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.makemusiccount.android.activity.MainActivity.ivWelcomeSong;
import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    PianoView pv;

    RecyclerView recyclerView;

    List<SongList> songLists = new ArrayList<>();

    SongAdapter songAdapter;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "",
            subscription_msg = "", subscription_img = "",
            badge_title = "", badge_msg = "", badge_img = "";

    ImageView ivBack, ivNext, ivLock;

    boolean IsLoading = false;

    ProgressBar pbLoading;

    int page = 0;

    LinearLayoutManager linearLayoutManager;

    String search_text = "";

    ImageView ivNotification, ivHelp;

    SharedPreferences sharedpreferences;

    TextView tvSongNext;

    MaterialShowcaseSequence sequence;

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

    public SongListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_song_list, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        UserId = Util.getUserId(context);

        global = new Global(context);

        initComp(view);

        tvSongNext.setOnClickListener(view -> {
            ivWelcomeSong.setVisibility(View.GONE);
            if (songAdapter != null) {
                songAdapter.startAnimation();
            }
        });

        view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);
        pv.setVisibility(View.VISIBLE);

        sequence = new MaterialShowcaseSequence(context, "complete1");
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        sequence.setConfig(config);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        songAdapter = new SongAdapter(context, songLists, sequence);
        recyclerView.setAdapter(songAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached = lastVisible + 2 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (IsLoading) {
                        IsLoading = false;
                        page++;
                        new GetSongList().execute();
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetSongList().execute();
        } else {
            retryInternet("song_list");
        }

        songAdapter.setOnItemClickListener((position, view, which) -> {
            if (which == 3) {
                MainActivity.SongId = songLists.get(position).getID();
                MainActivity.SongName = songLists.get(position).getName();
                Fragment fragment = new SongEquationFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                MainActivity.isHome = 3;
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
            if (songLists.size() != 0) {
                if (linearLayoutManager.findFirstVisibleItemPosition() < (songLists.size() - 1)) {
                    recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                } else {
                    recyclerView.smoothScrollToPosition(songLists.size() - 1);
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
            resCode = "";
            resMessage = "";
            if (page == 0) {
                songLists.clear();
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

            String strAPI = AppConstant.API_SONG_LIST + UserId
                    + "&catID=" + MainActivity.CatId
                    + "&subcatID=" + MainActivity.SubCatId
                    + "&pagecode=" + page
                    + "&search_text=" + search_text
                    + "&songType=" + MainActivity.songType
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String response = restClient.getResponse();
                Log.e("API", response);

                if (response != null && response.length() != 0) {
                    jsonObjectList = new JSONObject(response);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            subscription_msg = jsonObjectList.getString("subscription_msg");
                            subscription_img = jsonObjectList.getString("subscription_img");
                            badge_title = jsonObjectList.getString("badge_title");
                            badge_msg = jsonObjectList.getString("badge_msg");
                            badge_img = jsonObjectList.getString("badge_img");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        SongList songList = new SongList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        songList.setID(jsonObjectList.getString("ID"));
                                        songList.setName(jsonObjectList.getString("name"));
                                        songList.setImage(jsonObjectList.getString("image"));
                                        songList.setStatus(jsonObjectList.getString("status"));
                                        songList.setPlay_songs(jsonObjectList.getString("play_songs"));
                                        songLists.add(songList);
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
                IsLoading = true;
                songAdapter.notifyDataSetChanged();
                if (!badge_msg.isEmpty()) {
                    openBadgesPopup();
                } else {
                    if (!subscription_msg.isEmpty()) {
                        openPopup();
                    }
                }
                if (MainActivity.songType.equals("Premium")) {
                    sharedpreferences = context.getSharedPreferences("showcase", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    String isFirst = sharedpreferences.getString("isFirstSong", "");
                    assert isFirst != null;
                    if (!isFirst.equals("")) {
                        ivWelcomeSong.setVisibility(View.GONE);
                    } else {
                        ivWelcomeSong.setVisibility(View.VISIBLE);
                        editor.putString("isFirstSong", "Yes");
                        editor.apply();
                        editor.commit();
                    }
                }
            } else {
                IsLoading = false;
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
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
        ivLock = context.findViewById(R.id.ivLock);
        pv = context.findViewById(R.id.pv);
        ivBack = view.findViewById(R.id.ivBack);
        ivNext = view.findViewById(R.id.ivNext);
        pbLoading = view.findViewById(R.id.pbLoading);
        ivNotification = context.findViewById(R.id.ivNotification);
        ivHelp = context.findViewById(R.id.ivHelp);
        tvSongNext = context.findViewById(R.id.tvSongNext);
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