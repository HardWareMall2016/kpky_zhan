package com.zhan.kykp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.util.PixelUtils;

/**
 * Created by WuYue on 2015/10/26.
 */
public class RedTipTextView extends TextView {
    public static final int RED_TIP_INVISIBLE = 0;
    public static final int RED_TIP_VISIBLE = 1;
    public static final int RED_TIP_GONE = 2;

    private static int RED_DOT_PADDING_LEFT= PixelUtils.dp2px(4);

    private int mRadius= PixelUtils.dp2px(5);
    private int mTipVisibility = 0;

    public RedTipTextView(Context context) {
        super(context);
    }

    public RedTipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RedTipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void init(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RedTipTextView);
            mTipVisibility = array.getInt(R.styleable.RedTipTextView_redTipsVisibility, 0);
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //在右上角画个圆点
        if(mTipVisibility == 1) {
            int width = getWidth();
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            //中心点坐标
            final int compoundPaddingLeft = getCompoundPaddingLeft();
            final int compoundPaddingRight = getCompoundPaddingRight();

            int hspace = getRight() - getLeft() - compoundPaddingRight - compoundPaddingLeft;
            int cy=getPaddingTop()+mRadius;

            float textLeft=getScrollX()+getCompoundPaddingLeft()+(hspace-getLayout().getLineWidth(0))/2;

            int cx= (int) (textLeft+getLayout().getLineWidth(0))+mRadius+RED_DOT_PADDING_LEFT;

            canvas.drawCircle(cx, cy, mRadius, paint);
        }
    }

    public void setRedTipVisibility(int visibility) {
        mTipVisibility = visibility;
        invalidate();
    }

}
