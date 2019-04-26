package com.orange.customdesignview.medium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.orange.customdesignview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * created by czh on 2019/4/20
 */
public class LeafLoadingView extends View {

    public LeafLoadingView(Context context) {
        super(context);
        init();
    }

    public LeafLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeafLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //生成的树叶数目
    private final int LEAF_MAX_NUMS=8;
    //树叶飘动的周期长度
    private final int LEAF_ANIMATE_TIME=3000;
    //风扇框的宽度
    private final float CIRCLE_WIDTH=8f;
    //风扇的旋转速度
    private int fanSpeed=3;

    //背景paint
    private Paint mBgPaint;
    //风扇外边的白色圆圈
    private Paint mWhitePaint;
    //树叶绘制
    private Paint mBitmapPiant;

    private Bitmap mLeafBitmap;
    private Drawable mFanDrawable;

    private int mTotalWith,mToalHeight;
    private int mBgRadius;

    private float mFanRotateeDegree=0;

    private List<Leaf> mLeafs;



    private void init(){
        initPaints();
        initBitmap();

        mLeafs=createLeafs(LEAF_MAX_NUMS);
    }

    private void initPaints(){
        mBgPaint=new Paint();
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setAlpha(180);

        mWhitePaint=new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setStyle(Paint.Style.STROKE);
        mWhitePaint.setStrokeWidth(CIRCLE_WIDTH);

        mBitmapPiant=new Paint();
        mBitmapPiant.setAntiAlias(true);
        //防抖动,绘制更柔和
        mBitmapPiant.setDither(true);
        //如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度，本设置项依赖于dither和xfermode的设置。
        mBitmapPiant.setFilterBitmap(true);
    }

    private void initBitmap(){
        mLeafBitmap=((BitmapDrawable)getContext().getResources().getDrawable(R.drawable.leaf)).getBitmap();
        mFanDrawable=getContext().getResources().getDrawable(R.drawable.fengshan);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWith=w;
        mToalHeight=h;
        mBgRadius=mToalHeight/2;

        int fanPadding=(int) CIRCLE_WIDTH*3;
        Rect rect=new Rect(mTotalWith-2*mBgRadius+fanPadding,fanPadding,mTotalWith-fanPadding,mToalHeight-fanPadding);
        mFanDrawable.setBounds(rect);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteBg(canvas);
        drawWhiteCircle(canvas);
        drawFan(canvas);
        postInvalidate();
    }


    private void drawWhiteBg(Canvas canvas){
        RectF rectf=new RectF(0,0,mTotalWith,mToalHeight);
        canvas.drawRoundRect(rectf,mBgRadius,mBgRadius,mBgPaint);
    }

    private void drawWhiteCircle(Canvas canvas){
        canvas.drawCircle(mTotalWith-mBgRadius,mBgRadius,mBgRadius-CIRCLE_WIDTH,mWhitePaint);
    }


    private void drawFan(Canvas canvas){
        int rotateSaveCount=canvas.save();
        canvas.rotate(mFanRotateeDegree,mTotalWith-mBgRadius,mBgRadius);
        mFanDrawable.draw(canvas);
        canvas.restoreToCount(rotateSaveCount);
        mFanRotateeDegree+=fanSpeed;
        if (mFanRotateeDegree>360){
            mFanRotateeDegree=fanSpeed;
        }
    }

    private void drawLeafs(){

    }

    private void updateLeafByTime(long time,Leaf leaf){

    }

    private void updateLeafLocation(Leaf leaf){
        //正弦函数y=Asin(ωx+φ)+h  A振幅  ω角频率 φ初相位 h上下偏移量

    }

    private List<Leaf> createLeafs(int maxNum){
        List<Leaf> tempList=new ArrayList<>();
        for (int i = 0; i < maxNum; i++) {
            tempList.add(new Leaf(LEAF_ANIMATE_TIME));
        }
        return tempList;
    }

    private class Leaf{
        float x,y;
        //旋转角度
        int rotateDegree;
        //旋转方向
        int rotateDirection;

        long startTime;

        public Leaf(int animateTime){
            Random random=new Random();
            this.rotateDegree=random.nextInt(360);
            this.rotateDirection=random.nextInt(2);
            //把树叶随机分布在一个飘动周期内,出场时间
            this.startTime=System.currentTimeMillis()+random.nextInt(animateTime);
        }
    }



}
