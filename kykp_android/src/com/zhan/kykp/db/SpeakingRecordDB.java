package com.zhan.kykp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhan.kykp.entity.dbobject.SpeakingRecord;

import java.util.LinkedList;
import java.util.List;

public class SpeakingRecordDB extends BaseDB {
	public static final String TABLE = "SPEAKING_ANSWER_RECORD";

	public static final String QUESTION_OBJECT_ID = "object_id";
	public static final String USER_OBJECT_ID = "user_object";
	public static final String IMG_KEY_WORD = "img_keyword";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String SUB_TITLE = "sub_title";
	public static final String EXT_DATA = "ext_data";
	public static final String START_TIME = "start_time";
	public static final String RECORDING_SECONDS = "record_seconds";
	public static final String RECORD_FILE_PATH = "record_file_path";

	public SpeakingRecordDB(Context context) {
		super(context);
	}

	public void save(SpeakingRecord speakingRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(QUESTION_OBJECT_ID, speakingRecord.questionObjectId);
		values.put(USER_OBJECT_ID, speakingRecord.userObjectId);
		values.put(IMG_KEY_WORD, speakingRecord.imgKeyWord);
		values.put(TYPE, speakingRecord.questionType);
		values.put(TITLE, speakingRecord.title);
		values.put(SUB_TITLE, speakingRecord.subTitle);
		values.put(EXT_DATA, speakingRecord.extData);
		values.put(START_TIME, speakingRecord.startTime);
		values.put(RECORDING_SECONDS, speakingRecord.recordingSeconds);
		values.put(RECORD_FILE_PATH, speakingRecord.recordingFilePath);

		db.insert(TABLE, null, values);
		db.close();
	}

	public void update(SpeakingRecord speakingRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(QUESTION_OBJECT_ID, speakingRecord.questionObjectId);
		values.put(USER_OBJECT_ID, speakingRecord.userObjectId);
		values.put(IMG_KEY_WORD, speakingRecord.imgKeyWord);
		values.put(TYPE, speakingRecord.questionType);
		values.put(TITLE, speakingRecord.title);
		values.put(SUB_TITLE, speakingRecord.subTitle);
		values.put(EXT_DATA, speakingRecord.extData);
		values.put(START_TIME, speakingRecord.startTime);
		values.put(RECORDING_SECONDS, speakingRecord.recordingSeconds);
		values.put(RECORD_FILE_PATH, speakingRecord.recordingFilePath);

		db.update(TABLE, values, _ID + "=" + speakingRecord.ID, null);
		db.close();
	}
	
	public void delete(long id) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "delete from " + TABLE + " where " + _ID + "=" + id;
		db.execSQL(sql);
		db.close();
	}

	public List<SpeakingRecord> queryData(String userObject) {
		List<SpeakingRecord> dataList = new LinkedList<SpeakingRecord>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " + USER_OBJECT_ID + "= ? order by " + START_TIME + " desc ";
		Cursor cursor = db.rawQuery(sql, new String[] { userObject });
		SpeakingRecord data;
		if (cursor.moveToFirst()) {
			data = fromCursor(cursor);
			dataList.add(data);
			while (cursor.moveToNext()) {
				data = fromCursor(cursor);
				dataList.add(data);
			}
		}
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return dataList;
	}
	
	public SpeakingRecord queryDataByObjectId(String objectId){

		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " + QUESTION_OBJECT_ID +" = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(objectId)});
		SpeakingRecord data = null;
		if (cursor.moveToFirst()) {
			data=fromCursor(cursor);
		}
		if(cursor!=null){
			cursor.close();
		}
		db.close();
		return data;
	}

	/*public List<Integer> queryAllUid(String userObject) {
		List<Integer> dataList = new LinkedList<Integer>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select DISTINCT " + UID + " from " + TABLE +" where "+USER_OBJECT_ID+" = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{userObject});
		if (cursor.moveToFirst()) {
			dataList.add(cursor.getInt(0));
			while (cursor.moveToNext()) {
				dataList.add(cursor.getInt(0));
			}
		}
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return dataList;
	}*/


	private SpeakingRecord fromCursor(Cursor cursor) {
		SpeakingRecord data = new SpeakingRecord();

		final int idIndex = cursor.getColumnIndexOrThrow(_ID);
		final int questionObjectIdIndex = cursor.getColumnIndexOrThrow(QUESTION_OBJECT_ID);
		final int userObjectIdIndex = cursor.getColumnIndexOrThrow(USER_OBJECT_ID);
		final int imgKeyWordIndex = cursor.getColumnIndexOrThrow(IMG_KEY_WORD);
		final int typeIndex = cursor.getColumnIndexOrThrow(TYPE);
		final int titleIndex = cursor.getColumnIndexOrThrow(TITLE);
		final int subTitleIndex = cursor.getColumnIndexOrThrow(SUB_TITLE);
		final int extDataIndex = cursor.getColumnIndexOrThrow(EXT_DATA);
		final int startTimeIndex = cursor.getColumnIndexOrThrow(START_TIME);
		final int secondsIndex = cursor.getColumnIndexOrThrow(RECORDING_SECONDS);
		final int pathIndex = cursor.getColumnIndexOrThrow(RECORD_FILE_PATH);


		data.ID = cursor.getInt(idIndex);
		data.questionObjectId = cursor.getString(questionObjectIdIndex);
		data.userObjectId = cursor.getString(userObjectIdIndex);
		data.imgKeyWord=cursor.getString(imgKeyWordIndex);
		data.questionType = cursor.getInt(typeIndex);
		data.title = cursor.getString(titleIndex);
		data.subTitle = cursor.getString(subTitleIndex);
		data.extData = cursor.getString(extDataIndex);
		data.startTime = cursor.getLong(startTimeIndex);
		data.recordingSeconds = cursor.getInt(secondsIndex);
		data.recordingFilePath=cursor.getString(pathIndex);

		return data;
	}
}
