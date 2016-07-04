package com.zhan.kykp.speaking;

import java.util.List;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public interface ISpeakingCallback {
	
	LinearLayout getMenuContent();
	
	void gotoPosition(int position);
	
	void setSpeakingTitle(String title);
	
	List<Question> getQuestionList();
	
	FrameLayout getCustomerActionBarLayout();
	
	View getActionBarDefaultLayout();
	
	void showSearchResult(String keyword);
}
