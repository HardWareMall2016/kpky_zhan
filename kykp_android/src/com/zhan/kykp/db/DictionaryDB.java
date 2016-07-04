package com.zhan.kykp.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class DictionaryDB {
	private final static String TAG = "DictionaryDB";

	public static final String TABLE = "dictionary";

	public static final String WORD = "word";
	public static final String CONTENT = "content";

	private DictionaryDBHelper mDBHelper;

	public DictionaryDB(Context context) {
		mDBHelper = new DictionaryDBHelper(context);
		try {
			mDBHelper.createDataBase();
		} catch (IOException e) {
			Log.e(TAG, "createDataBase error : " + e.getMessage());
		}
	}

	public WordTrans queryData(String word) {

		if (TextUtils.isEmpty(word)) {
			return null;
		}
		
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " + WORD + " = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { word.toLowerCase() });
		String content = null;
		if (cursor.moveToFirst()) {
			content = cursor.getString(1);
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return parseContent(content);
	}

	private WordTrans parseContent(String content) {
		if (TextUtils.isEmpty(content)) {
			return null;
		}

		WordTrans wordTrans = new WordTrans();
		try {
			JSONObject contentJson = new JSONObject(content);
			JSONObject enJson = contentJson.getJSONObject(JSON_ATTR_EN);

			// word
			wordTrans.word = enJson.optString(JSON_ATTR_WORD);

			// contents
			wordTrans.contents = new ArrayList<Content>();
			JSONArray contentArra = enJson.getJSONArray(JSON_ATTR_CONTENTS);
			for (int i = 0; i < contentArra.length(); i++) {
				JSONObject jsonContent = contentArra.getJSONObject(i);

				Content contentObj = new Content();
				wordTrans.contents.add(contentObj);
				// pron
				contentObj.pron = jsonContent.optString(JSON_ATTR_PRON);
				// type
				contentObj.type = jsonContent.optString(JSON_ATTR_TYPE);

				// trans
				JSONArray transArray = jsonContent.optJSONArray(JSON_ATTR_TRANS);
				contentObj.trans = new ArrayList<Tran>();
				if (transArray != null) {
					for (int index = 0; index < transArray.length(); index++) {
						JSONObject jsonTran = transArray.getJSONObject(index);

						Tran tran = new Tran();
						contentObj.trans.add(tran);
						// mean
						tran.mean = jsonTran.optString(JSON_ATTR_MEAN);

						// example
						JSONArray exampleArray = jsonTran.optJSONArray(JSON_ATTR_EXAMPLE);
						tran.examples = new ArrayList<Example>();
						if (exampleArray != null) {
							for (int k = 0; k < exampleArray.length(); k++) {
								JSONObject jsonExample = exampleArray.getJSONObject(k);

								Example exmaple = new Example();
								tran.examples.add(exmaple);
								// en
								exmaple.en = jsonExample.optString(JSON_ATTR_EN);
								if(!TextUtils.isEmpty(exmaple.en)){
									exmaple.en=htmlDEcode(exmaple.en);
								}
								// ch
								exmaple.ch = jsonExample.optString(JSON_ATTR_CH);
							}
						}
					}
				}

			}

		} catch (JSONException e) {
			Log.e(TAG, "parseContent Error : " + e.getMessage());
			Log.e(TAG, "parseContent content :\n " + content);
			return null;
		}

		return wordTrans;
	}
	
	
    public static String htmlDEcode(String s) {
    	s=s.replace("&lt;", "<");
    	s=s.replace("&gt;", ">");
    	s=s.replace("&amp;", "&");
    	s=s.replace("&#39;", "'");
    	s=s.replace("&quot;", "\"");
    	
    	return s;
    }

	private final String JSON_ATTR_EN = "en";
	private final String JSON_ATTR_CH = "ch";
	private final String JSON_ATTR_WORD = "word";
	private final String JSON_ATTR_CONTENTS = "contents";
	private final String JSON_ATTR_PRON = "pron";
	private final String JSON_ATTR_TYPE = "type";
	private final String JSON_ATTR_TRANS = "trans";
	private final String JSON_ATTR_MEAN = "mean";
	private final String JSON_ATTR_EXAMPLE = "example";

	public class WordTrans {
		public String word;
		public List<Content> contents;

	}

	public class Content {
		public String pron;
		public String type;
		public List<Tran> trans;
	}

	public class Tran {
		public String mean;
		public List<Example> examples;
	}

	public class Example {
		public String en;
		public String ch;
	}

}
