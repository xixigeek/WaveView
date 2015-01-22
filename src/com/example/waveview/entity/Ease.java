
package com.example.waveview.entity;

import android.animation.TimeInterpolator;

public class Ease {
    public static class Linear {
        public static final TimeInterpolator easeNone = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return input;
            }
        };
    }
}
