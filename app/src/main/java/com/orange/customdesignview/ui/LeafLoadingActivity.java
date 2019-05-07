package com.orange.customdesignview.ui;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.orange.customdesignview.R;
import com.orange.customdesignview.medium.LeafLoadingView;

public class LeafLoadingActivity extends AppCompatActivity {

    private LeafLoadingView mView;
    private Button resetBtn;
    private Button startBtn;
    private Button stopBtn;
    private ValueAnimator animator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaf_loading);
        mView=findViewById(R.id.mTestView);
        resetBtn=findViewById(R.id.resetBtn);
        startBtn=findViewById(R.id.startBtn);
        stopBtn=findViewById(R.id.stopBtn);

        animator=ValueAnimator.ofFloat(100);
        animator.setDuration(7000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mView.setProgress((Float) animation.getAnimatedValue());
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.resume();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.pause();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.cancel();
                mView.reset();
                animator.start();
            }
        });
    }
}
