package com.zhan.kykp.db;

import java.util.LinkedList;
import java.util.List;

import com.zhan.kykp.entity.dbobject.PracticeRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PracticeRecordDB extends BaseDB {
	public static final String TABLE = "PRACTICE_RECORD";
	
	public static final String USER_OBJECT_ID = "user_object_id";
	public static final String PRACTICE_INDEX = "practice_index";
	public static final String START_TIME = "start_time";
	public static final String RECORDING_SECONDS = "record_seconds";
	public static final String RECORD_FILE_PATH = "record_file_path";
	
	public PracticeRecordDB(Context context) {
		super(context);
	}
	
	public void save(PracticeRecord record){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_OBJECT_ID, record.UserObject);
		values.put(PRACTICE_INDEX, record.PracticeIndex);
		values.put(START_TIME, record.StartTime);
		values.put(RECORDING_SECONDS, record.RecordingSeconds);
		values.put(RECORD_FILE_PATH, record.RecordFilePath);
		
		db.insert(TABLE, null, values);
		db.close();
	}
	
	public void delete(long id) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "delete from " + TABLE + " where " + _ID + "=" + id;
		db.execSQL(sql);
		db.close();
	}
	
	
	public List<List<PracticeRecord>> queryDataGroupByIndex(String userObjectID) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		List<List<PracticeRecord>> dataGroupByIndex = new LinkedList<List<PracticeRecord>>();

		String sql = "select * from " + TABLE + " where "+USER_OBJECT_ID+" = ? order by " + PRACTICE_INDEX + " asc";
		Cursor cursor = db.rawQuery(sql, new String[]{userObjectID});
		PracticeRecord data;
		int preIndex = -1;
		List<PracticeRecord> dataList = null;
		if (cursor.moveToFirst()) {
			data = fromCursor(cursor);
			dataList = new LinkedList<PracticeRecord>();
			dataGroupByIndex.add(dataList);
			dataList.add(data);
			
			preIndex=data.PracticeIndex;

			while (cursor.moveToNext()) {
				data = fromCursor(cursor);
				
				if(data.PracticeIndex!=preIndex){
					dataList = new LinkedList<PracticeRecord>();
					dataGroupByIndex.add(dataList);
					preIndex=data.PracticeIndex;
				}
				dataList.add(data);
			}
		}
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		
		return dataGroupByIndex;
	}
	
	private PracticeRecord fromCursor(Cursor cursor) {
		PracticeRecord data = new PracticeRecord();

		final int idIndex = cursor.getColumnIndexOrThrow(_ID);
		final int userObjectIdIndex = cursor.getColumnIndexOrThrow(USER_OBJECT_ID);
		final int indexIndex = cursor.getColumnIndexOrThrow(PRACTICE_INDEX);
		final int startTimeIndex = cursor.getColumnIndexOrThrow(START_TIME);
		final int secondsIndex = cursor.getColumnIndexOrThrow(RECORDING_SECONDS);
		final int pathIndex = cursor.getColumnIndexOrThrow(RECORD_FILE_PATH);

		data.ID = cursor.getInt(idIndex);
		data.UserObject = cursor.getString(userObjectIdIndex);
		data.PracticeIndex = cursor.getInt(indexIndex);
		data.StartTime = cursor.getLong(startTimeIndex);
		data.RecordingSeconds = cursor.getInt(secondsIndex);
		data.RecordFilePath= cursor.getString(pathIndex);

		return data;
	}
}
