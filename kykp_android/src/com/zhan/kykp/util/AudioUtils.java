package com.zhan.kykp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.zhan.kykp.base.App;

public class AudioUtils {
	/**
	 * 需求:将两个amr格式音频文件合并为1个 注意:amr格式的头文件为6个字节的长度
	 * 
	 * @param partsPaths
	 *            各部分路径
	 * @param unitedFilePath
	 *            合并后路径
	 */
	public static boolean uniteAMRFile(String[] partsPaths, String unitedFilePath) {
		try {
			File unitedFile = new File(unitedFilePath);
			FileOutputStream fos = new FileOutputStream(unitedFile);
			RandomAccessFile ra = null;
			for (int i = 0; i < partsPaths.length; i++) {
				ra = new RandomAccessFile(partsPaths[i], "r");
				if (i != 0) {
					ra.seek(6);
				}
				byte[] buffer = new byte[1024 * 8];
				int len = 0;
				while ((len = ra.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			}
			fos.flush();
			ra.close();
			fos.close();
			return true;
		} catch (Exception e) {
			Log.e("AudioUtils", "uniteAMRFile Exception : "+e.getMessage());
			return false;
		}
	}
	
	
	public static int  getAudioSeconds(String audioPath){
		MediaPlayer mp = MediaPlayer.create(App.ctx, Uri.parse(audioPath));
		int seconds=mp.getDuration()/1000;
		mp.release();
		mp=null;
		return seconds;
	}
}
