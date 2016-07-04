package com.zhan.kykp.widget;

import com.zhan.kykp.R;
import com.zhan.kykp.util.PixelUtils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class SampleProgressView extends View {

	private Drawable mDrawableIndicator;
	private Drawable mDrawableFullBar;
	private Drawable mDrawableEmptyBar;

	/*private Bitmap mBmpDrawableBackground;
	private Bitmap mBmpDrawableFull;*/
	
	protected Paint mPaint = new Paint();
	
	/*private Rect mSrcRect=new Rect();
	private Rect mDesRect=new Rect();*/
	private int mRadius;
	private int mDrawableHieght;
	private int mDrawableWidth;
	
	private int mProgress=0;

	public SampleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res=context.getResources();
		/*mBmpDrawableBackground=BitmapFactory.decodeResource(res, R.drawable.progress_bg);
		mBmpDrawableFull=BitmapFactory.decodeResource(res, R.drawable.progress_full);*/
		
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL);//设置填满  
		mPaint.setAntiAlias(true);// 设置画笔的锯齿效果。

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SampleProgress, 0, 0);
		mDrawableEmptyBar=a.getDrawable(R.styleable.SampleProgress_empty_drawable);
		if(mDrawableEmptyBar==null){
			mDrawableEmptyBar=res.getDrawable(R.drawable.progress_bg);
		}
		mDrawableFullBar=a.getDrawable(R.styleable.SampleProgress_full_drawable);
		if(mDrawableFullBar==null){
			mDrawableFullBar=res.getDrawable(R.drawable.progress_full);
		}
		mDrawableIndicator=a.getDrawable(R.styleable.SampleProgress_indicator_drawable);
		a.recycle();


	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获得父View传递给我们地测量需求
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int width = 0;
		int height = 0;
		// 对UNSPECIFIED 则抛出异常
		if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)
			throw new RuntimeException("widthMode or heightMode cannot be UNSPECIFIED");

		mDrawableHieght=mDrawableEmptyBar.getIntrinsicHeight();
		mDrawableWidth=mDrawableEmptyBar.getIntrinsicWidth();

		if(mDrawableIndicator!=null){
			height=mDrawableIndicator.getIntrinsicHeight();
			mRadius=height/2;
			width=mDrawableWidth+mRadius*2;
		}else{
			mRadius=(mDrawableHieght+PixelUtils.dp2px(3))/2;
			width=mDrawableWidth+mRadius*2;
			height=mRadius*2;
		}

		
		/*// 精确指定
		if (widthMode == MeasureSpec.EXACTLY) {
			Log.i("SampleProgressView", "EXACTLY");
			width = 100;
		}
		// 模糊指定
		else if (widthMode == MeasureSpec.AT_MOST)
			Log.i("SampleProgressView", "AT_MOST");
			width = 50;

		// 精确指定
		if (heightMode == MeasureSpec.EXACTLY) {
			height = 100;
		}
		// 模糊指定
		else if (heightMode == MeasureSpec.AT_MOST)
			height = 50;*/

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*mSrcRect.left=0;
		mSrcRect.top=0;
		mSrcRect.right=(int) (mDrawableWidth*mProgress/100f);
		mSrcRect.bottom=mDrawableHieght;
		
		mDesRect.left=mRadius;
		mDesRect.top=(getHeight()-mDrawableHieght)/2;
		mDesRect.right=mDesRect.left+(int) (mDrawableWidth*mProgress/100f);
		mDesRect.bottom=mDesRect.top+mDrawableHieght;*/
		
		//canvas.drawBitmap(mBmpDrawableBackground, mDesRect.left, mDesRect.top, mPaint);
		//canvas.drawBitmap(mBmpDrawableFull, mSrcRect, mDesRect, mPaint);
		//canvas.drawCircle(mDesRect.right, getHeight() / 2, mRadius, mPaint);

		int startX=mRadius;
		int startY=(getHeight()-mDrawableHieght)/2;
		int progressWidth=(int) (mDrawableWidth * mProgress / 100f);

		mDrawableEmptyBar.setBounds(startX, startY, startX+mDrawableWidth, startY+mDrawableHieght);
		mDrawableEmptyBar.draw(canvas);

		canvas.save();
		canvas.clipRect(startX, startY, startX+progressWidth, startY+mDrawableHieght);
		mDrawableFullBar.setBounds(startX, startY,startX+ mDrawableWidth, startY+mDrawableHieght);
		mDrawableFullBar.draw(canvas);
		canvas.restore();

		if(mDrawableIndicator!=null){
			mDrawableIndicator.setBounds(progressWidth, 0,progressWidth+mDrawableIndicator.getIntrinsicWidth(),mDrawableIndicator.getIntrinsicHeight());
			mDrawableIndicator.draw(canvas);
		}else{
			canvas.drawCircle(startX+progressWidth, getHeight() / 2, mRadius, mPaint);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		/*mBmpDrawableBackground.recycle();
		mBmpDrawableFull.recycle();*/
		
		super.onDetachedFromWindow();
	}
	
	public void setProgress(int progress){
		mProgress=progress;
		postInvalidate();
	}

}
