package com.makemusiccount.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.makemusiccount.android.activity.NoNetworkActivity;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public final class Global {

    private Context mContext;

    public Global(Context mCon) {
        this.mContext = mCon;
    }

    public synchronized boolean isNetworkAvailable() {
        boolean flag;
        flag = checkNetworkAvailable();
        return flag;
    }

    private boolean checkNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(mContext, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        ((Activity) mContext).startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }
}