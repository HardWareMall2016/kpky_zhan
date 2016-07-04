package com.zhan.kykp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "xiao_zhan.db";

	private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TPORecordDB.TABLE + " ("
                    + TPORecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TPORecordDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                    + TPORecordDB.TPO_NAME + " VARCHAR(32) NOT NULL,"
                    + TPORecordDB.TPO_INDEX + " INTEGER NOT NULL,"
                    + TPORecordDB.QUESTION_INDEX + " VARCHAR(32) NOT NULL,"
                    + TPORecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                    + TPORecordDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                    + TPORecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL"
                    + ");");

            db.execSQL("CREATE TABLE " + PracticeRecordDB.TABLE + " ("
                    + PracticeRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PracticeRecordDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                    + PracticeRecordDB.PRACTICE_INDEX + " INTEGER NOT NULL,"
                    + PracticeRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                    + PracticeRecordDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                    + PracticeRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL"
                    + ");");

            db.execSQL("CREATE TABLE " + SpeakingRecordDB.TABLE + " ("
                    + SpeakingRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SpeakingRecordDB.QUESTION_OBJECT_ID + " TEXT NOT NULL,"
                    + SpeakingRecordDB.USER_OBJECT_ID + " TEXT NOT NULL ,"
                    + SpeakingRecordDB.IMG_KEY_WORD + " TEXT DEFAULT NULL ,"
                    + SpeakingRecordDB.TYPE + " INTEGER NOT NULL ,"
                    + SpeakingRecordDB.TITLE + " TEXT DEFAULT NULL ,"
                    + SpeakingRecordDB.SUB_TITLE + " TEXT DEFAULT NULL ,"
                    + SpeakingRecordDB.EXT_DATA + " TEXT DEFAULT NULL ,"
                    + SpeakingRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                    + SpeakingRecordDB.RECORDING_SECONDS + "  INTEGER NOT NULL DEFAULT 0 ,"
                    + SpeakingRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL "
                    + ");");

        /*db.execSQL("CREATE TABLE " + SpeakingRecordDB.TABLE + " ("
                + SpeakingRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SpeakingRecordDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                + SpeakingRecordDB.UID + " VARCHAR(32) NOT NULL,"
                + SpeakingRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                + SpeakingRecordDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                + SpeakingRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL ,"
                + SpeakingRecordDB.OBJECT_ID + " TEXT DEFAULT NULL "
                + ");");*/

        /*db.execSQL("CREATE TABLE " + ReadRecordDB.TABLE + " ("
                + ReadRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReadRecordDB.TAG_NAME + " TEXT NOT NULL,"
                + ReadRecordDB.HAS_READ + " INTEGER NOT NULL DEFAULT 0,"
                + ReadRecordDB.UPDATE_TIME + " INTEGER NOT NULL DEFAULT 0"
                + ");");*/
        
        /*db.execSQL("CREATE TABLE " + SpeakingRecordIeltsDB.TABLE + " ("
                + SpeakingRecordIeltsDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SpeakingRecordIeltsDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                + SpeakingRecordIeltsDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                + SpeakingRecordIeltsDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                + SpeakingRecordIeltsDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL ,"
                + SpeakingRecordIeltsDB.OBJECT_ID + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.BOOK + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.PART + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.QUESTION + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.QUESTION_TITLE + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.QUESTION_TYPE + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.TEST + " TEXT DEFAULT NULL,"
                + SpeakingRecordIeltsDB.INDEX + " INTEGER NOT NULL DEFAULT 1"
                + ");");*/
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DBHelper","onUpgrade oldVersion = "+oldVersion+" newVersion = "+newVersion);
            //删除无用表
            db.execSQL("DROP TABLE IF EXISTS SPEAKING_RECORD");
            db.execSQL("DROP TABLE IF EXISTS ReadRecord");
            db.execSQL("DROP TABLE IF EXISTS SPEAKING_RECORD_IELTS");

            if (oldVersion == 1 && newVersion == 3) {
                    db.execSQL("CREATE TABLE " + PracticeRecordDB.TABLE + " ("
                            + PracticeRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + PracticeRecordDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                            + PracticeRecordDB.PRACTICE_INDEX + " INTEGER NOT NULL,"
                            + PracticeRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                            + PracticeRecordDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                            + PracticeRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL"
                            + ");");

                    db.execSQL("CREATE TABLE " + SpeakingRecordDB.TABLE + " ("
                            + SpeakingRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + SpeakingRecordDB.QUESTION_OBJECT_ID + " TEXT NOT NULL,"
                            + SpeakingRecordDB.USER_OBJECT_ID + " TEXT NOT NULL ,"
                            + SpeakingRecordDB.IMG_KEY_WORD + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.TYPE + " INTEGER NOT NULL ,"
                            + SpeakingRecordDB.TITLE + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.SUB_TITLE + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.EXT_DATA + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                            + SpeakingRecordDB.RECORDING_SECONDS + "  INTEGER NOT NULL DEFAULT 0 ,"
                            + SpeakingRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL "
                            + ");");
            }

            if (oldVersion == 2 && newVersion == 3) {
                    db.execSQL("CREATE TABLE " + SpeakingRecordDB.TABLE + " ("
                            + SpeakingRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + SpeakingRecordDB.QUESTION_OBJECT_ID + " TEXT NOT NULL,"
                            + SpeakingRecordDB.USER_OBJECT_ID + " TEXT NOT NULL ,"
                            + SpeakingRecordDB.IMG_KEY_WORD + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.TYPE + " INTEGER NOT NULL ,"
                            + SpeakingRecordDB.TITLE + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.SUB_TITLE + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.EXT_DATA + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                            + SpeakingRecordDB.RECORDING_SECONDS + "  INTEGER NOT NULL DEFAULT 0 ,"
                            + SpeakingRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL "
                            + ");");
            }

            /*if (oldVersion == 1 && newVersion == 2) {
                    db.execSQL("CREATE TABLE " + PracticeRecordDB.TABLE + " ("
                            + PracticeRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + PracticeRecordDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                            + PracticeRecordDB.PRACTICE_INDEX + " INTEGER NOT NULL,"
                            + PracticeRecordDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                            + PracticeRecordDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                            + PracticeRecordDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL"
                            + ");");

                    db.execSQL("CREATE TABLE " + ReadRecordDB.TABLE + " ("
                            + ReadRecordDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + ReadRecordDB.TAG_NAME + " TEXT NOT NULL,"
                            + ReadRecordDB.HAS_READ + " INTEGER NOT NULL DEFAULT 0,"
                            + ReadRecordDB.UPDATE_TIME + " INTEGER NOT NULL DEFAULT 0"
                            + ");");

                    db.execSQL("CREATE TABLE " + SpeakingRecordIeltsDB.TABLE + " ("
                            + SpeakingRecordIeltsDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + SpeakingRecordIeltsDB.USER_OBJECT_ID + " TEXT NOT NULL,"
                            + SpeakingRecordIeltsDB.START_TIME + " INTEGER NOT NULL DEFAULT 0,"
                            + SpeakingRecordIeltsDB.RECORDING_SECONDS + " INTEGER NOT NULL DEFAULT 0,"
                            + SpeakingRecordIeltsDB.RECORD_FILE_PATH + " TEXT DEFAULT NULL ,"
                            + SpeakingRecordIeltsDB.OBJECT_ID + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.BOOK + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.PART + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.QUESTION + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.QUESTION_TITLE + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.QUESTION_TYPE + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.TEST + " TEXT DEFAULT NULL,"
                            + SpeakingRecordIeltsDB.INDEX + " INTEGER NOT NULL DEFAULT 1"
                            + ");");

                    Log.i("DBHelper", "onUpgrade success");
            }*/
    }

}
