
package com.example.waveview.entity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import android.util.Log;

public class Tweener {
    private static Animator.AnimatorListener mCleanupListener;
    private static HashMap<Object, Tweener> sTweens = new HashMap();
    public ObjectAnimator animator;
    private static final String TAG = "Tweener";
    private static final boolean DEBUG = true;
    static
    {
        mCleanupListener = new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animation) {
                remove(animation);
            }

            public void onAnimationEnd(Animator animation) {
                remove(animation);
            }
        };
    }

    public Tweener(ObjectAnimator object) {
        animator = object;
    }

    private static void remove(Animator animator) {
        Iterator<Entry<Object, Tweener>> iter = sTweens.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Object, Tweener> entry = iter.next();
            if (entry.getValue().animator == animator) {
                if (DEBUG)
                    Log.v(TAG, "Removing Tweener " + sTweens.get(entry.getKey())
                            + " sTweens.size() = " + sTweens.size());
                iter.remove();
                break;
            }
        }
    }

    public static void removeAll() {
        Iterator<Entry<Object, Tweener>> iter = sTweens.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Object, Tweener> entry = iter.next();
            if (entry.getValue().animator != null) {
                iter.remove();
            }
        }
    }

    private static void replace(ArrayList<PropertyValuesHolder> props, Object... args) {
        for (final Object killobject : args) {
            Tweener tween = sTweens.get(killobject);
            if (tween != null) {
                tween.animator.cancel();
                if (props != null) {
                    tween.animator.setValues(
                            props.toArray(new PropertyValuesHolder[props.size()]));
                } else {
                    sTweens.remove(tween);
                }
            }
        }
    }

    public static Tweener to(Object object, long duration, Object... vars) {
        long delay = 0;
        AnimatorUpdateListener updateListener = null;
        AnimatorListener listener = null;
        TimeInterpolator interpolator = null;

        // Iterate through arguments and discover properties to animate
        ArrayList<PropertyValuesHolder> props = new ArrayList<PropertyValuesHolder>(vars.length / 2);
        for (int i = 0; i < vars.length; i += 2) {
            if (!(vars[i] instanceof String)) {
                throw new IllegalArgumentException("Key must be a string: " + vars[i]);
            }
            String key = (String) vars[i];
            Object value = vars[i + 1];
            if ("simultaneousTween".equals(key)) {
                // TODO
            } else if ("ease".equals(key)) {
                interpolator = (TimeInterpolator) value; // TODO: multiple interpolators?
            } else if ("onUpdate".equals(key) || "onUpdateListener".equals(key)) {
                updateListener = (AnimatorUpdateListener) value;
            } else if ("onComplete".equals(key) || "onCompleteListener".equals(key)) {
                listener = (AnimatorListener) value;
            } else if ("delay".equals(key)) {
                delay = ((Number) value).longValue();
            } else if ("syncWith".equals(key)) {
                // TODO
            } else if (value instanceof float[]) {
                props.add(PropertyValuesHolder.ofFloat(key,
                        ((float[]) value)[0], ((float[]) value)[1], ((float[]) value)[2]));
            } else if (value instanceof int[]) {
                props.add(PropertyValuesHolder.ofInt(key,
                        ((int[]) value)[0], ((int[]) value)[1]));
            } else if (value instanceof Number) {
                float floatValue = ((Number) value).floatValue();
                props.add(PropertyValuesHolder.ofFloat(key, floatValue));
            } else {
                throw new IllegalArgumentException(
                        "Bad argument for key \"" + key + "\" with value " + value.getClass());
            }
        }

        // Re-use existing tween, if present
        Tweener tween = sTweens.get(object);
        ObjectAnimator anim = null;
        if (tween == null) {
            anim = ObjectAnimator.ofPropertyValuesHolder(object,
                    props.toArray(new PropertyValuesHolder[props.size()]));
            tween = new Tweener(anim);
            sTweens.put(object, tween);
            if (DEBUG)
                Log.v(TAG, "Added new Tweener " + tween);
        } else {
            anim = sTweens.get(object).animator;
            replace(props, object); // Cancel all animators for given object
        }

        if (interpolator != null) {
            anim.setInterpolator(interpolator);
        }

        // Update animation with properties discovered in loop above
        anim.setStartDelay(delay);
        anim.setDuration(duration);
        anim.removeAllUpdateListeners(); // There should be only one
        if (updateListener != null) {
            anim.addUpdateListener(updateListener);
        }
        anim.removeAllListeners(); // There should be only one.
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.addListener(mCleanupListener);

        return tween;
    }

    public static void reset() {
        sTweens.clear();
    }
}
