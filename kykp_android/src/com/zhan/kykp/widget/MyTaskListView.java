package com.zhan.kykp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


/**
 * Created by Administrator on 2015/11/6.
 */
public class MyTaskListView extends ListView{
    public MyTaskListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyTaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTaskListView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;   // 返回false，list不对点击事件做响应
    }
}
