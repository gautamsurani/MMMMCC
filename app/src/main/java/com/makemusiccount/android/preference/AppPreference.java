package com.makemusiccount.android.preference;

import android.content.Context;

/**
 * Created by welcome on 11-12-2017.
 */

public class AppPreference {

    public static void setPreference(Context context, Enum Name, String Value) {
        AppPersistence.start(context).save(Name, Value);
    }

    public static String getPreference(Context context, Enum Name) {
        return (String) AppPersistence.start(context).get(Name);
    }

    public static void removePreference(Context context, Enum Name) {
        AppPersistence.start(context).remove(Name);
    }
}