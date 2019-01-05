package com.makemusiccount.android.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyAngryBirdsTextView extends TextView {
    public MyAngryBirdsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyAngryBirdsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyAngryBirdsTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Angry Birds Movie.otf");
            setTypeface(tf);
        }
    }
}
