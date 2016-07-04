package com.zhan.kykp.spokenSquare;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.MarkIndexBean;
import com.zhan.kykp.network.bean.MarkResultBean;
import com.zhan.kykp.network.bean.PraiseBean;
import com.zhan.kykp.userCenter.PersonCenterActivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.RatingBar;
import com.zhan.kykp.widget.RatingBar.OnDisableClickedlListener;
import com.zhan.kykp.widget.RatingBar.OnRatingBarChangeListener;
import com.zhan.kykp.widget.SampleProgressView;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GradingByMeFragment extends BaseSpokenSquareFragment implements OnClickListener,OnRatingBarChangeListener,MediaPlayer.OnCompletionListener{
	private final static String TAG = "GradingByMeFragment";
	
	// Views
	private View mRootView;
	private TextView mTVQuestion;
	private SampleProgressView mProgressView;
	private ImageView mPlayPause;
	private TextView mTVPlayTime;
	private RatingBar mRatingBar;
	private RatingBar mOtherRatingBar;
	private ImageView mImgAvatar;
	private TextView mTVAllMarkCount;
	private TextView mTVPraise;
	private TextView mTVAnswerName;
	//问题下面分两块，每次只显示其中之一
	private View mViewGradingByMeContent;
	private View mViewGradingByOtherContent;
	private Dialog mProgressDlg;
	
	// Tools
	private LayoutInflater mInflater;
	private MediaPlayerHelper mMediaPlayerHelper;
	private Timer mPlayAnswerTimer;
	private TimerTask mPlayAnswerTimerTask;
	private DisplayImageOptions options;
	
	//Data
	private MarkIndexBean mMarkIndexBean;
	private MarkResultBean mMarkresult;

	//是否已经打分
	private boolean mHasGrading=false;
	//是否可以评分
	private boolean mCanGrading=false;
	private int mRecordingSec;
	private final static int HND_MSG_REFRESH_RATING=1000;
	
	//Network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mMarkIndexRequestHandle;//请求数据
	private RequestHandle mDownloadRequestHandle;//现在音频
	private RequestHandle mMarkRequestHandle;//打分
	private RequestHandle mPraiseRequestHandle;//点赞
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mInflater=inflater;
		mMediaPlayerHelper=new MediaPlayerHelper(this);
		mHttpRequest=new BaseHttpRequest();
		
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();
		
		mRootView=inflater.inflate(R.layout.spoken_square_frag_grading_by_me, null);
		initViews(mRootView);
		
		populateView();
		
		if(!checkJsonDataValid()){
			queryMarkDataAndPupulateView();
		}
		
		return mRootView;
	}
	
	private void initViews(View contentView) {
		mViewGradingByMeContent=contentView.findViewById(R.id.grading_by_me_content);
		mViewGradingByOtherContent=contentView.findViewById(R.id.grading_by_other_content);
		
		mTVQuestion = (TextView) contentView.findViewById(R.id.question_content);
		
		mProgressView= (SampleProgressView)contentView.findViewById(R.id.play_progress);
		
		mPlayPause= (ImageView)contentView.findViewById(R.id.play_pause);
		mPlayPause.setOnClickListener(this);
		
		mTVPlayTime=(TextView) contentView.findViewById(R.id.play_time);
		
		mRatingBar= (RatingBar)contentView.findViewById(R.id.ratingbar);
		mRatingBar.setOnRatingBarChangeListener(this);
		mRatingBar.setOnDisableClickedListener(new OnDisableClickedlListener(){
			@Override
			public void onDisableChanged() {
				if(mMediaPlayerHelper.isPlaying()){
					Utils.toast(R.string.spoken_square_mark_later);
				}else{
					Utils.toast(R.string.spoken_square_listening_first);
				}
			}
		});
		
		contentView.findViewById(R.id.switch_other).setOnClickListener(this);
		
		mOtherRatingBar=(RatingBar)contentView.findViewById(R.id.ratingbar_other);
		
		mImgAvatar=(ImageView)contentView.findViewById(R.id.avatar);
		mImgAvatar.setOnClickListener(this);
		mTVAllMarkCount=(TextView) contentView.findViewById(R.id.grading_count);
		
		mTVPraise=(TextView) contentView.findViewById(R.id.praise);
		contentView.findViewById(R.id.praise_content).setOnClickListener(this);
		
		mTVAnswerName=(TextView) contentView.findViewById(R.id.answer_name);
		
		contentView.findViewById(R.id.listen_other).setOnClickListener(this);
		
		//问题
		mTVQuestion.setText(mQuestionContent);
	}
	
	/**
	 * 显示从服务器上获取数据
	 */
	private void populateView(){
		mRatingBar.setRating(0);
		stopPalyAnswer();
		
		if(checkJsonDataValid()){
			//音频时长
			mRecordingSec=mMarkIndexBean.getDatas().getAnswer().getRecordTime();
			mTVPlayTime.setText(String.format("%02d:%02d", mRecordingSec/60,mRecordingSec%60));
		}else{
			mTVPlayTime.setText("");
		}
		refreshView();
	}
	
	private void refreshView(){
		//已经评分
		if(mHasGrading){
			mViewGradingByMeContent.setVisibility(View.GONE);
			mViewGradingByOtherContent.setVisibility(View.VISIBLE);
			pupulateOtherContent();
		}else{
			mViewGradingByMeContent.setVisibility(View.VISIBLE);
			mViewGradingByOtherContent.setVisibility(View.GONE);
			refreshMyRatingBar();
		}
	}
	
	private void pupulateOtherContent(){
		//头像
		String avatarPath=mMarkresult.getDatas().getAvatar();
		ImageLoader.getInstance().displayImage(avatarPath,mImgAvatar,options);
		mOtherRatingBar.setRating(mMarkresult.getDatas().getMark());
		String markCountStr=getString(R.string.spoken_square_grade_count);
		mTVAllMarkCount.setText(String.format(markCountStr, mMarkresult.getDatas().getMarkerCount()));
		
		if(mMarkresult.getDatas().getIspraise()==1){
			mTVPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_praised, 0, 0, 0);
		}else{
			mTVPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spoken_square_praise, 0, 0, 0);
		}
		
		mTVPraise.setText(String.valueOf(mMarkresult.getDatas().getPraise()));
		
		mTVAnswerName.setText(mMarkresult.getDatas().getNickname());
	}
	
	private void refreshMyRatingBar(){
		if(mCanGrading){
			mRatingBar.setEnable(true);
			mRatingBar.setIsIndicator(false);
		}else{
			mRatingBar.setEnable(false);
			mRatingBar.setIsIndicator(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.play_pause:

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_grading_by_me), getString(R.string.statistic_play_stop_audio));

			if(mMediaPlayerHelper.isPlaying()){
				stopPalyAnswer();
			}else{
				if(!checkJsonDataValid()){
					Utils.toast(R.string.spoken_square_no_answer);
					return;
				}
				//音频已下载
				if(SpokenSquareUtils.hasDownloadAudio(mMarkIndexBean.getDatas().getAnswer().getObjectId())){
					startPalyAnswer();
				}else{
					downloadAndPlay();
				}
			}
			break;
		case R.id.avatar:

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_grading_by_me), getString(R.string.user_info_head));

			if(mMarkIndexBean!=null){
				if(mMediaPlayerHelper.isPlaying()){
					stopPalyAnswer();
				}
				String userObjectId=mMarkIndexBean.getDatas().getAnswer().getUserId();
				Intent personalCenterIntent=new Intent(getActivity(),PersonCenterActivity.class);
				personalCenterIntent.putExtra(PersonCenterActivity.EXT_KEY_USEROBJECT, userObjectId);
				startActivity(personalCenterIntent);
			}
			break;
		case R.id.listen_other:
		case R.id.switch_other:

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_grading_by_me), getString(R.string.spoken_square_listen_other));

			queryMarkDataAndPupulateView();
			break;
		case R.id.praise_content:
			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_grading_by_me), getString(R.string.sys_praise));

			if(mMarkresult!=null&&mMarkresult.getStatus()==1&&mMarkresult.getDatas().getIspraise()==0){
				praise(mMarkIndexBean.getDatas().getAnswer().getObjectId());
			}
			
			if(mMarkresult!=null&&mMarkresult.getStatus()==1&&mMarkresult.getDatas().getIspraise()==1){
				Utils.toast(R.string.spoken_square_has_praised);
			}
			break;
		}
	}
	
	@Override
	public void onDestroyView() {
		release();
		super.onDestroyView();
	}
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HND_MSG_REFRESH_RATING:
				refreshMyRatingBar();
			}
		}
	};
	
	/**
	 * 音频文件本地存储路径
	 * @return
	 */
	private String getAudioLocalPath(){
		String answerAudioPath=SpokenSquareUtils.getSpokenSquareAudioPath(mMarkIndexBean.getDatas().getAnswer().getObjectId());
		return answerAudioPath;
	}
	
	/***
	 * 开始播放答题
	 */
	private void startPalyAnswer(){
		releasePlayAnswerTimer();
		mPlayAnswerTimer = new Timer();
		mPlayAnswerTimerTask = new TimerTask() {
			@Override
			public void run() {
				if(mMediaPlayerHelper.isPlaying()){
					int progress = (int) (100f * mMediaPlayerHelper.getCurrentPosition() / mMediaPlayerHelper.getDuration());
					mProgressView.setProgress(progress);
					//在没有评分，并且播放时间超过一半时：允许评分
					if(!mHasGrading&&progress>50&&!mCanGrading){
						mCanGrading=true;
						mHandler.sendEmptyMessage(HND_MSG_REFRESH_RATING);
					}
				}
			}
		};
		mPlayPause.setImageResource(R.drawable.spoken_square_stop);
		mProgressView.setProgress(0);
		mMediaPlayerHelper.play(getAudioLocalPath());
		mPlayAnswerTimer.schedule(mPlayAnswerTimerTask, 0, 10);
	}
	
	/***
	 * 停止播放
	 */
	private void stopPalyAnswer(){
		releasePlayAnswerTimer();
		if(mMediaPlayerHelper!=null){
			mMediaPlayerHelper.stopMedia();
		}
		
		if(mProgressView!=null){
			mProgressView.setProgress(0);
		}
		
		if(mPlayPause!=null){
			mPlayPause.setImageResource(R.drawable.spoken_square_play);
		}
	}
	

	private void release(){
		releasePlayAnswerTimer();
		mMediaPlayerHelper.releaseMedia();
		releaseHttpRequest();
	}
	
	private void releasePlayAnswerTimer() {
		if (mPlayAnswerTimer != null) {
			mPlayAnswerTimer.cancel();
			mPlayAnswerTimer = null;
		}
		
		if(mPlayAnswerTimerTask != null){
			mPlayAnswerTimerTask.cancel();
			mPlayAnswerTimerTask = null;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		releasePlayAnswerTimer();
		mProgressView.setProgress(0);
		mPlayPause.setImageResource(R.drawable.spoken_square_play);
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,boolean byUser) {
		if(byUser){
			markAnswer((int) rating);

			StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.spoken_square_grading_by_me), getString(R.string.spoken_square_grade_for_answer));
		}
	}
	
	/***
	 * 检查服务器返回的Json是否有效
	 * @return
	 */
	private boolean checkJsonDataValid(){
		if(mMarkIndexBean!=null&&mMarkIndexBean.getStatus()==1&&!TextUtils.isEmpty(mMarkIndexBean.getDatas().getAnswer().getAudio())){
			return true;
		}else{
			return false;
		}
	}
	
	
	private void releaseHttpRequest(){
		releaseDownloadRequest();
		releaseMarkIndexRequest();
		releaseMarkRequest();
		releasePraiseRequest();
	}	
	
	private void downloadAndPlay(){
		//如果当前还有未完成的下载请求，先取消掉
		releaseDownloadRequest();
		//音频地址
		String serviceFileUrl=mMarkIndexBean.getDatas().getAnswer().getAudio();
		mDownloadRequestHandle=mHttpRequest.downloadFile(getActivity(), serviceFileUrl, getAudioLocalPath(), mAudioDownloadListener);
	}
	
	private FileDownloadListener mAudioDownloadListener=new FileDownloadListener(){
		@Override
		public void onDownloadFailed(String errorMsg) {
			Utils.toast(R.string.spoken_square_download_audio_failed);	
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
			startPalyAnswer();
		}
	};
	

	private void releaseDownloadRequest(){
		if(mDownloadRequestHandle!=null&&!mDownloadRequestHandle.isFinished()){
			mDownloadRequestHandle.cancel(true);
		}
	}
	
	
	/***
	 * Network
	 */
	
	//Topic - 我来打分
	private final static String NET_PARAM_USER="user";
	private final static String NET_PARAM_TOPIC="topic";
	
	private void queryMarkDataAndPupulateView() {
		
		releaseMarkIndexRequest();
		
		Log.i(TAG, "start queryMarkData");
		mRootView.setVisibility(View.GONE);
		showProgressDialog();
		RequestParams requestParams = new RequestParams();

		requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_TOPIC, mCallback.getTopicIndexBean().getDatas().getObjectId());

		mMarkIndexRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_MARK_INDEX, requestParams, mQueryMarkDataCallback, RequestType.GET);

	}
	
	private HttpRequestCallback mQueryMarkDataCallback=new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			mRootView.setVisibility(View.VISIBLE);
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			mRootView.setVisibility(View.VISIBLE);
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
			mRootView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeDialog();
			mRootView.setVisibility(View.VISIBLE);
			BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
			if(baseBean==null||baseBean.getStatus()==0){
				Utils.toast(R.string.spoken_square_no_answer);
			}else{
				mMarkIndexBean=Utils.parseJson(content, MarkIndexBean.class);
				mHasGrading=false;
				mCanGrading=false;
				populateView();
				if(mMarkIndexBean==null){
					Utils.toast(R.string.json_format_error);
				}
			}
		}
	};
	
	private void releaseMarkIndexRequest(){
		if(mMarkIndexRequestHandle!=null&&!mMarkIndexRequestHandle.isFinished()){
			mMarkIndexRequestHandle.cancel(true);
		}
	}
	
	
	//Topic - 我来打分
	private final static String NET_PARAM_TOPIC_ANSWER_ID="record";//回答objectId
	private final static String NET_PARAM_TOPIC_ANSWER_MARK="mark";//分数
	private final static String NET_PARAM_TOPIC_ANSWER_NOW_MARK="nowMark";//分数
	
	private void markAnswer(int mark) {
		if(mMarkRequestHandle!=null&&!mMarkRequestHandle.isFinished()){
			return;
		}
		Log.i(TAG, "start markAnswer mark : "+mark);
		
		RequestParams requestParams = new RequestParams();

		requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_TOPIC_ANSWER_ID, mMarkIndexBean.getDatas().getAnswer().getObjectId());
		requestParams.put(NET_PARAM_TOPIC_ANSWER_MARK,mark);
		requestParams.put(NET_PARAM_TOPIC_ANSWER_NOW_MARK, mMarkIndexBean.getDatas().getAnswer().getMark());

		mMarkRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.TOPIC_MARK, requestParams, mMarkDataCallback, RequestType.POST);

	}
	
	private HttpRequestCallback mMarkDataCallback=new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
			mRatingBar.setRating(0);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
			mRatingBar.setRating(0);
		}

		@Override
		public void onRequestCanceled() {
			
		}

		@Override
		public void onRequestSucceeded(String content) {
			mMarkresult=Utils.parseJson(content, MarkResultBean.class);
			if(mMarkresult!=null){
				mHasGrading=true;
				refreshView();
			}else{
				Utils.toast(R.string.spoken_square_mark_failed);
				mRatingBar.setRating(0);
			}
		}
	};
	
	private void releaseMarkRequest(){
		if(mMarkRequestHandle!=null&&!mMarkRequestHandle.isFinished()){
			mMarkRequestHandle.cancel(true);
		}
	}
	
	//Topic - 点赞
	private final static String NET_PARAM_PRAISE_ANSWER_ID="record";
	/**
	 * 点赞
	 * @param answerObjectId
	 */
	private void praise(String answerObjectId){
		if(mPraiseRequestHandle!=null&&!mPraiseRequestHandle.isFinished()){
			return;
		}
		Log.i(TAG, "start praise");
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
			Log.i(TAG, "onRequestSucceeded");
			PraiseBean praiseBean=Utils.parseJson(content, PraiseBean.class);
			if(praiseBean!=null){
				mMarkresult.getDatas().setPraise(praiseBean.getDatas().getPraise());
				mMarkresult.getDatas().setIspraise(praiseBean.getDatas().getIspraise());
				pupulateOtherContent();
			}else{
				Utils.toast(R.string.spoken_square_praise_failed);
			}
		}
	};
	
	private void releasePraiseRequest(){
		if(mPraiseRequestHandle!=null&&!mPraiseRequestHandle.isFinished()){
			mPraiseRequestHandle.cancel(true);
		}
	}
	
	private void showProgressDialog() {
		mProgressDlg = DialogUtils.getProgressDialog(getActivity(), getString(R.string.loading));
		mProgressDlg.show();
	}

	private void closeDialog() {
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}

	@Override
	public void stopPlayMedia() {
		stopPalyAnswer();
	}
	
}
