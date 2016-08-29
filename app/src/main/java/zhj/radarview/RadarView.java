package zhj.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by HongJay on 2016/8/28.
 */
public class RadarView extends View implements Runnable{
    private boolean threadFlag = true;
    private int rotate = 0;
    //用于画圆的画笔
    private Paint circlePaint;
    //用于画扫描图像
    private Paint shaderPaint;
    //获得用于画圆的坐标位置以及半径
    int x, y;
    //设置扫描图像的坐标矩阵
    Matrix matrix = new Matrix();
    //用于绘制扫描图像
    Shader mShader;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //为了避免在onDraw中重复创建对象，所以将一些初始化工作放入构造方法中来做

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        //设置画笔的宽度
        circlePaint.setStrokeWidth(1);
        //设置抗锯齿模式
        circlePaint.setAntiAlias(true);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //设置画笔风格
        circlePaint.setStyle(Paint.Style.STROKE);

        shaderPaint = new Paint();
        shaderPaint.setAntiAlias(true);
        shaderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //设置画笔风格为填充模式
        shaderPaint.setStyle(Paint.Style.FILL);

        postDelayed(this, 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算圆的坐标值及半径
        y = getMeasuredHeight() / 2;
        x = getMeasuredWidth() / 2;

        //为矩阵设置旋转坐标
        matrix.setRotate(rotate, x, y);

        //为了避免重复创建对象，则使用这种方式
        if (mShader == null)
            mShader = new SweepGradient(x, y, Color.TRANSPARENT, Color.BLUE);

        mShader.setLocalMatrix(matrix);
        shaderPaint.setShader(mShader);

        //画一个扫描图像
        canvas.drawCircle(x, y, x, shaderPaint);

        //画四个等距圆
//        canvas.drawCircle(x, y, x, circlePaint);
        canvas.drawCircle(x, y, x / 2, circlePaint);
//        canvas.drawCircle(x, y, x / 4 * 3, circlePaint);
        canvas.drawCircle(x, y, x / 4, circlePaint);
    }

    @Override
    public void run() {
        if (threadFlag) {
            rotate++;
            postInvalidate();
            //如果到了360度，则重新开始
            rotate = rotate == 360 ? 0 : rotate;
            //一秒延迟这个任务
            postDelayed(this, 1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //停止循环
        threadFlag = false;
    }
}
