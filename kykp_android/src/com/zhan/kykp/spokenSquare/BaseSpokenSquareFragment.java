package com.zhan.kykp.spokenSquare;

import com.zhan.kykp.network.bean.TopicIndexBean;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public abstract class BaseSpokenSquareFragment extends Fragment {
	
	public interface Callback{
		TopicIndexBean getTopicIndexBean();
		String getTopicTime();
	}
	
	protected static final String ARG_QUESTION_KEY = "question";
	protected String mQuestionContent;
	protected Callback mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		mCallback = (Callback) activity;
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		mCallback = null;
		super.onDetach();
	}
	
	public void prepareArguments(String question){
		Bundle args = new Bundle();
		args.putString(ARG_QUESTION_KEY,question);
		setArguments(args);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mQuestionContent=args.getString(ARG_QUESTION_KEY);
	}
	
	public abstract void stopPlayMedia();
}
