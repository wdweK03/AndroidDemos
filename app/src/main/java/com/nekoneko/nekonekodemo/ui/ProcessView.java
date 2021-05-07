package com.nekoneko.nekonekodemo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.nekoneko.nekonekodemo.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 想法的猫 on 2017/8/15 0015.
 */

public class ProcessView extends View implements Runnable {
    String TAG = "ProcessView";
    Path path = new Path();
    Paint paint = new Paint();
    int startAngle = 0;
    float sweepAngle = 0;
    final float ANGLE_STEP = 4;
    float step;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    boolean stopProcess;
    RectF rectF;
    int processBarColor;
    float processSize;

    public ProcessView(Context context) {
        super(context);
        initPaint();
    }

    public ProcessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProcessView);
        processSize = typedArray.getDimension(R.styleable.ProcessView_processBarSize, 2);
        processBarColor = typedArray.getColor(R.styleable.ProcessView_processBarColor, Color.DKGRAY);
        initPaint();
    }

    public ProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float paintSize = paint.getStrokeWidth();
        rectF = new RectF(paintSize, paintSize, getWidth() - paintSize, getHeight() - paintSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.addArc(rectF, startAngle, sweepAngle);
        canvas.drawPath(path, paint);
    }

    private void initPaint() {
        paint.setStrokeWidth(processSize);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(processBarColor);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        stopProcess = false;
        executorService.execute(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopProcess = true;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        stopProcess = visibility != VISIBLE;//不可见时都停止
        if (!stopProcess) {
            executorService.execute(this);
        } else {
            startAngle = 0;
            sweepAngle = 0;
        }
    }

    @Override
    public void run() {
        while (!stopProcess) {
            try {
                startAngle += ANGLE_STEP;
                if (sweepAngle <= 0) {
                    step = ANGLE_STEP;
                } else if (sweepAngle >= 300) {
                    step = -ANGLE_STEP;
                }
                if (step <= 0) {
                    startAngle += ANGLE_STEP * 2;
                    sweepAngle -= ANGLE_STEP * 3;
                } else {
                    sweepAngle += step;
                }
                postInvalidate();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
