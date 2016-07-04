package com.zhan.kykp.util;

import android.os.Environment;

import com.zhan.kykp.base.App;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by wuyue on 15-7-21.
 */
public class PathUtils {
	//模考，下载地址
	private final static String TOP = "TPO";
	//录音
	private final static String RECORD = "RECORD";
	//头像
	private final static String PIC = "PIC";
	//跟读训练，下载地址
	private final static String PRACTICE = "PRACTICE";

	//临时目录
	private final static String TEMP = "TEMP";
	//名人英语
	private final static String CELEBRITY = "CELEBRITY";
	//口语广场
	private final static String SPOKEN_SQUERE = "SPOKEN_SQUERE";
	//音乐调频
	private final static String MUSIC = "MUSIC" ;


	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private static File getAvailableCacheDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalCacheDir();
		} else {
			return App.ctx.getCacheDir();
		}
	}

	public static String checkAndMkdirs(String dir) {
		File file = new File(dir);
		if (file.exists() == false) {
			file.mkdirs();
		}
		return dir;
	}

	public static String getAvatarCropPath() {
		return new File(getAvailableCacheDir(), "avatar_crop").getAbsolutePath();
	}

	public static String getAvatarTmpPath() {
		return new File(getAvailableCacheDir(), "avatar_tmp").getAbsolutePath();
	}

	public static File getExternalTPOFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(TOP);
		} else {
			return null;
		}
	}
	
	public static File getExternalRecordFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(RECORD);
		} else {
			return null;
		}
	}
	
	public static File getExternalPicFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(PIC);
		} else {
			return null;
		}
	}
	
	public static File getExternalPracticeFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(PRACTICE);
		} else {
			return null;
		}
	}

	public static File getExternalTempFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(TEMP);
		} else {
			return null;
		}
	}

	public static File getExternalSpokenSquareFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(TEMP+"/"+SPOKEN_SQUERE);
		} else {
			return null;
		}
	}

	public static File getExternalCelebrityFilesDir() {
		if (isExternalStorageWritable()) {
			return App.ctx.getExternalFilesDir(TEMP+"/"+CELEBRITY);
		} else {
			return null;
		}
	}

	public static File getExternalMusicFileDir(){
		if(isExternalStorageWritable()){
			return App.ctx.getExternalFilesDir(TEMP+"/"+MUSIC);
		}else{
			return null;
		}
	}

	
	public static String getUserAvatarPath(String userObjectId) {
		return getExternalPicFilesDir().getPath()+ "/"+userObjectId+".png";
	}
	
	/**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
	public static long recursionDeleteFile(File file) {
		if (file == null) {
			return 0;
		}
		long fileSize = 0;
		if (file.isFile()) {
			fileSize = file.length();
			file.delete();
			return fileSize;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return 0;
			}
			for (File f : childFile) {
				fileSize += recursionDeleteFile(f);
			}
			file.delete();
		}
		return fileSize;
	}
	
	/** 
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节) 
     *  
     * @param bytes 
     * @return 
     */  
	public static String bytes2kb(long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
		if (returnValue > 1)
			return (returnValue + "MB");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
		return (returnValue + "KB");
	} 
}
