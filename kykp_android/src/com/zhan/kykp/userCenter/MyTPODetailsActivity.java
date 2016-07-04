package com.zhan.kykp.userCenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.TPO.TPOActivity;
import com.zhan.kykp.TPO.TPOConstant;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.TPORecordDB;
import com.zhan.kykp.entity.dbobject.TPORecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class MyTPODetailsActivity extends BaseActivity implements OnClickListener ,OnCompletionListener,OnItemClickListener,OnItemLongClickListener{
	private final static String TAG = MyTPODetailsActivity.class.getSimpleName();
	
	public final static String EXTRA_KEY_TPO_NAME = "TPO_NAME";
	public final static String EXTRA_KEY_TPO_QUESTION = "TPO_QUESTION";
	public final static String EXTRA_KEY_TPO_INDEX = "TPO_INDEX";

	private String mTPOName;
	private String mTPOQuestion;
	private int mTPOIndex;
	private JSONObject mTPOJson;
	
	//Views
	private TextView mTVTitleMyRecording;
	private TextView mTVTitleViewSample;
	
	private View mMyRecordingContent;
	private TextView mTVQuestion;
	private TextView mTVTab;
	private ListView mRecordingList;
	
	private View mViewSampleConetnt;
	private TextView mTVSample;
	private TextView mTVSampleTab;
	
	private boolean mShowMyrecordingContent=true;
	
	private LayoutInflater mInflater;
	private TPORecordDB mTPORecordDB;
	private List<TPORecord> mRecordingdataList = new LinkedList<TPORecord>();

	private MediaPlayer mMediaPlayer;
	private RecodingAdapter mAdapter;
	private int mPlayingPosition=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_tpo_details);
		
		mInflater=LayoutInflater.from(this);

		Intent intent = getIntent();
		mTPOName = intent.getStringExtra(EXTRA_KEY_TPO_NAME);
		mTPOQuestion = intent.getStringExtra(EXTRA_KEY_TPO_QUESTION);
		mTPOIndex=intent.getIntExtra(EXTRA_KEY_TPO_INDEX, 0);

		String titleFormat = this.getString(R.string.my_tpo_details_title);
		setTitle(String.format(titleFormat, mTPOName, mTPOQuestion));

		parseTPOJson();
		initMediaPlayer();

		initView();
	}

	
	@Override
	protected void onDestroy() {
		releaseMedia();
		super.onDestroy();
	}


	private void initView(){
		mTVTitleMyRecording=(TextView)findViewById(R.id.my_recording);
		mTVTitleMyRecording.setOnClickListener(this);
		mTVTitleViewSample=(TextView)findViewById(R.id.view_example);
		mTVTitleViewSample.setOnClickListener(this);;
		
		mMyRecordingContent=findViewById(R.id.content_my_recording);
		mTVQuestion=(TextView)findViewById(R.id.tpo_question);
		mTVTab=(TextView)findViewById(R.id.question_tab);
		mRecordingList=(ListView)findViewById(R.id.recording_list);
		mRecordingList.setOnItemClickListener(this);
		initFootView();
		
		mTVQuestion.setText(getTPOQuestion(mTPOQuestion));
		mTVTab.setText(mTPOQuestion);
		
		mViewSampleConetnt=findViewById(R.id.content_view_sample);
		mTVSample=(TextView)findViewById(R.id.tpo_sample);
		mTVSampleTab=(TextView)findViewById(R.id.sample_tab);
		mTVSample.setText(getTPOSample(mTPOQuestion));
		mTVSample.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		mTVSampleTab.setText(mTPOQuestion);
		
		refreshViews();
		
		mTPORecordDB=new TPORecordDB(this);
		mRecordingdataList=mTPORecordDB.queryData(UserInfo.getCurrentUser().getObjectId(), mTPOName, mTPOQuestion);
		mAdapter=new RecodingAdapter();
		mRecordingList.setAdapter(mAdapter);
		mRecordingList.setOnItemLongClickListener(this);
	}
	
	private void refreshViews(){
		if(mShowMyrecordingContent){
			mMyRecordingContent.setVisibility(View.VISIBLE);
			mViewSampleConetnt.setVisibility(View.GONE);
			
			mTVTitleMyRecording.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVTitleMyRecording.setTextColor(getResources().getColor(R.color.dark_red));
			
			mTVTitleViewSample.setBackgroundColor(Color.WHITE);
			mTVTitleViewSample.setTextColor(getResources().getColor(R.color.text_color_content));
			
		}else{
			mMyRecordingContent.setVisibility(View.GONE);
			mViewSampleConetnt.setVisibility(View.VISIBLE);
			
			mTVTitleMyRecording.setBackgroundColor(Color.WHITE);
			mTVTitleMyRecording.setTextColor(getResources().getColor(R.color.text_color_content));
			
			mTVTitleViewSample.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVTitleViewSample.setTextColor(getResources().getColor(R.color.dark_red));
		}
	}
	
	private void initFootView(){
		View footer=mInflater.inflate(R.layout.my_tpo_tetails_footer, null);
		footer.setOnClickListener(this);
		mRecordingList.addFooterView(footer);
	}
	
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.my_recording:
			StatisticUtils.onEvent(R.string.my_tpo, R.string.my_tpo_recording);
			mShowMyrecordingContent=true;
			refreshViews();
			break;
		case R.id.view_example:
			StatisticUtils.onEvent(R.string.my_tpo, R.string.my_tpo_view_sample);
			mShowMyrecordingContent=false;
			refreshViews();
			break;
		case R.id.re_answer:
			StatisticUtils.onEvent(R.string.my_tpo, R.string.tpo_re_answer_question);
			if (mMediaPlayer.isPlaying()) {
				stopMedia();
				mPlayingPosition = -1;
				mAdapter.notifyDataSetChanged();
			}
			
			Intent TPOIntent = new Intent(this, TPOActivity.class);
			TPOIntent.putExtra(TPOActivity.EXTRA_KEY_TPO_NAME, mTPOName);
			TPOIntent.putExtra(TPOActivity.EXTRA_KEY_TPO_INDEX, mTPOIndex);
			startActivity(TPOIntent);
			break;
		}
	}
	
	private class ViewHolder {
		ImageView imgPlayPause;
		TextView recordingTime;
		TextView startTime;
	}
	
	private class RecodingAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mRecordingdataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mRecordingdataList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View conventView, ViewGroup arg2) {
			ViewHolder holder;
			if(conventView==null){
				holder=new ViewHolder();
				conventView=mInflater.inflate(R.layout.my_tpo_deatils_recording_item, null);
				holder.imgPlayPause=(ImageView)conventView.findViewById(R.id.imgPlayPause);
				holder.recordingTime=(TextView)conventView.findViewById(R.id.recording_seconds);
				holder.startTime=(TextView)conventView.findViewById(R.id.recording_start_time);
				conventView.setTag(holder);
			}else{
				holder=(ViewHolder)conventView.getTag();
			}
			
			if(mPlayingPosition==arg0){
				holder.imgPlayPause.setImageResource(R.drawable.audio_pause_selector);
			}else{
				holder.imgPlayPause.setImageResource(R.drawable.audio_paly_selector);
			}
			
			holder.recordingTime.setText(mRecordingdataList.get(arg0).RecordingSeconds+"\"");
			holder.startTime.setText(Utils.getFullFormateTimeStr(mRecordingdataList.get(arg0).StartTime));
			
			return conventView;
		}

	}
	
	private void parseTPOJson() {
		String tpoJsonPath=getTPOFilePath(TPOConstant.TPO_JSON_PATH);
		StringBuffer sb = new StringBuffer();
		File file = new File(tpoJsonPath);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			
			mTPOJson = new JSONObject(sb.toString());
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException : "+e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : "+e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : "+e.getMessage());
		}
	}
	
	private String getTPOQuestion(String question){
		String questionAttr=null;
		String questionStr=null;
		if(TPOConstant.Q1.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK1;
		}else if(TPOConstant.Q2.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK2;
		}else if(TPOConstant.Q3.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK3_QUESTION;
		}else if(TPOConstant.Q4.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK4_QUESTION;
		}else if(TPOConstant.Q5.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK5_QUESTION;
		}else if(TPOConstant.Q6.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK6_QUESTION;
		}
		
		try {
			questionStr=mTPOJson.getString(questionAttr);
		} catch (JSONException e) {
			Log.i(TAG, "getTPOQuestion JSONException : "+e.getMessage());
		}
		
		return questionStr;
	}
	
	private String getTPOSample(String question){
		String questionAttr=null;
		String questionStr=null;
		if(TPOConstant.Q1.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK1_ANSWER;
		}else if(TPOConstant.Q2.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK2_ANSWER;
		}else if(TPOConstant.Q3.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK3_ANSWER;
		}else if(TPOConstant.Q4.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK4_ANSWER;
		}else if(TPOConstant.Q5.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK5_ANSWER;
		}else if(TPOConstant.Q6.equals(question)){
			questionAttr=TPOConstant.TPO_JSON_ATTR_TASK6_ANSWER;
		}
		
		try {
			questionStr=mTPOJson.getString(questionAttr);
		} catch (JSONException e) {
			Log.i(TAG, "getTPOSample JSONException : "+e.getMessage());
		}
		
		return questionStr;
	}
	
	private String getTPOFilePath(String fileSubPath) {
		String TPOPath = PathUtils.getExternalTPOFilesDir().getAbsolutePath();
		String filePath = TPOPath + "/" +mTPOName + fileSubPath;
		return filePath;
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
	
	public void play(String audioFile){
		try {
			mMediaPlayer.setDataSource(audioFile);
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
	
	private void stopMedia(){
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
	}

	private void releaseMedia() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		mPlayingPosition=-1;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		StatisticUtils.onEvent(R.string.my_tpo, R.string.mytpodetails_bofang);
		TPORecord record=mRecordingdataList.get(arg2);
		File file=new File(record.RecordFilePath);
		if(!file.exists()){
			Utils.toast(R.string.my_tpo_recording_not_exists);
			return;
		}
		
		if(mMediaPlayer.isPlaying()){
			stopMedia();
		}
		
		if(mPlayingPosition!=arg2){
			play(record.RecordFilePath);
			mPlayingPosition=arg2;
		}else{
			mPlayingPosition=-1;
		}
		
		mAdapter.notifyDataSetChanged();
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		
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
		confirmBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_tpo, R.string.confirm);
				stopMedia();
				
				TPORecord record=mRecordingdataList.get(arg2);
				File audioFile=new File(record.RecordFilePath);
				if(audioFile.exists()){
					audioFile.delete();
				}
				mTPORecordDB.delete(record.ID);
				
				mRecordingdataList=mTPORecordDB.queryData(UserInfo.getCurrentUser().getObjectId(), mTPOName, mTPOQuestion);
				mAdapter.notifyDataSetChanged();
				
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				StatisticUtils.onEvent(R.string.my_tpo, R.string.cancel);
				dialog.dismiss();
			}
		});
		
		return false;
	}
}
