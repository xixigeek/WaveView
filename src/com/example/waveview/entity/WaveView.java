
package com.example.waveview.entity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WaveView extends View {
    private String TAG = "WaveView";
    private int mCirclePosX;
    private int mCirclePosY;
    private boolean mStart = false;
    private final int startWavesAnswer = 2;
    Paint mPaint = new Paint();
    private HashMap<Tweener, WaveDrawable> mWaveDrawable = new HashMap();
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    };
    private Animator.AnimatorListener mWaveEndListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            Iterator<Map.Entry<Tweener, WaveDrawable>> iter = mWaveDrawable.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Tweener, WaveDrawable> entry = (Map.Entry) iter.next();
                if (entry.getKey().animator == animator) {
                    iter.remove();
                    return;
                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case startWavesAnswer:
                    startOneWave();
                    if (mStart) {
                        sendEmptyMessageDelayed(startWavesAnswer, 3100L);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void startWaves() {
        if (!mStart) {
            mStart = true;
            mHandler.sendEmptyMessage(startWavesAnswer);
        }
    }

    public void stopWaves() {
        mStart = false;
        mHandler.removeCallbacksAndMessages(null);
        //mHandler.removeMessages(startWavesAnswer);
        mWaveDrawable.clear();
        Tweener.reset();
    }

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void drawWaves(Canvas canvas) {
        Iterator i = mWaveDrawable.values().iterator();
        while (i.hasNext()) {
            WaveDrawable wave = (WaveDrawable) i.next();
            if (wave != null) {
                wave.draw(canvas);
            }
        }
    }

    private void startOneWave() {
        if (mStart) {
            Log.d(TAG, "startOneWave" + mCirclePosX + " mCirclePosY=" + mCirclePosY);
            if ((mCirclePosX > 0) && (mCirclePosY > 0)) {
                WaveDrawable wave = new WaveDrawable();
                wave.setPosition(mCirclePosX, mCirclePosY);
                Tweener tweener = Tweener.to(wave, 10000L,
                        "ease", Ease.Linear.easeNone,
                        "radius", new float[] {
                                100.0f, 300.0f, 550.0f
                        },
                        "onUpdate", mUpdateListener,
                        "onComplete", mWaveEndListener);
                tweener.animator.start();
                mWaveDrawable.put(tweener, wave);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWaves(canvas);
    }

    public void setCenterPoint(int circlePosX, int circlePosY) {
        mCirclePosX = circlePosX;
        mCirclePosY = circlePosY;
    }
}
