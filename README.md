####自定义雷达扫描控件
效果展示

![效果图](http://upload-images.jianshu.io/upload_images/1877523-63345cdff18b9a2d.gif?imageMogr2/auto-orient/strip)


###涉及知识点
---
####Shader类
在Android中，提供了Shader类专门用来渲染图像以及一些几何图形。
- 使用Shader类进行图像渲染时，首先需要构建Shader对象，然后通过Paint的setShader()方法来设置渲染对象，最后将这个Paint对象绘制到屏幕上即可。
- Shader类包括了5个直接子类
 - BitmapShader 图像渲染
 - ComposeShader 混合渲染
 - LinearGradient 线性渲染
 - RadialGradient 环形渲染
 - SweepGradient 梯度渲染
SweepGradient也称为扫描渲染，是指在某一中心以x轴正方向逆时针旋转一周而形成的扫描效果的渲染形式。
 ``` 
//坐标(cx,cy)决定了中心点的位置，会绕着该中心点进行360度旋转。color0表示的是起点的颜色位置，color1表示的是终点的颜色位置。
public SweepGradient(float cx, float cy, int[] colors, float[] positions)
public SweepGradient(float cx, float cy, int color0, int color1)
 ```

[图像渲染的详细介绍](http://www.cnblogs.com/menlsh/archive/2012/12/09/2810372.html)
 

####Matrix类
Matrix是一个矩阵，主要功能是坐标映射，数值转换。
- Matrix类有四种基本变换
 - 平移 setTranslate()； 平移意味着在x轴和y轴上简单地移动图像。
 - 缩放 setScale();  它采用两个浮点数作为参数，分别表示在每个轴上所产生的缩放量。
 - 旋转 setRotate(); 它采用一个浮点数表示旋转的角度。围绕默认点(0,0)，正数将顺时针旋转图像，而负数将逆时针旋转图像，其中默认点是图像的左上角。
 - 错切 setSkew();   对于错切变换，在数学上又称为Shear mapping(可译为“剪切变换”)或者Transvection(缩并)，它是一种比较特殊的线性变换。
 

[Matrix原理](https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B09%5DMatrix_Basic.md)
 

####实现思路
---
1. 新建RadarView4类继承View
2. 重写onDraw()方法，画四个无锯齿空心圆环，两条直线
4. 画以最大圆为半径的实心渐变圆
5. 创建矩阵，旋转画布，重绘，并用Handler实现循环

####1. 初始化数据
---
```
public RadarView4(Context context) {
        this(context, null);
    }
 
    public RadarView4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public RadarView4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
 
        //为了避免在onDraw中重复创建对象，所以将一些初始化工作放入构造方法中来做
        init();
 
        //提交计划任务马上执行
        mHandler.post(run);
    }
 
    private void init() {
        //创建圆环画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.GRAY);
        //设置画笔的宽度
        mCirclePaint.setStrokeWidth(3);
        //设置抗锯齿模式
        mCirclePaint.setAntiAlias(true);
        //设置画笔风格
        mCirclePaint.setStyle(Paint.Style.STROKE);
 
        //创建扫描线画笔
        mShaderPaint = new Paint();
        mShaderPaint.setAntiAlias(true);
        //设置画笔风格为填充模式
        mShaderPaint.setStyle(Paint.Style.FILL);
    }

```
####2. 开始绘制
---

```
protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        w = getMeasuredWidth();//获取view的宽度
        h = getMeasuredHeight();//获取view的高度
 
        //以中点为圆心
        canvas.drawCircle(w / 2, h / 2, w / 12, mCirclePaint);
        canvas.drawCircle(w / 2, h / 2, w / 6, mCirclePaint);
        canvas.drawCircle(w / 2, h / 2, w / 4, mCirclePaint);
        canvas.drawCircle(w / 2, h / 2, w / 3, mCirclePaint);
        //画两条直线
        canvas.drawLine(w / 2 - w / 3, h / 2, w / 2 + w / 3, h / 2, mCirclePaint);
        canvas.drawLine(w / 2, h / 2 - w / 3, w / 2, h / 2 + w / 3, mCirclePaint);
 
        //避免重复创建对象
        if (mShader == null)
            //新建扫描渲染，扫描边由透明->红色进行渐变
            mShader = new SweepGradient(w / 2, h / 2, Color.TRANSPARENT, getResources().getColor(R.color.RED));
 
        //设置渲染对象
        mShaderPaint.setShader(mShader);
        //指定画布的当前矩阵
        canvas.concat(mMatrix);
        //画一个扫描图像
        canvas.drawCircle(w / 2, h / 2, w / 3, mShaderPaint);
    }

```

####3.通过Handler循环绘制实现转动
---

```
private Handler mHandler = new Handler();
Runnable run = new Runnable() {
 
  @Override
   public void run() {
      start++;
      mMatrix = new Matrix();
      //为矩阵设置旋转坐标
      mMatrix.setRotate(start, w / 2, h / 2);
      //刷新ui
      postInvalidate();
      //如果到了360度，则重新开始
      start = start == 360 ? 0 : start;
      //延迟执行
      postDelayed(this, 10);
    }
};

```


这里是[项目地址](https://github.com/zjxuzhj/RadarView)。

参考
http://blog.csdn.net/itjianghuxiaoxiong/article/details/50207009
http://www.jianshu.com/p/4918034e3f0e#
http://blog.csdn.net/sahadev_/article/details/50432764
