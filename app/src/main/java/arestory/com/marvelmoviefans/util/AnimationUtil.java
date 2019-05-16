package arestory.com.marvelmoviefans.util;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.*;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class AnimationUtil {

    public static void startShakeByViewAnim(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //TODO 验证参数的有效性

        //由小变大
        Animation scaleAnim = new ScaleAnimation(scaleSmall, scaleLarge, scaleSmall, scaleLarge);
        //从左向右
        Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnim.setDuration(duration);
        rotateAnim.setDuration(duration / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(10);

        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(scaleAnim);
        smallAnimationSet.addAnimation(rotateAnim);

        view.startAnimation(smallAnimationSet);
    }


    public static void upDownAnim(View view,long duration){

        ObjectAnimator upAnim = ObjectAnimator.ofFloat(view,"y",view.getY(),5f,view.getY());
        upAnim.setTarget(view);
        upAnim.setDuration(duration);
        upAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        upAnim.start();
        upAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Float currentY = (Float) animation.getAnimatedValue();

            }
        });
    }

    public static RecyclerView.ItemAnimator create(){

        final List<View> mAnimatorViewList = new ArrayList<>();


        return new SimpleItemAnimator() {
            @Override
            public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
                return false;
            }

            @Override
            public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i1, int i2, int i3) {
                return false;
            }

            @Override
            public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1, int i, int i1, int i2, int i3) {
                mAnimatorViewList.add(viewHolder.itemView);

                return true;
            }

            @Override
            public void runPendingAnimations() {


                for (int i = 0; i < mAnimatorViewList.size(); i++) {
                    View view = mAnimatorViewList.get(i);
                    ObjectAnimator upAnim = ObjectAnimator.ofFloat(view,"y",view.getY(),5f,view.getY());
                    upAnim.setTarget(view);
                    upAnim.setDuration(200);
                    upAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    upAnim.start();
                }
                mAnimatorViewList.clear();
            }

            @Override
            public void endAnimation(@NonNull RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    public static void startShakeByPropertyAnim(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }

        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );
        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );

        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }
}
