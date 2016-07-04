package com.zhan.kykp.TPO;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhan.kykp.R;
import com.zhan.kykp.widget.HorizontalProgressBarWithNumber;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentReading extends BaseTPOFragment implements OnCompletionListener {
	private final static String TAG = FragmentReading.class.getSimpleName();
	private ImageView mImgTip;
	private TextView mTVRemainTime;
	private TextView mTVTitle;
	private TextView mTVContent;
	private HorizontalProgressBarWithNumber mProgress;
	
	private CountDownTimer mCountDownTimer;
	
	private String mStrFormatRemainTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tpo_reading_layout, null);
		mTVTitle = (TextView) rootView.findViewById(R.id.title);
		mTVContent = (TextView) rootView.findViewById(R.id.content);

		mTVRemainTime = (TextView) rootView.findViewById(R.id.remain_seconds);
		mStrFormatRemainTime=getString(R.string.tpo_remain_time);
		mTVRemainTime.setText(String.format(mStrFormatRemainTime, 45));
		
		mImgTip=(ImageView)rootView.findViewById(R.id.img_tip);
		mTVContent.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		
		mProgress=(HorizontalProgressBarWithNumber)rootView.findViewById(R.id.progress);
		mProgress.setProgress(0);
		
		mImgTip.setVisibility(View.VISIBLE);
		mTVTitle.setVisibility(View.GONE);
		mTVContent.setVisibility(View.GONE);
		
		JSONObject tpoJson = mTPOCallback.getTPOJson();
		String jsonAttr=null;
		try {
			if (mQuestionIndex.equals(TPOConstant.Q3)){
				play(TPOConstant.speaking_question3_beforeread);
				jsonAttr=TPOConstant.TPO_JSON_ATTR_TASK3_READING;
			}else if (mQuestionIndex.equals(TPOConstant.Q4)){
				play(TPOConstant.speaking_question4_beforeread);
				jsonAttr=TPOConstant.TPO_JSON_ATTR_TASK4_READING;
			}
			String content = tpoJson.getString(jsonAttr);
			
			String[]  strArray=TextUtils.split(content, "\n");
			
			mTVTitle.setText(strArray[0]);
			mTVContent.setText(strArray[1]);
			
		} catch (JSONException e) {
			Log.i(TAG, "Error parse json : " + jsonAttr);
		}

		return rootView;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		release();
		//mTPOCallback.gotoNext();
		mImgTip.setVisibility(View.GONE);
		mTVTitle.setVisibility(View.VISIBLE);
		mTVContent.setVisibility(View.VISIBLE);
		mProgress.setProgress(0);
		
		mCountDownTimer=new CountDownTimer(1000 * 45, 100) {
			@Override
			public void onFinish() {
				mCountDownTimer=null;
				if(mTPOCallback!=null){
					mTPOCallback.gotoNext();
				}
			}

			@Override
			public void onTick(long millisUntilFinished) {
				mTVRemainTime.setText(String.format(mStrFormatRemainTime, millisUntilFinished/1000));
				mProgress.setProgress((int) (100-millisUntilFinished / (1000 * 45f) * 100));
			}
		}.start();
	}

	@Override
	public void release() {
		if(mCountDownTimer!=null){
			mCountDownTimer.cancel();
			mCountDownTimer=null;
		}
		super.release();
	}
	
	
}
