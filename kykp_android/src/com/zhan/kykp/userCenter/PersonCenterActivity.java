package com.zhan.kykp.userCenter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhan.kykp.R;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.message.PrivateLetterActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.PersonCenterBean;
import com.zhan.kykp.network.bean.PraiseBean;
import com.zhan.kykp.network.bean.TopicAddBean;
import com.zhan.kykp.network.bean.TopicCancelBean;
import com.zhan.kykp.network.bean.PersonCenterBean.DatasEntity.AllAnswerEntity;
import com.zhan.kykp.network.bean.PersonCenterRefreshBean;
import com.zhan.kykp.network.bean.PersonCenterRefreshBean.DatasEntity;
import com.zhan.kykp.spokenSquare.SpokenSquareActivity;
import com.zhan.kykp.spokenSquare.SpokenSquareUtils;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.ActionSheetDialog;
import com.zhan.kykp.widget.FlatButton;
import com.zhan.kykp.widget.RatingBar;
import com.zhan.kykp.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zhan.kykp.widget.ActionSheetDialog.SheetItemColor;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonCenterActivity extends Activity implements OnClickListener, OnCompletionListener {
	public static final String EXT_KEY_USEROBJECT = "userobject_id";

	private TextView mPersonSpeaking ;
	private TextView mPersonAnswer ;
	private TextView mName ;
	private TextView mFocus ;
	private TextView mFans ;
	private TextView mTitle ;
	private TextView mLetter ;
	private TextView mFocused ;

	private LinearLayout mPersonll ;
	private RelativeLayout mPersonrl ;

	private FlatButton mSpokenSquare ;

	private ImageView mBack ;
	private ImageView mImgAvatar ;
	private ImageView mMore ;

	private Resources mRes ;
	private LayoutInflater mInflater;
	private Dialog mProgressDlg;

	private MediaPlayerHelper mMediaPlayerHelper;
	private AnswerItemInfo itemInfo;

	private RequestHandle mRequestHandle;//用戶主頁
	private RequestHandle mDownloadRequestHandle ;//下載
	private RequestHandle mRefreshRequestHandle ;//口语练习列表
	private RequestHandle mCancelRequestHandle ;//取消关注
	private RequestHandle mAddRequestHandle ;//加关注
	private RequestHandle mPraiseRequestHandle;

	private DisplayImageOptions options;
	private PersonCenterBean statusBean ;
	private PersonCenterRefreshBean mRefreshBean ;
	private TopicCancelBean mCancelBean ;
	private TopicAddBean mAddBean ;

	private PullToRefreshListView mPullRefreshListView;
	private PersonSpeakAdapter mAdapter ;

	private enum ContentType{PERSON_SPEAKING,PERSON_ANSWER};
	private ContentType mContentType = ContentType.PERSON_SPEAKING;//默认口语练习为选中


	private final static String USERID = "user";
	private final static String INTERVIEWEE = "interviewee";
	private final static String PAGE = "page";
	private final static String TYPE = "type";
	private final static String FOLLOWE = "followe";
	private final static String NET_PARAM_PRAISE_ANSWER_ID="record";

	private String mUserobjectId ;
	private int mMorePage = 1 ;
	private String type = "all" ;//判断是口语练习（all）还是精彩回答（sp）
	private String mUserObjId;

	private List<AnswerItemInfo> mSpeangDatas = new LinkedList<AnswerItemInfo>();//口语练习数据
	private List<AnswerItemInfo> mAnswerDatas = new LinkedList<AnswerItemInfo>();//精彩回答数据
	private List<AnswerItemInfo> mCurrentDatas = mSpeangDatas;//用它来判断切换到你选择的数据

	private AnswerItemInfo mPlayingAudioItem ;
	private BaseHttpRequest mHttpRequest ;

	private String cancelfollowee = "取消关注";
	public Handler handler ;
	private ValueAnimator mPersonAnim;//点赞动画

	/*
	 * 释放资源
	 */
	 @Override
		protected void onDestroy() {
			mMediaPlayerHelper.releaseMedia();
			release(mRequestHandle);
			release(mDownloadRequestHandle);
			release(mRefreshRequestHandle);
			release(mCancelRequestHandle);
			release(mAddRequestHandle);
			release(mPraiseRequestHandle);

			super.onDestroy();
		}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindow();
		setContentView(R.layout.activity_person_center);

		mRes = getResources() ;
		mInflater=getLayoutInflater();
		mMediaPlayerHelper=new MediaPlayerHelper(this);
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mUserobjectId = bundle.getString(EXT_KEY_USEROBJECT);

		initView();
		showProgressDialog();
		queryDate();
		// 下拉刷新
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						mMorePage = 1 ;
						refreshData(0);
					}
				});
		//上拉加载更多
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						refreshData(mMorePage++);
					}
				});

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == -1) {
					mFocused.setText(R.string.person_addfocus);
					cancelfollowee = "加关注";
				}else if(msg.what == 1){
					Utils.toast(R.string.person_cancelsuccessfollowee);
					mFocused.setText(R.string.person_addfocus);
					cancelfollowee = "加关注";
				}else if(msg.what == 2){
					Utils.toast(R.string.person_cancelfailefollowee);
					mFocused.setText(R.string.person_followeeed);
					cancelfollowee = "取消关注";
				}else if(msg.what == 3){
					Utils.toast(R.string.person_successfollowee);
//					mFocused.setText(R.string.person_followeeed);
					cancelfollowee = "取消关注";
				}else if(msg.what == 4){
					Utils.toast(R.string.person_failefollowee);
					mFocused.setText(R.string.person_addfocus);
					cancelfollowee = "加关注";
				}
			}
		};

	}


	 private void initView() {
		    mPersonSpeaking = (TextView)findViewById(R.id.tv_pesron_speaking);
			mPersonAnswer = (TextView)findViewById(R.id.tv_pesron_answer);

			mPersonSpeaking.setOnClickListener(mOnTitleClick);
			mPersonAnswer.setOnClickListener(mOnTitleClick);

			mBack = (ImageView)findViewById(R.id.person_back);
			mBack.setOnClickListener(this);


			mName = (TextView)findViewById(R.id.tv_person_name);
			mImgAvatar = (ImageView) findViewById(R.id.person_avatar);

			mSpokenSquare = (FlatButton)findViewById(R.id.person_spokensquare);
			mSpokenSquare.setOnClickListener(this);

			mFocus = (TextView)findViewById(R.id.person_focus);
			mFocus.setOnClickListener(this);
			mFans = (TextView)findViewById(R.id.person_fans);
			mFans.setOnClickListener(this);

			mPersonll = (LinearLayout)findViewById(R.id.person_ll);

			mFocused = (TextView)findViewById(R.id.focus_ed);
			mFocused.setOnClickListener(this);

			mAdapter = new PersonSpeakAdapter();
			mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.person_list);
			mPullRefreshListView.setMode(Mode.PULL_FROM_START);
			mPullRefreshListView.setAdapter(mAdapter);

			mMore = (ImageView)findViewById(R.id.person_more);
			mMore.setOnClickListener(this);

			mPersonrl = (RelativeLayout)findViewById(R.id.rl_person_center);
			mTitle = (TextView)findViewById(R.id.person_title);

			mLetter = (TextView)findViewById(R.id.person_letter);
			mLetter.setOnClickListener(this);

			//传递如果是自己的userid则显示自己的个人主页，否则显示他的个人主页
		if (mUserobjectId.equals(UserInfo.getCurrentUser().getObjectId())) {
			mName.setVisibility(View.VISIBLE);
			mPersonrl.setVisibility(View.GONE);
			mMore.setVisibility(View.GONE);
		} else {
			mName.setVisibility(View.GONE);
			mPersonrl.setVisibility(View.VISIBLE);
			mMore.setVisibility(View.VISIBLE);
		}

			mPersonSpeaking.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mPersonSpeaking.setTextColor(mRes.getColor(R.color.dark_red));
			mPersonAnswer.setBackgroundColor(Color.WHITE);
			mPersonAnswer.setTextColor(mRes.getColor(R.color.text_color_content));
	}

	 /*
	  * 刷新
	  */
	 private void refreshData(int mPage) {
		    mHttpRequest = new BaseHttpRequest();
			RequestParams requestParams=new RequestParams();

			requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
			requestParams.put(INTERVIEWEE, mUserobjectId);
			requestParams.put(PAGE, mPage);
			requestParams.put(TYPE, type);
			mRefreshRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_TOPICANSWER, requestParams, new refershCallback(mPage,type), RequestType.GET);
	 }


	 private class refershCallback implements HttpRequestCallback{

		 private int mPage;
		 private String mType;

		 public refershCallback(int page,String type) {
			 mPage=page;
			 mType = type ;
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
			mRefreshBean = Utils.parseJson(content, PersonCenterRefreshBean.class);
			if(mRefreshBean != null){
				if(mPage == 0){
					 if(mType.equals("all")){
						 mSpeangDatas.clear();
		    	    }else{
		    	    	mAnswerDatas.clear();
		    	    }
					 mMediaPlayerHelper.stopMedia();
					//在刷新的时候判断书否有数据，没有数据显示没有记录
					if(mRefreshBean.getDatas().size() != 0){
						mPullRefreshListView.setVisibility(View.VISIBLE);
						mPersonll.setVisibility(View.GONE);
					}else{
							mPullRefreshListView.setVisibility(View.GONE);
							mPersonll.setVisibility(View.VISIBLE);
							//如果没有答题，只有自己的情况下
							if(!UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
								mSpokenSquare.setVisibility(View.GONE);
							}
						}
				}

				if(mRefreshBean.getDatas().size() != 0){
					for(DatasEntity item:mRefreshBean.getDatas()){
			    		AnswerItemInfo data = new AnswerItemInfo() ;
			    	    data.audio = item.getAudio();
			    	    data.createdAt = item.getCreatedAt();
			    	    data.mark = item.getMark();
			    	    data.markerCount = item.getMarkerCount();
			    	    data.objectId = item.getObjectId();
			    	    data.praise = item.getPraise();
			    	    data.recordTime = item.getRecordTime();
			    	    data.topic = item.getTopic();
			    	    data.updatedAt = item.getUpdatedAt();
			    	    data.isspraise = item.getIspraise()==1;

			    	    if(mType.equals("all")){
			    	    	 mSpeangDatas.add(data);
			    	    }else{
			    	    	 mAnswerDatas.add(data);
			    	    }
			    	}

					  mAdapter.notifyDataSetChanged();
				}

				if (mPage == 2 && mRefreshBean.getDatas().size() == 0) {
					Utils.toast(R.string.no_more_data);
				}

		    }else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}

	 }



	private void release(RequestHandle request) {
		if(request!=null && !request.isFinished()){
			request.cancel(true);
		}
	}

	/**
	 * 页面统计
	 */
	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onPageStart(getString(R.string.home_page));
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		//友盟统计
		MobclickAgent.onPageEnd(getString(R.string.home_page));
		MobclickAgent.onPause(this);
	}

	/*
	 * 个人主页请求接口
	 */
	private void queryDate() {
	    mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();

		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(INTERVIEWEE, mUserobjectId);
		mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_USERHOME, requestParams, requestCallback, RequestType.GET);
	}

	private HttpRequestCallback requestCallback = new HttpRequestCallback(){

		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeDialog();
		    statusBean = Utils.parseJson(content, PersonCenterBean.class);
		    mSpeangDatas.clear();
		    if(statusBean  != null){
		    	mName.setText(statusBean.getDatas().getNickname());
		    	ImageLoader.getInstance().displayImage(statusBean.getDatas().getAvatar(),mImgAvatar,options);
		    	mFocus.setText(" 关注 "+statusBean.getDatas().getFollowee());
		    	mFans.setText(" 粉丝 "+statusBean.getDatas().getFollower());
		    	if(!UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
		    		mTitle.setText(statusBean.getDatas().getNickname());
		    		if(statusBean.getDatas().getIsfollowe() == 0){
		    			Message msg = new Message();
	                    msg.what = -1;
	                    handler.sendMessage(msg);
		    		}else if(statusBean.getDatas().getIsfollowe() == 1){
		    			mFocused.setText(R.string.person_followeeed);
		    		}else if(statusBean.getDatas().getIsfollowe() == 2){
		    			mFocused.setText(R.string.person_otherfollowee);
		    		}
		    	}else{
		    		mTitle.setText(R.string.person_center);
		    	}

		    	mUserObjId = statusBean.getDatas().getObjectId();

		    	if(statusBean.getDatas().getAllAnswer() != null){
		    		for(AllAnswerEntity item:statusBean.getDatas().getAllAnswer()){
			    		AnswerItemInfo data = new AnswerItemInfo() ;
			    	    data.audio = item.getAudio();
			    	    data.createdAt = item.getCreatedAt();
//			    	    data.ispraise = item.getIspraise() ;
			    	    data.mark = item.getMark();
			    	    data.markerCount = item.getMarkerCount();
			    	    data.objectId = item.getObjectId();
			    	    data.praise = item.getPraise();
			    	    data.recordTime = item.getRecordTime();
			    	    data.topic = item.getTopic();
			    	    data.updatedAt = item.getUpdatedAt();
			    	    data.isspraise = item.getIspraise()==1;
			    	    mSpeangDatas.add(data);
			    	}

		    		if(statusBean.getDatas().getAllAnswer().size() != 0){
						mAdapter.notifyDataSetChanged();
			    	}else{
			    		mPersonll.setVisibility(View.VISIBLE);
						mPullRefreshListView.setVisibility(View.GONE);
						mAdapter.notifyDataSetChanged();
			    		return ;
			    	}
		    	}else{
		    		mPullRefreshListView.setVisibility(View.GONE);
		    		mPersonll.setVisibility(View.VISIBLE);
		    		//如果没有答题，只有自己的情况下
		    		if(!UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
		    			 mSpokenSquare.setVisibility(View.GONE);
		    		}
		    	}

		    }else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}

	};



	private void initWindow() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	private OnClickListener mOnTitleClick=new OnClickListener(){
		@Override
		public void onClick(View v) {
			ContentType type = null;
			switch(v.getId()){
			case R.id.tv_pesron_speaking:
				StatisticUtils.onEvent(R.string.person_center, R.string.person_speaking);
				type = ContentType.PERSON_SPEAKING;
				break;
			case R.id.tv_pesron_answer:
				StatisticUtils.onEvent(R.string.person_center, R.string.person_answer);
				type = ContentType.PERSON_ANSWER;
				break;
			}
			if(type != mContentType){
				mContentType = type ;
			}

			changData();
		}

	};

	protected void changData() {
		//切换之前把录音关掉,图片换位原先的图片
		if(mPlayingAudioItem!=null){
			mPlayingAudioItem.isplayingAudio = false;
		}
		mMediaPlayerHelper.stopMedia();

		switch(mContentType){
		case PERSON_SPEAKING://口语练习(点击口语练习的时候不需要请求接口，只有上拉或者下拉需要加载)
			mPersonSpeaking.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mPersonSpeaking.setTextColor(mRes.getColor(R.color.dark_red));
			mPersonAnswer.setBackgroundColor(Color.WHITE);
			mPersonAnswer.setTextColor(mRes.getColor(R.color.text_color_content));
			mCurrentDatas = mSpeangDatas;
			type = "all";
            mPullRefreshListView.setRefreshing();
			break;
		case PERSON_ANSWER://精彩回答（需要请求接口）
			mPersonAnswer.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mPersonAnswer.setTextColor(mRes.getColor(R.color.dark_red));
			mPersonSpeaking.setBackgroundColor(Color.WHITE);
			mPersonSpeaking.setTextColor(mRes.getColor(R.color.text_color_content));
			mCurrentDatas = mAnswerDatas;
			type = "sp";
			if (mCurrentDatas.size() == 0) {//去刷新
				mPullRefreshListView.setRefreshing();
			}
			break;
		}
		mAdapter.notifyDataSetChanged();
	}



	private class PersonSpeakAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mCurrentDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mCurrentDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder ;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.person_center_speaking_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.mContent = (TextView) convertView.findViewById(R.id.person_content);
				viewHolder.mCreatedAt = (TextView) convertView.findViewById(R.id.attention_or_private_letter);
			    viewHolder.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar_default);
			    viewHolder.mRecordTime = (TextView) convertView.findViewById(R.id.seconds);
			    viewHolder.mAudio = (ImageView) convertView.findViewById(R.id.img_play_stop);
			    viewHolder.mPraise = (TextView) convertView.findViewById(R.id.person_center_item_praise);
			    viewHolder.personContent = (RelativeLayout) convertView.findViewById(R.id.person_center_content);
			    viewHolder.personTip = (TextView) convertView.findViewById(R.id.person_center_tip);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.mContent.setText(mCurrentDatas.get(position).topic);

			viewHolder.personContent.setOnClickListener(mOnItemElementClickListener);
			viewHolder.personContent.setTag(mCurrentDatas.get(position));
			//点赞动画
			if(mCurrentDatas.get(position).playPersonAnim){
				playPersonAnim(viewHolder.personTip);
			}else{
				viewHolder.personTip.setVisibility(View.GONE);
			}

			viewHolder.mPraise.setText(mCurrentDatas.get(position).praise+"");
			if(mCurrentDatas.get(position).isspraise){
				viewHolder.mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_un_praised, 0, 0, 0);
			}else{
				viewHolder.mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_un_praise, 0, 0, 0);
			}

			viewHolder.ratingbar.setRating(mCurrentDatas.get(position).mark);

			int time = mCurrentDatas.get(position).createdAt + 28800;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date(time*1000L));
		    viewHolder.mCreatedAt.setText(date);

		    viewHolder.mRecordTime.setText(mCurrentDatas.get(position).recordTime+"''");

		    if(mCurrentDatas.get(position).isplayingAudio){
		    	viewHolder.mAudio.setImageResource(R.drawable.icon_pause);
		    }else{
		    	viewHolder.mAudio.setImageResource(R.drawable.icon_play);
		    }

		    viewHolder.mAudio.setOnClickListener(mOnItemElementClickListener);
		    viewHolder.mAudio.setTag(mCurrentDatas.get(position));

			return convertView;
		}
	}

	private class ViewHolder{
		TextView mContent ;
		TextView mPraise ;
		RatingBar ratingbar;
		TextView mCreatedAt ;
		TextView mRecordTime ;
		ImageView mAudio ;
		RelativeLayout personContent ;
		TextView personTip ;
	}

	private OnClickListener mOnItemElementClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

		   itemInfo = (AnswerItemInfo) v.getTag();
			switch(v.getId()){
			case R.id.img_play_stop:
				StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_bofang);
				//same item
				if(itemInfo==mPlayingAudioItem){
					if(itemInfo.isplayingAudio){
						mMediaPlayerHelper.stopMedia();
						itemInfo.isplayingAudio=false;
					}else{
						playDownLoadAudio(itemInfo);
					}
				}else{
					if(mPlayingAudioItem!=null){
						mPlayingAudioItem.isplayingAudio = false;
					}
					playDownLoadAudio(itemInfo);
				}

				mPlayingAudioItem=itemInfo;
				mAdapter.notifyDataSetChanged();
				break;
			case R.id.person_center_content:
				StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_thumbs_up);
				//如果请求还没有完成的话 点赞不成功（防止暴力点击）
				if(mPraiseRequestHandle!=null&&!mPraiseRequestHandle.isFinished()){
					return;
				}
				if(UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
					//不许赞自己
					Utils.toast(R.string.spoken_square_praisen_self);
					return;
				}
				if(!itemInfo.isspraise){
					praise(itemInfo.objectId);
				}else{
					Utils.toast(R.string.spoken_square_has_praised);
				}
				break;
			}


		}
	};


	private void playDownLoadAudio(AnswerItemInfo itemInfo){

	    release(mDownloadRequestHandle);

		if(SpokenSquareUtils.hasDownloadAudio(itemInfo.objectId)){
			itemInfo.isplayingAudio=true;
			mMediaPlayerHelper.play(SpokenSquareUtils.getSpokenSquareAudioPath(itemInfo.objectId));
			mAdapter.notifyDataSetChanged();
		}else{
			String saveFilePath=SpokenSquareUtils.getSpokenSquareAudioPath(itemInfo.objectId);
			String serviceFileUrl= itemInfo.audio;
			mDownloadRequestHandle= mHttpRequest.downloadFile(getApplicationContext(), serviceFileUrl, saveFilePath, mAudioDownloadListener);

		}
	}

	private void playPersonAnim(final View personTip) {
		if(mPersonAnim != null && mPersonAnim.isRunning()){
			mPersonAnim.cancel();
		}
		personTip.setVisibility(View.VISIBLE);
		mPersonAnim = ValueAnimator.ofFloat(1f, 0f);
		mPersonAnim.setDuration(1500);
		mPersonAnim.addUpdateListener(new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float cVal = (Float) animation.getAnimatedValue();
				personTip.setAlpha(cVal);
			}
		});
		mPersonAnim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				personTip.setVisibility(View.GONE);
				if(itemInfo != null){
					itemInfo.playPersonAnim = false ;
				}
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				personTip.setVisibility(View.GONE);
				if(itemInfo != null){
					itemInfo.playPersonAnim = false ;
				}
			}
		});
		mPersonAnim.start();
	}


	protected void praise(String objectId) {
		release(mPraiseRequestHandle);
		RequestParams requestParams=new RequestParams();
		requestParams.put(USERID,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_PRAISE_ANSWER_ID,  objectId);

		mPraiseRequestHandle=mHttpRequest.startRequest(getApplicationContext(), ApiUrls.TOPIC_PRAISE, requestParams, mPraiseCallback, RequestType.POST);

	}

	private HttpRequestCallback mPraiseCallback=new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {

		}

		@Override
		public void onRequestSucceeded(String content) {
			PraiseBean praiseBean=Utils.parseJson(content, PraiseBean.class);
			if(praiseBean!=null){
				itemInfo.praise = praiseBean.getDatas().getPraise();
				itemInfo.isspraise = true;//点赞时绑定图片
				itemInfo.playPersonAnim = true;//点击点赞时绑定动画
				mAdapter.notifyDataSetChanged();
				Utils.toast(R.string.spoken_square_praise_success);
			}else{
				Utils.toast(R.string.spoken_square_praise_failed);
			}
		}
	};

	private FileDownloadListener mAudioDownloadListener=new FileDownloadListener(){
		@Override
		public void onDownloadFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onCanceled() {

		}

		@Override
		public void onDownloadSuccess(File downFile) {
			mPlayingAudioItem.isplayingAudio=true;
			mMediaPlayerHelper.play(SpokenSquareUtils.getSpokenSquareAudioPath(mPlayingAudioItem.objectId));
			mAdapter.notifyDataSetChanged();
		}
	};

	private class AnswerItemInfo {
		 int praise;
         String updatedAt;
         float mark;
         String objectId;
         int markerCount;
         int createdAt;
         String topic;
         int recordTime;
         int ispraise;
         String audio;
         boolean isplayingAudio = false ;
         boolean isspraise = false;
         boolean playPersonAnim = false;

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.person_back:
			StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_back);
		finish();
		break;
		case R.id.person_spokensquare:
			StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_spokensquare);
			startActivity(new Intent(getApplicationContext(),SpokenSquareActivity.class));
			break;
		case R.id.person_more:
			StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_more);
			new ActionSheetDialog(PersonCenterActivity.this)
			.builder()
			.setCancelable(false)
			.setCanceledOnTouchOutside(false)
			.addSheetItem(cancelfollowee, SheetItemColor.Blue, new OnSheetItemClickListener(){
				public void onClick(int which) {
					if(cancelfollowee.equals("取消关注")){
						StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_more_cancelFances);
						cancelFances();
					}else{
						StatisticUtils.onEvent(R.string.person_center, R.string.personcenter_more_addFances);
						addFances();
					}
				}
			}).addSheetItem("私信", SheetItemColor.Blue,new OnSheetItemClickListener() {
				public void onClick(int which) {
					if(statusBean.getDatas().getIsfollowe() != 0){   //关注后才能私信
						StatisticUtils.onEvent(R.string.person_center, R.string.spoken_square_private_letter);
						Intent privateLetterIntent=new Intent(getApplicationContext(),PrivateLetterActivity.class);
						privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_ID, mUserobjectId);
						privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_NAME, statusBean.getDatas().getNickname());
						startActivity(privateLetterIntent);
					}else{
						Utils.toast("请先关注");
					}
				}
			}).show();
			break;
		case R.id.person_focus://关注
			    StatisticUtils.onEvent(R.string.person_center, R.string.person_focus);
				Intent centerIntent = new Intent(getApplicationContext(),MyPersonFocus.class);
	        	Bundle bundle = new Bundle();
				if(!UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
		    		bundle.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, mUserobjectId);
				}else{
					bundle.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, UserInfo.getCurrentUser().getObjectId());
				}
				bundle.putString(MyPersonFocus.EXT_KEY_TYPE, "1");
	    		centerIntent.putExtras(bundle);
	            startActivity(centerIntent);

			break;
        case R.id.person_fans://粉丝
			StatisticUtils.onEvent(R.string.person_center, R.string.person_fans);
        	Intent centerIntent_fans = new Intent(getApplicationContext(),MyPersonFocus.class);
        	Bundle bundle_fans = new Bundle();
			if(!UserInfo.getCurrentUser().getObjectId().equals(mUserobjectId)){
				bundle_fans.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, mUserobjectId);
			}else{
				bundle_fans.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, UserInfo.getCurrentUser().getObjectId());
			}
			bundle_fans.putString(MyPersonFocus.EXT_KEY_TYPE, "0");
			centerIntent_fans.putExtras(bundle_fans);
            startActivity(centerIntent_fans);
			break;
        case R.id.person_letter://私信    (关注后才能私信
			StatisticUtils.onEvent(R.string.person_center, R.string.spoken_square_private_letter);
        	if(statusBean.getDatas().getIsfollowe() != 0){
        		if(mUserobjectId != null && statusBean.getDatas().getNickname() != null){
            		Intent privateLetterIntent=new Intent(getApplicationContext(),PrivateLetterActivity.class);
            		privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_ID, mUserobjectId);
        			privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_NAME, statusBean.getDatas().getNickname());
        			startActivity(privateLetterIntent);
            	}
        	}else{
				Utils.toast("请先关注");
			}

        	break;
        case R.id.focus_ed:
			StatisticUtils.onEvent(R.string.person_center, R.string.person_focus);
			//防止点击关注时暴力点击
			if (Utils.isFastClick()) {
				return ;
			}
        	if(mFocused.getText().toString().equals(getString(R.string.person_addfocus))){
        		addFances();
        	}
        	break;
		}

	}

	//加关注
	protected void addFances() {
		if(mAddRequestHandle != null && !mAddRequestHandle.isFinished()){
			return;
		}
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(FOLLOWE, mUserobjectId);
		mAddRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.TOPIC_ATTENTION, requestParams, new AddCallback(), RequestType.POST);
	}

	private class AddCallback implements HttpRequestCallback{

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String content) {
			mAddBean = Utils.parseJson(content, TopicAddBean.class);
			if(mAddBean != null){
				Message msg = new Message();
                msg.what = 3;
                handler.sendMessage(msg);
			}else{
				Message msg = new Message();
                msg.what = 4;
                handler.sendMessage(msg);
			}
			queryDate();
			mAdapter.notifyDataSetChanged();
		}

	};

   //取消关注
	protected void cancelFances() {
		if(mCancelRequestHandle != null && !mCancelRequestHandle.isFinished()){
			return;
		}
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(FOLLOWE, mUserobjectId);
		mCancelRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.TOPIC_CANCELFOLLOWE, requestParams, new CancelCallback(), RequestType.POST);
	}

	private class CancelCallback implements HttpRequestCallback{

		public CancelCallback() {
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String jsonStr) {
			mCancelBean = Utils.parseJson(jsonStr, TopicCancelBean.class);
			if(mCancelBean != null){
				Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
			}else{
				Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
			}
			queryDate();
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mPlayingAudioItem.isplayingAudio=false;
		mAdapter.notifyDataSetChanged();
	}

	private void showProgressDialog(){
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
