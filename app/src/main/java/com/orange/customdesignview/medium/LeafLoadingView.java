package com.orange.customdesignview.medium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

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

    private Paint mWhitePaint;
    private int mTotalWith,mToalHeight;
    private int mBgRadius;



    private void init(){
        initPaints();
    }

    private void initPaints(){
        mWhitePaint=new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setAntiAlias(true);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWith=w;
        mToalHeight=h;
        mBgRadius=mToalHeight/2;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteBg(canvas);
    }


    private void drawWhiteBg(Canvas canvas){
        RectF rectf=new RectF(0,0,mTotalWith,mToalHeight);
        canvas.drawRoundRect(rectf,mBgRadius,mBgRadius,mWhitePaint);
    }


}
