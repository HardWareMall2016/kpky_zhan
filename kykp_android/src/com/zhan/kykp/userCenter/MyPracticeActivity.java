package com.zhan.kykp.userCenter;

import java.io.File;
import java.util.List;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.PracticeRecordDB;
import com.zhan.kykp.entity.dbobject.PracticeRecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.practice.PracticeHelper;
import com.zhan.kykp.practice.PracticeHelper.PracticeJsonObj;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class MyPracticeActivity extends BaseActivity implements OnClickListener, OnCompletionListener,View.OnLongClickListener {

	private ListView mListView;
	private PracticeAdapter mAdapter;

	private PracticeRecordDB mPracticeRecordDB;
	private List<List<PracticeRecord>> mDataList;

	private PracticeHelper mPracticeHelper;
	private MediaPlayerHelper mMediaPlayerHelper;
	
	//正在播放的录音信息
	private PracticeRecord  mPlayingRecording;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_practice);

		mListView = (ListView) findViewById(R.id.list);
		
		View emptyView =findViewById(R.id.empty);
		mListView.setEmptyView(emptyView);

		mPracticeHelper = PracticeHelper.getInstance();
		mMediaPlayerHelper =new MediaPlayerHelper(this);
		//mMediaPlayerHelper.initMediaPlayer(this);

		mPracticeRecordDB = new PracticeRecordDB(this);
		mDataList = mPracticeRecordDB.queryDataGroupByIndex(UserInfo.getCurrentUser().getObjectId());

		mAdapter = new PracticeAdapter();
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		mMediaPlayerHelper.releaseMedia();
		super.onDestroy();
	}

	private class PracticeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.my_practice_list_item, null);
				holder = new ViewHolder();
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Title = (TextView) convertView.findViewById(R.id.title);
				holder.SubTitle = (TextView) convertView.findViewById(R.id.sub_title);
				holder.RecordingContent = (LinearLayout) convertView.findViewById(R.id.recording_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PracticeRecord record = mDataList.get(position).get(0);

			PracticeJsonObj practicejson = mPracticeHelper.parsePracticeFromJsonFile(record.PracticeIndex);

			holder.Title.setText(practicejson.TitleTranslation);
			holder.SubTitle.setText(practicejson.CoreTopic);

			if (record.PracticeIndex % 7 == 0) {
				holder.Icon.setImageResource(R.drawable.sample1);
			} else if (record.PracticeIndex % 7 == 1) {
				holder.Icon.setImageResource(R.drawable.sample2);
			} else if (record.PracticeIndex % 7 == 2) {
				holder.Icon.setImageResource(R.drawable.sample3);
			} else if(record.PracticeIndex % 7 == 3){
				holder.Icon.setImageResource(R.drawable.sample7);
			}else if(record.PracticeIndex % 7 == 4){
				holder.Icon.setImageResource(R.drawable.sample5);
			}else if(record.PracticeIndex % 7 == 5){
				holder.Icon.setImageResource(R.drawable.sample6);
			}else if(record.PracticeIndex % 7 == 6){
				holder.Icon.setImageResource(R.drawable.sample4);
			}else {
				holder.Icon.setImageResource(R.drawable.sample12);
			}

			// 录音信息
			holder.RecordingContent.removeAllViews();
			for (PracticeRecord recordingInfo : mDataList.get(position)) {
				View reconrdingLayout = getLayoutInflater().inflate(R.layout.my_practice_list_item_recording_item, null);
				reconrdingLayout.setOnLongClickListener(MyPracticeActivity.this);
				reconrdingLayout.setTag(recordingInfo);

				holder.RecordingContent.addView(reconrdingLayout);

				TextView recordingSeconds = (TextView) reconrdingLayout.findViewById(R.id.recording_seconds);
				TextView recordingStartTime = (TextView) reconrdingLayout.findViewById(R.id.recording_start_time);
				ImageView palyPauseView =(ImageView) reconrdingLayout.findViewById(R.id.imgPlayPause);

				recordingSeconds.setText(String.format("%d\"", recordingInfo.RecordingSeconds));
				recordingStartTime.setText(Utils.getFullFormateTimeStr(recordingInfo.StartTime));
				
				if(mPlayingRecording!=null&&mPlayingRecording.ID==recordingInfo.ID){
					palyPauseView.setImageResource(R.drawable.audio_pause_selector);
				}else{
					palyPauseView.setImageResource(R.drawable.audio_paly_selector);	
				}
				palyPauseView.setOnClickListener(MyPracticeActivity.this);
				palyPauseView.setTag(recordingInfo);
			}

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView Icon;
		TextView Title;
		TextView SubTitle;
		LinearLayout RecordingContent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgPlayPause:
			StatisticUtils.onEvent(R.string.my_practice, R.string.statistic_mypraticebofang);
			PracticeRecord recording=(PracticeRecord)v.getTag();
			//没有正在播放的录音或正在播放的不是当前的，都开始播放当前的
			if(mPlayingRecording==null||mPlayingRecording.ID!=recording.ID){
				mPlayingRecording=recording;
				mMediaPlayerHelper.play(mPlayingRecording.RecordFilePath);
			}else{//点击当前的，就停止
				mMediaPlayerHelper.stopMedia();
				mPlayingRecording=null;
			}
			mAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mAdapter.notifyDataSetChanged();
		mPlayingRecording=null;
	}


	@Override
	public boolean onLongClick(View v) {
		final PracticeRecord recordingInfo= (PracticeRecord) v.getTag();

		LayoutInflater inflater=LayoutInflater.from(this);

		final Dialog dialog = new Dialog(this, R.style.Dialog);
		View dlgContent = inflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.delete_confirm);
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setVisibility(View.GONE);
		dialog.setContentView(dlgContent);
		dialog.show();

		View confirmBtn = dlgContent.findViewById(R.id.confirm);
		View cancelBtn = dlgContent.findViewById(R.id.cancel);
		confirmBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_practice, R.string.confirm);
				mMediaPlayerHelper.stopMedia();

				File audioFile=new File(recordingInfo.RecordFilePath);
				if(audioFile.exists()){
					audioFile.delete();
				}
				mPracticeRecordDB.delete(recordingInfo.ID);

				mDataList = mPracticeRecordDB.queryDataGroupByIndex(UserInfo.getCurrentUser().getObjectId());
				mAdapter.notifyDataSetChanged();

				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_practice, R.string.cancel);
				dialog.dismiss();
			}
		});
		return false;
	}
}
