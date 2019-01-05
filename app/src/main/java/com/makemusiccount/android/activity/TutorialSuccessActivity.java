package com.makemusiccount.android.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.makemusiccount.android.R;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.CountAnimationTextView;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.PlaySound;
import com.makemusiccount.android.util.Util;
import com.whygraphics.gifview.gif.GIFView;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class TutorialSuccessActivity extends AppCompatActivity {

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "", UserId = "", tutorialID = "";

    TextView tvTitle;

    ImageView ivBack;

    String point_text = "", heading = "", song_name = "", share = "", best_score = "", rate = "0";

    TextView tvHeading, tvScoreLabel;

    CountAnimationTextView count_animation_textView;

    SimpleRatingBar myRatingBar;

    CircleImageView ivShare, ivRetry, ivList;

    GIFView mGifView;

    ImageView ivDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_success);

        context = this;

        global = new Global(context);

        tutorialID = getIntent().getStringExtra("tutorialID");

        initComp();

        UserId = Util.getUserId(context);

        ivBack.setOnClickListener(view -> onBackPressed());

        ivList.setOnClickListener(view -> onBackPressed());

        ivShare.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, share);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        mGifView.setGifResource("asset:trophygif");

        ivRetry.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("screenType", "tutorial_question");
            startActivity(intent);
            finish();
        });

        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });

        if (global.isNetworkAvailable()) {
            new SongCompleteData().execute();
        } else {
            global.retryInternet("song_complete");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("song_complete")) {
                    new SongCompleteData().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SongCompleteData extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_Tutorial_COMPLETE_DATA + UserId
                    + "&tutorialID=" + tutorialID
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
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equals("0")) {
                            song_name = jsonObjectList.getString("tutorial_name");
                            heading = jsonObjectList.getString("heading");
                            point_text = jsonObjectList.getString("point_text");
                            share = jsonObjectList.getString("share_msg");
                            best_score = jsonObjectList.getString("point_sub_text");
                            rate = jsonObjectList.getString("star_count");
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
                tvHeading.setText(heading);
                SimpleRatingBar.AnimationBuilder builder = myRatingBar.getAnimationBuilder()
                        .setRatingTarget(Float.parseFloat(rate))
                        .setDuration(1400)
                        .setInterpolator(new BounceInterpolator())
                        .setAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                myRatingBar.setRating(Float.parseFloat(rate));
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                builder.start();
                count_animation_textView.setAnimationDuration(1500).countAnimation(0, Integer.parseInt(point_text));
                PlaySound.play(context, R.raw.score);
                tvTitle.setText(song_name);
                tvScoreLabel.setText(best_score);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("screenType", "Tutorial_2");
        startActivity(intent);
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        tvScoreLabel = findViewById(R.id.tvScoreLabel);
        tvHeading = findViewById(R.id.tvHeading);
        ivDashboard = findViewById(R.id.ivDashboard);
        ivShare = findViewById(R.id.ivShare);
        ivRetry = findViewById(R.id.ivRetry);
        ivList = findViewById(R.id.ivList);
        myRatingBar = findViewById(R.id.myRatingBar);
        mGifView = findViewById(R.id.main_activity_gif_vie);
        count_animation_textView = findViewById(R.id.count_animation_textView);
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