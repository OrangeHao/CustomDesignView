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
import android.util.Log;
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
    private final int DEFAULT_STROKE_WIDTH = 10;
    private final int DEFAULT_VIEW_WIDTH = 50;

    //进入搜索状态的动画时间
    private final int DURATION_START_STOP=300;
    private final int DURATION_SEARCHING_PER_CIRCLE=200;

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


    private Paint mPaint;
    private Path mSearchPath;
    private Path mSearchTempPath;
    private Path mCirclePath;
    private Path mCircleTempPath;
    private PathMeasure mMeasure;

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
        mStartAnimator=ValueAnimator.ofFloat(0,1).setDuration(DURATION_START_STOP);
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

            }

            @Override
            public void onAnimationCancel(Animator animation) {

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

        float radiusMax=Math.min(mWidht,mHeight)/2f;
        float radiusSmall=radiusMax/2f-mStrokeWidth/2f;
        float radiusBig=radiusMax-mStrokeWidth/2f;
        mMeasure=new PathMeasure();

        mSearchPath=new Path();
        RectF rectFSmall=new RectF(mWidht/2f-radiusSmall,mHeight/2f-radiusSmall,mWidht/2f+radiusSmall,mHeight/2f+radiusSmall);
        mSearchPath.addArc(rectFSmall,45,359.9f);

        mCirclePath=new Path();
        RectF rectFBig=new RectF(mWidht/2f-radiusBig,mHeight/2f-radiusBig,mWidht/2f+radiusBig,mHeight/2f+radiusBig);
        mCirclePath.addArc(rectFBig,45,359.9f);

        mMeasure.setPath(mCirclePath,false);
        float[]pos=new float[2];
        mMeasure.getPosTan(0,pos,null);

        //或者：Math.cos(45*(Math.PI/180))*radiusBig;Math.sin(45*(Math.PI/180))*radiusBig;
        mSearchPath.lineTo(pos[0],pos[1]);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.translate(mWidht/2f,mHeight/2f);
        switch (mCurrentState){
            case STATE_NORMAL:
                canvas.drawPath(mSearchPath,mPaint);
                break;
            case STATE_START:
            case STATE_STOP:
                mMeasure.setPath(mSearchPath,false);
                mSearchTempPath.reset();
                mMeasure.getSegment(mMeasure.getLength()*mAnimatorValue,mMeasure.getLength(),mSearchTempPath,true);
                canvas.drawPath(mSearchTempPath,mPaint);
                break;
            case STATE_SEARCHING:

                break;
            default:
                break;
        }
    }


    public void startLoading(){
        mStartAnimator.start();
    }


    public void stopLoading(){

    }






}
