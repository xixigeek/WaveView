
package com.example.waveview.entity;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Color;

public class WaveDrawable {
    private String TAG = "WaveDrawable";
    private int mAlpha;
    private float mCircleX;
    private float mCircleY;
    private Paint mPaint = new Paint();
    private float mRadius;

    public WaveDrawable() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3.0f);
        mAlpha = 255;
    }

    public void draw(Canvas c) {
        c.drawCircle(mCircleX, mCircleY, mRadius, mPaint);
    }

    public void setPosition(float positionX, float positionY) {
        mCircleX = positionX;
        mCircleY = positionY;
    }

    public void setRadius(float r) {
        if (mRadius > 120.0F) {
            mAlpha = (int) (255.0f - (((r - 120.0f) / 480.0f) * 255.0f));
            mPaint.setAlpha(mAlpha);
        }
        double angle = 90.0;
        float y1 = (float) ((double) r * Math.sin(Math.toRadians(angle)));
        LinearGradient lg = new LinearGradient(mCircleX, (mCircleY + y1), mCircleX,
                (mCircleY - y1), Color.GREEN, Color.RED, Shader.TileMode.CLAMP);
        mPaint.setShader(lg);
        mRadius = r;
    }
}
