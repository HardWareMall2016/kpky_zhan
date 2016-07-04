package com.zhan.kykp.speakingIelts;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;

abstract public class FragmentIeltsList extends Fragment{

	protected ISpeakingIeltsCallback mCallback ;
	
	@Override
	public void onAttach(Activity activity) {
		mCallback = (ISpeakingIeltsCallback) activity;
		initActionBar();
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		mCallback = null;
		super.onDetach();
	}
	public void initActionBar() {
		mCallback.getMenuContent().removeAllViews();
	}
	
	abstract public List<IeltsData> getIeltsList();
	
	//abstract public String getQuestionListType();
	
}
