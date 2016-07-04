package com.zhan.kykp.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LodingImageView extends ImageView{

	public LodingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		((AnimationDrawable)getDrawable()).start();
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		((AnimationDrawable)getDrawable()).stop();
		super.onDetachedFromWindow();
	}


}
