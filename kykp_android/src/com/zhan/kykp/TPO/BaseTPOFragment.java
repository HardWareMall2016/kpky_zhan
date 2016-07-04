package com.zhan.kykp.TPO;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseTPOFragment extends Fragment implements OnCompletionListener{
	private final static String TAG = BaseTPOFragment.class.getSimpleName();
	
	protected static final String ARG_QUESTION_INDEX_KEY = "subfix_key";
	protected static final String ARG_TITLE_SKIP_KEY = "skip";

	protected TPOCallback mTPOCallback;
	
	private MediaPlayer mMediaPlayer;
	protected String mQuestionIndex;
	protected boolean mSkip;
	
	public void prepareArguments(String questionIndex,boolean showSkip){
		//Log.i("TEST", "prepareArguments");
		Bundle args = new Bundle();
		args.putString(ARG_QUESTION_INDEX_KEY, questionIndex);
		args.putBoolean(ARG_TITLE_SKIP_KEY, showSkip);
		setArguments(args);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//Log.i("TEST", "onAttach : "+mKey);
		mTPOCallback=(TPOCallback)activity;
	}
	
	public void release(){
		stopSpeaking();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.i("TEST", "onCreate");
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mQuestionIndex = args.getString(ARG_QUESTION_INDEX_KEY);
		mSkip=args.getBoolean(ARG_TITLE_SKIP_KEY);
		
		mTPOCallback.setTPOTitle(getTPOTitle(),mSkip);
		initMediaPlayer();
	}
	
	public String getTPOTitle(){
		return mTPOCallback.getTPOName()+"-"+mQuestionIndex;
	}
	
	@Override
	public void onDestroyView() {
		release();
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTPOCallback=null;
	}

	public String getKey(){
		return mQuestionIndex;
	}
	
	private void initMediaPlayer(){
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(this);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException : " + e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException : " + e.getMessage());
		}
	}
	
	public void play(String TPOSubPath){
		try {
			mMediaPlayer.setDataSource(mTPOCallback.getTPOFilePath(TPOSubPath));
			mMediaPlayer.prepare();
			mMediaPlayer.start();
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

	private void stopSpeaking() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();// ֹͣ����
			mMediaPlayer.release();// �ͷ���Դ
			mMediaPlayer = null;
		}
	}
}
