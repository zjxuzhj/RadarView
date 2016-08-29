package zhj.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by HongJay on 2016/8/28.
 */
public class RadarView4 extends View {

    //获取控件宽高，用于画圆的坐标位置以及半径
    private int w, h;
    //定义两种画笔
    private Paint mCirclePaint;
    private Paint mShaderPaint;
    private Matrix mMatrix;
    //开始的角度
    private int start = 0;
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
    private Shader mShader;

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


    @Override
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

        canvas.concat(mMatrix);
        //画一个扫描图像
        canvas.drawCircle(w / 2, h / 2, w / 3, mShaderPaint);
    }
}
