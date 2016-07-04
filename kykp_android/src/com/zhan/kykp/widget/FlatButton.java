package com.zhan.kykp.widget;


import com.zhan.kykp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class FlatButton extends View implements OnGestureListener{
	private Paint paint = new Paint();
	private GestureDetector mGestureDetector;	
	private OnClickListener onClickListener; 
	//属性
	private String text;
	private int textSize;
	private int normalColor;
	private int pressedColor;
	private int border;
	private int padding;
	private int paddingLeft;
	private int paddingTop;
	private int paddingRight;
	private int paddingBottom;
	
	//运行时参数
	private int color;
	
	public FlatButton(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(this);
	}
	public FlatButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(this);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.flatbutton);
		text = a.getString(0);
		text = text == null ? "" : text.trim();
		textSize = (int)a.getDimension(1, sp2px(this.getContext(), 20));
		
		normalColor = (int)a.getColor(R.styleable.flatbutton_normalColor, 0);
		if(normalColor == 0)
			normalColor = a.getResourceId(R.styleable.flatbutton_normalColor, 0);
		if(normalColor == 0)
			normalColor = 0xFF5A5858;
		
		pressedColor = (int)a.getColor(R.styleable.flatbutton_pressedColor, 0);
		if(pressedColor == 0)
			pressedColor = a.getResourceId(R.styleable.flatbutton_pressedColor, 0);
		if(pressedColor == 0)
			pressedColor = 0xFFAAAAAA;
		
		border = (int)a.getDimension(R.styleable.flatbutton_border, dip2px(this.getContext(), 1));
		
		padding = (int)a.getDimension(R.styleable.flatbutton_padding, 0);
		paddingLeft = (int)a.getDimension(R.styleable.flatbutton_paddingLeft, padding);
		paddingTop = (int)a.getDimension(R.styleable.flatbutton_paddingTop, padding);
		paddingRight = (int)a.getDimension(R.styleable.flatbutton_paddingRight, padding);
		paddingBottom = (int)a.getDimension(R.styleable.flatbutton_paddingBottom, padding);
		
		//设置初始参数
		this.color = normalColor;
	}
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }	
	/**
     * 计算组件宽度
     */
    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	result = getDefaultWidth();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    /**
     * 计算组件高度
     */
    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getDefaultHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    /**
     * 计算默认宽度
     */
    private int getDefaultWidth(){
    	int defaultWidth = this.paddingLeft + this.paddingRight;   
    	int txtWidth = (int)this.paint.measureText(text);
    	defaultWidth += txtWidth;
    	defaultWidth += dip2px(this.getContext(), 25) * 2;//加上文字与上下边框额距离
    	return defaultWidth;
    }
    /**
     * 计算默认宽度
     */
    private int getDefaultHeight(){
    	paint.setTextSize(this.textSize);
		paint.setStyle(Paint.Style.FILL);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
		int defaultHeight = txtHeight + this.paddingTop + this.paddingBottom;
		defaultHeight += dip2px(this.getContext(), 10) * 2;//加上文字与上下边框的距离
		return defaultHeight;
    }
    @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//canvas.drawColor(0xFF00FF33);
		drawBorder(canvas);
		drawText(canvas);
    }
    /**
     * 画border
     */
    private void drawBorder(Canvas canvas){
    	RectF rectF = new RectF();	
    	rectF.left = this.paddingLeft + this.border;
    	rectF.top = this.paddingTop + this.border;
    	rectF.bottom = this.getHeight() - this.paddingBottom - this.border;
    	rectF.right = this.getWidth() - this.paddingRight - this.border;
    	paint.setAntiAlias(true);
    	paint.setColor(this.color);  
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(this.border);
    	int cornerRadius = (this.getHeight() - this.paddingTop - this.paddingBottom- this.border)/2;
    	canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
    }
    /**
     * 画文字
     */
    private void drawText(Canvas canvas){
    	paint.setTextSize(this.textSize);
		paint.setStyle(Paint.Style.FILL); 
		paint.setColor(this.color);  
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
    	int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
    	int txtWidth = (int)this.paint.measureText(text);
    	int left = this.paddingLeft + ((this.getWidth()-this.paddingLeft-this.paddingRight)/2-txtWidth/2);
    	int top = this.paddingTop + ((this.getHeight()-this.paddingTop-this.paddingBottom)/2-txtHeight/2);
    	canvas.drawText(text, left, top-fontMetrics.ascent, paint);
    }
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    /** 
     * 从 sp 的单位 转成为 px(像素) 
     */  
    public static int sp2px(Context context, float spValue) {  
    	final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    }  
    /**
	 * 触碰事件
	 */
	@Override
    public boolean onTouchEvent(MotionEvent event) {    	
		try {
			mGestureDetector.onTouchEvent(event);
    		if(event.getAction() == MotionEvent.ACTION_UP){
    			upEventHandler((int)event.getX(), (int)event.getY());
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
    }
	///手势监听
	@Override
	public boolean onDown(MotionEvent event) {
		downEventHandler((int)event.getX(), (int)event.getY());
		return true;
	}
	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent event) {		
	}
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent event) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		clickEventHandler((int)event.getX(), (int)event.getY());
		return true;
	}
	/**
	 * 按下事件
	 */
	private void downEventHandler(int x, int y){
		if(!inTouchArea(x, y))
			return ;
		this.color = this.pressedColor;
		this.invalidate();
	}
	/**
	 * 抬起事件
	 */
	private void upEventHandler(int x, int y){
		this.color = this.normalColor;
		this.invalidate();
	}
	/**
	 * 单击事件
	 */
	private void clickEventHandler(int x, int y){
		/*if(!inTouchArea(x, y))
			return ;*/
		if(this.onClickListener != null)
			this.onClickListener.onClick(this);
	}
	/**
	 * 是否在触碰区内
	 */
	private boolean inTouchArea(int x, int y){
		if(x < this.paddingLeft || x > (this.getWidth() - this.paddingRight))
			return false;
		if(y < this.paddingTop || x > (this.getHeight() - this.paddingBottom))
			return false;
		return true;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		this.invalidate();
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
		this.invalidate();
	}
	public int getNormalColor() {
		return normalColor;
	}
	public void setNormalColor(int normalColor) {
		this.normalColor = normalColor;
		this.invalidate();
	}
	public int getPressedColor() {
		return pressedColor;
	}
	public void setPressedColor(int pressedColor) {
		this.pressedColor = pressedColor;
	}
	public int getBorder() {
		return border;
	}
	public void setBorder(int border) {
		this.border = border;
		this.invalidate();
	}
	public int getPadding() {
		return padding;
	}
	public void setPadding(int padding) {
		this.padding = padding;
		this.paddingLeft = padding;
		this.paddingTop = padding;
		this.paddingRight = padding;
		this.paddingBottom = padding;
		this.invalidate();
	}
	public int getPaddingLeft() {
		return paddingLeft;
	}
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
		this.invalidate();
	}
	public int getPaddingTop() {
		return paddingTop;
	}
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
		this.invalidate();
	}
	public int getPaddingRight() {
		return paddingRight;
	}
	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
		this.invalidate();
	}
	public int getPaddingBottom() {
		return paddingBottom;
	}
	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
		this.invalidate();
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
		this.invalidate();
	}
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
}
