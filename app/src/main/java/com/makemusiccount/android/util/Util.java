package com.makemusiccount.android.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.LoginActivity;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Welcome on 20-01-2018.
 */

public class Util {

    public static String getUserName(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_NAME);
    }

    public static void callIntent(Activity context, String number) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 50);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + number));
            context.startActivity(intent);
        }
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getIsFirst(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.isFirst);
    }

    public static void setIsFirst(Context context, String value) {
        AppPreference.setPreference(context, AppPersistence.keys.isFirst, value);
    }

    public static String getEmail(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_EMAIL);
    }

    public static String getUserId(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_ID);
    }

    public static String getPianoSound(Context context) {
        String s;
        s = AppPreference.getPreference(context, AppPersistence.keys.PianoSound);
        if (s == null) {
            s = "60";
        }
        return s;
    }

    public static String getSongSound(Context context) {
        String s;
        s = AppPreference.getPreference(context, AppPersistence.keys.SongSound);
        if (s == null) {
            s = "60";
        }
        return s;
    }

    public static void setIntroVideo(Context context, String value) {
        AppPreference.setPreference(context, AppPersistence.keys.IntroVideo, value);
    }

    public static String getCity(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.CITY);
    }

    public static String getCompany(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.COMPANY);
    }

    @SuppressLint("SetTextI18n")
    public static void setDate(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        String currentDate = sdf.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayName = sdf_.format(date);
        textView.setText(dayName + ", " + currentDate);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String getUserImage(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_IMAGE);
    }

    public static String getMobileNo(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_NUMBER);
    }

    public static String getIntroVideo(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.IntroVideo);
    }

    public static String getAddress(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_ADDRESS);
    }

    public static String getUserType(Context context) {
        String User = AppPreference.getPreference(context, AppPersistence.keys.User_Type);
        if (User == null) {
            return "Sign in";
        } else if (User.isEmpty()) {
            return "";
        } else {
            return User;
        }
    }

    public static AutoPlayEntity getAutoPlayObject(int position, long time) {
        AutoPlayEntity autoPlayList;
        Piano.PianoKeyType type;
        int group;
        int positionOfGroup;
        switch (position) {
            case 1:
                positionOfGroup = 0;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 3:
                positionOfGroup = 1;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 5:
                positionOfGroup = 2;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 6:
                positionOfGroup = 3;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 8:
                positionOfGroup = 4;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 10:
                positionOfGroup = 5;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 12:
                positionOfGroup = 6;
                type = Piano.PianoKeyType.WHITE;
                group = 1;
                break;
            case 2:
                positionOfGroup = 0;
                type = Piano.PianoKeyType.BLACK;
                group = 1;
                break;
            case 4:
                positionOfGroup = 1;
                type = Piano.PianoKeyType.BLACK;
                group = 1;
                break;
            case 7:
                positionOfGroup = 2;
                type = Piano.PianoKeyType.BLACK;
                group = 1;
                break;
            case 9:
                positionOfGroup = 3;
                type = Piano.PianoKeyType.BLACK;
                group = 1;
                break;
            case 11:
                positionOfGroup = 4;
                type = Piano.PianoKeyType.BLACK;
                group = 1;
                break;
            case 13:
                positionOfGroup = 0;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 15:
                positionOfGroup = 1;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 17:
                positionOfGroup = 2;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 18:
                positionOfGroup = 3;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 20:
                positionOfGroup = 4;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 22:
                positionOfGroup = 5;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 24:
                positionOfGroup = 6;
                type = Piano.PianoKeyType.WHITE;
                group = 2;
                break;
            case 14:
                positionOfGroup = 0;
                type = Piano.PianoKeyType.BLACK;
                group = 2;
                break;
            case 16:
                positionOfGroup = 1;
                type = Piano.PianoKeyType.BLACK;
                group = 2;
                break;
            case 19:
                positionOfGroup = 2;
                type = Piano.PianoKeyType.BLACK;
                group = 2;
                break;
            case 21:
                positionOfGroup = 3;
                type = Piano.PianoKeyType.BLACK;
                group = 2;
                break;
            case 23:
                positionOfGroup = 4;
                type = Piano.PianoKeyType.BLACK;
                group = 2;
                break;
            default:
                positionOfGroup = 0;
                type = Piano.PianoKeyType.WHITE;
                group = 3;
                break;
        }
        autoPlayList = new AutoPlayEntity(type, group, positionOfGroup, time);
        return autoPlayList;
    }

    public static Integer getPianoPosition(Piano.PianoKeyType type, int group, int positionOfGroup) {
        int pianoPosition = 0;
        if (group == 1) {
            if (type == Piano.PianoKeyType.WHITE) {
                switch (positionOfGroup) {
                    case 0:
                        pianoPosition = 1;
                        break;
                    case 1:
                        pianoPosition = 3;
                        break;
                    case 2:
                        pianoPosition = 5;
                        break;
                    case 3:
                        pianoPosition = 6;
                        break;
                    case 4:
                        pianoPosition = 8;
                        break;
                    case 5:
                        pianoPosition = 10;
                        break;
                    case 6:
                        pianoPosition = 12;
                        break;
                }
            } else if (type == Piano.PianoKeyType.BLACK) {
                switch (positionOfGroup) {
                    case 0:
                        pianoPosition = 2;
                        break;
                    case 1:
                        pianoPosition = 4;
                        break;
                    case 2:
                        pianoPosition = 7;
                        break;
                    case 3:
                        pianoPosition = 9;
                        break;
                    case 4:
                        pianoPosition = 11;
                        break;
                }
            }
        } else if (group == 2) {
            if (type == Piano.PianoKeyType.WHITE) {
                switch (positionOfGroup) {
                    case 0:
                        pianoPosition = 13;
                        break;
                    case 1:
                        pianoPosition = 15;
                        break;
                    case 2:
                        pianoPosition = 17;
                        break;
                    case 3:
                        pianoPosition = 18;
                        break;
                    case 4:
                        pianoPosition = 20;
                        break;
                    case 5:
                        pianoPosition = 22;
                        break;
                    case 6:
                        pianoPosition = 24;
                        break;
                }
            } else if (type == Piano.PianoKeyType.BLACK) {
                switch (positionOfGroup) {
                    case 0:
                        pianoPosition = 14;
                        break;
                    case 1:
                        pianoPosition = 16;
                        break;
                    case 2:
                        pianoPosition = 19;
                        break;
                    case 3:
                        pianoPosition = 21;
                        break;
                    case 4:
                        pianoPosition = 23;
                        break;
                }
            }
        } else {
            pianoPosition = 25;
        }
        return pianoPosition;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void Logout(Context context) {
        AppPersistence.start(context).remove(AppPersistence.keys.USER_NAME);
        AppPersistence.start(context).remove(AppPersistence.keys.USER_ID);
        AppPersistence.start(context).remove(AppPersistence.keys.USER_NUMBER);
        AppPersistence.start(context).remove(AppPersistence.keys.USER_EMAIL);
        AppPersistence.start(context).remove(AppPersistence.keys.USER_ADDRESS);
        AppPersistence.start(context).remove(AppPersistence.keys.CITY);
        AppPersistence.start(context).remove(AppPersistence.keys.USER_IMAGE);
        AppPersistence.start(context).remove(AppPersistence.keys.COMPANY);
        AppPersistence.start(context).remove(AppPersistence.keys.User_Type);
    }

    public static void loginDialog(final Activity context, String Message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_login_now);
        TextView btnRetryinternet = dialog.findViewById(R.id.btnRetryinternet);
        ImageView ivClose = dialog.findViewById(R.id.ivClose);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setText(Message);
        btnRetryinternet.setOnClickListener(v -> {
            Util.Logout(context);
            context.startActivity(new Intent(context, LoginActivity.class));
            context.finish();
        });
        ivClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static String extractFilename(String song_file) {
        if (song_file.equals("")) {
            return "";
        }
        String newFilename;
        if (song_file.contains("/")) {
            int dotPosition = song_file.lastIndexOf("/");
            newFilename = song_file.substring(dotPosition + 1, song_file.length());
        } else {
            newFilename = song_file;
        }
        return newFilename;
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        String pxx;
        if (String.valueOf(px).contains(".")) {
            pxx = String.valueOf(px).substring(0, String.valueOf(px).indexOf("."));
        } else {
            pxx = String.valueOf(px);
        }
        return Integer.parseInt(pxx);
    }
}