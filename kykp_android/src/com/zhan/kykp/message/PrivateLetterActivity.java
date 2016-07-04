package com.zhan.kykp.message;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.base.EnvConfig;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.PrivateMessagesBean;
import com.zhan.kykp.network.bean.PrivateMessagesBean.DatasEntity;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.userCenter.PersonCenterActivity;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class PrivateLetterActivity extends BaseActivity implements OnClickListener{
	private final static String TAG="PrivateLetterActivity";
	
	public final static String EXT_KET_TO_USER_ID="toUserId";
	public final static String EXT_KET_TO_USER_NAME="toUserName";
	
	private final String MSG_TYPE_ME="me";
	private final String MSG_TYPE_HE="he";
	
	//Views
	private PullToRefreshListView mPullRefreshListView;
	private EditText mETMessage;
	private View mSendBtn;
	
	//Data
	private List<MsgInfo> mItemList = new LinkedList<MsgInfo>();
	
	//Tools
	private MessageAdapter mAdapter;
	private Handler mMsgHandler=new Handler();
	
	//Params
	private String mToUserId;
	private String mUserName;
	private DisplayImageOptions options;
	private int mCurrentPage=0;

	private boolean mHasMoreData=true;

	//Network
	private BaseHttpRequest mHttpRequest;
	
	private RequestHandle mQueryMsgRequestHandle;
	private RequestHandle mSendMsgRequestHandle;
	private RequestHandle mUpdateMsgStatusRequestHandle;
	
	
	private class MsgInfo{
		//String userObjectId;
		String msgContent;
		String time;
		boolean sendByMe;
		String fromUserAvatar;
		
		String toUserInfoId;
		String formUserInfoId;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		init();
		
		initView();
		
		getPrivateMsg(0);
		
		updatePrivateMsgStatus();
	}

	private void init(){
		mToUserId=getIntent().getStringExtra(EXT_KET_TO_USER_ID);
		mUserName=getIntent().getStringExtra(EXT_KET_TO_USER_NAME);
		setTitle(mUserName);
		
		mHttpRequest=new BaseHttpRequest();
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();
		
	}
	
	private void initView() {
		setContentView(R.layout.activity_private_letter);
		mETMessage=(EditText)findViewById(R.id.send_msg_conetnt);
		mSendBtn=findViewById(R.id.btn_send);
		mSendBtn.setOnClickListener(this);
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(PrivateLetterActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				if(mHasMoreData){
					int queryPage=mItemList.size()==0?0:(mCurrentPage+1);
					getPrivateMsg(queryPage);
				}else{
					mPullRefreshListView.postDelayed(new Runnable(){
						@Override
						public void run() {
							mPullRefreshListView.onRefreshComplete();
						}
					}, 100);
				}
			}
		});
		
		ListView listView=mPullRefreshListView.getRefreshableView();
		//保持在打开输入法后，依然能看到最新数据
		listView.addOnLayoutChangeListener(new OnLayoutChangeListener(){
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				//当在第一页开发输入法软键盘，保持最新记录在底
				if(bottom<oldBottom&&mCurrentPage==0){
					mMsgHandler.postDelayed(new Runnable(){
						@Override
						public void run() {
							moveListToLastItem();
						}
					}, 300);
				}
			}
		});
		
		mAdapter = new MessageAdapter();
		mPullRefreshListView.setAdapter(mAdapter);
	}
	
	
	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	}
	
	private void release(){
		releaseRequest();
	}
	
	private void releaseRequest(){
		releaseRequest(mQueryMsgRequestHandle);
		releaseRequest(mSendMsgRequestHandle);
	}
	
	/*
	 * 倒序显示
	 */
	private class MessageAdapter extends BaseAdapter {
		private class ViewHolder {
			ImageView avatarLeft;
			ImageView avatarRight;
			TextView content;
			TextView time;
		}

		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemList.get(mItemList.size()-1-position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();

				convertView = inflater.inflate(R.layout.private_letter_list_item, null);
				holder = new ViewHolder();
				holder.avatarLeft=(ImageView)convertView.findViewById(R.id.avatar_left);
				holder.avatarRight=(ImageView)convertView.findViewById(R.id.avatar_right);
				holder.content=(TextView)convertView.findViewById(R.id.content);
				holder.time=(TextView)convertView.findViewById(R.id.time);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			MsgInfo info=(MsgInfo)getItem(position);
			
			
			if(info.sendByMe){
				holder.avatarLeft.setVisibility(View.INVISIBLE);
				holder.avatarRight.setVisibility(View.VISIBLE);
				holder.content.setBackgroundResource(R.drawable.new_item_bg_right);
				
				int color=getResources().getColor(R.color.white);
				holder.content.setTextColor(color);
				
				String avatarPath= UserInfo.getCurrentUser().getAvatar();
				ImageLoader.getInstance().displayImage(avatarPath,holder.avatarRight,options);
				
				holder.avatarRight.setTag(UserInfo.getCurrentUser().getObjectId());
				holder.avatarRight.setOnClickListener(mOnAvatarClickListener);
				
			}else{
				holder.avatarLeft.setVisibility(View.VISIBLE);
				holder.avatarRight.setVisibility(View.INVISIBLE);
				holder.content.setBackgroundResource(R.drawable.new_item_bg);
				
				int color=getResources().getColor(R.color.text_color_content);
				holder.content.setTextColor(color);
				
				String avatarPath=info.fromUserAvatar;
				
				ImageLoader.getInstance().displayImage(avatarPath,holder.avatarLeft,options);
				
				holder.avatarLeft.setTag(info.formUserInfoId);
				holder.avatarLeft.setOnClickListener(mOnAvatarClickListener);
			}
			
			holder.content.setText(info.msgContent);
			holder.time.setText(info.time);

			return convertView;
		}
	}
	
	private OnClickListener mOnAvatarClickListener=new OnClickListener(){
		@Override
		public void onClick(View v) {
			String userObjectId=(String) v.getTag();
			//禁止查看“小站客户”信息
			if(EnvConfig.CUSTOMER_SERVICE_ID.equals(userObjectId)){
				return;
			}

			StatisticUtils.onEvent(R.string.statistic_private_letter, R.string.user_info_head);

			Intent personalCenterIntent=new Intent(PrivateLetterActivity.this,PersonCenterActivity.class);
			personalCenterIntent.putExtra(PersonCenterActivity.EXT_KEY_USEROBJECT, userObjectId);
			startActivity(personalCenterIntent);
		}
	};
	
	
	/***
	 * Network
	 */
	
	private void releaseRequest(RequestHandle request){
		if(request!=null&&!request.isFinished()){
			request.cancel(true);
		}
	}
	
	//Message - 获取私信
	private final static String NET_PARAM_FROM_USER="formUser";//发送用户objectId
	private final static String NET_PARAM_TO_USER="toUser";//接收用户objectId
	private final static String NET_PARAM_PAGE="page";//页数
	
	private void getPrivateMsg(int page){
		if(TextUtils.isEmpty(mToUserId)){
			Utils.toast("请先传入接收者的ID");
			return;
		}
		Log.i(TAG, "getPrivateMsg");
		releaseRequest(mQueryMsgRequestHandle);
		
		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_FROM_USER,  mToUserId);
		requestParams.put(NET_PARAM_TO_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_PAGE,  page);
		
		mQueryMsgRequestHandle=mHttpRequest.startRequest(this, ApiUrls.MESSAGE_GET_PRIV_MSG, requestParams, new QueryMsgCallback(page), RequestType.GET);
	}
	
	private class QueryMsgCallback implements HttpRequestCallback{
		private int mPage;
		
		public QueryMsgCallback(int page){
			mPage=page;
		}
		
		@Override
		public void onRequestFailed(String errorMsg) {
			mPullRefreshListView.onRefreshComplete();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			mPullRefreshListView.onRefreshComplete();
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestSucceeded(String content) {
			mPullRefreshListView.onRefreshComplete();
			
			PrivateMessagesBean privateMessagesBean=Utils.parseJson(content, PrivateMessagesBean.class);
			
			if(privateMessagesBean!=null){
				if(mPage==0){
					mItemList.clear();
				}
				
				if(privateMessagesBean.getDatas().size()>0){
					mCurrentPage=mPage;
					mHasMoreData=true;
					
					for(DatasEntity data:privateMessagesBean.getDatas()){
						MsgInfo msgInfo=new MsgInfo();
						msgInfo.fromUserAvatar=data.getFormUserInfo();
						msgInfo.msgContent=data.getContent();
						msgInfo.formUserInfoId=data.getFormUserInfoId();
						msgInfo.toUserInfoId=data.getToUserInfoId();
						long time=Utils.getCurrentTimeZoneUnixTime(data.getCreatedAt());
						msgInfo.time=Utils.getFullFormateTimeStr(time);
						if(MSG_TYPE_HE.equals(data.getType())){
							msgInfo.sendByMe=true;
						}else{
							msgInfo.sendByMe=false;
						}
						mItemList.add(msgInfo);
					}
					
				}else{
					mHasMoreData=false;
					mPullRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel(getString(R.string.no_more_sys_msg)); 
					mPullRefreshListView.getLoadingLayoutProxy(true, false).setReleaseLabel(getString(R.string.no_more_sys_msg));
				}
				
				mAdapter.notifyDataSetChanged();
				
				if(mPage==0){
					moveListToLastItem();
				}
				
			}else{
				BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
				if(baseBean!=null&&baseBean.getStatus()==0){
					Utils.toast(baseBean.getMessage());
				}else{
					Utils.toast(R.string.query_privte_msg_failed);
				}
			}
		}
	}
	
	
	private final static String NET_PARAM_MSG_CONTENT="message";//信息内容
	
	private void sendPrivateMsg(String msg){
		if(TextUtils.isEmpty(mToUserId)){
			Utils.toast("请先传入接收者的ID");
			return;
		}
		Log.i(TAG, "sendPrivateMsg");
		releaseRequest(mSendMsgRequestHandle);
		
		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_FROM_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_TO_USER,  mToUserId);
		requestParams.put(NET_PARAM_MSG_CONTENT,  msg);
		
		//防止重复发送
		mSendBtn.setEnabled(false);
		mSendMsgRequestHandle=mHttpRequest.startRequest(this, ApiUrls.MESSAGE_SEND_PRIV_MSG, requestParams, new SendPrivateMsgCallback(msg), RequestType.POST);
	}
	
	private class SendPrivateMsgCallback implements HttpRequestCallback{
		private String mMessageContent;
		
		public SendPrivateMsgCallback(String msg){
			mMessageContent=msg;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(R.string.send_msg_failed);
			mSendBtn.setEnabled(true);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.send_msg_network_error);
			mSendBtn.setEnabled(true);
		}

		@Override
		public void onRequestCanceled() {
			mSendBtn.setEnabled(true);
		}

		@Override
		public void onRequestSucceeded(String content) {
			mSendBtn.setEnabled(true);
			Gson gson = new Gson();
			BaseBean ststusBean=gson.fromJson(content, BaseBean.class);
			if(ststusBean.getStatus()==1){
				mETMessage.getText().clear();
				MsgInfo msgInfo=new MsgInfo();
				msgInfo.msgContent=mMessageContent;
				msgInfo.time=Utils.getFullFormateTimeStr(System.currentTimeMillis());
				msgInfo.sendByMe=true;

				mItemList.add(0, msgInfo);
				
				mAdapter.notifyDataSetChanged();
				
				moveListToLastItem();
			}else{
				Utils.toast(R.string.send_msg_failed);
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			StatisticUtils.onEvent(R.string.statistic_private_letter, R.string.send_msg);

			String msg=mETMessage.getText().toString();
			if(!TextUtils.isEmpty(msg)){
				sendPrivateMsg(msg);
			}else{
				Utils.toast(R.string.send_empty_msg);
			}
			break;
		}
	}
	
	
	private void moveListToLastItem(){
		ListView listView=mPullRefreshListView.getRefreshableView();
		int position=mItemList.size() - 1;
		if(position>=0){
			listView.setSelection(position);
		}
	}
	
	private void updatePrivateMsgStatus() {
		Log.i(TAG, "updatePrivateMsgStatus");
		releaseRequest(mUpdateMsgStatusRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put(NET_PARAM_FROM_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_TO_USER, mToUserId );

		mUpdateMsgStatusRequestHandle = mHttpRequest.startRequest(this, ApiUrls.MESSAGE_UPDATE_PRIV_MSG, requestParams, null, RequestType.POST);
	}
}
