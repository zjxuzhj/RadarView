package zhj.radarview;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by HongJay on 2016/8/31.
 */
public class Wave {
    public float x;//圆心x坐标
    public float y;//圆心y坐标
    public Paint paint; //画圆的画笔
    public float width; //线条宽度
    public int radius; //圆的半径



    public Wave(float x, float y) {
        this.x = x;
        this.y = y;
        initData();
    }
    /**
     * 初始化数据,每次点击一次都要初始化一次
     */
    private void initData() {
        paint=new Paint();//因为点击一次需要画出不同的圆环
        paint.setAntiAlias(true);//打开抗锯齿
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//描边
        paint.setStrokeWidth(width);//设置描边宽度
        paint.setAlpha(255);//透明度的设置(0-255),0为完全透明
        radius=50;//控制波纹的半径为图片半径
        width=7;
    }
}
