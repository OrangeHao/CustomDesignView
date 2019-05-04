package com.orange.customdesignview.medium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
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
    private final int LEAF_MAX_NUMS=6;
    //树叶飘动的周期长度
    private final int LEAF_ANIMATE_TIME=3000;
    //树叶旋转一周需要的时间
    private final int LEAF_ROTATE_TIME = 2000;
    //树叶绘制
    private Paint mBitmapPiant;
    private Bitmap mLeafBitmap;
    private int mLeafWidth;
    private int mLeafHeight;
    //树叶
    private List<Leaf> mLeafs;

    //风扇框的宽度
    private final float CIRCLE_WIDTH=8f;
    //风扇的旋转速度
    private int fanSpeed=3;
    //风扇旋转角度
    private float mFanRotateeDegree=0;
    //风扇外边的白色圆圈
    private Paint mWhitePaint;
    private Drawable mFanDrawable;

    //0~100
    private float mProgress=0f;
    private int mProgressPaint;
    //进度条离view边界的距离
    private float mProgressPadding=10f;

    //进度条长度
    private int mProgressWidth;

    //背景paint
    private Paint mBgPaint;
    private Paint mOrangePaint;
    //总宽度和长度
    private int mTotalWith,mToalHeight;
    //圆角半径
    private int mBgRadius;



    private void init(){
        initPaints();
        initBitmap();

    }

    private void initPaints(){
        mBgPaint=new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setAlpha(120);

        mOrangePaint=new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(Color.parseColor("#fed255"));

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
        mLeafWidth=mLeafBitmap.getWidth();
        mLeafHeight=mLeafBitmap.getHeight();
        mFanDrawable=getContext().getResources().getDrawable(R.drawable.fengshan);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWith=w;
        mToalHeight=h;
        mBgRadius=mToalHeight/2;
        mProgressWidth=mTotalWith-mBgRadius;

        int fanPadding=(int) CIRCLE_WIDTH*3;
        Rect rect=new Rect(mTotalWith-2*mBgRadius+fanPadding,fanPadding,mTotalWith-fanPadding,mToalHeight-fanPadding);
        mFanDrawable.setBounds(rect);


        mLeafs=createLeafs(LEAF_MAX_NUMS);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteBg(canvas);
        drawLeafs(canvas);
        drawProgressBar(canvas);
        drawWFanBg(canvas);
        drawFan(canvas);
//        drawLine(canvas);
        postInvalidate();
    }

    private void drawLine(Canvas canvas){
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawLine(0f,(float)0,(float)mTotalWith,(float)0,paint);
    }


    private void drawWhiteBg(Canvas canvas){
        RectF rectf=new RectF(0,0,mTotalWith,mToalHeight);
        canvas.drawRoundRect(rectf,mBgRadius,mBgRadius,mBgPaint);
    }


    private void drawProgressBar(Canvas canvas){
        RectF arcRect=new RectF(mProgressPadding,mProgressPadding,mProgressPadding+mBgRadius,mToalHeight-mProgressPadding);

        Path path=new Path();
        path.addArc(arcRect,90,180);
        canvas.drawPath(path,mOrangePaint);
    }

    private Path createProgressPath(float progress, float circleRadius, RectF progressRect) {
        RectF arcProgressRect = new RectF(progressRect.left, progressRect.top, progressRect.left + circleRadius * 2, progressRect.bottom);
        RectF rectProgressRect = null;

        float progressWidth = progress * progressRect.width();
        float progressModeWidth = mMode == MODE_LEAF_COUNT ?
                (float) mCurrentLeafCount / (float) LEAF_COUNT * progressRect.width() : progress * progressRect.width();

        float swipeAngle = DEGREE_180;
        //the left half circle of the progressbar
        if (progressModeWidth < circleRadius) {
            swipeAngle = progressModeWidth / circleRadius * DEGREE_180;
        }

        //the center rect of the progressbar
        if (progressModeWidth < progressRect.width() - circleRadius && progressModeWidth >= circleRadius) {
            rectProgressRect = new RectF(progressRect.left + circleRadius, progressRect.top, progressRect.left + progressModeWidth, progressRect.bottom);
        }

        //the right half circle of the progressbar
        if (progressWidth >= progressRect.width() - circleRadius) {
            rectProgressRect = new RectF(progressRect.left + circleRadius, progressRect.top, progressRect.right - circleRadius, progressRect.bottom);
            mScale = (progressRect.width() - progressWidth) / circleRadius;
        }

        //the left of the right half circle
        if (progressWidth < progressRect.width() - circleRadius) {
            mRotation = (progressWidth / (progressRect.width() - circleRadius)) * FULL_GROUP_ROTATION % DEGREE_360;

            RectF leafRect = new RectF(progressRect.left + progressWidth, progressRect.top, progressRect.right - circleRadius, progressRect.bottom);
            addLeaf(progress, leafRect);
        }

        Path path = new Path();
        path.addArc(arcProgressRect, DEGREE_180 - swipeAngle / 2, swipeAngle);

        if (rectProgressRect != null) {
            path.addRect(rectProgressRect, Path.Direction.CW);
        }

        return path;
    }

    private void drawWFanBg(Canvas canvas){
        canvas.drawCircle(mTotalWith-mBgRadius,mBgRadius,mBgRadius-CIRCLE_WIDTH/2,mWhitePaint);
        canvas.drawCircle(mTotalWith-mBgRadius,mBgRadius,mBgRadius-CIRCLE_WIDTH,mOrangePaint);
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

    private void drawLeafs(Canvas canvas){
        long currentTime=System.currentTimeMillis();
        for (Leaf leaf:mLeafs){
            //通过时间控制leaf的出现
            long timePass=currentTime-leaf.startTime;
            //未来周期，leaf还没到出场时间
            if(timePass<0){
                continue;
            //leaf周期中
            }else if(timePass<=LEAF_ANIMATE_TIME){
                leaf.updateLeafLocation(timePass);
                //draw leaf
                canvas.save();
                //移动
                Matrix matrix=new Matrix();
                matrix.postTranslate(leaf.x,leaf.y);
                //旋转角度=角速度*时间
                int angle=(int) ((360f/LEAF_ROTATE_TIME)*(timePass%LEAF_ROTATE_TIME));
                Log.d("czh","angle:"+angle);
                int rotate=leaf.rotateDirection==0?leaf.rotateAngle+angle:leaf.rotateAngle-angle;
                matrix.postRotate(rotate,leaf.x+mLeafWidth/2,leaf.y+mLeafHeight/2);
                canvas.drawBitmap(mLeafBitmap,matrix,mBitmapPiant);
                canvas.restore();
            //leaf周期完成，已过期
            }else{
                //更新leaf的轨迹函数和出生时间
                leaf.updateLoacationFactor();
            }

            if (leaf.startTime!=0 && currentTime>leaf.startTime){

            }
        }
    }

    private List<Leaf> createLeafs(int maxNum){
        List<Leaf> tempList=new ArrayList<>();
        for (int i = 0; i < maxNum; i++) {
            tempList.add(new Leaf());
        }
        return tempList;
    }

    private class Leaf{
        //正弦函数y=Asin(ωx+φ)+h  A振幅  ω角频率 φ初相位 h上下偏移量
        //y轴原点在view的左上角
        //w = (float) ((float) 2 * Math.PI / mProgressWidth); 一个周期
        //φ:0~2PI
        //H:view高度，A和h满足：1/4H<h<3/4H,h-A>0,h+A<H
        float x;
        float y;
        //旋转角度
        int rotateAngle;
        //旋转方向
        int rotateDirection;
        long startTime;

        int A;
        float w;
        float b;
        int h;

        public Leaf(){
            updateLoacationFactor();
        }

        //随机生成轨迹函数，更新叶子出生时间
        public void updateLoacationFactor(){
            Random random=new Random();
            int leafValueHeight=Math.max(mLeafHeight,mLeafWidth);
            //把树叶随机分布在一个飘动周期内,出场时间
            startTime=System.currentTimeMillis()+random.nextInt(LEAF_ANIMATE_TIME);
            rotateAngle=random.nextInt(360);
            rotateDirection=random.nextInt(2);

            //ω 随机半个周期的轨迹到1个半周期的轨迹 0.5~1.5
            float cycle=(random.nextInt(10)+5)/10f;
            w=(float) (cycle *2* Math.PI / mProgressWidth);

            //φ:0~2PI
            b=(float)(2* Math.PI*(random.nextInt(10)/10f));

            //叶子可动范围距离上下边的宽度
            float leafPadding=(mToalHeight)/6f;

            //振幅范围计算：1/3~3/3
            float maxA=mToalHeight/2-leafPadding;
            A=random.nextInt((int) (maxA/3))+(int) (maxA/3);

            float minh=leafPadding+A;
            //绘制时从坐标点向下绘制，所以多减去一个叶子的高度
            float maxh=mToalHeight-leafPadding-leafValueHeight-A;

            if (maxh<=minh){
                h=mToalHeight/2;
            }else {
                h=random.nextInt((int) (maxh-minh))+(int) minh;
            }
        }

        public void updateLeafLocation(long passTime){
            this.x=mProgressWidth*(1-(float)passTime/LEAF_ANIMATE_TIME);
            this.y=(float) (A* Math.sin(w*x+b)+h);
//            Log.d("czh",(y+mLeafBitmap.getHeight())+"/"+mToalHeight);
//            Log.d("czh","sin:"+A*Math.sin(w*x+b));
            if (y>mToalHeight){
                Log.d("czh",y+"/"+mToalHeight);
                Log.d("czh",A+"/"+h);
                Log.d("czh","A+h:"+(A+h)+"/"+mToalHeight);
                Log.d("czh","h-A:"+(h-A)+"/"+mToalHeight);
            }
        }
    }




}
