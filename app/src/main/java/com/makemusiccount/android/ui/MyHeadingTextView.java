package com.makemusiccount.android.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyHeadingTextView extends TextView {

    public MyHeadingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyHeadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyHeadingTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Axiforma-Black.ttf");
            setTypeface(tf);
        }
    }
}