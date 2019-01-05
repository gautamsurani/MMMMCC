package com.makemusiccount.android.util;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashSet;

public class PlaySound {
    private static HashSet<MediaPlayer> mpSet = new HashSet<>();

    public static void play(Context context, int resId) {
        MediaPlayer mp = MediaPlayer.create(context, resId);
        mp.setOnCompletionListener(mp1 -> {
            mpSet.remove(mp1);
            mp1.stop();
            mp1.release();
        });
        mpSet.add(mp);
        mp.start();
    }

    public static void stop() {
        for (MediaPlayer mp : mpSet) {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
        }
        mpSet.clear();
    }
}
