package com.zhan.kykp.speaking;

import com.zhan.kykp.R;

import android.content.Context;

public class SpeakingTools {
	public enum Type {
		TPO, NEW_GOLD, JJ
	};
	
	public static String getTypeString(Context context,Type type){
		String queryType = "";
		switch (type) {
		case TPO:
			queryType = context.getString(R.string.speaking_type_tpo);
			break;
		case NEW_GOLD:
			queryType = context.getString(R.string.speaking_type_new_gold);
			break;
		case JJ:
			queryType = context.getString(R.string.speaking_type_jj);
			break;
		}
		return queryType;
	}

	public static String getTypeParam(Context context,Type type){
		String queryType = "";
		switch (type) {
			case TPO:
				queryType = "tpo";
				break;
			case NEW_GOLD:
				queryType = "new";
				break;
			case JJ:
				queryType = "jijing";
				break;
		}
		return queryType;
	}
	
	public static String getTitleString(Context context,Type type){
		String queryType = "";
		switch (type) {
		case TPO:
			queryType = context.getString(R.string.speaking_tpo_speaking);
			break;
		case NEW_GOLD:
			queryType = context.getString(R.string.speaking_type_new_gold);
			break;
		case JJ:
			queryType = context.getString(R.string.speaking_type_jj);
			break;
		}
		return queryType;
	}
}
