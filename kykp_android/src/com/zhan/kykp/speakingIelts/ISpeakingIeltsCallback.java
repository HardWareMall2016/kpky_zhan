package com.zhan.kykp.speakingIelts;

import java.util.List;


import android.widget.LinearLayout;

public interface ISpeakingIeltsCallback {

	LinearLayout getMenuContent();
	
	void gotoPosition(int position) ;
	
	void setSpeakingIeltsTitle(String title);
	
	List<IeltsData> getIeltsList() ;
}
