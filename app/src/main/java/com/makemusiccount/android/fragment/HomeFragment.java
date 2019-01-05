package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.iid.FirebaseInstanceId;
import com.makemusiccount.android.BuildConfig;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.BadgesActivity;
import com.makemusiccount.android.activity.LoginActivity;
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.PointHistoryActivity;
import com.makemusiccount.android.activity.ProgressActivity;
import com.makemusiccount.android.activity.SubscribePackageActivity;
import com.makemusiccount.android.activity.VideoTutorialActivity;
import com.makemusiccount.android.adapter.DashboardAdapter;
import com.makemusiccount.android.listener.OnHomeAdiCallListener;
import com.makemusiccount.android.listener.onHelpClickListener;
import com.makemusiccount.android.model.DashboardList;
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
import java.util.regex.Pattern;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.makemusiccount.android.activity.MainActivity.ivWelcome;
import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    static onHelpClickListener onHelpClickListener;

    View view, view1;

    RelativeLayout rlPiano, rlDemo;

    Activity context;

    Global global;

    String userId = "", resMessage = "", resCode = "", DeviceID = "", AppVersion = "", version = "",
            version_msg = "", FCMId = "", record_menu = "", trial_package_msg = "", subscription_img = "",
            subscription_msg = "";

    public static String share_image = "", share_msg = "";

    ProgressDialog progressDialog;

    RecyclerView rvDashboard;

    ImageView ivHelp, ivNotification;

    List<DashboardList> dashboardLists = new ArrayList<>();

    public static OnHomeAdiCallListener onHomeAdiCallListener;

    @SuppressLint("StaticFieldLeak")
    public static DashboardAdapter dashboardAdapter;

    PianoView pv;

    SharedPreferences sharedpreferences;

    MaterialShowcaseSequence sequence;

    TextView tvNext, tvStartPlaying;

    LinearLayout llLoginView;

    public static void setOnHelpClickListener(onHelpClickListener listener) {
        onHelpClickListener = listener;
    }

    public static void initHomeApiListener(OnHomeAdiCallListener listener) {
        onHomeAdiCallListener = listener;
    }

    public HomeFragment() {
    }

    @SuppressLint("HardwareIds")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        context = getActivity();

        global = new Global(context);

        initComp(view);

        MainActivity.CatId = "";

        MainActivity.SubCatId = "";

        userId = Util.getUserId(context);

        AppVersion = BuildConfig.VERSION_NAME;

        FCMId = FirebaseInstanceId.getInstance().getToken();

        DeviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        pv.setVisibility(View.VISIBLE);
        view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);

        sequence = new MaterialShowcaseSequence(context, "complete");
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        sequence.setConfig(config);

        final LinearLayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvDashboard.setLayoutManager(mLayoutManagerBestProduct);
        rvDashboard.setHasFixedSize(true);
        dashboardAdapter = new DashboardAdapter(context, dashboardLists, sequence);
        rvDashboard.setAdapter(dashboardAdapter);

        sequence.setOnItemDismissedListener((materialShowcaseView, i) -> {
            if (i == 4) {
                MainActivity.isHome = 1;
                MainActivity.songType = "Premium";
                openFragment(new SongListFragment());
            }
        });

        tvNext.setOnClickListener(view -> {
            ivWelcome.setVisibility(View.GONE);
            if (dashboardAdapter != null) {
                dashboardAdapter.startAnimation();
            }
        });

        ivHelp.setOnClickListener(view -> {
            if (onHelpClickListener != null) {
                if (MainActivity.isHome == 0) {
                    Intent intent = new Intent(getActivity(), VideoTutorialActivity.class);
                    intent.putExtra("videoURL", AppConstant.Help_Video);
                    startActivity(intent);
                }
            }
        });

        tvStartPlaying.setOnClickListener(view -> {
            MainActivity.isHome = 1;
            MainActivity.songType = "Free";
            openFragment(new SongListFragment());
        });

        dashboardAdapter.setOnItemClickListener((position, view, i) -> {
            switch (position) {
                case 3:
                    if (Util.getUserId(context) == null) {
                        Util.loginDialog(context, "You need to be signed in to this action.");
                    } else {
                        MainActivity.isHome = 1;
                        openFragment(new LeaderBoardFragment());
                    }
                    break;
                case 0:
                    if (Util.getUserId(context) == null) {
                        Util.loginDialog(context, "You need to be signed in to this action.");
                    } else {
                        Intent intent = new Intent(context, ProgressActivity.class);
                        startActivity(intent);
                    }
                    break;
                case 1:
                    if (Util.getUserId(context) == null) {
                        Util.loginDialog(context, "You need to be signed in to this action.");
                    } else {
                        Intent intent = new Intent(context, BadgesActivity.class);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    if (Util.getUserId(context) == null) {
                        Util.loginDialog(context, "You need to be signed in to this action.");
                    } else {
                        Intent intent = new Intent(context, PointHistoryActivity.class);
                        startActivity(intent);
                    }
                    break;
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("dashboard")) {
                    new Dashboard().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Dashboard extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dashboardLists.clear();
            resMessage = "";
            resCode = "";
            version = "";
            version_msg = "";
            share_image = "";
            share_msg = "";
            record_menu = "";
            trial_package_msg = "";
            subscription_img = "";
            subscription_msg = "";
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Dashboard + userId
                    + "&fcmID=" + FCMId
                    + "&deviceID=" + DeviceID
                    + "&versionID=" + AppVersion
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
                String response = restClient.getResponse();
                Log.e("API", response);

                if (response != null && response.length() != 0) {
                    jsonObjectList = new JSONObject(response);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        version = jsonObjectList.getString("version");
                        version_msg = jsonObjectList.getString("version_msg");
                        share_image = jsonObjectList.getString("share_image");
                        share_msg = jsonObjectList.getString("share_msg");
                        record_menu = jsonObjectList.getString("record_menu");
                        trial_package_msg = jsonObjectList.getString("trial_package_msg");
                        subscription_img = jsonObjectList.getString("subscription_img");
                        subscription_msg = jsonObjectList.getString("subscription_msg");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("dasboard_data");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        DashboardList dashboardList = new DashboardList();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        dashboardList.setImage(jsonObjectList.getString("image"));
                                        dashboardList.setValue(jsonObjectList.getString("value"));
                                        dashboardList.setTitle(jsonObjectList.getString("title"));
                                        dashboardList.setTitle_sup(jsonObjectList.getString("title_sup"));
                                        dashboardList.setColor_code(jsonObjectList.getString("color_code"));
                                        dashboardLists.add(dashboardList);
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
                PackageInfo pInfo;
                boolean new_version_available = false;
                try {
                    pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    new_version_available = compare(pInfo.versionName, version);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (new_version_available) {
                    if (context != null) {
                        showUpdateDialog(version_msg);
                    }
                }
                if (!trial_package_msg.isEmpty()) {
                    showSubscriptionPopup();
                }
                if (!subscription_msg.isEmpty()) {
                    openExpiryPopup();
                }
                dashboardAdapter.notifyDataSetChanged();
                onHomeAdiCallListener.recordMenuStatus(record_menu);

                sharedpreferences = context.getSharedPreferences("showcase", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                String isFirst = sharedpreferences.getString("isFirst", "");
                assert isFirst != null;
                if (!isFirst.equals("")) {
                    ivWelcome.setVisibility(View.GONE);
                } else {
                    ivWelcome.setVisibility(View.VISIBLE);
                    editor.putString("isFirst", "Yes");
                    editor.apply();
                    editor.commit();
                }
            } else if (resCode.equalsIgnoreCase("2")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                Util.Logout(context);
                startActivity(new Intent(context, LoginActivity.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.finishAffinity();
                } else {
                    context.finish();
                }
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    AlertDialog dialog;

    AlertDialog dialog_trial;

    private void openExpiryPopup() {

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

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnSubscribe.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
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

    private void showSubscriptionPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_trial_subscripsion, null);

        alertDialogBuilder.setView(alert_layout);

        dialog_trial = alertDialogBuilder.create();

        dialog_trial.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);

        tvTitle.setText(trial_package_msg);

        btnCancel.setOnClickListener(view -> dialog_trial.dismiss());

        btnSubscribe.setOnClickListener(view -> {
            dialog_trial.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
        });

        dialog_trial.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog_trial.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(400, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    protected void showUpdateDialog(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Update Available")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Update", (dialog1, id) -> {
                    dialog1.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        context.finishAffinity();
                    } else {
                        context.finish();
                    }
                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                })
                .setNegativeButton("Close", (dialog12, id) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        context.finishAffinity();
                    } else {
                        context.finish();
                    }
                })
                .create();
        dialog.show();
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, 4);
    }

    public static String normalisedVersion(String version, int maxWidth) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    private static boolean compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        System.out.printf("result: " + "'%s' %s '%s'%n", v1, cmpStr, v2);
        if (cmpStr.contains("<")) {
            return true;
        }
        if (cmpStr.contains(">") || cmpStr.contains("==")) {
            return false;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.getUserId(context) == null) {
            llLoginView.setVisibility(View.VISIBLE);
            ivNotification.setVisibility(View.GONE);
            ivHelp.setVisibility(View.GONE);
            rlDemo.setVisibility(View.VISIBLE);
        } else {
            rlDemo.setVisibility(View.GONE);
            llLoginView.setVisibility(View.GONE);
            ivNotification.setVisibility(View.VISIBLE);
            ivHelp.setVisibility(View.VISIBLE);
            if (global.isNetworkAvailable()) {
                new Dashboard().execute();
            } else {
                retryInternet("dashboard");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        llLoginView.setVisibility(View.GONE);
        /*ivNotification.setVisibility(View.VISIBLE);
        ivHelp.setVisibility(View.VISIBLE);*/
    }

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        rvDashboard = view.findViewById(R.id.rvDashboard);
        rlDemo = view.findViewById(R.id.rlDemo);
        tvStartPlaying = view.findViewById(R.id.tvStartPlaying);
        ivHelp = context.findViewById(R.id.ivHelp);
        pv = context.findViewById(R.id.pv);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        tvNext = context.findViewById(R.id.tvNext);
        llLoginView = context.findViewById(R.id.llLoginView);
        ivNotification = context.findViewById(R.id.ivNotification);
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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