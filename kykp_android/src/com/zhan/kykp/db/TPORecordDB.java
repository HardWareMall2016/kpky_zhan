package com.zhan.kykp.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zhan.kykp.entity.dbobject.TPORecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TPORecordDB extends BaseDB {
	public static final String TABLE = "TPO_RECORD";
	
	public static final String USER_OBJECT_ID = "user_object_id";
	public static final String TPO_NAME = "tpo_name";
	public static final String TPO_INDEX = "tpo_index";
	public static final String QUESTION_INDEX = "question_index";
	public static final String START_TIME = "start_time";
	public static final String RECORDING_SECONDS = "record_seconds";
	public static final String RECORD_FILE_PATH = "record_file_path";
	
	public TPORecordDB(Context context) {
		super(context);
	}
	
	public void save(TPORecord tpoRecord){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_OBJECT_ID, tpoRecord.UserObject);
		values.put(TPO_NAME, tpoRecord.TPOName);
		values.put(TPO_INDEX, tpoRecord.TPOIndex);
		values.put(QUESTION_INDEX, tpoRecord.QuestionIndex);
		values.put(START_TIME, tpoRecord.StartTime);
		values.put(RECORDING_SECONDS, tpoRecord.RecordingSeconds);
		values.put(RECORD_FILE_PATH, tpoRecord.RecordFilePath);
		
		db.insert(TABLE, null, values);
		db.close();
	}
	
	public void delete(long id) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "delete from " + TABLE + " where " + _ID + "=" + id;
		db.execSQL(sql);
		db.close();
	}
	
	public List<TPORecord> queryData(String userName){
		List<TPORecord> dataList = new LinkedList<TPORecord>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " +USER_OBJECT_ID+"=? order by " + TPO_NAME + " asc , "+QUESTION_INDEX+" asc";
		Cursor cursor = db.rawQuery(sql, new String[]{userName});
		TPORecord data;
		if (cursor.moveToFirst()) {
			data=fromCursor(cursor);
			dataList.add(data);
			while(cursor.moveToNext()){
				data=fromCursor(cursor);
				dataList.add(data);
			}
		}
		if(cursor!=null){
			cursor.close();
		}

		db.close();
		return dataList;
	}
	
	public List<TPORecord> queryData(String userObject,String TPOName,String Question){
		List<TPORecord> dataList = new LinkedList<TPORecord>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " +USER_OBJECT_ID+"=? and "+TPO_NAME+"=? and "+QUESTION_INDEX+"=? order by " + START_TIME + " asc ";
		Cursor cursor = db.rawQuery(sql, new String[]{userObject,TPOName,Question});
		TPORecord data;
		if (cursor.moveToFirst()) {
			data=fromCursor(cursor);
			dataList.add(data);
			while(cursor.moveToNext()){
				data=fromCursor(cursor);
				dataList.add(data);
			}
		}
		if(cursor!=null){
			cursor.close();
		}

		db.close();
		return dataList;
	}
	
	
	public List<List<TPORecord>> queryDataGroupByTPOName(String userObject){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		String sql = "select * from " + TABLE + " where " +USER_OBJECT_ID+"=? order by " + TPO_INDEX + " asc , "+QUESTION_INDEX+" asc";
		Cursor cursor = db.rawQuery(sql, new String[]{userObject});
		
		
		List<List<TPORecord>> TPOGroup=new ArrayList<List<TPORecord>>();
		List<TPORecord> dataList=null;
		TPORecord data;
		TPORecord preData;
		if (cursor.moveToFirst()) {
			data=fromCursor(cursor);
			dataList = new LinkedList<TPORecord>();
			dataList.add(data);
			TPOGroup.add(dataList);
			preData=data;
			
			while(cursor.moveToNext()){
				data=fromCursor(cursor);
				if(!preData.TPOName.equals(data.TPOName)){
					dataList = new LinkedList<TPORecord>();
					TPOGroup.add(dataList);
				}
				
				if(preData.TPOName.equals(data.TPOName)&&preData.QuestionIndex.equals(data.QuestionIndex)){
					//两条数据相同，不需要加入
				}else{
					dataList.add(data);
				}
				preData=data;
			}
		}
		if(cursor!=null){
			cursor.close();
		}

		db.close();
		return TPOGroup;
	}

	private TPORecord fromCursor(Cursor cursor) {
		TPORecord data = new TPORecord();

		final int idIndex = cursor.getColumnIndexOrThrow(_ID);
		final int userObjectIdIndex = cursor.getColumnIndexOrThrow(USER_OBJECT_ID);
		final int nameIdIndex = cursor.getColumnIndexOrThrow(TPO_NAME);
		final int tpoIndexIndex = cursor.getColumnIndexOrThrow(TPO_INDEX);
		final int qIndex = cursor.getColumnIndexOrThrow(QUESTION_INDEX);
		final int startTimeIndex = cursor.getColumnIndexOrThrow(START_TIME);
		final int secondsIndex = cursor.getColumnIndexOrThrow(RECORDING_SECONDS);
		final int pathIndex = cursor.getColumnIndexOrThrow(RECORD_FILE_PATH);

		data.ID = cursor.getInt(idIndex);
		data.UserObject = cursor.getString(userObjectIdIndex);
		data.TPOName = cursor.getString(nameIdIndex);
		data.TPOIndex = cursor.getInt(tpoIndexIndex);
		data.QuestionIndex = cursor.getString(qIndex);
		data.StartTime = cursor.getLong(startTimeIndex);
		data.RecordingSeconds = cursor.getInt(secondsIndex);
		data.RecordFilePath= cursor.getString(pathIndex);

		return data;
	}
}
