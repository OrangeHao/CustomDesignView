package com.orange.customdesignview.easy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * created by czh on 2019/5/27
 */
public class SearchingView extends View {

    public SearchingView(Context context) {
        super(context);
        init();
    }

    public SearchingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //画笔宽度
    private final int DEFAULT_STROKE_WIDTH = 12;
    private final int DEFAULT_VIEW_WIDTH = 50;

    //进入搜索状态的动画时间
    private final int DURATION_START_STOP=900;
    private final int DURATION_SEARCHING_PER_CIRCLE=1700;

    //绘制状态
    private final int STATE_NORMAL=0;
    private final int STATE_START=1;
    private final int STATE_SEARCHING=2;
    private final int STATE_STOP=3;

    private int mWidht=0;
    private int mHeight=0;
    private int mStrokeWidth=DEFAULT_STROKE_WIDTH;

    private ValueAnimator mStartAnimator;
    private ValueAnimator mSearchingAnimator;
    private ValueAnimator mStopAnimator;
    private float mAnimatorValue=0f;
    private boolean mIsGoingStop=false;


    private Paint mPaint;
    private Path mSearchPath;
    private Path mSearchTempPath;
    private Path mCirclePath;
    private Path mCircleTempPath;
    private PathMeasure mSearchMeasure;
    private PathMeasure mCircleMeasure;

    private int mCurrentState=STATE_NORMAL;

    private void init() {
        initPaint();
        initAnimator();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAnimator(){
        //不取0~1是防止切换状态时，绘制空白的图形，有种闪屏的感觉，观感不好
        mStartAnimator=ValueAnimator.ofFloat(0.0001f,0.9999f).setDuration(DURATION_START_STOP);
        mStartAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentState=STATE_START;
                mAnimatorValue=(float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mStartAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //开始loading动画
                mSearchingAnimator.cancel();
                mSearchingAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mSearchingAnimator=ValueAnimator.ofFloat(0.9999f,0.0001f).setDuration(DURATION_SEARCHING_PER_CIRCLE);
        mSearchingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mSearchingAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSearchingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentState=STATE_SEARCHING;
                mAnimatorValue=(float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mSearchingAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (mIsGoingStop){
                    mSearchingAnimator.cancel();
                    mStopAnimator.cancel();
                    mStopAnimator.start();
                }
            }
        });

        mStopAnimator=ValueAnimator.ofFloat(0.9999f,0.0001f).setDuration(DURATION_START_STOP);
        mStopAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentState=STATE_STOP;
                mAnimatorValue=(float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mStopAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentState=STATE_NORMAL;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentState=STATE_NORMAL;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {

        } else {

        }

        if (heightMode == MeasureSpec.EXACTLY) {

        } else {

        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidht=w;
        mHeight=h;
        initPath();
    }

    private void initPath() {
        mSearchTempPath=new Path();
        mCircleTempPath=new Path();

        //计算大圆小圆的半径,原点位于view的中心
        float radiusMax=Math.min(mWidht,mHeight)/2f-mStrokeWidth;
        float radiusSmall=radiusMax/2f-mStrokeWidth/2f;
        float radiusBig=radiusMax-mStrokeWidth/2f;

        //放大镜的path
        mSearchPath=new Path();
        RectF rectFSmall=new RectF(mWidht/2f-radiusSmall,mHeight/2f-radiusSmall,mWidht/2f+radiusSmall,mHeight/2f+radiusSmall);
        mSearchPath.addArc(rectFSmall,45,359.9f);

        //外面搜索时的大圆的path
        mCirclePath=new Path();
        RectF rectFBig=new RectF(mWidht/2f-radiusBig,mHeight/2f-radiusBig,mWidht/2f+radiusBig,mHeight/2f+radiusBig);
        mCirclePath.addArc(rectFBig,45,359.9f);

        mCircleMeasure=new PathMeasure();
        mCircleMeasure.setPath(mCirclePath,false);

        float[]pos=new float[2];
        mCircleMeasure.getPosTan(0,pos,null);

        //45度的位置添加放大镜把柄
        //或者：Math.cos(45*(Math.PI/180))*radiusBig;Math.sin(45*(Math.PI/180))*radiusBig;
        mSearchPath.lineTo(pos[0],pos[1]);

        mSearchMeasure=new PathMeasure();
        mSearchMeasure.setPath(mSearchPath,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentState){
            case STATE_NORMAL:
                canvas.drawPath(mSearchPath,mPaint);
                break;
            case STATE_START:
            case STATE_STOP:
                //按值截取path
                mSearchTempPath.reset();
                mSearchMeasure.getSegment(mSearchMeasure.getLength()*mAnimatorValue,mSearchMeasure.getLength(),mSearchTempPath,true);
                canvas.drawPath(mSearchTempPath,mPaint);
                break;
            case STATE_SEARCHING:
                mCircleTempPath.reset();
                //距离中点越近，path越长，最长1/4path，具体计算过程有简化
                mCircleMeasure.getSegment((float)(mCircleMeasure.getLength()*(mAnimatorValue-(1f/2*(0.5-Math.abs(mAnimatorValue-0.5))))),
                        mCircleMeasure.getLength()*mAnimatorValue,mCircleTempPath,true);
                canvas.drawPath(mCircleTempPath,mPaint);
                break;
            default:
                break;
        }
    }


    public void startLoading(){
        mIsGoingStop=false;
        //进行中状态直接返回，不用重新开始动画
        if (mCurrentState==STATE_SEARCHING || mCurrentState==STATE_START){
            return;
        }
        //静止状态，开始动画
        if (mCurrentState==STATE_NORMAL){
            mSearchingAnimator.cancel();
            mStopAnimator.cancel();
            mStartAnimator.start();
        }

        //结束动作进行中，直接开始loading状态
        if (mCurrentState==STATE_STOP){
            mStopAnimator.cancel();
            mStartAnimator.cancel();
            mSearchingAnimator.cancel();
            mSearchingAnimator.start();
        }
    }


    public void stopLoading(){
        //静止和停止中状态，直接返回
        if (mCurrentState==STATE_NORMAL || mCurrentState==STATE_STOP){
            return;
        }

        //搜索状态，直接进行退出动画
        if (mCurrentState==STATE_SEARCHING){
            mIsGoingStop=true;
        }

        //开始状态进行中，直接重置回静止状态
        if (mCurrentState==STATE_START){
            mStopAnimator.cancel();
            mStartAnimator.cancel();
            mSearchingAnimator.cancel();
            mCurrentState=STATE_NORMAL;
        }
    }


}
