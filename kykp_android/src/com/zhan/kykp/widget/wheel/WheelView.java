package com.zhan.kykp.widget.wheel;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.zhan.kykp.R;

/**
 * Numeric wheel view.
 */
public class WheelView extends View {
	/** Scrolling duration */
	private static final int SCROLLING_DURATION = 400;

	/** Minimum delta for scrolling */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	/** Current value & label text color */
	//private  int VALUE_TEXT_COLOR = 0xF0FF6347;

	/** Items text color */
	//private  int ITEMS_TEXT_COLOR = 0xFF000000;

	/** Top and bottom shadows colors */
	private int[] SHADOWS_COLORS;

	/** Additional items height (is added to standard text item height) */
	//private static final int ADDITIONAL_ITEM_HEIGHT = 15;

	/** Text size */
	private static final int TEXT_SIZE = 24;

	/** Top and bottom items offset (to hide that) */
	//private int ITEM_OFFSET = TEXT_SIZE / 5;


	/** Left and right padding value */
	private static final int PADDING = 10;


	/** Additional width for items layout */
	private static final int ADDITIONAL_ITEMS_SPACE = 10;

	//Divier
	private Drawable dividerDrawable;

	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Scrolling
	private boolean isScrollingPerformed;
	private int scrollingOffset;

	// Scrolling animation
	private GestureDetector gestureDetector;
	private Scroller scroller;
	private int lastScrollY;


	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
	
	//wuyue
	//private int mCenterTextSize=TEXT_SIZE;

	private int mMaxItemWidth = 0;

	// Widths
	private int itemsWidth = 0;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;
	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;

	// Item height
	private int itemHeight = 0;

	//item padding top and botom
	private final static int ITEM_PADDING_DP =8;
	private int mItemPadding =0;

	// Text paints
	private TextPaint itemsPaint;
	//Lable paints
	private TextPaint lablePaint;

	//main text
	private final static int MAIN_TEXT_SIZE_DP=20;
	private int mMainTextSize;
	private int mMainTextColor=0xff494949;

	//second text
	private final static int SECOND_TEXT_SIZE_DP=16;
	private int mSecondTextSize;
	private int mSecondTextColor=0xff8e8e8e;

	//third text
	private final static int THIRD_TEXT_SIZE_DP=14;
	private int mThirdTextSize;
	private int mThirdTextColor=0xffb3b3b3;

	//lable
	private int labelWidth = 0;
	private final static int LABLE_SIZE_DP=14;
	private int mLableSize;
	private int mLableColor=0xffb3b3b3;

	//Divider
	private final static int DIVIDER_HEIGHT_DP=1;
	private int mDividerHeight=2;

	// Label & background
	private String label;
	private Drawable centerDrawable;

	/** Label offset */
	private static final int LABEL_OFFSET = 8;

	// Cyclic
	boolean isCyclic = false;

	// Wheel Values
	private WheelAdapter adapter = null;
	private int currentItem = 0;

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initConfig(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initConfig(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}

	/**
	 * Initializes class data
	 * 
	 * @param context
	 *            the context
	 */
	private void initData(Context context) {
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);

		scroller = new Scroller(context);
	}

	private void initConfig(Context context, AttributeSet attrs){
		//wuyue
		float density=context.getResources().getDisplayMetrics().density;
		mItemPadding =dp2px(density, ITEM_PADDING_DP);
		//mDividerHeight=dp2px(density, DIVIDER_HEIGHT_DP);
		mDividerHeight=1;
		
		TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.WheelView);
		//divider
		dividerDrawable= a.getDrawable(R.styleable.WheelView_divierDrawable);
		//main item
		mMainTextSize=a.getDimensionPixelSize(R.styleable.WheelView_mainTextSize,dp2px(density, MAIN_TEXT_SIZE_DP));
		mMainTextColor=a.getColor(R.styleable.WheelView_mainTextColor, mMainTextColor);
		//second item
		mSecondTextSize=a.getDimensionPixelSize(R.styleable.WheelView_secondTextSize,dp2px(density, SECOND_TEXT_SIZE_DP));
		mSecondTextColor=a.getColor(R.styleable.WheelView_secondTextColor, mSecondTextColor);
		//third item
		mThirdTextSize=a.getDimensionPixelSize(R.styleable.WheelView_thirdTextSize, dp2px(density, THIRD_TEXT_SIZE_DP));
		mThirdTextColor=a.getColor(R.styleable.WheelView_thirdTextColor, mThirdTextColor);
		//lable
		mLableSize=a.getDimensionPixelSize(R.styleable.WheelView_lableTextSize, dp2px(density, LABLE_SIZE_DP));
		mLableColor=a.getColor(R.styleable.WheelView_lableTextColor, mLableColor);
		//center drawble
		centerDrawable = a.getDrawable(R.styleable.WheelView_centerDrawable);

		itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		itemsPaint.setTextSize(mMainTextSize);
		itemsPaint.setColor(mMainTextColor);

		lablePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
		lablePaint.setTextSize(mLableSize);
		lablePaint.setColor(mLableColor);

		int shadowColor=a.getColor(R.styleable.WheelView_shadowColor, context.getResources().getColor(R.color.main_background));

		SHADOWS_COLORS = new int[] { shadowColor, 0x00ffffff&shadowColor, 0x00ffffff&shadowColor };
		topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);

		a.recycle();
	}
	
	public static int dp2px(float density, int dp) {
		if (dp == 0) {
			return 1;
		}
		return (int) (dp * density + 0.5f);

	}

	/**
	 * Gets wheel adapter
	 * 
	 * @return the adapter
	 */
	public WheelAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets wheel adapter
	 * 
	 * @param adapter
	 *            the new wheel adapter
	 */
	public void setAdapter(WheelAdapter adapter) {
		this.adapter = adapter;
		invalidateLayouts();
		invalidate();
	}

	/**
	 * Set the the specified scrolling interpolator
	 * 
	 * @param interpolator
	 *            the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}

	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets count of visible items
	 * 
	 * @param count
	 *            the new count
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
	}

	/**
	 * Gets label
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label
	 * 
	 * @param newLabel
	 *            the label to set
	 */
	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			invalidate();
		}
	}

	/**
	 * Adds wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	/**
	 * Notifies changing listeners
	 * 
	 * @param oldValue
	 *            the old wheel value
	 * @param newValue
	 *            the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 * @param animated
	 *            the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return; // throw?
		}
		if (index < 0 || index >= adapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += adapter.getItemsCount();
				}
				index %= adapter.getItemsCount();
			} else {
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				invalidateLayouts();

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown
	 * the last one
	 * 
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * 
	 * @param isCyclic
	 *            the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;

		invalidate();
		invalidateLayouts();
	}

	/**
	 * Invalidates layouts
	 */
	private void invalidateLayouts() {
		scrollingOffset = 0;
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight() {
		int desired = getItemHeight() * visibleItems;
		// Check against our minimum height
		desired = Math.max(desired, getSuggestedMinimumHeight());
		return desired;
	}

	/**
	 * Returns text item by index
	 * 
	 * @param index
	 *            the item index
	 * @return the item or null
	 */
	private String getTextItem(int index) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return null;
		}
		int count = adapter.getItemsCount();
		if ((index < 0 || index >= count) && !isCyclic) {
			return null;
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return adapter.getItem(index);
	}

	/**
	 * Returns the max item length that can be present
	 * 
	 * @return the max length
	 */
	private int getMaxTextLength() {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return 0;
		}

		int adapterLength = adapter.getMaximumLength();
		if (adapterLength > 0) {
			return adapterLength;
		}

		String maxText = null;
		int addItems = visibleItems / 2;
		for (int i = Math.max(currentItem - addItems, 0); i < Math.min(currentItem + visibleItems, adapter.getItemsCount()); i++) {
			String text = adapter.getItem(i);
			if (text != null && (maxText == null || maxText.length() < text.length())) {
				maxText = text;
			}
		}

		return maxText != null ? maxText.length() : 0;
	}

	/**
	 * Returns height of wheel item
	 * 
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		} else {
			//通过最大的字体计算Item的高度
			TextPaint mainPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			mainPaint.setTextSize(mMainTextSize);
			itemHeight = calcTextHeight(mainPaint)+ mItemPadding *2;
			return itemHeight;
		}
	}

    public static int calcTextHeight(Paint paint) {
    	FontMetrics fm = paint.getFontMetrics(); 
    	int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);  
    	return textHeight;
    }
    
	/**
	 * Calculates control width and creates text layouts
	 * 
	 * @param widthSize
	 *            the input layout width
	 * @param mode
	 *            the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {

		int width = widthSize;

		int maxLength = getMaxTextLength();
		if (maxLength > 0) {
			float textWidth = FloatMath.ceil(Layout.getDesiredWidth("0", itemsPaint));
			itemsWidth = (int) (maxLength * textWidth);
			mMaxItemWidth=(int) (maxLength * textWidth);
		} else {
			itemsWidth = 0;
		}
		itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more

		labelWidth = 0;
		if (label != null && label.length() > 0) {
			labelWidth = (int) FloatMath.ceil(Layout.getDesiredWidth(label, lablePaint));
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + labelWidth + 2 * PADDING;
			if (labelWidth > 0) {
				width += LABEL_OFFSET;
			}

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}

		if (recalculate) {
			// recalculate width
			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (pureWidth <= 0) {
				itemsWidth = labelWidth = 0;
			}
			if (labelWidth > 0) {
				double newWidthItems = (double) itemsWidth * pureWidth / (itemsWidth + labelWidth);
				itemsWidth = (int) newWidthItems;
				labelWidth = pureWidth - itemsWidth;
			} else {
				itemsWidth = pureWidth + LABEL_OFFSET; // no label
			}
		}

		return width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
			itemHeight=height/ visibleItems;
		} else {
			height = getDesiredHeight();
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawCenterRect(canvas);
		drawDivider(canvas);
		if (itemsWidth > 0) {
			canvas.save();
			// Skip padding space and hide a part of top and bottom items
			canvas.translate(PADDING, 0);
			drawValue(canvas);
			canvas.restore();
		}
		//drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {

		topShadow.setBounds(0, 0, getWidth(), getHeight() / 2);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() / 2, getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}


	/**
	 * Draws value and label layout
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawValue(Canvas canvas) {
		int center = getHeight() / 2;
		
		canvas.save();
		canvas.translate(0, scrollingOffset);

		int centerIndex=(visibleItems+2)/2;
		int offsetCenterIndex=0;
		
		for(int i=0;i<(visibleItems+2);i++){
			offsetCenterIndex=i-centerIndex;

			if(offsetCenterIndex==0){
				itemsPaint.setTextSize(mMainTextSize);
				itemsPaint.setColor(mMainTextColor);
			}else if(offsetCenterIndex==1||offsetCenterIndex==-1){
				itemsPaint.setTextSize(mSecondTextSize);
				itemsPaint.setColor(mSecondTextColor);
			}else{
				itemsPaint.setTextSize(mThirdTextSize);
				itemsPaint.setColor(mThirdTextColor);
			}
			String text = getTextItem(currentItem+offsetCenterIndex);

			if (text != null) {

				FontMetrics fontMetrics = itemsPaint.getFontMetrics();
				float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
				float offset = fontTotalHeight / 2 - fontMetrics.bottom;

				float textWidth = FloatMath.ceil(Layout.getDesiredWidth(text, itemsPaint));
				canvas.drawText(text, (getWidth() - textWidth) / 2, center + getItemHeight() * offsetCenterIndex + offset, itemsPaint);
			}
		}
		canvas.restore();
		
		// draw label
		if(label!=null){
			int offset=calcTextHeight(lablePaint)/2;
			canvas.drawText(label, (getWidth() - mMaxItemWidth) / 2 + mMaxItemWidth + LABEL_OFFSET, center + offset, lablePaint);
		}
	}

	/**
	 * Draws rect for current value
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		if(centerDrawable==null){
			return;
		}

		int center = getHeight() / 2;
		int offset = getItemHeight() / 2;
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
	}

	/***
	 * 绘制分割线
	 * @param canvas
	 */
	private void drawDivider(Canvas canvas){
		if(dividerDrawable==null){
			return;
		}
		int center = getHeight() / 2;

		for(int i=0;i<visibleItems/2;i++){
			int offset = getItemHeight() / 2+i*getItemHeight();

			dividerDrawable.setBounds(0, center - offset, getWidth(), center - offset+mDividerHeight);
			dividerDrawable.draw(canvas);
			dividerDrawable.setBounds(0, center + offset, getWidth(), center + offset+mDividerHeight);
			dividerDrawable.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return true;
		}

		if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}
		return true;
	}

	/**
	 * Scrolls the wheel
	 * 
	 * @param delta
	 *            the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		if (isCyclic && adapter.getItemsCount() > 0) {
			// fix position by rotating
			while (pos < 0) {
				pos += adapter.getItemsCount();
			}
			pos %= adapter.getItemsCount();
		} else if (isScrollingPerformed) {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= adapter.getItemsCount()) {
				count = currentItem - adapter.getItemsCount() + 1;
				pos = adapter.getItemsCount() - 1;
			}
		} else {
			// fix position
			pos = Math.max(pos, 0);
			pos = Math.min(pos, adapter.getItemsCount() - 1);
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	// gesture listener
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			startScrolling();
			doScroll((int) -distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount() * getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	// Messages
	private final int MESSAGE_SCROLL = 0;
	private final int MESSAGE_JUSTIFY = 1;

	/**
	 * Set next message to queue. Clears queue before.
	 * 
	 * @param message
	 *            the message to set
	 */
	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue
	 */
	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}

	// animation handler
	private Handler animationHandler = new Handler() {
		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}

			// scrolling is not finished when it comes to final Y
			// so, finish it manually
			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};

	/**
	 * Justifies wheel
	 */
	private void justify() {
		if (adapter == null) {
			return;
		}

		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < adapter.getItemsCount() : currentItem > 0;
		if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}

	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
	}

	/**
	 * Starts scrolling
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	/**
	 * Finishes scrolling
	 */
	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		invalidateLayouts();
		invalidate();
	}

	/**
	 * Scroll the wheel
	 * 
	 * @param itemsToSkip
	 *            items to scroll
	 * @param time
	 *            scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);

		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();

		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);

		startScrolling();
	}

}
