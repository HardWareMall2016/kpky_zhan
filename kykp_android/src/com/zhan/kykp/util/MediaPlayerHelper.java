package com.zhan.kykp.util;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class MediaPlayerHelper {
	private final static String TAG=MediaPlayerHelper.class.getSimpleName();

	private MediaPlayer mMediaPlayer;
	private boolean mIsPause=false;

	public MediaPlayerHelper(MediaPlayer.OnCompletionListener listener) {
		try {
			mMediaPlayer = new MediaPlayer();
			if(listener!=null){
				mMediaPlayer.setOnCompletionListener(listener);
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException : " + e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException : " + e.getMessage());
		}
	}

	public void play(String audioFile) {
		try {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			
			mMediaPlayer.setDataSource(audioFile);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mIsPause=false;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException : " + e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException : " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : " + e.getMessage());
		}
	}

	public void pause(){
		mMediaPlayer.pause();
		mIsPause=true;
	}

	public void replay(){
		mMediaPlayer.start();
		mIsPause=false;
	}

	public int getCurrentPosition(){
		return mMediaPlayer.getCurrentPosition();
	}
	
	public int getDuration(){
		return mMediaPlayer.getDuration();
	}
	
	public void stopMedia() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
		mIsPause=false;
	}

	public void releaseMedia() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mIsPause=false;
	}
	
	public boolean isReleased(){
		return mMediaPlayer==null;
	}
	
	public boolean isPlaying(){
		return mMediaPlayer.isPlaying();
	}

	public boolean isPaused(){
		return mIsPause;
	}
}
