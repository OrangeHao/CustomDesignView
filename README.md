

### 核心代码

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