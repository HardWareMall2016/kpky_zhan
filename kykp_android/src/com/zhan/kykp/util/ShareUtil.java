package com.zhan.kykp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareUtil {
	private static final String PREFERENCE_NAME = "KYKP";
	//首页每日一句
	public static final String EN_SENTENCE = "mTVSentence";
	public static final String CH_SENTENCE = "mCnsentence";
	public static final String AUTHOR = "mTVAuthor";
	//口语题库搜索记录
	public static final String SPEAKING_SEARCH_HISTORY = "speaking_search_history";
	//极光推送别名
	public static final String JPUSH_ALIAS = "JPush_Alias";

	//version版本  判断引导页是否显示
	public static final String VERSION = "version";

	private static SharedPreferences getInstance(Context con) {
		return con.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static void setValue(Context context, String key, String value) {
		Editor edit = getInstance(context).edit();
		edit.putString(key + "", value + "");
		edit.commit();
	}

	public static String getValue(Context context, String key) {
		return getInstance(context).getString(key, "");
	}

}
