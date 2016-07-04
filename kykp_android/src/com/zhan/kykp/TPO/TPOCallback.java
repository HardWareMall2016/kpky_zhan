package com.zhan.kykp.TPO;

import org.json.JSONObject;

public interface TPOCallback {
	String getTPOName();
	
	int getTPOIndex();

	JSONObject getTPOJson();

	void setTPOTitle(String title, boolean skip);

	String getTPOFilePath(String fileSubPath);

	void gotoNext();
}
