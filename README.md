

### 核心代码（其他关键地方基本都写了注释）

#### 随机生成不一样的正弦轨迹函数，每一片出现的叶子的运动轨迹都不一样

```java
        //正弦函数y=Asin(ωx+φ)+h  A振幅  ω角频率 φ初相位 h上下偏移量
        //y轴原点在view的左上角
        //w = (float) ((float) 2 * Math.PI / mProgressWidth); 一个周期
        //φ:0~2PI
        //H:view高度，A和h满足：1/4H<h<3/4H,h-A>0,h+A<H
        
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
```

### 截图

<img src="https://github.com/OrangeHao/CustomDesignView/blob/master/screenshot/leafLoading.gif"  height="50%" width="50%" >



### 一个动态的搜索view，提供了开始和停止两个动作

#### 核心代码如下，其他难点都有注释

```java

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
        
        
```

### 截图

<img src="https://github.com/OrangeHao/CustomDesignView/blob/master/screenshot/searchingView.gif"  height="50%" width="50%" >