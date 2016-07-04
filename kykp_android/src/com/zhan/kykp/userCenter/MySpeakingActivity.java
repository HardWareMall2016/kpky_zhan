package com.zhan.kykp.userCenter;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.SpeakingRecordDB;
import com.zhan.kykp.entity.dbobject.SpeakingRecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class MySpeakingActivity extends BaseActivity implements OnCompletionListener,OnItemLongClickListener{
	public final static String TAG = "MySpeakingActivity";

	//Type
	private class SpeakingRecordInfo{
		SpeakingRecord RecordInfo;
		Boolean isPlaying=false;
	}
	private enum ContentType{LOCAL_RECORD,CORRECTING,CORRECTED};

	//Title
	private TextView mTVLocalRecord;
	private TextView mTVCorrecting;
	private TextView mTVCorrected;
	
	private View mEmptyView;
	
	//本地录音
	private List<SpeakingRecordInfo> mSpeakingRecordInfoList=new ArrayList<>();
	private ListView mLocalRecordList;
	private QuestionAdapter mLocalRecordAdapter;
	
	//批改中
	private ListView mCorrectingList;

	//Tools
	private LayoutInflater mInflater;
	private Resources mRes;
	private MediaPlayerHelper mMediaPlayerHelper;
	private SpeakingRecordDB mSpeakingRecordDB;

	//正在播放的录音相
	private SpeakingRecordInfo mPlayingItem;
	//显示Type
	private ContentType mContentType=ContentType.LOCAL_RECORD;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_speaking);
		
		mInflater=getLayoutInflater();
		mRes = getResources();
		mSpeakingRecordDB=new SpeakingRecordDB(this);
		mMediaPlayerHelper=new MediaPlayerHelper(this);
		
		mTVLocalRecord = (TextView) findViewById(R.id.local_recording);
		mTVCorrecting = (TextView) findViewById(R.id.correcting);
		mTVCorrected = (TextView) findViewById(R.id.corrected);
		mTVLocalRecord.setOnClickListener(mOnTitleClick);
		mTVCorrecting.setOnClickListener(mOnTitleClick);
		mTVCorrected.setOnClickListener(mOnTitleClick);
		
		mEmptyView= findViewById(R.id.empty_view);
		
		mLocalRecordList=(ListView)findViewById(R.id.local_record_list);
		mLocalRecordAdapter=new QuestionAdapter();
		mLocalRecordList.setAdapter(mLocalRecordAdapter);
		mLocalRecordList.setOnItemLongClickListener(this);
		
		mCorrectingList=(ListView)findViewById(R.id.correcting_list);
		mCorrectingList.setAdapter(mLocalRecordAdapter);

		refreshData();
	}

	@Override
	protected void onDestroy() {
		mMediaPlayerHelper.releaseMedia();
		super.onDestroy();
	}

	private void populateLocalRecordingList() {
		mSpeakingRecordInfoList.clear();
		List<SpeakingRecord> speakingRecordList = mSpeakingRecordDB.queryData(UserInfo.getCurrentUser().getObjectId());
		for(SpeakingRecord record:speakingRecordList){
			SpeakingRecordInfo info=new SpeakingRecordInfo();
			info.RecordInfo=record;
			mSpeakingRecordInfoList.add(info);
		}
		mLocalRecordAdapter.notifyDataSetChanged();
		refreshContent();
	}
	
	
	private void loadCorrectingOrCorrectedList(boolean corrected) {

	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		final Dialog dialog = new Dialog(this, R.style.Dialog);
		View dlgContent = mInflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.delete_confirm);
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setVisibility(View.GONE);
		dialog.setContentView(dlgContent);
		dialog.show();

		View confirmBtn = dlgContent.findViewById(R.id.confirm);
		View cancelBtn = dlgContent.findViewById(R.id.cancel);
		confirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_speaking_answer_record, R.string.confirm);
				//停止播放
				stopMedia();
				//删除文件和记录
				File audioFile = new File(mSpeakingRecordInfoList.get(position).RecordInfo.recordingFilePath);
				if (audioFile.exists()) {
					audioFile.delete();
				}
				mSpeakingRecordDB.delete(mSpeakingRecordInfoList.get(position).RecordInfo.ID);
				//刷新数据
				refreshData();
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_speaking_answer_record, R.string.cancel);
				dialog.dismiss();
			}
		});
		return false;
	}

	private class LocalRecordViewHolder {
		ImageView Icon;
		TextView Content;
		TextView Name;
		
		ImageView imgPlayStop;
		TextView recordSeconds;
		TextView recordStartTime;
		Button apply;
		Button viewDetails;
	}

	private class QuestionAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mSpeakingRecordInfoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mSpeakingRecordInfoList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LocalRecordViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_speaking_list_item, null);
				holder = new LocalRecordViewHolder();
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Content = (TextView) convertView.findViewById(R.id.content);
				holder.Name = (TextView) convertView.findViewById(R.id.name);
				
				holder.imgPlayStop = (ImageView) convertView.findViewById(R.id.img_play_stop);
				holder.recordSeconds = (TextView) convertView.findViewById(R.id.seconds);
				holder.recordStartTime = (TextView) convertView.findViewById(R.id.start_time);
				holder.apply= (Button) convertView.findViewById(R.id.btn_apply);
				holder.viewDetails= (Button) convertView.findViewById(R.id.btn_view_correct_details);
				
				convertView.setTag(holder);
			} else {
				holder = (LocalRecordViewHolder) convertView.getTag();
			}

			if(mContentType==ContentType.LOCAL_RECORD){
				holder.apply.setVisibility(View.VISIBLE);
				holder.apply.setTag(position);
				holder.apply.setOnClickListener(mOnApplyButtonClick);
				holder.viewDetails.setVisibility(View.GONE);
			}else if(mContentType==ContentType.CORRECTING){
				holder.viewDetails.setVisibility(View.GONE);
				holder.apply.setVisibility(View.GONE);
			}else{
				holder.viewDetails.setVisibility(View.VISIBLE);
				holder.viewDetails.setTag(position);
				holder.viewDetails.setOnClickListener(mOnViewDetailsButtonClick);
				
				holder.apply.setVisibility(View.GONE);
			}
			
			//暂时隐藏，批改
			holder.apply.setVisibility(View.GONE);

			SpeakingRecordInfo itemInfo=mSpeakingRecordInfoList.get(position);

			holder.imgPlayStop.setTag(itemInfo);
			holder.imgPlayStop.setOnClickListener(mOnPlayButtonClick);

			holder.Content.setText(itemInfo.RecordInfo.title);

			holder.Name.setText(itemInfo.RecordInfo.subTitle);

			holder.recordSeconds.setText(String.format("%d\"", itemInfo.RecordInfo.recordingSeconds));

			if(itemInfo.isPlaying){
				holder.imgPlayStop.setImageResource(R.drawable.audio_pause_selector);
			}else{
				holder.imgPlayStop.setImageResource(R.drawable.audio_paly_selector);
			}

			//托福
			if(itemInfo.RecordInfo.questionType== SpeakingRecord.TYPE_TOEFL){
				String topic = itemInfo.RecordInfo.imgKeyWord;
				if(topic.equals("activity")){
					holder.Icon.setImageResource(R.drawable.sample8);
				}else if (topic.equals("advice")){
					holder.Icon.setImageResource(R.drawable.sample2);
				}else if(topic.equals("education")){
					holder.Icon.setImageResource(R.drawable.sample7);
				}else if(topic.equals("experience")){
					holder.Icon.setImageResource(R.drawable.sample3);
				}else if(topic.equals("job")){
					holder.Icon.setImageResource(R.drawable.sample5);
				}else if(topic.equals("leisure time")){
					holder.Icon.setImageResource(R.drawable.sample1);
				}else if(topic.equals("life")){
					holder.Icon.setImageResource(R.drawable.sample9);
				}else if(topic.equals("media")){
					holder.Icon.setImageResource(R.drawable.sample10);
				}else if(topic.equals("modern tech.")){
					holder.Icon.setImageResource(R.drawable.sample6);
				}else if(topic.equals("object")){
					holder.Icon.setImageResource(R.drawable.sample11);
				}else if(topic.equals("person")){
					holder.Icon.setImageResource(R.drawable.sample4);
				}else if(topic.equals("place")){
					holder.Icon.setImageResource(R.drawable.sample12);
				}else if(topic.equals("random")){
					holder.Icon.setImageResource(R.drawable.sample13);
				}else if(topic.equals("skill")){
					holder.Icon.setImageResource(R.drawable.sample14);
				}else if(topic.equals("money")){
					holder.Icon.setImageResource(R.drawable.sample15);
				}
			}else{
				//雅思
				String test=itemInfo.RecordInfo.imgKeyWord;
				if(test.equals("1")){
					holder.Icon.setImageResource(R.drawable.sample1);
				}else if(test.equals("2")){
					holder.Icon.setImageResource(R.drawable.sample2);
				}else if(test.equals("3")){
					holder.Icon.setImageResource(R.drawable.sample3);
				}else if(test.equals("4")){
					holder.Icon.setImageResource(R.drawable.sample4);
				}
			}
			return convertView;
		}
	}
	
	private OnClickListener mOnTitleClick=new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			stopMedia();
			ContentType type = null;
			switch(arg0.getId()){
			case R.id.local_recording:
				type=ContentType.LOCAL_RECORD;
				break;
			case R.id.correcting:
				type=ContentType.CORRECTING;
				break;
			case R.id.corrected:
				type=ContentType.CORRECTED;
				break;
			}
			if(type!=mContentType){
				mContentType=type;
				refreshData();
			}
		}
	};
	
	private void refreshData() {
		switch (mContentType) {
		case LOCAL_RECORD:
			mTVLocalRecord.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVLocalRecord.setTextColor(mRes.getColor(R.color.dark_red));
			mTVCorrecting.setBackgroundColor(Color.WHITE);
			mTVCorrecting.setTextColor(mRes.getColor(R.color.text_color_content));
			mTVCorrected.setBackgroundColor(Color.WHITE);
			mTVCorrected.setTextColor(mRes.getColor(R.color.text_color_content));
			
			populateLocalRecordingList();
			break;
		case CORRECTING:
			mTVLocalRecord.setBackgroundColor(Color.WHITE);
			mTVLocalRecord.setTextColor(mRes.getColor(R.color.text_color_content));
			mTVCorrecting.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVCorrecting.setTextColor(mRes.getColor(R.color.dark_red));
			mTVCorrected.setBackgroundColor(Color.WHITE);
			mTVCorrected.setTextColor(mRes.getColor(R.color.text_color_content));
			
			loadCorrectingOrCorrectedList(false);
			break;
		case CORRECTED:
			mTVLocalRecord.setBackgroundColor(Color.WHITE);
			mTVLocalRecord.setTextColor(mRes.getColor(R.color.text_color_content));
			mTVCorrecting.setBackgroundColor(Color.WHITE);
			mTVCorrecting.setTextColor(mRes.getColor(R.color.text_color_content));
			mTVCorrected.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVCorrected.setTextColor(mRes.getColor(R.color.dark_red));
			loadCorrectingOrCorrectedList(true);
			break;
		}
	}
	
	private void refreshContent(){
		if(mSpeakingRecordInfoList.size()==0){
			mEmptyView.setVisibility(View.VISIBLE);
			mLocalRecordList.setVisibility(View.GONE);
			mCorrectingList.setVisibility(View.GONE);
		}else{
			mEmptyView.setVisibility(View.GONE);
			switch (mContentType) {
			case LOCAL_RECORD:
				mLocalRecordList.setVisibility(View.VISIBLE);
				mCorrectingList.setVisibility(View.GONE);
				break;
			case CORRECTING:
				mLocalRecordList.setVisibility(View.GONE);
				mCorrectingList.setVisibility(View.VISIBLE);
				break;
			case CORRECTED:
				mLocalRecordList.setVisibility(View.GONE);
				mCorrectingList.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
	
	private OnClickListener mOnApplyButtonClick=new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			/*stopMedia();
			mPlayingPosition=-1;
			mLocalRecordAdapter.notifyDataSetChanged();
			
			int position=(Integer) arg0.getTag();
			SpeakingRecord record=mSpeakingRecordInfoList.get(position).RecordInfo;
			checkAndApplyForCorrecting(record);*/
		}
	};
	
	private OnClickListener mOnViewDetailsButtonClick=new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			/*stopMedia();
			mPlayingPosition=-1;
			mLocalRecordAdapter.notifyDataSetChanged();
			
			int position=(Integer) arg0.getTag();
			SpeakingRecord record=mSpeakingRecordInfoList.get(position).RecordInfo;
			//applyForCorrecting(record);
			Intent intent=new Intent(MySpeakingActivity.this,MySpeakingCorrectDetailsActivity.class);
			intent.putExtra(MySpeakingCorrectDetailsActivity.EXT_KEY_OBJECT_ID, record.objectId);
			startActivity(intent);*/
		}
	};
	
	private OnClickListener mOnPlayButtonClick=new OnClickListener(){
		@Override
		public void onClick(View view) {
			StatisticUtils.onEvent(R.string.personal_center, R.string.statistic_kouyubofang);
			SpeakingRecordInfo itemInfo=(SpeakingRecordInfo)view.getTag();
			//是否点击的是同一个录音
			if(itemInfo==mPlayingItem){
				if(itemInfo.isPlaying){
					stopMedia();
				}else{
					playMedia(itemInfo);
				}
			}else {
				stopMedia();
				playMedia(itemInfo);
			}
			mPlayingItem=itemInfo;
		}
	};

	@Override
	public void onCompletion(MediaPlayer arg0) {
		stopMedia();
	}

	private void stopMedia(){
		if(mPlayingItem!=null){
			mPlayingItem.isPlaying=false;
			mPlayingItem=null;
		}
		if(mMediaPlayerHelper!=null){
			mMediaPlayerHelper.stopMedia();
		}
		mLocalRecordAdapter.notifyDataSetChanged();
	}

	private void playMedia(SpeakingRecordInfo itemInfo){
		File file=new File(itemInfo.RecordInfo.recordingFilePath);
		if(!file.exists()){
			Utils.toast(R.string.recording_not_exist);
			return;
		}
		itemInfo.isPlaying=true;
		mMediaPlayerHelper.play(itemInfo.RecordInfo.recordingFilePath);
		mLocalRecordAdapter.notifyDataSetChanged();
	}
}
