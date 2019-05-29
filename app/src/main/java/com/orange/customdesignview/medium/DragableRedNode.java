package com.orange.customdesignview.medium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.customdesignview.R;

/**
 * file created by czh on 2019/5/8
 * copy from  https://github.com/siwangqishiq/DragIndicatorView
 */
@SuppressLint("AppCompatCustomView")
public class DragableRedNode extends TextView {

    private static float DEFAULT_VISCOUS_VALUE = 0.15f;//粘滞系数

    private float mViscous = DEFAULT_VISCOUS_VALUE;

    private ViewGroup mRootView;
    private DragableRedNode mCloneView;
    private SpringView mSpringView;
    private OnIndicatorDismiss mOnDismissAction;

    private Paint mPaint;
    private int mRadius;

    private float mDx=0;
    private float mDy=0;
    private float mOriginX = 0;
    private float mOriginY = 0;

    public DragableRedNode(Context context) {
        super(context);
        initView(context);
    }

    public DragableRedNode(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DragableRedNode(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void  initView(Context context){
        setGravity(Gravity.CENTER);

        mPaint=new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        if (context instanceof Activity){
            mRootView=(ViewGroup)((Activity)context).getWindow().getDecorView();
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        showCannotSetBgErrorLog();
    }

    @Override
    public void setBackground(Drawable background) {
        showCannotSetBgErrorLog();
    }

    @Override
    public void setBackgroundColor(int color) {
        showCannotSetBgErrorLog();
    }

    private void showCannotSetBgErrorLog() {
        Log.e("error", "This drag indicator view can not set custom background");
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //绘制圆圈背景
        mRadius=Math.min(getMeasuredHeight(),getMeasuredWidth())>>1;
        canvas.drawCircle(getWidth()>>1,getHeight()>>1,mRadius,mPaint);
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                mDx=event.getX();
                mDy=event.getY();

                mOriginX = event.getRawX() - mDx + (getWidth() >> 1);
                mOriginY = event.getRawY() - mDy + (getHeight() >> 1);
                break;
            case MotionEvent.ACTION_MOVE:
                if (getVisibility()== View.VISIBLE){
                    setVisibility(View.INVISIBLE);

                    //添加拉伸形状的view
                    mSpringView = new SpringView(this.getContext());
                    mSpringView.initSpring(mOriginX, mOriginY, mRadius, getWidth(), getHeight());
                    mRootView.addView(mSpringView);

                    //添加随手指一动的view
                    mCloneView=getCloneView();
                    mRootView.addView(mCloneView,getLayoutParams());
                }

                if (mCloneView!=null){
                    mCloneView.setX(event.getRawX()-mDy);
                    mCloneView.setY(event.getRawY()-mDy);
                    mCloneView.invalidate();
                }

                //拉伸水滴效果
                if (mSpringView != null) {
                    //更新弹性控件
                    mSpringView.update(event.getRawX() - mDx, event.getRawY() - mDy);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mSpringView != null && mSpringView.radius <= 0) {
                    killView(event.getRawX(), event.getRawY());
                    mRootView.removeView(mSpringView);
                    mSpringView = null;

                    if (mCloneView != null) {
                        mRootView.removeView(mCloneView);
                        mCloneView = null;
                    }
                } else {//不取消
                    if (mSpringView != null && mSpringView.spring_len > 1f) {//存在弹性势能  显示弹性动画效果
                        mSpringView.startSpringAction();
                    } else {
                        resetView();
                    }
                }

                break;
            default:
                break;
        }
        return true;
    }


    private void resetView() {
        if (mCloneView != null) {
            mRootView.removeView(mCloneView);
        }
        if (mSpringView != null) {
            mRootView.removeView(mSpringView);
        }
        setVisibility(View.VISIBLE);
    }


    private DragableRedNode getCloneView(){
        DragableRedNode view=new DragableRedNode(getContext());
        view.setText(getText());
        view.setTextColor(getTextColors());
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize());
        view.setGravity(getGravity());
        view.setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
        view.setEnabled(false);
        return view;
    }



    private final class SpringView extends View {
        public float from_x;
        public float from_y;
        public float radius;
        public float to_x;
        public float to_y;

        public float toWidth;
        public float toHeight;

        private Path mPath = new Path();
        boolean isSpringAction = false;
        float cur_x;
        float cur_y;
        float spring_len = 0;

        ValueAnimator mSpringAnimation;

        public SpringView(Context context) {
            super(context);
            isSpringAction = false;
        }

        public void initSpring(float init_x, float init_y, float r, float w, float h) {
            this.from_x = init_x;
            this.from_y = init_y;
            this.to_x = init_x;
            this.to_y = init_y;
            this.radius = r;

            this.toWidth = w;
            this.toHeight = h;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (radius > 0) {
                canvas.drawPath(mPath, mPaint);//draw path
                canvas.drawCircle(from_x, from_y, radius, mPaint);
            }//end if
        }


        public void update(float x, float y) {
            this.to_x = x;
            this.to_y = y;

            //目的圆 球心坐标
            float dest_x = to_x + toWidth / 2;
            float dest_y = to_y + toHeight / 2;
            updatePosition(dest_x, dest_y);
        }

        private void updatePosition(final float dest_x, final float dest_y) {
            this.cur_x = dest_x;
            this.cur_y = dest_y;

            //求距离，用于计算圆点连接线与x轴或y轴的夹角，范围0~π
            float deltaX = 0;
            float deltaY = 0;
            if (dest_x >= from_x) {
                deltaX = dest_x - from_x;
                deltaY = dest_y - from_y;
            } else {
                deltaX = from_x - dest_x;
                deltaY = from_y - dest_y;
            }//end if

            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            //radius = (float)mRadius/(distance + 1);
            //  r = R - R * (1 -1/d));
            radius = mRadius - mViscous * distance;
            if (radius < 0.2f * mRadius) {
                radius = 0;
            }

            if (radius > 0) {
                // (1 , 0)  (x,y)
//                Log.d("czh","deltaX:"+deltaX);
                //求连接线与y轴的夹角
                double sin = deltaY / distance;
                double angle = Math.asin(sin);
                Log.d("czh","angle:"+angle*(180/Math.PI ));
                //过原点做垂直于连接线的直线，直线与圆相交，有两个点。下面两个角度为圆心与这两个点连线，也就是半径，与相应轴的夹角
                double circle_from_thela1 = angle+Math.PI/2;
                double circle_from_thela2 = circle_from_thela1 + Math.PI;


                //下面四个点，起始圆边上的两个点，和手指处也就是目标圆边上的两个点，各自的坐标
                float circle_from_circle_x1 = (float) (from_x + radius * Math.cos(circle_from_thela1));
                float circle_from_circle_y1 = (float) (from_y + radius * Math.sin(circle_from_thela1));

                float circle_from_circle_x2 = (float) (from_x + radius * Math.cos(circle_from_thela2));
                float circle_from_circle_y2 = (float) (from_y + radius * Math.sin(circle_from_thela2));

                float circle_to_circle_x1 = (float) (dest_x + mRadius * Math.cos(circle_from_thela1));
                float circle_to_circle_y1 = (float) (dest_y + mRadius * Math.sin(circle_from_thela1));

                float circle_to_circle_x2 = (float) (dest_x + mRadius * Math.cos(circle_from_thela2));
                float circle_to_circle_y2 = (float) (dest_y + mRadius * Math.sin(circle_from_thela2));

                //绘制path路径，沙漏型
                mPath.reset();
                mPath.moveTo(circle_from_circle_x1, circle_from_circle_y1);
                mPath.lineTo(circle_from_circle_x2, circle_from_circle_y2);
                mPath.quadTo((from_x + dest_x) / 2, (from_y + dest_y) / 2,
                        circle_to_circle_x2, circle_to_circle_y2);
                //mPath.lineTo(dest_x,dest_y);
                //mPath.lineTo(circle_to_circle_x2, circle_to_circle_y2);
                mPath.lineTo(circle_to_circle_x1, circle_to_circle_y1);
                mPath.quadTo((from_x + dest_x) / 2, (from_y + dest_y) / 2,
                        circle_from_circle_x1, circle_from_circle_y1);
                mPath.close();

                if (mCloneView != null) {
                    mCloneView.setX(cur_x - toWidth / 2);
                    mCloneView.setY(cur_y - toHeight / 2);
                }

                spring_len = distance;
            } else {
                spring_len = 0;
            }

            invalidate();
        }


        /**
         * 做回弹操作
         */
        public void startSpringAction() {
            isSpringAction = true;

            if (mSpringAnimation != null) {
                mSpringAnimation.cancel();
            }
            mSpringAnimation = ValueAnimator.ofObject(new PointEvaluator(),
                    new Point(cur_x, cur_y), new Point(from_x, from_y));
            mSpringAnimation.setDuration(120);
            mSpringAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Point p = (Point) animation.getAnimatedValue();
                    updatePosition(p.getX(), p.getY());
                    //invalidate();
                }
            });

            mSpringAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetView();
                }
            });

            mSpringAnimation.setInterpolator(new OvershootInterpolator(5));
            mSpringAnimation.start();
            postInvalidate();
        }

    }//end inner class


    protected void killView(final float x, final float y) {
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.clean_anim);
        mRootView.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setX(x - (imageView.getMeasuredWidth() >> 1));
                imageView.setY(y - (imageView.getMeasuredHeight() >> 1));
            }
        });

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        int totalDuring = 0;
        for (int i = 0, len = animationDrawable.getNumberOfFrames(); i < len; i++) {
            totalDuring += animationDrawable.getDuration(i);
        }
        animationDrawable.start();

        //动画播放结束后 移除ImageView
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootView.removeView(imageView);
            }
        }, totalDuring + 20);

        if (mOnDismissAction != null) {
            mOnDismissAction.OnDismiss(this);
        }

        setVisibility(View.GONE);
    }

    public interface OnIndicatorDismiss {
        void OnDismiss(DragableRedNode view);
    }

    public void setOnDismissAction(OnIndicatorDismiss mOnDismissAction) {
        this.mOnDismissAction = mOnDismissAction;
    }
}
