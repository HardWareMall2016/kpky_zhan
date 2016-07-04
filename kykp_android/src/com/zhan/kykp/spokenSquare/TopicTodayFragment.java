package com.zhan.kykp.spokenSquare;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.message.PrivateLetterActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.PraiseBean;
import com.zhan.kykp.network.bean.TopicAnswerListBean;
import com.zhan.kykp.network.bean.TopicIndexBean;
import com.zhan.kykp.network.bean.TopicIndexBean.DatasEntity.SpAnswerEntity;
import com.zhan.kykp.userCenter.PersonCenterActivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.RatingBar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

public class TopicTodayFragment extends BaseSpokenSquareFragment implements OnClickListener ,MediaPlayer.OnCompletionListener{
	private final static String TAG = "TopicTodayFragment";
	
	// 中间TAB背景色
	private final static int FOCUS_BG_COLOR = 0xff24b2a4;
	private final static int UNFOCUS_BG_COLOR = 0xff2dcbbb;
	private final static String MY_RECORDING_FILE="my_recording.amr";
	//录音最长时间
	private final static int MAX_RECORDING_SEC = 45;
	//录音最短时长
	private final static int MIN_RECORDING_SEC = 35;
	//精彩回答
	private final static String LIST_TYPE_SP = "sp";
	//全部回答
	private final static String LIST_TYPE_ALL = "all";

	// Views
	private TextView mTVQuestion;
	private TextView mTVGoodAnswer;
	private TextView mTVAllanswer;
	private PullToRefreshListView mPullRefreshListView;
	//Views 录音
	private ImageView mImgRecording;
	private View mRecordingContent;
	private TextView mRecordingTime;
	
	private Dialog mRecordingResultDialog ;
	private Dialog mUploadProgressDlg;

	// Tools
	private LayoutInflater mInflater;
	private boolean mShowGoodAnswers = true;
	private AnswerAdapter mAdapter;
	private Timer mTimer;
	private MediaPlayerHelper mMediaPlayerHelper;
	private AnimationDrawable mAnimSound;
	private DisplayImageOptions options;
	private boolean mIsFirst=true;
	private ValueAnimator mPraiseAnim;//点赞动画
	
	//Params
	private String mSpokenSquarePath;
	private String mMyRecordingPath;
	private int mRecordingSecoinds;
	private AnswerItemInfo mPlayingAudioItem;//正在播放声音的数据项
	private AnswerItemInfo mPraiseItem;//正在播放声音的数据项
	private AnswerItemInfo mAttentionItem;//正在关注的数据项
	private int mSpPageIndex=0;//页索引:精彩回答 
	private int mAllPageIndex=0;//页索引:全部回答
	private String mAnswerDataType=LIST_TYPE_SP;
	
	//Dimens
	private int mRecordingBtnTranslationY;
	private int mRecordingBtnMarginBottom;
	
	//Handler MSG
	private static final int MSG_SHOW_RECORDING_BTN=100;
	private static final int MSG_REFRESH_RECORDING_TIME=101;
	private static final int MSG_FINISH_RECORDING=102;
	
	private static final int SHOW_RECORDING_DELAY=2*1000;
	private final int ANIM_DURATION=300;
	
	//Data
	private List<AnswerItemInfo> mSPAnswerDatas=new LinkedList<AnswerItemInfo>();//精彩回答 
	private List<AnswerItemInfo> mAllAnswerDatas=new LinkedList<AnswerItemInfo>();//全部回答
	private List<AnswerItemInfo> mCurAnswerDatas=mSPAnswerDatas;//当前显示数据
	
	
	//Network
	private BaseHttpRequest mHttpRequest;
	//Request
	private RequestHandle mUploadRequestHandle;
	private RequestHandle mPraiseRequestHandle;
	private RequestHandle mAttentionRequestHandle;
	private RequestHandle mQueryAnswerListRequestHandle;
	private RequestHandle mDownloadRequestHandle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		
		mMediaPlayerHelper=new MediaPlayerHelper(this);
		mAnimSound = (AnimationDrawable) getResources().getDrawable(R.drawable.sound_anim_right);
		mSpokenSquarePath=PathUtils.getExternalSpokenSquareFilesDir().getAbsolutePath();
		mMyRecordingPath=mSpokenSquarePath+"/"+MY_RECORDING_FILE;
		mHttpRequest=new BaseHttpRequest();
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();
		
		View contentView = inflater.inflate(R.layout.spoken_square_frag_today_topic, null);
		initViews(contentView);
		return contentView;
	}

	private void initViews(View contentView) {
		mTVQuestion = (TextView) contentView.findViewById(R.id.question_content);
		mTVQuestion.setText(mQuestionContent);

		mTVGoodAnswer = (TextView) contentView.findViewById(R.id.good_answer);
		mTVGoodAnswer.setOnClickListener(this);
		mTVAllanswer = (TextView) contentView.findViewById(R.id.all_answer);
		mTVAllanswer.setOnClickListener(this);
		
		mImgRecording=(ImageView)contentView.findViewById(R.id.recording);
		mImgRecording.setOnClickListener(this);
		
		mRecordingContent = contentView.findViewById(R.id.content_recording);
		mRecordingTime=(TextView)contentView.findViewById(R.id.recording_time);
		contentView.findViewById(R.id.cancel_recording).setOnClickListener(this);
		contentView.findViewById(R.id.finish_recording).setOnClickListener(this);
		
		ViewTreeObserver vto = mImgRecording.getViewTreeObserver();   
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override  
            public void onGlobalLayout() { 
            	mImgRecording.getViewTreeObserver().removeOnGlobalLayoutListener(this); 
            	FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)mImgRecording.getLayoutParams();
            	mRecordingBtnMarginBottom=lp.bottomMargin;
            	mRecordingBtnTranslationY=mRecordingBtnMarginBottom+mImgRecording.getHeight();
            }   
        });
		
		mAdapter = new AnswerAdapter();
		mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.answer_list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				queryAnswerList(0,mAnswerDataType);
			}
		});
		
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){
			@Override
			public void onLastItemVisible() {
				if(LIST_TYPE_SP.equals(mAnswerDataType)){
					queryAnswerList(mSpPageIndex+1,mAnswerDataType);
				}else{
					queryAnswerList(mAllPageIndex+1,mAnswerDataType);
				}
			}
		});
			
		mPullRefreshListView.getRefreshableView().setOnTouchListener(mListViewOnTouchListener);
		mPullRefreshListView.setAdapter(mAdapter);
		
		ImageView emptyView=new ImageView(getActivity());
		emptyView.setImageResource(R.drawable.no_answer);
		emptyView.setScaleType(ScaleType.CENTER);
		mPullRefreshListView.setEmptyView(emptyView);
		
		refreshTabViews();
		
		if(mIsFirst){
			populateView();
			mIsFirst=false;
		}
	}

	private void refreshTabViews() {
		//首先释放所有请求
		releaseRequest();
		//关闭正在播放的音频
		mMediaPlayerHelper.stopMedia();
		stopSoundAnim();
		
		//显示精彩回答或全部回答
		if (mShowGoodAnswers) {
			mTVGoodAnswer.setBackgroundColor(FOCUS_BG_COLOR);
			mTVAllanswer.setBackgroundColor(UNFOCUS_BG_COLOR);
			mCurAnswerDatas=mSPAnswerDatas;
			mAnswerDataType=LIST_TYPE_SP;
			
			mAdapter.notifyDataSetChanged();
		} else {
			mTVGoodAnswer.setBackgroundColor(UNFOCUS_BG_COLOR);
			mTVAllanswer.setBackgroundColor(FOCUS_BG_COLOR);
			mCurAnswerDatas=mAllAnswerDatas;
			mAnswerDataType=LIST_TYPE_ALL;
			
			//如果进入全部回答是空就请求数据
			if(mAllAnswerDatas.isEmpty()){
				mPullRefreshListView.setRefreshing();
			}else{
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	

	@Override
	public void onDestroyView() {
		release();
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.good_answer:
			if(MediaRecordFunc.getInstance().isRecording()){
				Utils.toast(R.string.spoken_square_recording_no_opt);
				return;
			}
			mShowGoodAnswers = true;
			refreshTabViews();

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today),getString(R.string.spoken_square_good_answer));
			break;
		case R.id.all_answer:
			if(MediaRecordFunc.getInstance().isRecording()){
				Utils.toast(R.string.spoken_square_recording_no_opt);
				return;
			}
			mShowGoodAnswers = false;
			refreshTabViews();

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.spoken_square_all_answer));
			break;
		case R.id.recording:
			startRecording();

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), "开始录音");
			break;
		case R.id.cancel_recording:
			cancelRecording();

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), "取消录音");
			break;
		case R.id.finish_recording:
			finishRecording();

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), "完成录音");
			break;
		}
	}
	
	private void showRecordingView(boolean showRecordingContent){
		if(showRecordingContent){
			mRecordingContent.setVisibility(View.VISIBLE);
			mImgRecording.setVisibility(View.GONE);
		}else{
			mRecordingContent.setVisibility(View.GONE);
			mImgRecording.setVisibility(View.VISIBLE);
		}
	}
	
	private void populateView(){
		TopicIndexBean bean=mCallback.getTopicIndexBean();
		List<SpAnswerEntity> SpAnswerEntityList=bean.getDatas().getSpAnswer();
		if(SpAnswerEntityList==null){
			return;
		}
		for(SpAnswerEntity item:SpAnswerEntityList){
			AnswerItemInfo data=new AnswerItemInfo();
			data.userObjectId=item.getUserId();
			data.name=item.getNickname();
			data.avatar=item.getAvatar();
			data.soundSeconds=item.getRecordTime();
			data.time=parseDateStr(item.getCreatedAt());
			data.hasAttention=item.getIsfollowe()==1;
			data.rating=item.getMark();
			data.praiseCount=item.getPraise();
			data.audio=item.getAudio();
			data.answerObjectId=item.getObjectId();
			data.ispraise=item.getIspraise()==1;
			data.level=item.getLevel();

			mSPAnswerDatas.add(data);
		}
		
		mAdapter.notifyDataSetChanged();
	}

	private String parseDateStr(long unixTime){
		final String dataMigrationTime="2015-09-25";
		long time=Utils.getCurrentTimeZoneUnixTime(unixTime);
		String timeStr=Utils.getDateFormateStr(time);
		if(dataMigrationTime.equals(timeStr)){
			return mCallback.getTopicTime();
		}else{
			return timeStr;
		}
	}

	
	private class AnswerItemInfo{
		String answerObjectId;
		String userObjectId;
		String name;
		int soundSeconds;
		String time;
		String avatar;
		float rating;
		boolean hasAttention;
		int praiseCount;
		String audio;
		String level;
		boolean ispraise=false;
		boolean playPraiseAnim =false;
		boolean playingAudioAnim=false;
		boolean isTeacher=false;

	}
	
	private class AnswerAdapter extends BaseAdapter {
		
		private class ViewHolder{
			ImageView avatar;
			TextView name;
			View soundContent;
			TextView soundSeconds;
			ImageView soundAnim;
			TextView time;
			RatingBar ratingbar;
			TextView level;
			
			ImageView teacher;
			
			View attentionOrPrivateLetterContent;
			TextView attentionOrPrivateLetter;
			
			View praiseContent;
			TextView praise;
			TextView praiseTip;
		}
		
		@Override
		public int getCount() {
			return mCurAnswerDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mCurAnswerDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.spoken_square_answer_list_item, null);
				
				holder.avatar=(ImageView)convertView.findViewById(R.id.avatar);
				holder.name=(TextView)convertView.findViewById(R.id.name);
				holder.soundContent=convertView.findViewById(R.id.sound_content);
				holder.soundSeconds=(TextView)convertView.findViewById(R.id.sound_seconds);
				holder.soundAnim=(ImageView)convertView.findViewById(R.id.sound_anim);
				holder.time=(TextView)convertView.findViewById(R.id.time);
				holder.ratingbar=(RatingBar)convertView.findViewById(R.id.ratingbar);
				
				holder.teacher=(ImageView)convertView.findViewById(R.id.teach);
				
				holder.attentionOrPrivateLetterContent=convertView.findViewById(R.id.attention_or_private_letter_conetnt);
				holder.attentionOrPrivateLetter=(TextView)convertView.findViewById(R.id.attention_or_private_letter);
				
				holder.praiseContent=convertView.findViewById(R.id.praise_conetnt);
				holder.praise=(TextView)convertView.findViewById(R.id.praise);
				holder.praiseTip=(TextView)convertView.findViewById(R.id.praise_tip);

				holder.level=(TextView)convertView.findViewById(R.id.level);
				
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder)convertView.getTag();
			}
			

			//头像
			String avatarPath=mCurAnswerDatas.get(position).avatar;//
			
			ImageLoader.getInstance().displayImage(avatarPath, holder.avatar, options);
			
			holder.name.setText(mCurAnswerDatas.get(position).name);
			
			if(mCurAnswerDatas.get(position).isTeacher){
				holder.teacher.setVisibility(View.VISIBLE);
			}else{
				holder.teacher.setVisibility(View.GONE);
			}

			holder.level.setText(mCurAnswerDatas.get(position).level);

			holder.soundContent.setTag(mCurAnswerDatas.get(position));
			holder.soundContent.setOnClickListener(mOnItemElementClickListener);
			holder.soundSeconds.setText(mCurAnswerDatas.get(position).soundSeconds+"\"");
			holder.time.setText(mCurAnswerDatas.get(position).time);
			AnimationDrawable audioAnim=(AnimationDrawable) holder.soundAnim.getBackground();
			if(mCurAnswerDatas.get(position).playingAudioAnim){
				audioAnim.start();
			}else{
				audioAnim.stop();
			}
			
			holder.ratingbar.setRating(mCurAnswerDatas.get(position).rating);
			
			if(mCurAnswerDatas.get(position).hasAttention){
				holder.attentionOrPrivateLetter.setText(getString(R.string.spoken_square_private_letter));
				holder.attentionOrPrivateLetter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_private_letter, 0, 0, 0);
			}else{
				holder.attentionOrPrivateLetter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_attention, 0, 0, 0);
				holder.attentionOrPrivateLetter.setText(getString(R.string.spoken_square_attention));
			}
			
			holder.attentionOrPrivateLetterContent.setOnClickListener(mOnItemElementClickListener);
			holder.attentionOrPrivateLetterContent.setTag(mCurAnswerDatas.get(position));
			

			holder.praiseContent.setOnClickListener(mOnItemElementClickListener);
			holder.praiseContent.setTag(mCurAnswerDatas.get(position));
			
			if(mCurAnswerDatas.get(position).playPraiseAnim){
				playPraiseAnim(holder.praiseTip);
			}else{
				holder.praiseTip.setVisibility(View.GONE);
			}
			
			holder.praise.setText(String.valueOf(mCurAnswerDatas.get(position).praiseCount));
			if(mCurAnswerDatas.get(position).ispraise){
				holder.praise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_un_praised, 0, 0, 0);
			}else{
				holder.praise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_un_praise, 0, 0, 0);
			}
			
			
			String userObjectId=mCurAnswerDatas.get(position).userObjectId;
			holder.avatar.setTag(userObjectId);
			holder.name.setTag(userObjectId);
			holder.avatar.setOnClickListener(mOnItemElementClickListener);
			holder.name.setOnClickListener(mOnItemElementClickListener);
			
			return convertView;
		}
		
		
		private OnClickListener mOnItemElementClickListener=new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(MediaRecordFunc.getInstance().isRecording()){
					return;
				}
				switch(v.getId()){
				case R.id.avatar:
				case R.id.name:
					stopPlayMedia();
					String userObjectId=(String) v.getTag();
					Intent personalCenterIntent=new Intent(getActivity(),PersonCenterActivity.class);
					personalCenterIntent.putExtra(PersonCenterActivity.EXT_KEY_USEROBJECT, userObjectId);
					startActivity(personalCenterIntent);

					StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.user_info_head));
					break;
				case R.id.attention_or_private_letter_conetnt:
					mAttentionItem=(AnswerItemInfo)v.getTag();
					if(UserInfo.getCurrentUser().getObjectId().equals(mAttentionItem.userObjectId)){
						//不许关注自己
						Utils.toast(R.string.spoken_square_attention_self);
						return;
					}
					
					if(!mAttentionItem.hasAttention){
						attention(mAttentionItem.userObjectId);

						StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.spoken_square_attention));
					}else{
						stopPlayMedia();
						Intent privateLetterIntent=new Intent(getActivity(),PrivateLetterActivity.class);
						privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_ID, mAttentionItem.userObjectId);
						privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_NAME, mAttentionItem.name);
						startActivity(privateLetterIntent);

						StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.spoken_square_private_letter));
					}
					break;
				case R.id.praise_conetnt:
					mPraiseItem=(AnswerItemInfo)v.getTag();
					if(UserInfo.getCurrentUser().getObjectId().equals(mPraiseItem.userObjectId)){
						//不许赞自己
						Utils.toast(R.string.spoken_square_praisen_self);
						return;
					}
					if(!mPraiseItem.ispraise){
						praise(mPraiseItem.answerObjectId);

						StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), "点赞");
					}else{
						Utils.toast(R.string.spoken_square_has_praised);
					}
					break;
				case R.id.sound_content:

					StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.statistic_play_stop_audio));

					AnswerItemInfo itemInfo=(AnswerItemInfo)v.getTag();

					//当有录音播放时，先停止当前录音
					if(mPlayingAudioItem!=null&&mPlayingAudioItem.playingAudioAnim){
						mPlayingAudioItem.playingAudioAnim=false;
						stopPlayMyRecording();
						mAdapter.notifyDataSetChanged();

						//如果点击的是当前的，就到此为止
						if(itemInfo==mPlayingAudioItem){
							mPlayingAudioItem=null;
							return;
						}
					}

					mPlayingAudioItem=itemInfo;
					//如果当前还有未完成的下载请求，先取消掉
					releaseRequest(mDownloadRequestHandle);
					
					if(SpokenSquareUtils.hasDownloadAudio(mPlayingAudioItem.answerObjectId)){
						
						mPlayingAudioItem.playingAudioAnim=true;
						mMediaPlayerHelper.play(SpokenSquareUtils.getSpokenSquareAudioPath(mPlayingAudioItem.answerObjectId));
						
						mAdapter.notifyDataSetChanged();
					}else{
						String saveFilePath=SpokenSquareUtils.getSpokenSquareAudioPath(mPlayingAudioItem.answerObjectId);
						String serviceFileUrl=mPlayingAudioItem.audio;
						mDownloadRequestHandle=mHttpRequest.downloadFile(getActivity(), serviceFileUrl, saveFilePath, mAudioDownloadListener);
					}
					break;
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
				if(mPlayingAudioItem==null){
					return;
				}
				mPlayingAudioItem.playingAudioAnim=true;
				mMediaPlayerHelper.play(SpokenSquareUtils.getSpokenSquareAudioPath(mPlayingAudioItem.answerObjectId));
				mAdapter.notifyDataSetChanged();
			}
		};
	}

	private OnTouchListener mListViewOnTouchListener=new OnTouchListener(){
		private float mDownY;
		private float distance;
		boolean mHideRecordingBtn=false;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if(mImgRecording.getTranslationY()==mRecordingBtnTranslationY){
				mRecordingHandler.removeMessages(MSG_SHOW_RECORDING_BTN);
				mRecordingHandler.sendEmptyMessageDelayed(MSG_SHOW_RECORDING_BTN, SHOW_RECORDING_DELAY+ANIM_DURATION);
				return false;
			}
				
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHideRecordingBtn=false;
				mDownY=event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				distance=mDownY-event.getY();
				//上滑隐藏
				if(distance>0){
					mImgRecording.setTranslationY(distance);
					if(distance>mRecordingBtnMarginBottom/2){
						mHideRecordingBtn=true;
					}else{
						mHideRecordingBtn=false;
					}
				}
				break;
			default:
				//上滑隐藏
				if(mHideRecordingBtn){
					ObjectAnimator.ofFloat(mImgRecording, "TranslationY",distance,mRecordingBtnTranslationY).setDuration(ANIM_DURATION).start();
					mRecordingHandler.removeMessages(MSG_SHOW_RECORDING_BTN);
					mRecordingHandler.sendEmptyMessageDelayed(MSG_SHOW_RECORDING_BTN, SHOW_RECORDING_DELAY+ANIM_DURATION);
				}else{
					if(distance>0){
						//滑动距离不够，返回
						ObjectAnimator.ofFloat(mImgRecording, "TranslationY",distance,0).setDuration(ANIM_DURATION).start();
					}else{
						mImgRecording.setTranslationY(0);
					}
				}
				break;
			}
			return false;
		}
	};
	
	
	private Handler mRecordingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_RECORDING_BTN:
				ObjectAnimator.ofFloat(mImgRecording, "TranslationY",mRecordingBtnTranslationY,0).setDuration(ANIM_DURATION).start();
				break;
			case MSG_REFRESH_RECORDING_TIME:
				mRecordingTime.setText(String.valueOf(mRecordingSecoinds));
				break;
			case MSG_FINISH_RECORDING:
				finishRecording();
				break;
			}
		}
	};
	
	
	private void release(){
		mRecordingHandler.removeMessages(MSG_SHOW_RECORDING_BTN);
		stopRecording();
		mMediaPlayerHelper.releaseMedia();
		
		releaseRequest();
	}
	
	private void releaseRequest(){
		releaseRequest(mUploadRequestHandle);
		releaseRequest(mPraiseRequestHandle);
		releaseRequest(mAttentionRequestHandle);
		releaseRequest(mQueryAnswerListRequestHandle);
		releaseRequest(mDownloadRequestHandle);
	}
	
	private void startRecording(){
		//先停止播放录音
		stopPlayMyRecording();
		if(mPlayingAudioItem!=null&&mPlayingAudioItem.playingAudioAnim){
			mPlayingAudioItem.playingAudioAnim=false;
			mAdapter.notifyDataSetChanged();
		}
		
		showRecordingView(true);
		removeMyRecording();
		mRecordingSecoinds = 0;
		mTimer = new Timer(true);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				mRecordingSecoinds++;
				mRecordingHandler.sendEmptyMessage(MSG_REFRESH_RECORDING_TIME);
				//超过MAX_RECORDING_SEC自动停止
				if (mRecordingSecoinds == MAX_RECORDING_SEC) {
					mRecordingHandler.sendEmptyMessage(MSG_FINISH_RECORDING);
				}
			}
		};
		
		mTimer.schedule(timerTask, 0, 1000);
		MediaRecordFunc.getInstance().startRecordAndFile(mMyRecordingPath);
	}
	
	private void stopRecording() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mRecordingHandler.removeMessages(MSG_REFRESH_RECORDING_TIME);
		mRecordingHandler.removeMessages(MSG_FINISH_RECORDING);
		MediaRecordFunc.getInstance().stopRecordAndFile();
	}
	
	private void finishRecording(){
		showRecordingView(false);
		stopRecording();
		if(mRecordingSecoinds<MIN_RECORDING_SEC){
			Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
			dialog.setContentView(R.layout.speaking_dialog_invalid_recording_view);
			dialog.show();
		}else{
			showRecordingResultDlg();
		}
	}
	
	private void cancelRecording(){
		showRecordingView(false);
		stopRecording();
	}
	
	private void showRecordingResultDlg(){
		mRecordingResultDialog = new Dialog(getActivity(), R.style.Dialog);
		View dlgContent = mInflater.inflate(R.layout.spoken_square_recording_dialog, null);
		TextView tvRecordingSeconds = (TextView) dlgContent.findViewById(R.id.recording_seconds);
		
		ImageView imgSound = (ImageView) dlgContent.findViewById(R.id.img_sound);
		imgSound.setBackground(mAnimSound);
		
		tvRecordingSeconds.setText(String.format("%s\"", mRecordingSecoinds));
		
		//上传录音
		dlgContent.findViewById(R.id.upload_recording).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopPlayMyRecording();
				uploadMyRecording();

				StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.spoken_square_upload_recording));

			}
		});
		
		//重新答题
		dlgContent.findViewById(R.id.re_answer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopPlayMyRecording();
				mRecordingResultDialog.dismiss();
				removeMyRecording();

				StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), getString(R.string.spoken_square_re_answer));
			}
		});
		
		//播放录音
		dlgContent.findViewById(R.id.recording_info_content).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mMediaPlayerHelper.isPlaying()){
					stopPlayMyRecording();
				}else{
					playMyRecording();
				}

				StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_topic_today), "播放/暂停我的录音");
			}
		});
		
		mRecordingResultDialog.setContentView(dlgContent);
		mRecordingResultDialog.setCancelable(false);
		mRecordingResultDialog.show();
	}
	
	private void playMyRecording(){
		mAnimSound.start();
		mMediaPlayerHelper.play(mMyRecordingPath);
	}
	
	private void stopPlayMyRecording(){
		mAnimSound.stop();
		mMediaPlayerHelper.stopMedia();
	}
	
	private void removeMyRecording(){
		File recordingFile=new File(mMyRecordingPath);
		if(recordingFile.exists()){
			recordingFile.delete();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stopSoundAnim();
	}
	
	private void stopSoundAnim(){
		if(mAnimSound!=null){
			mAnimSound.stop();
		}
		if(mPlayingAudioItem!=null){
			mPlayingAudioItem.playingAudioAnim=false;
			mPlayingAudioItem=null;
			mAdapter.notifyDataSetChanged();
		}
	}
	
	
	private void showUploadProgressDialog() {
		mUploadProgressDlg = DialogUtils.getCircleProgressDialog(getActivity(), getString(R.string.spoken_square_uploading_recording));
		mUploadProgressDlg.setCancelable(false);
		mUploadProgressDlg.show();
	}

	private void closeUploadProgressDialog() {
		if (mUploadProgressDlg != null) {
			mUploadProgressDlg.dismiss();
			mUploadProgressDlg = null;
		}
	}
	
	/***
	 * Network
	 */
	
	private void releaseRequest(RequestHandle request){
		if(request!=null&&!request.isFinished()){
			request.cancel(true);
		}
	}
	
	//Topic - 回答
	private final static String NET_PARAM_USER="user";
	private final static String NET_PARAM_TOPIC="topic";
	private final static String NET_PARAM_RECORDING_SEC="time";
	private final static String NET_PARAM_AUDIO="audio";
	
	private void uploadMyRecording(){
		Log.i(TAG, "start uploadMyRecording");
		if(mUploadRequestHandle!=null&&!mUploadRequestHandle.isFinished()){
			return;
		}
		showUploadProgressDialog();
		RequestParams requestParams=new RequestParams();
		try {
			requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
			requestParams.put(NET_PARAM_TOPIC, mCallback.getTopicIndexBean().getDatas().getObjectId());
			requestParams.put(NET_PARAM_RECORDING_SEC, mRecordingSecoinds);
			requestParams.put(NET_PARAM_AUDIO, new File(mMyRecordingPath));
			
			mUploadRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_UPLOAD_RECORDING, requestParams, mUploadRecordingCallback, RequestType.POST);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "uploadMyRecording FileNotFoundException : "+mMyRecordingPath);
			closeUploadProgressDialog();
		}
	}
	
	private HttpRequestCallback mUploadRecordingCallback=new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
			closeUploadProgressDialog();
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
			closeUploadProgressDialog();
		}

		@Override
		public void onRequestCanceled() {
			closeUploadProgressDialog();
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeUploadProgressDialog();
			Gson gson = new Gson();
			BaseBean statusBean=gson.fromJson(content, BaseBean.class);
			if(statusBean.getStatus()==1){
				//上传录音成功，关闭对话框
				if(mRecordingResultDialog!=null){
					mRecordingResultDialog.dismiss();
				}
				//清除录音文件
				removeMyRecording();
				Utils.toast(R.string.spoken_square_upload_recording_Success);
			}else{
				Utils.toast(R.string.spoken_square_upload_recording_failed);
			}
		}
	};
	
	
	//Topic - 点赞
	private final static String NET_PARAM_PRAISE_ANSWER_ID="record";
	/**
	 * 点赞
	 * @param answerObjectId
	 */
	private void praise(String answerObjectId){
		Log.i(TAG, "start praise");
		//releaseRequest(mPraiseRequestHandle);
		
		if(mPraiseRequestHandle!=null&&!mPraiseRequestHandle.isFinished()){
			return;
		}
		
		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_PRAISE_ANSWER_ID,  answerObjectId);
		
		mPraiseRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_PRAISE, requestParams, mPraiseCallback, RequestType.POST);
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
			if(mPraiseItem==null){
				return;
			}
			Log.i(TAG, "onRequestSucceeded");
			PraiseBean praiseBean=Utils.parseJson(content, PraiseBean.class);
			if(praiseBean!=null){
				mPraiseItem.praiseCount=praiseBean.getDatas().getPraise();
				mPraiseItem.ispraise=true;
				mPraiseItem.playPraiseAnim=true;
				mAdapter.notifyDataSetChanged();
				//Utils.toast(R.string.spoken_square_praise_success);
			}else{
				Utils.toast(R.string.spoken_square_praise_failed);
			}
		}
	};
	
	private void playPraiseAnim(final View praiseTip){
		if(mPraiseAnim!=null&&mPraiseAnim.isRunning()){
			mPraiseAnim.cancel();
		}
		praiseTip.setVisibility(View.VISIBLE);
		mPraiseAnim = ValueAnimator.ofFloat(1f, 0f);
		mPraiseAnim.setDuration(1500);
		mPraiseAnim.addUpdateListener(new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float cVal = (Float) animation.getAnimatedValue(); 
				praiseTip.setAlpha(cVal);
			}
		});
		mPraiseAnim.addListener(new AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				praiseTip.setVisibility(View.GONE);
				if(mPraiseItem!=null){
					mPraiseItem.playPraiseAnim=false;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				praiseTip.setVisibility(View.GONE);
				if(mPraiseItem!=null){
					mPraiseItem.playPraiseAnim=false;
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

				
			}});
		
		mPraiseAnim.start();
	}
	
	//Topic - 关注
	private final static String NET_PARAM_FOLLOWED_USER_ID="followe";
	
	private void attention(String attentionUserId){
		Log.i(TAG, "start attention");
		
		if(mAttentionRequestHandle!=null&&!mAttentionRequestHandle.isFinished()){
			return;
		}
		
		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_FOLLOWED_USER_ID,  attentionUserId);
		
		mAttentionRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_ATTENTION, requestParams, mAttentionCallback, RequestType.POST);
	}
	
	private HttpRequestCallback mAttentionCallback=new HttpRequestCallback(){
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
			if(mAttentionItem==null){
				return;
			}
			Gson gson = new Gson();
			BaseBean statusBean=gson.fromJson(content, BaseBean.class);
			if(statusBean.getStatus()==1){
				for(AnswerItemInfo item:mSPAnswerDatas){
					if(item.userObjectId.equals(mAttentionItem.userObjectId)){
						item.hasAttention=true;
					}
				}
				
				for(AnswerItemInfo item:mAllAnswerDatas){
					if(item.userObjectId.equals(mAttentionItem.userObjectId)){
						item.hasAttention=true;
					}
				}
				
				mAdapter.notifyDataSetChanged();
				Utils.toast(R.string.spoken_square_attention_success);
			}else{
				Utils.toast(R.string.spoken_square_attention_failed);
			}
		}
	};
	
	//Topic - 回答列表
	private final static String NET_PARAM_ANSWER_TOPIC="topic";
	private final static String NET_PARAM_ANSWER_LIST_PAGE="page";
	private final static String NET_PARAM_ANSWER_LIST_TYPE="type";
	
	private void queryAnswerList(int page,String type){
		Log.i(TAG, "start queryAnswerList");
		releaseRequest(mQueryAnswerListRequestHandle);
		
		if(page==0){
			stopPlayMyRecording();
		}
		
		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_ANSWER_TOPIC,  mCallback.getTopicIndexBean().getDatas().getObjectId());
		requestParams.put(NET_PARAM_USER,  UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_ANSWER_LIST_PAGE,  page);
		requestParams.put(NET_PARAM_ANSWER_LIST_TYPE,  type);
		
		mQueryAnswerListRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_ANSSERLIST, requestParams, new QueryAnswerListCallback(type,page), RequestType.GET);
		
	}
	
	
	private class QueryAnswerListCallback implements HttpRequestCallback{
		private int mPage;
		private String mType;
		
		public QueryAnswerListCallback(String type ,int page){
			mPage=page;
			mType=type;
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
			
			TopicAnswerListBean bean=Utils.parseJson(content, TopicAnswerListBean.class);
			
			if(bean==null){
				Utils.toast(R.string.network_query_data_failed);
				return;
			}
			
			List<com.zhan.kykp.network.bean.TopicAnswerListBean.DatasEntity> answerEntityList=bean.getDatas();
			
			
			if(mPage==0){
				mPlayingAudioItem=null;//正在播放声音的数据项
				mPraiseItem=null;//正在播放声音的数据项
				mAttentionItem=null;//正在关注的数据项
			}
			
			if(LIST_TYPE_SP.equals(mType)){
				if(mPage==0){
					mSPAnswerDatas.clear();
					mSpPageIndex=0;
				}
				if(answerEntityList.size()>0){
					mSpPageIndex=mPage;
				}
				
			}else{
				if(mPage==0){
					mAllAnswerDatas.clear();
					mAllPageIndex=0;
				}
				if(answerEntityList.size()>0){
					mAllPageIndex=mPage;
				}
			}

			for(com.zhan.kykp.network.bean.TopicAnswerListBean.DatasEntity item:answerEntityList){
				AnswerItemInfo data=new AnswerItemInfo();
				data.userObjectId=item.getUserId();
				data.name=item.getNickname();
				data.avatar=item.getAvatar();
				data.soundSeconds=item.getRecordTime();
				data.time=parseDateStr(item.getCreatedAt());
				data.hasAttention=item.getIsfollowe()==1;
				data.rating=item.getMark();
				data.praiseCount=item.getPraise();
				data.audio=item.getAudio();
				data.answerObjectId=item.getObjectId();
				data.ispraise=item.getIspraise()==1;
				data.isTeacher=item.getIsteacher()==1;
				data.level=item.getLevel();
				
				if(LIST_TYPE_SP.equals(mAnswerDataType)){
					mSPAnswerDatas.add(data);
				}else{
					mAllAnswerDatas.add(data);
				}
			}
			mAdapter.notifyDataSetChanged();
			
			//超过2页时没有数据才提示没有数据了
			if(mPage>2&&answerEntityList.size() == 0) {
				Utils.toast(R.string.no_more_data);
			}
		}
	}


	@Override
	public void stopPlayMedia() {
		//关闭正在播放的音频
		if(mMediaPlayerHelper!=null){
			mMediaPlayerHelper.stopMedia();
		}
		
		stopSoundAnim();
	}
}
