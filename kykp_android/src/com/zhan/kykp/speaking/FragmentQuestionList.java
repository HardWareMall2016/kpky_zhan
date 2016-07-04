package com.zhan.kykp.speaking;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

abstract public class FragmentQuestionList extends Fragment {

	protected ISpeakingCallback mCallback;

	@Override
	public void onAttach(Activity activity) {
		mCallback = (ISpeakingCallback) activity;
		super.onAttach(activity);
		initActionBar();
	}

	@Override
	public void onDetach() {
		mCallback = null;
		super.onDetach();
	}

	public void initActionBar() {
		FrameLayout cusTomerView=mCallback.getCustomerActionBarLayout();
		cusTomerView.removeAllViews();
		cusTomerView.setVisibility(View.GONE);
		
		View defLayout=mCallback.getActionBarDefaultLayout();
		defLayout.setVisibility(View.VISIBLE);
		
		mCallback.getMenuContent().removeAllViews();
	}
	
	abstract public List<Question> getQuestionList();
	
	abstract public String getQuestionListType();
}
