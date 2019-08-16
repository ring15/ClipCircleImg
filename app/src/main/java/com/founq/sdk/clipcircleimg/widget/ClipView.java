package com.founq.sdk.clipcircleimg.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ring on 2019/8/14.
 */
public class ClipView extends View {

    private Paint mPaint;

    private Paint mBorderPaint;

    private int mWidth, mHeight;

    private Xfermode mXfermode;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(3);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0, 0, mWidth, mHeight, null);
        } else {
            canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        }

        canvas.drawColor(Color.parseColor("#7f000000"));

        mPaint.setXfermode(mXfermode);

        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth < mHeight) ? mWidth * 2 / 9 : mHeight * 2 / 9, mPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth < mHeight) ? mWidth * 2 / 9 + 3 : mHeight * 2 / 9 + 3, mBorderPaint);
        canvas.restore();
    }
}
