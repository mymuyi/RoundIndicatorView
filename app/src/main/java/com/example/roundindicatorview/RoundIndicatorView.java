package com.example.roundindicatorview;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by 木易 on 2017/10/18.
 */

public class RoundIndicatorView extends View{

    /**
     * 圆弧开始的角度
     */
    private final static int SRARTANGLE = 155;

    /**
     * 圆弧扫过的角度
     */

    private final static int SWEEPANGLE = 230;

    private  String[] states= {"较差", "中等", "良好", "优秀", "极好"};

    /**
     * 渐变色数组
     */
    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    /**
     * 最大数值
     */
    private int maxNum;

    /**
     * 目前的数值
     */
    private int currentNum;

    /**
     * 内圆弧宽度
     */
    private int arcInWidth;

    /**
     * 外圆弧宽度
     */
    private int arcOutWidth;

    /**
     * 内圆弧半径
     */
    private int inRadius;

    /**
     * 外圆弧半径
     */
    private int outRadius;

    private Paint paint;

    private RectF inRectF;

    private RectF outRectF;

    public RoundIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取属性
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicatorView);
        maxNum = t.getInt(R.styleable.RoundIndicatorView_maxNum, 800);
        currentNum = t.getInt(R.styleable.RoundIndicatorView_currentNum, 250);
        arcInWidth = t.getInt(R.styleable.RoundIndicatorView_arcInWidth, 8);
        arcOutWidth = t.getInt(R.styleable.RoundIndicatorView_arcOutWidth, 3);

        // 初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 300;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 300;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAlpha(60);
        // 内外圆弧的距离
        int distance = 20 + arcOutWidth/2 + arcInWidth/2;

        // 以宽度的 3/10 为半径画内圆弧
        paint.setStrokeWidth(arcInWidth);
        inRadius = getWidth() * 3/10;
        inRectF = new RectF(getWidth()/2 - inRadius, getHeight()/2 - inRadius,
                getWidth()/2 + inRadius , getHeight()/2 + inRadius);
        canvas.drawArc(inRectF, SRARTANGLE, SWEEPANGLE, false, paint);

        // 画外圆弧
        paint.setStrokeWidth(arcOutWidth);
        outRadius = inRadius + distance;
        outRectF = new RectF(getWidth()/2 - outRadius, getHeight()/2 - outRadius,
                getWidth()/2 + outRadius , getHeight()/2 + outRadius);
        canvas.drawArc(outRectF, SRARTANGLE, SWEEPANGLE, false, paint);

        // 画刻度
        paint.setStrokeWidth(4);
        paint.setAlpha(100);
        // 保存画布状态
        int save1 = canvas.save();
        int save2 = canvas.save();
        // 刻度间隔
        float angle = (float) SWEEPANGLE/30;
        // 将画布旋转，从第一个刻度画起。
        canvas.rotate(-SWEEPANGLE/2, getWidth()/2, getHeight()/2);

        for (int i=0; i<31; i++) {
            // 细刻度
            if (i%6 != 0) {
                canvas.drawLine(getWidth()/2, getHeight()/2 - inRadius - arcInWidth/2,
                        getWidth()/2, getHeight()/2 - inRadius + arcInWidth/2, paint);
            } else {   // 粗刻度，数字
                paint.setStrokeWidth(8);
                paint.setAlpha(150);
                canvas.drawLine(getWidth()/2, getHeight()/2 - inRadius - arcInWidth/2,
                        getWidth()/2, getHeight()/2 - inRadius + arcInWidth/2 + 8, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(35);
                // 测量数字的宽度
                float width = paint.measureText((i/6) * maxNum/5 + "");
                canvas.drawText((i/6) * maxNum/5 + "", getWidth()/2 - width/2,
                        getHeight()/2 - inRadius + arcInWidth/2 + 50, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAlpha(100);
                paint.setStrokeWidth(4);
            }
            // 信用
            if (i == 3 || i == 9 || i == 15 || i == 21 || i ==27) {
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(35);
                // 测量数字的宽度
                float width = paint.measureText("较差");
                canvas.drawText(states[i/6], getWidth()/2 - width/2,
                        getHeight()/2 - inRadius + arcInWidth/2 + 50, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAlpha(100);
                paint.setStrokeWidth(4);
            }
            canvas.rotate(angle, getWidth()/2, getHeight()/2);
        }

        // 渐变，小圆点
        paint.setStrokeWidth(arcOutWidth);
        // 还原画布状态
        canvas.restoreToCount(save2);
        // 当前的进度
        int sweep;
        if (currentNum < maxNum) {
            sweep = (int)((float) currentNum / (float)maxNum * SWEEPANGLE);
        } else {
            sweep = SWEEPANGLE;
        }

        // 绘制渐变
        Shader shader =new SweepGradient(0,0,indicatorColor,null);
        paint.setShader(shader);
        canvas.drawArc(outRectF, SRARTANGLE, sweep, false, paint);

        // 绘制圆点
        canvas.rotate(-SWEEPANGLE/2 + sweep, getWidth()/2, getHeight()/2);
        float x = (float) getWidth()/2;
        float y = (float) getHeight()/2 - outRadius;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffffffff);
        //需关闭硬件加速
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(x,y,10,paint);

        // 绘制中间的文字
        canvas.restoreToCount(save1);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        paint.setTextSize(140);
        String num = currentNum + "";
        Rect numBound = new Rect();
        paint.getTextBounds(num, 0, num.length(), numBound);
        canvas.drawText(num, getWidth()/2 - numBound.width()/2, getHeight()/2, paint);

        paint.setTextSize(80);
        String text = "信用" + states[currentNum/(maxNum/5 + 1)];
        Rect textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        canvas.drawText(text, getWidth()/2 - textBound.width()/2,
                getHeight()/2 + numBound.height(), paint);
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        //根据进度差计算动画时间
        float duration = (float)Math.abs(num-currentNum)/maxNum * 1500 + 1000;
        ObjectAnimator anim = ObjectAnimator.ofInt(this,"currentNum",num);
        anim.setDuration((long) Math.min(duration,2000));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: " + value);
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }

    private int calculateColor(int value) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if(value <= maxNum / 2){
            fraction = (float)value/(maxNum/2);
            //由红到橙
            color = (int) evaluator.evaluate(fraction,0xFFFF6347,0xFFFF8C00);
        }else {
            fraction = ( (float)value-maxNum/2 ) / (maxNum/2);
            //由橙到蓝
            color = (int) evaluator.evaluate(fraction,0xFFFF8C00,0xFF00CED1);
        }
        return color;
    }
}
