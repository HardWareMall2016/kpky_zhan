package com.zhan.kykp.widget;

import com.zhan.kykp.util.PixelUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RedDotView extends View {

	protected Paint mPaint = new Paint();
	private int mRadius=PixelUtils.dp2px(5);

	public RedDotView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL);//设置填满  
		mPaint.setAntiAlias(true);// 设置画笔的锯齿效果。
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获得父View传递给我们地测量需求
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int width = 0;
		int height = 0;
		// 对UNSPECIFIED 则抛出异常
		/*if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)
			throw new RuntimeException("widthMode or heightMode cannot be UNSPECIFIED");*/

		width=mRadius*2;
		height=width;
		
		// 精确指定
		if (heightMode == MeasureSpec.EXACTLY) {
			width=MeasureSpec.getSize(heightMode);
			height=width;
			mRadius = width/2;
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(getWidth()/2, getHeight()/2, mRadius, mPaint);
	}

}
