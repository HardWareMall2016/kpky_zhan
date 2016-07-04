package com.zhan.kykp.spokenSquare;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.TopicIndexBean;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class SpokenSquareActivity extends BaseActivity implements BaseSpokenSquareFragment.Callback,OnClickListener ,HttpRequestCallback{
	public final static String EXT_KEY_TOPIC_OBJECT_ID="topic_object_id";
	public final static String EXT_KEY_TOPIC_TIME="topic_object_time";
	public final static String EXT_KEY_SHOW_TODAY="show_today";
	
	//Frag相关
	private boolean mShowTopicToday=true;
	private BaseSpokenSquareFragment mFragTopicToday;
	private BaseSpokenSquareFragment mFragGradingByMe;
	
	//Views
	private View mContentView;
	private TextView mTVTopicToday;
	private TextView mTVGradingByMe;
	
	private Dialog mDialogBackConfirm;
	private Dialog mProgressDlg;
	
	//Params
	private String mTopicObjectId;
	private String mTopicTime;
	
	//Tools
	private RequestHandle mRequestHandle;
	private final static String NET_PARAM_USER="user";
	private final static String NET_PARAM_TOPIC="topic";
	
	//Data
	private TopicIndexBean mTopicIndexBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spoken_square);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED  
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON  
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		initViews();
		
		initBackConfirmDlg();
		
		processExtraData();
	}
	
	@Override
	protected void onDestroy() {
		if(mRequestHandle!=null){
			mRequestHandle.cancel(true);
		}
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		processExtraData();
	}
	
	private void processExtraData() {
		Intent intent = getIntent();
		mTopicObjectId=intent.getStringExtra(EXT_KEY_TOPIC_OBJECT_ID);
		mTopicTime=intent.getStringExtra(EXT_KEY_TOPIC_TIME);
		//如果没有，表示是今日话题
		if(mTopicTime==null){
			mTopicTime=Utils.getDateFormateStr(System.currentTimeMillis());
		}

		mShowTopicToday=intent.getBooleanExtra(EXT_KEY_SHOW_TODAY,true);

		queryData();
	}

	private void initViews(){
		mContentView=findViewById(R.id.content);
		mTVTopicToday=(TextView)findViewById(R.id.topic_today);
		mTVGradingByMe=(TextView)findViewById(R.id.grading_by_me);
		
		mTVTopicToday.setOnClickListener(this);
		mTVGradingByMe.setOnClickListener(this);
		
		getActionBarRightMenu().setText(R.string.spoken_square_topic_history);
		getActionBarRightMenu().setOnClickListener(this);
	}
	
	private void queryData(){
		showProgressDialog();
		BaseHttpRequest httpRequest=new BaseHttpRequest();
		//String url=String.format("%s?%s=%s", ApiUrls.TOPIC_INDEX,NET_PARAM_USER,User.getCurrentUserId());
		
		RequestParams request=new RequestParams();
		request.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
		if(!TextUtils.isEmpty(mTopicObjectId)){
			request.put(NET_PARAM_TOPIC, mTopicObjectId);
		}
		
		mContentView.setVisibility(View.GONE);
		mRequestHandle=httpRequest.startRequest(this, ApiUrls.TOPIC_INDEX, request, this, RequestType.GET);
	}
	
	private void initFrag(String question){
		mFragTopicToday=new TopicTodayFragment();
		mFragTopicToday.prepareArguments(question);
		mFragGradingByMe=new GradingByMeFragment();
		mFragGradingByMe.prepareArguments(question);
	}
	
	private void refreshViews(){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		if(mShowTopicToday){
			transaction.replace(R.id.content, mFragTopicToday);
			mTVTopicToday.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVTopicToday.setTextColor(getResources().getColor(R.color.dark_red));
			mTVGradingByMe.setBackgroundColor(Color.WHITE);
			mTVGradingByMe.setTextColor(getResources().getColor(R.color.text_color_content));
		}else{
			transaction.replace(R.id.content, mFragGradingByMe);
			
			mTVTopicToday.setBackgroundColor(Color.WHITE);
			mTVTopicToday.setTextColor(getResources().getColor(R.color.text_color_content));
			mTVGradingByMe.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVGradingByMe.setTextColor(getResources().getColor(R.color.dark_red));
		}
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		//正在答题时禁止切换
		if(MediaRecordFunc.getInstance().isRecording()){
			Utils.toast(R.string.spoken_square_recording_no_opt);
			return;
		}
		//请求失败或还没结束时禁止切换
		if(mTopicIndexBean==null){
			return;
		}
		
		switch (v.getId()) {
		case R.id.topic_today:
			mShowTopicToday=true; 
			refreshViews();

			StatisticUtils.onEvent(getTitle().toString(), getString(R.string.spoken_square_topic_today));
			break;
		case R.id.grading_by_me:
			mShowTopicToday=false;
			refreshViews();

			StatisticUtils.onEvent(getTitle().toString(), getString(R.string.spoken_square_grading_by_me));
			break;
		case R.id.right_menu:
			startActivity(new Intent(getApplicationContext(),TopicHistoryActivity.class));
			if(mFragTopicToday!=null){
				mFragTopicToday.stopPlayMedia();
			}
			if(mFragGradingByMe!=null){
				mFragGradingByMe.stopPlayMedia();
			}

			StatisticUtils.onEvent(getTitle().toString(), getString(R.string.spoken_square_topic_history));
			break;
		}
	}

	
	@Override
	public void onBackPressed() {
		if(MediaRecordFunc.getInstance().isRecording()){
			mDialogBackConfirm.show();
		}else{
			super.onBackPressed();
		}
	}

	private void initBackConfirmDlg(){
		LayoutInflater inflater=getLayoutInflater();
		mDialogBackConfirm = new Dialog(this, R.style.Dialog);
		View dlgContent = inflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.spoken_square_exting);
		
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setText(R.string.spoken_square_exit_confirm_msg);
		
		TextView confirm = (TextView) dlgContent.findViewById(R.id.confirm);
		confirm.setText(R.string.speaking_exit_confirm);
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
				//退出广场界面
				SpokenSquareActivity.super.onBackPressed();
			}
		});
		
		TextView cancel = (TextView) dlgContent.findViewById(R.id.cancel);
		cancel.setText(R.string.speaking_exit_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
			}
		});
		
		mDialogBackConfirm.setContentView(dlgContent);
	}

	@Override
	public void onRequestFailed(String errorMsg) {
		closeDialog();
		mContentView.setVisibility(View.VISIBLE);
		Utils.toast(errorMsg);
	}

	@Override
	public void onRequestCanceled() {
		closeDialog();
		mContentView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRequestSucceeded(String jsonStr) {
		closeDialog();
		mContentView.setVisibility(View.VISIBLE);
		TopicIndexBean topicIndexBean=Utils.parseJson(jsonStr, TopicIndexBean.class);
		if(topicIndexBean!=null){
			mTopicIndexBean=topicIndexBean;

			initFrag(mTopicIndexBean.getDatas().getQuestion());
			refreshViews();
		}else{
			Utils.toast(R.string.network_query_data_failed);
		}
	}

	@Override
	public void onRequestFailedNoNetwork() {
		closeDialog();
		mContentView.setVisibility(View.VISIBLE);
		Utils.toast(R.string.network_error);
	}

	@Override
	public TopicIndexBean getTopicIndexBean() {
		return mTopicIndexBean;
	}

	@Override
	public String getTopicTime() {
		return mTopicTime;
	}

	private void showProgressDialog() {
		if(!mShowTopicToday){
			return;
		}
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
		mProgressDlg.setCancelable(false);
		mProgressDlg.show();
	}

	private void closeDialog() {
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}
}
