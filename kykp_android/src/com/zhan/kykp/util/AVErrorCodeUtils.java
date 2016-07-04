package com.zhan.kykp.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class AVErrorCodeUtils {
	private final static String TAG = "AVErrorCodeUtils";
	private final static String NODE_ERROR_CODE = "error_code";
	private final static String NODE_ERROR_MSG = "message";
	private final static String AV_ERROR_CODE_FILE = "av_error_codes.xml";
	
	private static SparseArray<String> sAVErrorCodes = new SparseArray<String>();
	private static boolean sHasParseXML = false;

	public static String getErrorCodeMsg(Context context, int errorCode) {
		// 解析XML
		if (!sHasParseXML) {
			try {
				InputStream xml = context.getAssets().open(AV_ERROR_CODE_FILE);
				XmlPullParser pullParser = Xml.newPullParser();
				pullParser.setInput(xml, "UTF-8"); // 为Pull解释器设置要解析的XML数据
				int event = pullParser.getEventType();

				int code = 0;
				String errorMsg = null;
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (NODE_ERROR_CODE.equals(pullParser.getName())) {
							code = Integer.valueOf(pullParser.getAttributeValue(0));
						}
						if (NODE_ERROR_MSG.equals(pullParser.getName())) {
							errorMsg = pullParser.nextText();
						}
						break;

					case XmlPullParser.END_TAG:
						if (NODE_ERROR_CODE.equals(pullParser.getName())) {
							sAVErrorCodes.put(code, errorMsg.trim());
						}
						break;
					}

					event = pullParser.next();
				}
			} catch (IOException e) {
				Log.e(TAG, "Open assets file "+AV_ERROR_CODE_FILE+" error");
			} catch (XmlPullParserException e) {
				Log.e(TAG, "Parse assets file "+AV_ERROR_CODE_FILE+" error : "+e.getMessage());
			}
		}
		
		return sAVErrorCodes.get(errorCode);
	}
}
