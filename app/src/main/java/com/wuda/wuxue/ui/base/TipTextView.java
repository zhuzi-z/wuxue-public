package com.wuda.wuxue.ui.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;

public class TipTextView extends View {

    public enum TIP_TYPE {
        TIP_INVISIBLE, TIP_VISIBLE, TIP_ERROR, TIP_OTHER_WEEK
    }

    TIP_TYPE tipType = TIP_TYPE.TIP_INVISIBLE;
    private String text = "";
    private StaticLayout mStaticLayout = null;
    private TextPaint mTextPaint;
    private Paint mPaint;
    private Paint bgPaint;
    private Paint strokePaint;
    private final Path path = new Path();
    private final RectF rect = new RectF();
    float dpUnit;
    int otherWeekTextAlpha = 255;
    int otherWeekBgAlpha = 255;
    int otherWeekStrokeAlpha = 255;

    public TipTextView(Context context) {
        super(context);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dpUnit = displayMetrics.density;

        this.invalidate();
    }

    public void init(String text, int textSize, int textColor, int bgColor, int bgAlpha, int strokeColor) {
        this.text = text;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(textSize * dpUnit);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setColor(textColor);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(textColor);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(2 * dpUnit);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setDither(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(bgAlpha);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(strokeColor);
        strokePaint.setDither(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2 * dpUnit);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);

        otherWeekTextAlpha = (int) (mTextPaint.getAlpha() * 0.3);
        otherWeekBgAlpha = (int) (bgPaint.getAlpha() * 0.3);
        otherWeekStrokeAlpha = (int) (strokePaint.getAlpha() * 0.3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        rect.left = dpUnit;
        rect.right = (float) width - dpUnit;
        rect.top = dpUnit;
        rect.bottom = (float) height - dpUnit;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tipType == TIP_TYPE.TIP_OTHER_WEEK) {
            mTextPaint.setAlpha(otherWeekTextAlpha);
            mPaint.setAlpha(otherWeekTextAlpha);
            strokePaint.setAlpha(otherWeekStrokeAlpha);
            bgPaint.setAlpha(otherWeekBgAlpha);
        }
        if (mStaticLayout == null) {
            mStaticLayout = StaticLayout.Builder
                    .obtain(text, 0, text.length(), mTextPaint, getWidth()-getPaddingRight()-getPaddingLeft())
                    .setIncludePad(false).build();
        } else {
            mStaticLayout = new StaticLayout(text, mTextPaint, getWidth()-getPaddingLeft()-getPaddingRight(),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }

        canvas.drawRoundRect(rect, 4*dpUnit, 4*dpUnit, bgPaint);
        canvas.drawRoundRect(rect, 4*dpUnit, 4*dpUnit, strokePaint);
        canvas.clipRect(rect);
        canvas.save();
        canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
        mStaticLayout.draw(canvas);
        canvas.restore();

        if (tipType == TIP_TYPE.TIP_VISIBLE) {
            path.moveTo(getWidth() - 12 * dpUnit, getHeight() - 6 * dpUnit);
            path.lineTo(getWidth() - 6 * dpUnit, getHeight() - 6 * dpUnit);
            path.lineTo(getWidth() - 6 * dpUnit, getHeight() - 12 * dpUnit);
            path.close();
            canvas.drawPath(path, mPaint);
        } else if (tipType == TIP_TYPE.TIP_ERROR) {
            canvas.drawLine(getWidth() - 12 * dpUnit,
                    getHeight() - 6 * dpUnit,
                    getWidth() - 6 * dpUnit,
                    getHeight() - 12 * dpUnit, mPaint);
            canvas.drawLine(getWidth() - 6 * dpUnit,
                    getHeight() - 6 * dpUnit,
                    getWidth() - 12 * dpUnit,
                    getHeight() - 12 * dpUnit, mPaint);
        }
    }
}
