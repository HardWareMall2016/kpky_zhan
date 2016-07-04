package com.zhan.kykp.TPO;

import com.zhan.kykp.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentListening extends BaseTPOFragment implements
		OnCompletionListener {
	private final static String TAG = FragmentListening.class.getSimpleName();
	private ImageView mImgTip;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tpo_listening_layout, null);
		mImgTip = (ImageView) rootView.findViewById(R.id.img_tip);


		String imgPath = null;
		if (mQuestionIndex.equals(TPOConstant.Q3)) {
			play(TPOConstant.speaking_question3_dialog);
			imgPath = mTPOCallback.getTPOFilePath(TPOConstant.img_question3);
		} else if (mQuestionIndex.equals(TPOConstant.Q4)) {
			play(TPOConstant.speaking_question4_dialog);
			imgPath = mTPOCallback.getTPOFilePath(TPOConstant.img_question4);
		} else if (mQuestionIndex.equals(TPOConstant.Q5)) {
			play(TPOConstant.speaking_question5_dialog);
			imgPath = mTPOCallback.getTPOFilePath(TPOConstant.img_question5);
		} else if (mQuestionIndex.equals(TPOConstant.Q6)) {
			play(TPOConstant.speaking_question6_dialog);
			imgPath = mTPOCallback.getTPOFilePath(TPOConstant.img_question6);
		}
		Bitmap bmp = BitmapFactory.decodeFile(imgPath);
		mImgTip.setImageBitmap(bmp);
		
		return rootView;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		release();
		mTPOCallback.gotoNext();
	}
}
