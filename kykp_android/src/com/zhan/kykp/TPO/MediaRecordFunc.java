package com.zhan.kykp.TPO;

import android.media.MediaRecorder;

import com.zhan.kykp.util.PathUtils;

import java.io.File;
import java.io.IOException;

public class MediaRecordFunc {
	public final static int SUCCESS = 1000;
	public final static int E_NOSDCARD = 1001;
	public final static int E_STATE_RECODING = 1002;
	public final static int E_UNKOWN = 1003;

	private boolean isRecord = false;

	private MediaRecorder mMediaRecorder;

	private MediaRecordFunc() {
	}

	private static MediaRecordFunc mInstance;

	public synchronized static MediaRecordFunc getInstance() {
		if (mInstance == null)
			mInstance = new MediaRecordFunc();
		return mInstance;
	}

	public int startRecordAndFile(String recordFilePath) {
		// 判断是否有外部存储设备sdcard
		if (PathUtils.isExternalStorageWritable()) {
			if (isRecord) {
				return E_STATE_RECODING;
			} else {
				if (mMediaRecorder == null)
					createMediaRecord(recordFilePath);
				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					// 让录制状态为true
					isRecord = true;
					return SUCCESS;
				} catch (IOException ex) {
					ex.printStackTrace();
					return E_UNKOWN;
				}
			}
		} else {
			return E_NOSDCARD;
		}
	}

	public void stopRecordAndFile() {
		close();
	}
	
	public boolean isRecording(){
		return isRecord;
	}

	private void createMediaRecord(String recordFilePath) {
		/* ①Initial：实例化MediaRecorder对象 */
		mMediaRecorder = new MediaRecorder();

		/* setAudioSource/setVedioSource */
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风

		/*
		 * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);

		/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		/* 设置输出文件的路径 */
		File file = new File(recordFilePath);
		if (file.exists()) {
			file.delete();
		}
		mMediaRecorder.setOutputFile(recordFilePath);
	}

	private void close() {
		if (mMediaRecorder != null) {
			isRecord = false;
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}
	
}
