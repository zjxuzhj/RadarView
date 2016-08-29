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
public class RadarView2 extends View {
    private int w;
    private int h;
    private Paint mPaint;
    private Paint radarPaint;
    private Matrix mMatrix;
    private int start = 0;
    private Handler mHandler = new Handler();

    Runnable run = new Runnable() {

        @Override
        public void run() {
            start += 5; // 每次旋转5度
            mMatrix = new Matrix();
            mMatrix.setRotate(start, w / 2, h / 2);
            RadarView2.this.invalidate();
            mHandler.postDelayed(run, 20); // 隔20ms重复执行
        }
    };

    public RadarView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        w = this.getResources().getDisplayMetrics().widthPixels;
        h = this.getResources().getDisplayMetrics().heightPixels;

        initPaint();
        mHandler.post(run); // 开始执行
    }

    private void initPaint() {
        mPaint = new Paint(); // 创建圆环画笔
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setColor(Color.parseColor("#A1A1A1"));
        mPaint.setStyle(Paint.Style.STROKE);

        radarPaint = new Paint(); // 创建扫描线画笔
        radarPaint.setAntiAlias(true);
        Shader shader = new SweepGradient(w / 2, h / 2, Color.TRANSPARENT, Color.parseColor("#AA000000")); // 扫描边由透明->#AA000000进行渐变
        radarPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制圆环。这些宽度高度设置仅供参考
        canvas.drawCircle(w / 2, h / 2, w / 12, mPaint);
        canvas.drawCircle(w / 2, h / 2, 2 * w / 12, mPaint);
        canvas.drawCircle(w / 2, h / 2, 11 * w / 40, mPaint);
        canvas.drawCircle(w / 2, h / 2, h / 4, mPaint);

        // 绘制扫描线
        canvas.concat(mMatrix);
        canvas.drawCircle(w / 2, h / 2, h / 4, radarPaint);

        super.onDraw(canvas);
    }
}
