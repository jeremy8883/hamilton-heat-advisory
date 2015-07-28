package net.jeremycasey.hamiltonheatalert.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.jeremycasey.hamiltonheatalert.R;

/**
 * Note that this custom view was quickly hacked together to just get working.
 * Be sure that the view is inserted with a width of fill/match parent and height of wrapcontent.
 */
public class MeterView extends RelativeLayout {

    private static final float HAND_PIVOT_X = 0.55f;
    private static final float HAND_PIVOT_Y = 0.95f;

    private int mStage;
    private static final float[] ROTATION_POSIONS = new float[] {
        -75, -25, 25, 75
    };

    private ImageView mBackImage;
    private ImageView mHandImage;

    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MeterView,
                0, 0);

        try {
            mStage = a.getInt(R.styleable.MeterView_stage, -1);
        } finally {
            a.recycle();
        }

        init();
    }

    public void setStage(int stage) {
        this.mStage = stage;
        update();
    }

    public int getStage() {
        return mStage;
    }

    private void init() {
        inflate(getContext(), R.layout.view_meter, this);
        mBackImage = (ImageView) findViewById(R.id.meterBack);
        mHandImage = (ImageView) findViewById(R.id.meterHand);
    }

//    Don't worry about the animations for now
//    private void animateHandToCurrentStage(int oldStage) {
//        mHandImage.clearAnimation();
//        mHandImage.setRotation(getRotationPos(mStage));
//        if (mStage == -1) {
//            startSearchingAnimation();
//        } else {
//            float from = mHandImage.getRotation() + getRotationPos(oldStage);
//            float to = mHandImage.getRotation();
//            animateRotateTo(from, to);
//        }
//    }

    private float getRotationPos() {
        return mStage == -1 ? -1 : ROTATION_POSIONS[mStage];
    }

//    private void startSearchingAnimation() {
//        if (mBackImage.getWidth() == 0) return;
//
//        animateRotateTo(mHandImage.getRotation(), -5f);
//    }
//
//    private void animateRotateTo(float from, float to) {
//        AnimationSet animSet = new AnimationSet(true);
//        animSet.setInterpolator(new OvershootInterpolator(3f));
//        animSet.setRepeatMode(0);
//        animSet.setFillAfter(true);
//        animSet.setFillEnabled(true);
//
//        RotateAnimation rotate = new RotateAnimation(from, to,
//                RotateAnimation.RELATIVE_TO_SELF, HAND_PIVOT_X,
//                RotateAnimation.RELATIVE_TO_SELF, HAND_PIVOT_Y);
//        rotate.setDuration(400);
//        rotate.setFillAfter(true);
//        animSet.addAnimation(rotate);
//
//        mHandImage.startAnimation(animSet);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == oldw && h != oldh) return; //Just a result of the update function being called
        update();
    }

    private void update() {
        float rootWidth = (float)this.getWidth();
        if (rootWidth == 0) return;
        float rootHeight = rootWidth * 204.75f / 406.682f;

        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(Math.round(rootWidth), Math.round(rootHeight));
        mBackImage.setLayoutParams(backParams);

        float height = rootWidth * 0.3f;
        float width = height * 36.686f / 120.504f;
        float pivotY = height * HAND_PIVOT_Y;
        float pivotX = width * HAND_PIVOT_X;
        float top = rootHeight * 0.93f - pivotY;
        float left = rootWidth / 2f - pivotX;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Math.round(width), Math.round(height));
        params.setMargins(Math.round(left), Math.round(top), 0, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        Log.i("TEST", this.getWidth() + ", " + this.getHeight() + "---" + width + ", " + height + ", " + left + ", " + top);

        mHandImage.setPivotY(Math.round(pivotY));
        mHandImage.setPivotX(Math.round(pivotX));

        if (mStage >= 0) {
            mHandImage.setVisibility(View.VISIBLE);
            mHandImage.setRotation(getRotationPos());
        } else {
            mHandImage.setVisibility(View.GONE);
        }
        mHandImage.setLayoutParams(params);
    }
}
