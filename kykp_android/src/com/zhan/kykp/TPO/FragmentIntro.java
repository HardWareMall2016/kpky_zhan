package com.zhan.kykp.TPO;

import com.zhan.kykp.R;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentIntro extends BaseTPOFragment implements OnCompletionListener {
	private final static String TAG = FragmentIntro.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tpo_intro_layout, null);
		TextView tvTitle = (TextView) rootView.findViewById(R.id.title);
		String strFormat = this.getString(R.string.tpo_intro);
		tvTitle.setText(String.format(strFormat, mQuestionIndex));

		if (mQuestionIndex.equals(TPOConstant.Q1)) {
			play(TPOConstant.speaking_question1_qd);
		} else if (mQuestionIndex.equals(TPOConstant.Q2)) {
			play(TPOConstant.speaking_question2_qd);
		}else if (mQuestionIndex.equals(TPOConstant.Q3)) {
			play(TPOConstant.speaking_question3_qd);
		}else if (mQuestionIndex.equals(TPOConstant.Q4)) {
			play(TPOConstant.speaking_question4_qd);
		}else if (mQuestionIndex.equals(TPOConstant.Q5)) {
			play(TPOConstant.speaking_question5_qd);
		}else if (mQuestionIndex.equals(TPOConstant.Q6)) {
			play(TPOConstant.speaking_question6_qd);
		}

		return rootView;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		release();
		mTPOCallback.gotoNext();
	}
}
