package com.zhan.kykp.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.EditText;
import android.widget.PopupWindow;

public class SelectableText extends EditText {
	public boolean isSupportExtractWord = true;
	private boolean isLongPressState;
	private Context mContext;

	private int mLastMotionX, mLastMotionY;
	// 是否移动了
	private boolean mIsMoved;
	// 移动的阈值
	private static final int TOUCH_SLOP = 20;
	private String mSelectWord;
	private int mStartOffset;
	private int mEndOffset;
	private boolean mHasWordSelected=false;
	private SpannableString mContentSpanString;
	/** 
	* 文字背景颜色 
	*/ 
	private BackgroundColorSpan mBgSpan = new BackgroundColorSpan(Color.YELLOW);
	
	private OnWordSelectedListener mOnWordSelectedListener;
	// ----------------------------------------------------$放大镜
	private PopupWindow mPopupWindow;
	private Bitmap mSelectRectBitmap;
	private Point mPopWindowPosition;
	private static final int WIDTH = 400;
	private static final int HEIGHT = 100;
	private static final long DELAY_TIME = 250;
	private Magnifier mMagnifier;
	
	
	private final int MSG_LONGPRESS = 1;
	private Handler mPressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 长按->初次启动--->显示放大镜&提词
			case MSG_LONGPRESS:
				isLongPressState = true;
				if(mOnWordSelectedListener!=null){
					mOnWordSelectedListener.onLongPressStatusChanged(isLongPressState);
				}
				Bundle data = msg.getData();
				int X = data.getInt("X");
				int RawX = data.getInt("RawX");
				int Y = data.getInt("Y");
				int RawY = data.getInt("RawY");
				if (!mIsMoved) {
					mSelectWord = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), X, Y));
				}
				mSelectRectBitmap = getBitmap(X - WIDTH / 2, Y - HEIGHT / 2, WIDTH, HEIGHT);
				// 放大镜-初次显示
				moveZoomPosition(RawX, RawY, MotionEvent.ACTION_DOWN);
				break;
			}
		}
	};
	
	public interface OnWordSelectedListener{
		public void onWorldSelected(String word,Rect winodwRect);
		public void onLongPressStatusChanged(boolean longPress);
	}
	
	// ---------三个构造----------------------------------------------$构造
	// 当设置,指定样式时调用
	public SelectableText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	// 布局文件初始化的时候,调用-------该构造方法,重用------------★
	// 布局文件里面定义的属性都放在 AttributeSet attrs
	public SelectableText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	// 该方法,一般,在代码中 new 该类的时候_使用
	public SelectableText(Context context) {
		super(context);
		initialize(context);
	}

	// --------------------------------------------------------------$初始
	private void initialize(Context context) {
		mContext = context;
		setBackgroundColor(Color.TRANSPARENT);// 背景透明-去掉底部输入框
		setHighlightColor(Color.TRANSPARENT);//高亮透明
		
		initMagnifier();
		// 搜索关键字
		mContentSpanString = new SpannableString(getText());
		/*Pattern p = Pattern.compile("This");
		Matcher m = p.matcher(mContentSpanString);

		HightLightClickableSpan mHightLightClickableSpan = new HightLightClickableSpan("This");
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			mContentSpanString.setSpan(mHightLightClickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}*/

		setText(mContentSpanString);
		
		
		//setMovementMethod(LinkMovementMethod.getInstance());
		
	}
	
	public void setSpannableString(SpannableString text){
		mContentSpanString=text;
		setText(mContentSpanString);
	}

	/*private class HightLightClickableSpan extends ClickableSpan{
		String mText;
		public HightLightClickableSpan(String text){
			mText=text;
		}
		@Override
		public void onClick(View widget) {
			Toast.makeText(mContext, "You have clicked : "+mText, Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(getResources().getColor(android.R.color.holo_red_light));
			ds.setUnderlineText(false);
			ds.clearShadowLayer();
		}
	}*/
	
	public void setOnWordSelectedListener(OnWordSelectedListener listener){
		mOnWordSelectedListener=listener;
	}
	
	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// 不做任何处理，为了阻止长按的时候弹出上下文菜单
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mIsMoved = false;

			Message message = mPressHandler != null ? mPressHandler.obtainMessage() : new Message();
			// 传对象,过去后,getRawY,不是相对的Y轴.
			// message.obj = event;
			Bundle bundle = new Bundle();
			bundle.putInt("X", (int) event.getX());
			bundle.putInt("RawX", (int) event.getRawX());
			bundle.putInt("Y", (int) event.getY());
			bundle.putInt("RawY", (int) event.getRawY());
			message.setData(bundle);
			message.what = MSG_LONGPRESS;
			mPressHandler.sendMessageDelayed(message, 500);
			break;
		case MotionEvent.ACTION_MOVE:
			if (isLongPressState)
				if (Math.abs(mLastMotionX - x) > TOUCH_SLOP || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
					// 提词
					mSelectWord = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), x, y));
					// 放大镜
					mSelectRectBitmap = getBitmap((int) event.getX() - WIDTH / 2, (int) event.getY() - HEIGHT / 2, WIDTH, HEIGHT);
					moveZoomPosition((int) event.getRawX(), (int) event.getRawY(), MotionEvent.ACTION_MOVE);
					return true;
				}
			if (mIsMoved && !isLongPressState)
				break;
			// 如果移动超过阈值
			if (Math.abs(mLastMotionX - x) > TOUCH_SLOP || Math.abs(mLastMotionY - y) > TOUCH_SLOP)
				// 并且非长按状态下
				if (!isLongPressState) {
					// 则表示移动了
					mIsMoved = true;
					cleanLongPress();// 如果超出规定的移动范围--取消[长按事件]
				}
			break;
		case MotionEvent.ACTION_UP:
			if (isLongPressState) {
				// dis掉放大镜
				removeCallbacks(mShowZoom);
				mPopupWindow.dismiss();
				cleanLongPress();
				if (!TextUtils.isEmpty(mSelectWord))
					onLongPressWord(mSelectWord);
				break;
			} else {
				clearSelection();
			}
			cleanLongPress();// 只要一抬起就释放[长按事件]
			break;
		case MotionEvent.ACTION_CANCEL:
			// dis掉放大镜
			removeCallbacks(mShowZoom);
			mPopupWindow.dismiss();
			// 事件一取消也释放[长按事件],解决在ListView中滑动的时候长按事件的激活
			cleanLongPress();
			clearSelection();
			break;
		}

		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean getDefaultEditable() {
		return false;
	}

	// 其实当前控件并没有获得焦点，我只是欺骗Android系统，让Android系统以我获得焦点的方式去处理
	// 用于将该控件Add到其他View下,导致失去焦点.
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return super.isFocused();// return true一定有焦点
	}

	private void cleanLongPress() {
		isLongPressState = false;
		if(mOnWordSelectedListener!=null){
			mOnWordSelectedListener.onLongPressStatusChanged(isLongPressState);
		}
		mPressHandler.removeMessages(MSG_LONGPRESS);
	}

	
	public boolean isLongPress(){
		return isLongPressState;
	}
	private boolean moveZoomPosition(int x, int y, int action) {
		mPopWindowPosition.set(x - WIDTH / 2, y - 2 * HEIGHT);
		if (y < 0) {
			mPopupWindow.dismiss();
			return true;
		}
		if (action == MotionEvent.ACTION_DOWN) {
			removeCallbacks(mShowZoom);
			postDelayed(mShowZoom, DELAY_TIME);
		} else if (!mPopupWindow.isShowing()) {
			mShowZoom.run();
		}
		mPopupWindow.update(mPopWindowPosition.x, mPopWindowPosition.y, -1, -1);
		mMagnifier.invalidate();
		return true;
	}

	
	private void initMagnifier() {
		mMagnifier = new Magnifier(mContext);
		// pop在宽高的基础上多加出边框的宽高
		mPopupWindow = new PopupWindow(mMagnifier, WIDTH + 2, HEIGHT + 10);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);
		mPopWindowPosition = new Point(0, 0);
	}

	Runnable mShowZoom = new Runnable() {
		public void run() {
			mPopupWindow.showAtLocation(SelectableText.this, Gravity.NO_GRAVITY, mPopWindowPosition.x, mPopWindowPosition.y);
		}
	};

	private class Magnifier extends View {
		private Paint mPaint;
		public Magnifier(Context context) {
			super(context);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(0xff008000);
			mPaint.setStyle(Paint.Style.STROKE);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Path borderPath = new Path();
			borderPath.moveTo(0, 0);
			borderPath.lineTo(WIDTH, 0);
			borderPath.lineTo(WIDTH, HEIGHT);
			borderPath.lineTo(WIDTH / 2 + 15, HEIGHT);
			borderPath.lineTo(WIDTH / 2, HEIGHT + 10);
			borderPath.lineTo(WIDTH / 2 - 15, HEIGHT);
			borderPath.lineTo(0, HEIGHT);
			borderPath.close();// 封闭
			//白底
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.FILL);// 设置空心
			canvas.drawPath(borderPath, mPaint);
			//灰色边框
			mPaint.setColor(Color.LTGRAY);
			mPaint.setStrokeWidth(2);
			mPaint.setStyle(Paint.Style.STROKE);// 设置空心
			canvas.drawPath(borderPath, mPaint);
			//截图
			canvas.save();
			mPaint.setAlpha(255);
			canvas.drawBitmap(mSelectRectBitmap, 0, 0, mPaint);
			canvas.restore();
			mPaint.reset();// 重置
		}
	}

	// 截图
	/**
	 * @param activity
	 * @param x
	 *            截图起始的横坐标
	 * @param y
	 *            截图起始的纵坐标
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap getBitmap(int x, int y, int width, int height) {
		setDrawingCacheEnabled(true);
		buildDrawingCache();
		Bitmap bmp = getDrawingCache();
		// 边界处理,否则会崩滴
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x + width > bmp.getWidth()) {
			// 保持不改变,截取图片宽高的原则
			x = bmp.getWidth() - width;
		}
		if (y + height > bmp.getHeight()) {
			y = bmp.getHeight() - height;
		}
		
		bmp = Bitmap.createBitmap(bmp, x, y, width, height);
		
		setDrawingCacheEnabled(false);
		return bmp;
	}

	private void onLongPressWord(String word) {
		if (!"".equals(word)) {
			if(mOnWordSelectedListener!=null){
				
				Rect selfRect=new Rect();
				getSelelctRect(selfRect,mStartOffset,mEndOffset);
				
				int[] location=new int[2];
				getLocationOnScreen(location);
				
				selfRect.offset(location[0], location[1]);
				
				mOnWordSelectedListener.onWorldSelected(word, selfRect);
				//mOnWordSelectedListener.onWorldSelected(word, mPopWindowPosition.x, mPopWindowPosition.y+HEIGHT);
			}
			//Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
		}else {
			requestFocus();
			setFocusable(false);
			// ewe.setFocusableInTouchMode(false);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	// 单词提取
	private int extractWordCurOff(Layout layout, int x, int y) {
		int line;
		line = layout.getLineForVertical(getScrollY() + y - 10);
		int curOff = layout.getOffsetForHorizontal(line, x);
		
		return curOff;
	}
	
	private void getSelelctRect(Rect r,int selStart,int selEnd){
		Layout layout=getLayout();
		 int lineStart = layout.getLineForOffset(selStart);
         int lineEnd = layout.getLineForOffset(selEnd);
         r.top = layout.getLineTop(lineStart);
         r.bottom = layout.getLineBottom(lineEnd);
         
         r.left = (int) layout.getPrimaryHorizontal(selStart);
         r.right = (int) layout.getPrimaryHorizontal(selEnd);


         // Adjust for padding and gravity.
         int paddingLeft = getCompoundPaddingLeft();
         int paddingTop = getExtendedPaddingTop();
         /*if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) != Gravity.TOP) {
             paddingTop += getVerticalOffset(false);
         }*/
         r.offset(paddingLeft, paddingTop);
         int paddingBottom = getExtendedPaddingBottom();
         r.bottom += paddingBottom;
         
         /*Log.i("TEST", "lineStart = "+lineStart);
         Log.i("TEST", "lineEnd = "+lineEnd);
         
         Log.i("TEST", "left = "+r.left);
         Log.i("TEST", "top = "+r.top);
         Log.i("TEST", "right = "+r.right);
         Log.i("TEST", "bottom = "+r.bottom);*/
	}
	
	
	public void clearSelection(){
		if(mHasWordSelected){
			mContentSpanString.removeSpan(mBgSpan);
			setText(mContentSpanString);
			mHasWordSelected=false;
		}
	}

	private String getSelectWord(Editable content, int curOff) {
		String word = "";
		mStartOffset = getWordLeftIndex(content, curOff);
		mEndOffset = getWordRightIndex(content, curOff);
		if (mStartOffset >= 0 && mEndOffset >= 0) {
			word = content.subSequence(mStartOffset, mEndOffset).toString();
			if (!"".equals(word)) {
				// setFocusable(false);
				setFocusableInTouchMode(true);
				requestFocus();
				
				mContentSpanString.removeSpan(mBgSpan);
				mContentSpanString.setSpan(mBgSpan, mStartOffset, mEndOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
				setText(mContentSpanString);
				
				mHasWordSelected=true;
				
				// 设置当前具有焦点的文本字段的选择范围,当前文本必须具有焦点，否则此方法无效
				//Selection.setSelection(content, start, end);
			}
		}
		return word;
	}

	private int getWordLeftIndex(Editable content, int cur) {
		// --left
		String editableText = content.toString();
		if (cur >= editableText.length())
			return cur;

		int temp = 0;
		if (cur >= 20)
			temp = cur - 20;
		Pattern pattern = Pattern.compile("[^'A-Za-z]");
		Matcher m = pattern.matcher(editableText.charAt(cur) + "");
		if (m.find())
			return cur;

		String text = editableText.subSequence(temp, cur).toString();
		int i = text.length() - 1;
		for (; i >= 0; i--) {
			Matcher mm = pattern.matcher(text.charAt(i) + "");
			if (mm.find())
				break;
		}
		int start = i + 1;
		start = cur - (text.length() - start);
		return start;
	}

	private int getWordRightIndex(Editable content, int cur) {
		// --right
		String editableText = content.toString();
		if (cur >= editableText.length())
			return cur;

		int templ = editableText.length();
		if (cur <= templ - 20)
			templ = cur + 20;
		Pattern pattern = Pattern.compile("[^'A-Za-z]");
		Matcher m = pattern.matcher(editableText.charAt(cur) + "");
		if (m.find())
			return cur;

		String text1 = editableText.subSequence(cur, templ).toString();
		int i = 0;
		for (; i < text1.length(); i++) {
			Matcher mm = pattern.matcher(text1.charAt(i) + "");
			if (mm.find())
				break;
		}
		int end = i;
		end = cur + end;
		return end;
	}
}
