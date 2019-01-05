package com.makemusiccount.pianoview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.makemusiccount.pianoview.R;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.entity.PianoKey;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.utils.AudioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Gautam on 2016-11-25.
 */

public class PianoView extends View {
    private final static String TAG = "PianoView";
    private Piano piano = null;
    private ArrayList<PianoKey[]> whitePianoKeys;
    private ArrayList<PianoKey[]> blackPianoKeys;
    private CopyOnWriteArrayList<PianoKey> pressedKeys = new CopyOnWriteArrayList<>();
    private Paint paint;
    private RectF square;
    private AudioUtils utils = null;
    private Context context;
    private int layoutWidth = 0;
    private float scale = 1;
    private OnLoadAudioListener loadAudioListener;
    private OnPianoAutoPlayListener autoPlayListener;
    private OnPianoListener pianoListener;
    private int progress = 100;
    private boolean canPress = true;
    private boolean isAutoPlaying = false;
    private boolean isInitFinish = false;
    private int minRange = 0;
    private int maxRange = 0;
    private int maxStream;
    public static float pianoVolume = 1f;

    private Handler autoPlayHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            handleAutoPlay(msg);
        }
    };
    private static final int HANDLE_AUTO_PLAY_START = 0;
    private static final int HANDLE_AUTO_PLAY_END = 1;
    private static final int HANDLE_AUTO_PLAY_BLACK_DOWN = 2;
    private static final int HANDLE_AUTO_PLAY_WHITE_DOWN = 3;
    private static final int HANDLE_AUTO_PLAY_KEY_UP = 4;
    int paddingToTopBlack, paddingToBottomBlack;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paddingToTopBlack = getResources().getDimensionPixelSize(R.dimen.paddingToTopBlack);
        paddingToBottomBlack = getResources().getDimensionPixelSize(R.dimen.paddingToBottomBlack);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        square = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        /*ViewGroup.MarginLayoutParams margins = ViewGroup.MarginLayoutParams.class.cast(getLayoutParams());
        margins.topMargin = 0;
        margins.bottomMargin = 0;
        margins.leftMargin = -((layoutWidth / 15) * 2);
        margins.rightMargin = 0;
        setLayoutParams(margins);*/
    }

    public Integer getWhiteKeyWidth() {
        return layoutWidth / 15;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key);
        assert whiteKeyDrawable != null;
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(height, whiteKeyHeight);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = whiteKeyHeight;
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
                break;
        }
        scale = (float) (height - getPaddingTop() - getPaddingBottom()) / (float) (whiteKeyHeight);
        layoutWidth = width - getPaddingLeft() - getPaddingRight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (piano == null) {
            minRange = 0;
            maxRange = layoutWidth;
            piano = new Piano(context, scale, maxRange);
            whitePianoKeys = piano.getWhitePianoKeys();
            blackPianoKeys = piano.getBlackPianoKeys();
            if (utils == null) {
                if (maxStream > 0) {
                    utils = AudioUtils.getInstance(getContext(), loadAudioListener, maxStream);
                } else {
                    utils = AudioUtils.getInstance(getContext(), loadAudioListener);
                }
                try {
                    utils.loadMusic(piano);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        if (whitePianoKeys != null) {
            for (int i = 0; i < whitePianoKeys.size(); i++) {
                for (PianoKey key : whitePianoKeys.get(i)) {
                    key.getKeyDrawable().draw(canvas);

                    Rect r = key.getKeyDrawable().getBounds();
                    int sideLength = (r.right - r.left) / 2;
                    int left = r.left + sideLength / 2;
                    int top = r.bottom - sideLength - sideLength / 3;
                    int right = r.right - sideLength / 2;
                    int bottom = r.bottom - sideLength / 3;
                    square.set(left, top, right, bottom);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(sideLength / 1.8f);
                    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                    int baseline = (int) ((square.bottom + square.top - fontMetrics.bottom - fontMetrics.top) / 2);
                    paint.setTextAlign(Paint.Align.CENTER);

                    canvas.drawText(key.getLetterName(), square.centerX(), baseline, paint);
                }
            }
        }
        if (blackPianoKeys != null) {
            for (int i = 0; i < blackPianoKeys.size(); i++) {
                for (PianoKey key : blackPianoKeys.get(i)) {
                    key.getKeyDrawable().draw(canvas);
                    if (i < blackPianoKeys.size() - 1) {
                        Rect r = key.getKeyDrawable().getBounds();
                        int sideLength = (r.right - r.left) / 2;
                        int left = r.left + sideLength / 2;
                        int top = r.bottom - sideLength - sideLength / 3;
                        int right = r.right - sideLength / 2;
                        int bottom = r.bottom - sideLength / 3;
                        square.set(left, top, right, bottom);
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(sideLength / 1.8f);
                        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                        int baseline = (int) ((square.bottom + square.top - fontMetrics.bottom - fontMetrics.top) / 2);
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(key.getLetterName(), square.centerX(), baseline - paddingToBottomBlack, paint);
                        canvas.drawText(key.getTopLetterName(), square.centerX(), r.top + paddingToTopBlack, paint);
                    }
                }

            }
        }
        if (!isInitFinish && piano != null && pianoListener != null) {
            isInitFinish = true;
            pianoListener.onPianoInitFinish();
            scroll(100);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canPress) {
            if (event.getPointerCount() > 1) {
                Log.d("Touch : ", "Multitouch event");
            } else {
                Log.d("Touch : ", "Single event");
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handleDown(event.getActionIndex(), event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        handleMove(i, event);
                    }
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        handleDown(i, event);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    handleDown(event.getActionIndex(), event);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    handlePointerUp(event.getPointerId(event.getActionIndex()));
                    break;
                case MotionEvent.ACTION_UP:
                    handleUp();
                    this.performClick();
                    break;
                default:
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleDown(int which, MotionEvent event) {
        int x = (int) event.getX(which) + this.getScrollX();
        int y = (int) event.getY(which);

        for (int i = 0; i < whitePianoKeys.size(); i++) {
            for (PianoKey key : whitePianoKeys.get(i)) {
                if (!key.isPressed() && key.contains(x, y)) {
                    handleWhiteKeyDown(which, event, key);
                }
            }
        }

        for (int i = 0; i < blackPianoKeys.size(); i++) {
            for (PianoKey key : blackPianoKeys.get(i)) {
                if (!key.isPressed() && key.contains(x, y)) {
                    handleBlackKeyDown(which, event, key);
                }
            }
        }
    }

    private void handleWhiteKeyDown(int which, MotionEvent event, PianoKey key) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        utils.playMusic(key);
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getVoice(), key.getGroup(), key.getPositionOfGroup());
        }
    }

    private void handleWhiteKeyDown(int which, MotionEvent event, PianoKey key, String type) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        if (!type.equals("hint")) {
            utils.playMusic(key);
        }
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getVoice(), key.getGroup(), key.getPositionOfGroup());
        }
    }

    private void handleBlackKeyDown(int which, MotionEvent event, PianoKey key) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        utils.playMusic(key);
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getVoice(), key.getGroup(), key.getPositionOfGroup());
        }
    }

    private void handleBlackKeyDown(int which, MotionEvent event, PianoKey key, String type) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        if (!type.equals("hint")) {
            utils.playMusic(key);
        }
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getVoice(), key.getGroup(), key.getPositionOfGroup());
        }
    }

    private void handleMove(int which, MotionEvent event) {
        int x = (int) event.getX(which) + this.getScrollX();
        int y = (int) event.getY(which);
        for (PianoKey key : pressedKeys) {
            if (key.getFingerID() == event.getPointerId(which)) {
                if (!key.contains(x, y)) {
                    key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                    invalidate(key.getKeyDrawable().getBounds());
                    key.setPressed(false);
                    key.resetFingerID();
                    pressedKeys.remove(key);
                }
            }
        }
    }

    private void handlePointerUp(int pointerId) {
        for (PianoKey key : pressedKeys) {
            if (key.getFingerID() == pointerId) {
                key.setPressed(false);
                key.resetFingerID();
                key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                invalidate(key.getKeyDrawable().getBounds());
                pressedKeys.remove(key);
                break;
            }
        }
    }

    private void handleUp() {
        if (pressedKeys.size() > 0) {
            for (PianoKey key : pressedKeys) {
                key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                key.setPressed(false);
                invalidate(key.getKeyDrawable().getBounds());
            }
            pressedKeys.clear();
        }
    }

    boolean autoPlayStop = false;

    public void stopAutoPlay() {
        autoPlayStop = true;
    }

    public void autoPlay(final List<AutoPlayEntity> autoPlayEntities, String type) {
        if (isAutoPlaying) {
            return;
        }
        autoPlayStop = false;
        isAutoPlaying = true;
        setCanPress(false);
        new Thread() {
            @Override
            public void run() {
                if (autoPlayHandler != null) {
                    autoPlayHandler.sendEmptyMessage(HANDLE_AUTO_PLAY_START);
                }
                try {
                    if (autoPlayEntities != null) {
                        for (AutoPlayEntity entity : autoPlayEntities) {
                            if (!autoPlayStop) {
                                if (entity != null) {
                                    Thread.sleep(entity.getCurrentBreakTime());
                                    if (!autoPlayStop) {
                                        ((Activity) context).runOnUiThread(() -> new Handler().postDelayed(() -> autoPlayHandler.sendEmptyMessage(HANDLE_AUTO_PLAY_KEY_UP), 200));

                                        if (entity.getType() != null) {
                                            switch (entity.getType()) {
                                                case BLACK:
                                                    PianoKey blackKey = null;
                                                    if (entity.getGroup() == 0) {
                                                        if (entity.getPosition() == 0) {
                                                            blackKey = blackPianoKeys.get(0)[0];
                                                        }
                                                    } else if (entity.getGroup() > 0 && entity.getGroup() <= 7) {
                                                        if (entity.getPosition() >= 0 && entity.getPosition() <= 4) {
                                                            blackKey = blackPianoKeys.get(entity.getGroup())[entity.getPosition()];
                                                        }
                                                    }
                                                    if (blackKey != null) {
                                                        Message msg = Message.obtain();
                                                        msg.what = HANDLE_AUTO_PLAY_BLACK_DOWN;
                                                        msg.obj = blackKey;
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("type", type);
                                                        msg.setData(bundle);
                                                        autoPlayHandler.sendMessage(msg);
                                                    }
                                                    break;
                                                case WHITE:
                                                    PianoKey whiteKey = null;
                                                    if (entity.getGroup() == 0) {
                                                        if (entity.getPosition() == 0) {
                                                            whiteKey = whitePianoKeys.get(0)[0];
                                                        } else if (entity.getPosition() == 1) {
                                                            whiteKey = whitePianoKeys.get(0)[1];
                                                        }
                                                    } else if (entity.getGroup() >= 0 && entity.getGroup() <= 7) {
                                                        if (entity.getPosition() >= 0 && entity.getPosition() <= 6) {
                                                            whiteKey = whitePianoKeys.get(entity.getGroup())[entity.getPosition()];
                                                        }
                                                    } else if (entity.getGroup() == 8) {
                                                        if (entity.getPosition() == 0) {
                                                            whiteKey = whitePianoKeys.get(8)[0];
                                                        }
                                                    }
                                                    if (whiteKey != null) {
                                                        Message msg = Message.obtain();
                                                        msg.what = HANDLE_AUTO_PLAY_WHITE_DOWN;
                                                        msg.obj = whiteKey;
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("type", type);
                                                        msg.setData(bundle);
                                                        autoPlayHandler.sendMessage(msg);
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (autoPlayHandler != null) {
                    autoPlayHandler.sendEmptyMessage(HANDLE_AUTO_PLAY_END);
                }
            }
        }.start();
    }

    public void releaseAutoPlay() {
        if (utils != null) {
            utils.stop();
        }
    }

    public int getPianoWidth() {
        if (piano != null) {
            return piano.getPianoWith();
        }
        return 0;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public void setCanPress(boolean canPress) {
        this.canPress = canPress;
    }

    public void scroll(int progress) {
        int x;
        switch (progress) {
            case 0:
                x = 0;
                break;
            case 100:
                x = getPianoWidth() - getLayoutWidth();
                break;
            default:
                x = (int) (((float) progress / 100f) * (float) (getPianoWidth() - getLayoutWidth()));
                break;
        }
        minRange = x;
        maxRange = x + getLayoutWidth();
        this.scrollTo(x, 0);
        this.progress = progress;
    }

    public void setSoundPollMaxStream(int maxStream) {
        this.maxStream = maxStream;
    }

    public void setPianoVolume(float pianoVolume) {
        this.pianoVolume = pianoVolume;
    }

    public void setPianoListener(OnPianoListener pianoListener) {
        this.pianoListener = pianoListener;
    }

    public void setLoadAudioListener(OnLoadAudioListener loadAudioListener) {
        this.loadAudioListener = loadAudioListener;
    }

    public void setAutoPlayListener(OnPianoAutoPlayListener autoPlayListener) {
        this.autoPlayListener = autoPlayListener;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void handleAutoPlay(Message msg) {
        switch (msg.what) {
            case HANDLE_AUTO_PLAY_BLACK_DOWN:
                if (msg.obj != null) {
                    try {
                        PianoKey key = (PianoKey) msg.obj;
                        autoScroll(key);
                        String type = msg.getData().getString("type");
                        handleBlackKeyDown(-1, null, key, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case HANDLE_AUTO_PLAY_WHITE_DOWN:
                if (msg.obj != null) {
                    try {
                        PianoKey key = (PianoKey) msg.obj;
                        autoScroll(key);
                        String type = msg.getData().getString("type");
                        handleWhiteKeyDown(-1, null, key, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case HANDLE_AUTO_PLAY_KEY_UP:
                handleUp();
                break;
            case HANDLE_AUTO_PLAY_START:
                if (autoPlayListener != null) {
                    autoPlayListener.onPianoAutoPlayStart();
                }
                break;
            case HANDLE_AUTO_PLAY_END:
                isAutoPlaying = false;
                setCanPress(true);
                if (autoPlayListener != null) {
                    autoPlayListener.onPianoAutoPlayEnd();
                }
                break;
        }
    }

    private void autoScroll(PianoKey key) {
        if (isAutoPlaying) {
            if (key != null) {
                Rect[] areas = key.getAreaOfKey();
                if (areas != null && areas.length > 0 && areas[0] != null) {
                    int left = areas[0].left, right = key.getAreaOfKey()[0].right;
                    for (int i = 1; i < areas.length; i++) {
                        if (areas[i] != null) {
                            if (areas[i].left < left) {
                                left = areas[i].left;
                            }
                            if (areas[i].right > right) {
                                right = areas[i].right;
                            }
                        }
                    }
                    if (left < minRange || right > maxRange) {
                        int progress = (int) ((float) left * 100 / (float) getPianoWidth());
                        //scroll(progress);
                    }
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        postDelayed(() -> scroll(progress), 200);
    }
}
