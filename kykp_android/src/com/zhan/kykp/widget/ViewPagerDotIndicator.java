package com.zhan.kykp.widget;

import com.zhan.kykp.util.PixelUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ViewPagerDotIndicator extends View {
	
	private int mRadius=PixelUtils.dp2px(4);
	private int mPadding=PixelUtils.dp2px(5);
	
	private int mColorSelected=0xff1fb9ba;
	private int mColorunSelected=0xff9e9c9c;
	
	protected Paint mPaint = new Paint();
	
	private int mPageCnt=0;
	private int mSelelctIndex=0;

	public ViewPagerDotIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setStyle(Paint.Style.FILL);//设置填满  
		mPaint.setAntiAlias(true);// 设置画笔的锯齿效果。
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int height = mRadius*2;
		
		if(mPageCnt>0){
			 width = mRadius*2*mPageCnt+mPadding*(mPageCnt+1);
		}
		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		for(int i=0;i<mPageCnt;i++){
			int cx=i*mRadius*2+mRadius+(i+1)*mPadding;
			if(mSelelctIndex==i){
				mPaint.setColor(mColorSelected);
			}else{
				mPaint.setColor(mColorunSelected);
			}
			canvas.drawCircle(cx, getHeight()/2, mRadius, mPaint);
		}
	}
	
	public void setPageCount(int pageCount){
		mPageCnt=pageCount;
		requestLayout();
	}
	
	public void setSelectIndex(int index){
		mSelelctIndex=index;
		invalidate();
	}
	
}
