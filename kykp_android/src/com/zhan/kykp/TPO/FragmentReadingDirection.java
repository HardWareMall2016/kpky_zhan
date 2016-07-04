package com.zhan.kykp.TPO;

import com.zhan.kykp.R;

import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentReadingDirection extends BaseTPOFragment {
	private final static String TAG = FragmentReadingDirection.class.getSimpleName();

	@Override
	public String getTPOTitle() {
		return mTPOCallback.getTPOName() + " " + mQuestionIndex;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tpo_reading_direct_layout, null);
		play(TPOConstant.speaking_direction);
		return rootView;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		release();
		mTPOCallback.gotoNext();
	}

}
