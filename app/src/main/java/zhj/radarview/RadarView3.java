package zhj.radarview;

import android.annotation.SuppressLint;
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
@SuppressLint("DrawAllocation")
public class RadarView3 extends View {
    private int w, h;// 获取控件宽高
    private Paint mPaintLine;// 画雷达圆线
    private Paint mPaintSolid;// 画雷达渐变实心圆
    private Matrix matrix;
    private int degrees;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            degrees++;
            matrix.postRotate(degrees, w / 2, h / 2);//旋转矩阵
            RadarView3.this.invalidate();// 重绘
            mHandler.postDelayed(mRunnable, 55);
        }
    };

    public RadarView3(Context context) {
        this(context, null);
    }

    public RadarView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setBackgroundResource(R.drawable.radar_bg);//雷达的背景图片(紫色满天星，可以在微信APP中直接找到图片资源)
        initPaint();
        mHandler.postDelayed(mRunnable,500);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = getMeasuredWidth();//获取view的宽度
        h = getMeasuredHeight();//获取view的高度
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#CCA1A1A1"));// 设置画笔
        mPaintLine.setStrokeWidth(1);// 设置画笔宽度
        mPaintLine.setAntiAlias(true);// 消除锯齿
        mPaintLine.setStyle(Paint.Style.STROKE);// 设置空心

        mPaintSolid = new Paint();
        mPaintSolid.setAntiAlias(true);// 消除锯齿
        mPaintSolid.setStyle(Paint.Style.FILL);//实心圆
        matrix = new Matrix();//创建组件
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //四个空心圆
        canvas.drawCircle(w / 2, h / 2, w / 6, mPaintLine);
        canvas.drawCircle(w / 2, h / 2, 5 * w / 14, mPaintLine);
        canvas.drawCircle(w / 2, h / 2, 12 * w / 20, mPaintLine);
        canvas.drawCircle(w / 2, h / 2, 9 * w / 11, mPaintLine);

        //渐变
        Shader mShader = new SweepGradient(w / 2, h / 2, Color.TRANSPARENT, Color.parseColor("#33FFFFFF"));
        mPaintSolid.setShader(mShader);
        canvas.setMatrix(matrix);
        canvas.drawCircle(w / 2, h / 2, 9 * w / 11, mPaintSolid);
        matrix.reset();//重置矩阵，避免累加，越转越快
        super.onDraw(canvas);
    }

}