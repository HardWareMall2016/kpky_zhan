package com.zhan.kykp.widget;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class ClickableTextView extends TextView {
	private long mLastActionDownTime = -1;

	public ClickableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		CharSequence text = getText();
		if (text != null && text instanceof Spannable) {
			handleLinkMovementMethod(this, (Spannable) text, event);
		}
		return super.onTouchEvent(event);
	}

	private boolean handleLinkMovementMethod(TextView widget, Spannable buffer, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			x -= widget.getTotalPaddingLeft();

			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();

			y += widget.getScrollY();

			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);
			ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					long actionUpTime = System.currentTimeMillis();
					if (actionUpTime - mLastActionDownTime > ViewConfiguration.getLongPressTimeout()) {
						// 长按事件，取消LinkMovementMethod处理，即不处理ClickableSpan点击事件
						return false;
					}
					link[0].onClick(widget);
					Selection.removeSelection(buffer);
				} else if (action == MotionEvent.ACTION_DOWN) {
					Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
					mLastActionDownTime = System.currentTimeMillis();
				}
			}
		}
		return false;
	}
}
