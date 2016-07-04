package com.zhan.kykp.widget;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class ScrollableTextView extends TextView {

	public ScrollableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMovementMethod(ScrollingMovementMethod.getInstance()); 
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			 getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			 getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

}
