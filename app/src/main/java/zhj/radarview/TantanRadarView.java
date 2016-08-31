package zhj.radarview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by HongJay on 2016/8/30.
 */
public class TantanRadarView extends View {
    //获取控件宽高，用于画圆的坐标位置以及半径
    private int w, h;
    private Bitmap mBitmap;
    private Bitmap mRadarBitmap;
    //缩放比例的数组
    private float[] s = {1.05f, 1.1f, 1.15f, 1.2f, 1.25f, 1.2f, 1.1f, 1.0f, 0.9f, 0.8f, 0.75f, 0.8f, 0.9f, 1.0f};
    //图片缩放的下标
    private int mScaleIndex = 0;
    private int mRadarBitmapWidth;
    private int mRadarBitmapHeight;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float x;
    private float y;
    private Boolean isTouch;
    //存放圆环的集合
    private ArrayList<Wave> mList;
    private Matrix mMatrix;
    //开始的角度
    private int start = 0;

    private Handler mHandler = new Handler();

    Runnable run = new Runnable() {

        @Override
        public void run() {
            start+=5;
            //刷新UI
            postInvalidate();
            //如果到了360度，则重新开始
            start = start == 360 ? 0 : start;
            //延迟执行
            postDelayed(this, 50);
        }
    };


    public TantanRadarView(Context context) {
        this(context, null);
    }

    public TantanRadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TantanRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mList = new ArrayList<Wave>();
        //提交计划任务马上执行
        mHandler.post(run);
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        mRadarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radar);
        mRadarBitmapWidth = mRadarBitmap.getWidth();
        mRadarBitmapHeight = mRadarBitmap.getHeight();

        //初始化矩阵
        mMatrix = new Matrix();

        isTouch = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        w = getMeasuredWidth();//获取view的宽度
        h = getMeasuredHeight();//获取view的高度

        //开始扫描
        startRadar(canvas);
        startWave(canvas);

        if (isTouch) {
            touchBitmapEvent(canvas);
        } else {
            canvas.drawBitmap(mBitmap, w / 2 - mBitmapWidth / 2, h / 2 - mBitmapHeight / 2, null);
        }

    }

    //绘制波纹
    private void startWave(Canvas canvas) {
        //避免程序一运行就进行绘制
        if (mList.size() > 0) {

            //对集合中的圆环对象循环绘制
            for (Wave wave : mList) {
                canvas.drawCircle(wave.x, wave.y, wave.radius, wave.paint);
                wave.radius += 3;
                //对画笔透明度进行操作
                int alpha = wave.paint.getAlpha();
                if (alpha < 160) {
                    alpha = 0;
                } else {
                    alpha -= 2;
                }
                //设置画笔宽度和透明度
                wave.paint.setStrokeWidth(wave.width-wave.radius / 30);
                wave.paint.setAlpha(alpha);

            }
        }
    }

    //雷达扫描
    private void startRadar(Canvas canvas) {

        //矩阵执行队列创建
        mMatrix.setRotate(start, mRadarBitmap.getWidth() / 2, mRadarBitmap.getHeight() / 2);
        mMatrix.postScale(1.3f, 1.3f);
        mMatrix.postTranslate(w / 2 - mRadarBitmapWidth / 2 * 1.3f, h / 2 - mRadarBitmapHeight / 2 * 1.3f);

        //对图片进行操作
        canvas.drawBitmap(mRadarBitmap, mMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //在图片的范围内点击
                if (x > w / 2 - mBitmapWidth / 2 && x < w / 2 + mBitmapWidth / 2
                        && y > h / 2 - mBitmapHeight / 2 && y < h / 2 + mBitmapHeight / 2) {
                    //波纹的圆心固定
                    x = w / 2;
                    y = h / 2;
                    deleteItem();
                    Wave wave = new Wave(x, y);
                    mList.add(wave);

                    isTouch = true;
                    mScaleIndex=0;
                    //刷新界面
                    invalidate();
                }
                break;
        }
        return true;
    }
    private void deleteItem() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).paint.getAlpha() == 0) {
                mList.remove(i);
            }
        }
    }

    //图片缩放动画
    private void touchBitmapEvent(Canvas canvas) {
        if (isTouch) {

            if (mScaleIndex < s.length) {
                //点击对图片进行缩放处理
                Matrix matrix = new Matrix();
                matrix.setScale(s[mScaleIndex], s[mScaleIndex], mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
                matrix.postTranslate(getWidth() / 2 - mBitmap.getWidth() / 2, getHeight() / 2 - mBitmap.getHeight() / 2);
                canvas.drawBitmap(mBitmap, matrix, null);
                mScaleIndex++;
            } else {
                canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmapWidth / 2, getHeight() / 2 - mBitmapHeight / 2, null);
                isTouch = false;
            }
        }
    }
}
