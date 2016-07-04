package com.zhan.kykp.practice;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.base.App;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.DictionaryDB;
import com.zhan.kykp.db.DictionaryDB.Content;
import com.zhan.kykp.db.DictionaryDB.Tran;
import com.zhan.kykp.db.DictionaryDB.WordTrans;
import com.zhan.kykp.db.PracticeRecordDB;
import com.zhan.kykp.entity.dbobject.PracticeRecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.practice.PracticeHelper.Paragraph;
import com.zhan.kykp.practice.PracticeHelper.PracticeJsonObj;
import com.zhan.kykp.practice.PracticeHelper.Pronunciation;
import com.zhan.kykp.userCenter.MyPracticeActivity;
import com.zhan.kykp.util.AudioUtils;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.PixelUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.ClickableTextView;
import com.zhan.kykp.widget.NoScrollViewPager;
import com.zhan.kykp.widget.SampleProgressView;
import com.zhan.kykp.widget.ViewPagerDotIndicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PracticeDetailsActivity extends BaseActivity implements OnPageChangeListener, OnClickListener, OnCompletionListener{
	public final static String TAG = PracticeDetailsActivity.class.getSimpleName();

	public final static String EXT_KEY_PRACTICE_OBJ = "PRACTICE_OBJECT";

	// Views
	private TextView mTVQuestion;
	private ImageView mImgDownUp;
	private TextView mTVRecording;
	private TextView mTVWholeContent;
	private SampleProgressView mMyRecordingProgress;

	private View mViewRecordingContent;
	private View mViewMyRecordingContent;
	
	private Dialog mDialogBackConfirm;

	// ViewPager 相关
	private NoScrollViewPager mViewPager;
	private PracticeAdpater mAdapter;
	private ViewPagerDotIndicator mViewPagerDotIndicator;
	private int mCurIndex = 0;
	private List<ParagraphsInfo> mParagraphsInfoList = new ArrayList<ParagraphsInfo>();

	private PracticeInfo mPractice;
	private Dialog mProgressDlg;
	private Dialog mMergeProgressDlg;
	private AnimationDrawable mPageSoundAdmin;

	// 跟读解压相关
	private PracticeJsonObj mPracticeJsonObj;

	private MediaPlayerHelper mMediaPlayerHelper;
	private String mMergedAudioPath;
	
	private Timer mPlayParagraphTimer;
	private TimerTask mPlayParagraphTask;
	
	private Timer mPlayMyRecordingTimer;
	private TimerTask mPlayMyRecordingTask;

	private CountDownTimer mRecordCountDownTimer;
	
	private DictionaryDB mDictionaryDB;
	
	private PopupWindow mPopupWindow;
	private DismissTranWindowHandler mDismissTranWindowHandler;
	
	private boolean mStoped=false;

	//Network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mDownloadRequestHandle;
	private RequestHandle mUploadRequestHandle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_practice_details);

		mHttpRequest=new BaseHttpRequest();

		mMediaPlayerHelper = new MediaPlayerHelper(this);
		//mMediaPlayerHelper.initMediaPlayer(this);
		
		mDictionaryDB=new DictionaryDB(this);

		initActionBar();

		getPracticeFromIntent();

		initView();
		
		intiPopMenu();

		checkAndDownloadData();
		
		initBackConfirmDlg();
	}

	private void getPracticeFromIntent() {
		mPractice = getIntent().getParcelableExtra(EXT_KEY_PRACTICE_OBJ);
	}
	
	

	@Override
	public void onBackPressed() {
		if(MediaRecordFunc.getInstance().isRecording()){
			mDialogBackConfirm.show();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	}

	
	@Override
	protected boolean displayActionbarUnderline() {
		return false;
	}

	private void initView() {
		findViewById(R.id.btn_back_recording).setOnClickListener(this);
		findViewById(R.id.btn_my_practice).setOnClickListener(this);
		
		mViewPager = (NoScrollViewPager) findViewById(R.id.pager);
		mViewPager.setPageTransformer(true, new PracticeTransformer());
		mAdapter = new PracticeAdpater();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mAdapter.notifyDataSetChanged();

		mViewPagerDotIndicator = (ViewPagerDotIndicator) findViewById(R.id.pager_indicator);

		mTVQuestion = (TextView) findViewById(R.id.question);
		mImgDownUp = (ImageView) findViewById(R.id.down_up);
		mImgDownUp.setOnClickListener(this);

		mTVRecording = (TextView) findViewById(R.id.recording);
		mTVRecording.setOnClickListener(this);
		
		//我的录音
		mTVWholeContent=(TextView) findViewById(R.id.whole_content);
		
		mMyRecordingProgress=(SampleProgressView) findViewById(R.id.progress_view);
				
		mViewRecordingContent=findViewById(R.id.recording_content);
		mViewMyRecordingContent=findViewById(R.id.my_recording_content);
		
		findViewById(R.id.play_my_recording).setOnClickListener(this);
		
		mStoped=false;

	}

	private void initActionBar() {
		LinearLayout rightContent = getActionBarRightContent();
		final ImageView rightMemu = new ImageView(this);
		rightMemu.setImageResource(R.drawable.action_bar_icon_key);
		rightMemu.setScaleType(ScaleType.CENTER);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightContent.addView(rightMemu, lp);
		rightMemu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPracticeJsonObj != null) {
					Intent resolvingIdea = new Intent(PracticeDetailsActivity.this, PracticeResolvingIdea.class);
					resolvingIdea.putExtra(PracticeResolvingIdea.EXT_KEY_DIFFCULTY, mPracticeJsonObj.Diffculty);
					resolvingIdea.putExtra(PracticeResolvingIdea.EXT_KEY_HINT, mPracticeJsonObj.Hint);
					startActivity(resolvingIdea);

					StatisticUtils.onEvent(getString(R.string.statistic_practice_details), getString(R.string.practice_resolving_idea));
				}
			}
		});
	}

	private void initBackConfirmDlg(){
		LayoutInflater inflater=getLayoutInflater();
		mDialogBackConfirm = new Dialog(this, R.style.Dialog);
		View dlgContent = inflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.practice_exting);
		
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setText(R.string.practice_exit_confirm_msg);
		
		TextView confirm = (TextView) dlgContent.findViewById(R.id.confirm);
		confirm.setText(R.string.practice_exit_confirm);
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
				//退出
				PracticeDetailsActivity.super.onBackPressed();
			}
		});
		
		TextView cancel = (TextView) dlgContent.findViewById(R.id.cancel);
		cancel.setText(R.string.practice_exit_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
			}
		});
		
		mDialogBackConfirm.setContentView(dlgContent);
	}

	private class ParagraphsInfo {
		Paragraph paragraph;
		boolean showCommand = false;
	}

	private class ViewHolder {
		ClickableTextView content;
		TextView comment;
		SampleProgressView progressView;
		TextView practiceAnswer;
		ImageView soundAnim;
	}

	private class PracticeAdpater extends PagerAdapter {
		@Override
		public int getCount() {
			return mParagraphsInfoList.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View view = getLayoutInflater().inflate(R.layout.practice_page_item, null, false);
			((ViewPager) container).addView(view);

			ViewHolder viewHolder = new ViewHolder();

			ParagraphsInfo info = mParagraphsInfoList.get(position);

			//动画View
			viewHolder.soundAnim=(ImageView) view.findViewById(R.id.sound_anim);
			
			// 文本内容
			viewHolder.content = (ClickableTextView) view.findViewById(R.id.content);
			intContent(viewHolder, info.paragraph);
			
			// 评论
			viewHolder.comment = (TextView) view.findViewById(R.id.comment);
			viewHolder.comment.setText(info.paragraph.Comment);

			if (!info.showCommand) {
				viewHolder.content.setVisibility(View.VISIBLE);
				viewHolder.comment.setVisibility(View.GONE);
			} else {
				viewHolder.content.setVisibility(View.GONE);
				viewHolder.comment.setVisibility(View.VISIBLE);
			}

			// 进度条
			SampleProgressView progressView = (SampleProgressView) view.findViewById(R.id.progress_view);
			progressView.setProgress(0);
			viewHolder.progressView=progressView;
			
			// 播放按钮
			ImageView imgPracticePlay = (ImageView) view.findViewById(R.id.practice_play);
			imgPracticePlay.setOnClickListener(PracticeDetailsActivity.this);
			imgPracticePlay.setTag(viewHolder);

			// 解题思路
			TextView practiceAnswer = (TextView) view.findViewById(R.id.practice_answer);
			practiceAnswer.setOnClickListener(PracticeDetailsActivity.this);
			practiceAnswer.setTag(viewHolder);

			return view;
		}

		private void intContent(ViewHolder viewHolder, Paragraph Paragraph) {
			SpannableString spString = new SpannableString(Paragraph.Content);

			for (Pronunciation item : Paragraph.Pronunciations) {
				Pattern p = Pattern.compile(item.Content);
				Matcher m = p.matcher(spString);

				HightLightClickableSpan mHightLightClickableSpan = new HightLightClickableSpan(item.File,viewHolder.soundAnim);
				while (m.find()) {
					int start = m.start();
					int end = m.end();
					spString.setSpan(mHightLightClickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			
			//设置文字可选
			viewHolder.content.setTextIsSelectable(true);
			viewHolder.content.setText(spString);
			
			viewHolder.content.setCustomSelectionActionModeCallback(new TransCallback(viewHolder.content.getText()));
		}

		private class HightLightClickableSpan extends ClickableSpan {
			String mAudioFile;
			ImageView mSoundAnimView;

			public HightLightClickableSpan(String audio,ImageView soundAnimView) {
				mAudioFile = audio;
				mSoundAnimView=soundAnimView;
			}

			@Override
			public void onClick(View widget) {
				//正好在录音，你还是闭嘴的好
				if(MediaRecordFunc.getInstance().isRecording()){
					return;
				}
				
				//要是别人在说话，你就不要插嘴了
				if(mMediaPlayerHelper.isPlaying()){
					return;
				}
				
				mPageSoundAdmin = (AnimationDrawable)mSoundAnimView.getBackground();
				mPageSoundAdmin.stop();
				
				// 获取音频路径
				String audoiFilePath = PracticeHelper.getInstance().getAudioPath(mPractice.getIndex(), mAudioFile);
				// 开始播放
				mMediaPlayerHelper.play(audoiFilePath);
				//播放动画
				mPageSoundAdmin.start();
				// 禁止滑动
				mViewPager.setNoScroll(true);


			}

			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(getResources().getColor(R.color.cyan));
				ds.setUnderlineText(false);
				ds.clearShadowLayer();
			}
		}
		
		private class TransCallback implements ActionMode.Callback{

			private CharSequence mText;
			
			public TransCallback(CharSequence text){
				mText=text;
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				menu.clear();
				menu.add(R.string.practice_trans);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				int startIndex=Selection.getSelectionStart(mText);
				int endIndex=Selection.getSelectionEnd(mText);
				
				if(startIndex!=-1&&endIndex!=-1&&(endIndex>startIndex)){
					//CharSequence word=mText.subSequence(startIndex, endIndex);
					String word=TextUtils.substring(mText, startIndex, endIndex);
					
					WordTrans wordTrans=mDictionaryDB.queryData(word);
					showTranPopWindow(wordTrans);
					
					mode.finish();
				}
				
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mCurIndex = arg0;
		mViewPagerDotIndicator.setSelectIndex(mCurIndex);
	}

	private void showProgressDialog() {
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
		mProgressDlg.show();
	}

	private void closeDialog() {
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}

	private void release() {
		closeDialog();
		
		mStoped=true;

		BaseHttpRequest.releaseRequest(mDownloadRequestHandle);
		
		mMediaPlayerHelper.releaseMedia();
		
		releaseParagraphTimer();
		releaseMyRecordingTimer();
		
		stopRecording();
		clearTempRecordings();
	}

	private void releaseParagraphTimer() {
		if (mPlayParagraphTimer != null) {
			mPlayParagraphTimer.cancel();
			mPlayParagraphTimer = null;
		}
		
		if(mPlayParagraphTask != null){
			mPlayParagraphTask.cancel();
			mPlayParagraphTask = null;
		}
	}
	
	private void releaseMyRecordingTimer() {
		if (mPlayMyRecordingTimer != null) {
			mPlayMyRecordingTimer.cancel();
			mPlayMyRecordingTimer = null;
		}
		
		if(mPlayMyRecordingTask!=null){
			mPlayMyRecordingTask.cancel();
			mPlayMyRecordingTask=null;
		}
	}

	private void checkAndDownloadData() {
		final String indexStr = String.valueOf(mPractice.getIndex());
		if (PracticeHelper.getInstance().isDownloaded(indexStr)) {
			populateData();
		} else {
			if (!Connectivity.isConnected(this)) {
				Utils.toast(R.string.network_disconnect);
				return;
			}
			showProgressDialog();

			BaseHttpRequest.releaseRequest(mDownloadRequestHandle);

			mDownloadRequestHandle=mHttpRequest.downloadFile(this,mPractice.getZipFile(),PracticeHelper.getInstance().getZipFileSavePath(indexStr),new FileDownloadListener(){
				@Override
				public void onDownloadFailed(String errorMsg) {
					Utils.toast(errorMsg);
					closeDialog();
				}

				@Override
				public void onNoNetwork() {
					Utils.toast(R.string.network_error);
					closeDialog();
				}

				@Override
				public void onCanceled() {
					closeDialog();
				}

				@Override
				public void onDownloadSuccess(File downFile) {
					closeDialog();
					PracticeHelper.getInstance().unZipFile(downFile,indexStr);
					if (PracticeHelper.getInstance().isDownloaded(indexStr)) {
						populateData();
					}
				}
			});
		}
	}

	private void populateData() {
		if (mPractice == null) {
			return;
		}
		mPracticeJsonObj = PracticeHelper.getInstance().parsePracticeFromJsonFile(mPractice.getIndex());
		mTVQuestion.setText(mPracticeJsonObj.Title);
		
		mTVQuestion.post(new Runnable() {  
            @Override  
            public void run() {  
            	Layout layout=mTVQuestion.getLayout();
        		int lines=layout.getLineCount();
        		//判断是否段落过长有省略号，有的话就显示展开图标
        		 if (lines > 0&&layout.getEllipsisCount(lines-1) > 0){
        			 mImgDownUp.setVisibility(View.VISIBLE);
        		 }
            }  
		});

		// 对数据做一次封装，一边绑定Adapter显示
		mParagraphsInfoList.clear();
		for (Paragraph item : mPracticeJsonObj.Paragraphs) {
			ParagraphsInfo info = new ParagraphsInfo();
			info.paragraph = item;
			info.showCommand = false;
			mParagraphsInfoList.add(info);
		}

		mViewPagerDotIndicator.setPageCount(mParagraphsInfoList.size());
		mAdapter.notifyDataSetChanged();
		
		//我的录音
		mTVWholeContent.setText(mPracticeJsonObj.Content);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.down_up:
			if(mTVQuestion.getEllipsize()!=null){
				mTVQuestion.setEllipsize(null); // 展开
				mTVQuestion.setMaxLines(Integer.MAX_VALUE);
				mImgDownUp.setImageResource(R.drawable.up);
			}else{
				mTVQuestion.setEllipsize(TextUtils.TruncateAt.END); // 收缩
				mTVQuestion.setMaxLines(3);
				mImgDownUp.setImageResource(R.drawable.down);
			}
			break;
			
		case R.id.practice_play:

			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), "播放原文段落");

			//正好在录音，你还是闭嘴的好
			if(MediaRecordFunc.getInstance().isRecording()){
				return;
			}
			
			// 进度条
			final ViewHolder holder = (ViewHolder) v.getTag();
			
			mPageSoundAdmin = (AnimationDrawable)holder.soundAnim.getBackground();
			mPageSoundAdmin.stop();
			
			//正在播放就停止
			if(mMediaPlayerHelper.isPlaying()){
				mMediaPlayerHelper.stopMedia();
				onMediaFinishOrStoped();
				holder.progressView.setProgress(0);
				return;
			}
			
			// 获取音频路径
			String audioFile = mParagraphsInfoList.get(mCurIndex).paragraph.File;
			String audoiFilePath = PracticeHelper.getInstance().getAudioPath(mPractice.getIndex(), audioFile);
			// 开始播放
			mMediaPlayerHelper.play(audoiFilePath);
			mPageSoundAdmin.start();

			releaseParagraphTimer();
			mPlayParagraphTimer = new Timer();
			mPlayParagraphTask = new TimerTask() {
				@Override
				public void run() {
					if(!mStoped&&mMediaPlayerHelper.isPlaying()){
						int progress = (int) (100f * mMediaPlayerHelper.getCurrentPosition() / mMediaPlayerHelper.getDuration());
						holder.progressView.setProgress(progress);
					}
				}
			};
			mPlayParagraphTimer.schedule(mPlayParagraphTask, 0, 10);

			// 禁止滑动
			mViewPager.setNoScroll(true);

			break;
		case R.id.practice_answer:
			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), getString(R.string.practice_resolving_idea));

			mParagraphsInfoList.get(mCurIndex).showCommand = !mParagraphsInfoList.get(mCurIndex).showCommand;

			ViewHolder viewHolder = (ViewHolder) v.getTag();
			if (!mParagraphsInfoList.get(mCurIndex).showCommand) {
				viewHolder.content.setVisibility(View.VISIBLE);
				viewHolder.comment.setVisibility(View.GONE);
			} else {
				viewHolder.content.setVisibility(View.GONE);
				viewHolder.comment.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.recording:
			//要是别人在说话，你就不要插嘴了
			if(mMediaPlayerHelper.isPlaying()){
				return;
			}
			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), "开始录音");
			startRecording();
			break;
			
		case R.id.btn_back_recording:
			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), getString(R.string.practice_back_recording));

			releaseMyRecordingTimer();
			mMediaPlayerHelper.stopMedia();
			
			mViewRecordingContent.setVisibility(View.VISIBLE);
			mViewMyRecordingContent.setVisibility(View.GONE);
			
			mViewPager.setCurrentItem(0, true);
			break;
		case R.id.btn_my_practice:
			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), getString(R.string.my_practice));

			releaseMyRecordingTimer();
			
			mMediaPlayerHelper.stopMedia();
			
			Intent mypracticeIntent = new Intent(this, MyPracticeActivity.class);
			startActivity(mypracticeIntent);
			
			break;
		case R.id.play_my_recording:
			StatisticUtils.onEvent(getString(R.string.statistic_practice_details), "播放我的跟读录音");

			if(mMergedAudioPath==null){
				return;
			}
			File file=new File(mMergedAudioPath);
			if(!file.exists()){
				return;
			}
			// 开始播放
			mMediaPlayerHelper.play(mMergedAudioPath);
			// 进度
			mMyRecordingProgress.setProgress(0);
			releaseMyRecordingTimer();
			mPlayMyRecordingTimer = new Timer();
			mPlayMyRecordingTask = new TimerTask() {
				@Override
				public void run() {
					if (!mStoped&&!mMediaPlayerHelper.isReleased()&&mMediaPlayerHelper.isPlaying()) {
						int progress = (int) (100f * mMediaPlayerHelper.getCurrentPosition() / mMediaPlayerHelper.getDuration());
						mMyRecordingProgress.setProgress(progress);
					}
				}
			};
			mPlayMyRecordingTimer.schedule(mPlayMyRecordingTask, 0, 10);
			break;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		onMediaFinishOrStoped();
	}
	
	
	private void onMediaFinishOrStoped(){
		releaseParagraphTimer();
		releaseMyRecordingTimer();
		mViewPager.setNoScroll(false);
		if(mPageSoundAdmin!=null){
			mPageSoundAdmin.stop();
		}
	}

	private void startRecording() {
		if (mPractice == null || mParagraphsInfoList.size() == 0) {
			return;
		}

		// 检查前面的段落是否录音
		if (mCurIndex > 0) {
			File file = new File(getrecordFilePath(mPractice.getIndex(), mCurIndex - 1));
			if (!file.exists()) {
				showUnfinishPreParagraphToast();
				return;
			}
		}

		int time = mParagraphsInfoList.get(mCurIndex).paragraph.Time;
		mTVRecording.setText(String.valueOf(time));
		mTVRecording.setBackgroundResource(R.drawable.practice_record_ing);
		// 禁止滑动
		mViewPager.setNoScroll(true);
		// 禁止点击
		mTVRecording.setClickable(false);

		mRecordCountDownTimer = new CountDownTimer(time * 1000, 500) {
			@Override
			public void onTick(long millisUntilFinished) {
				mTVRecording.setText(String.valueOf(millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				
				stopRecording();

				// View回复初始状态
				mTVRecording.setBackgroundResource(R.drawable.practice_record);
				mTVRecording.setText("");
				// 允许点击
				mTVRecording.setClickable(true);
				// 允许滑动
				mViewPager.setNoScroll(false);

				// 下一页
				if (mCurIndex < mParagraphsInfoList.size() - 1) {
					mViewPager.setCurrentItem(mCurIndex + 1, true);
				}else{
					//合并录音
					mergeRecordings();
				}
			}
		};

		String recordPath = getrecordFilePath(mPractice.getIndex(), mCurIndex);
		MediaRecordFunc.getInstance().startRecordAndFile(recordPath);
		mRecordCountDownTimer.start();
	}

	private void stopRecording() {
		if (mRecordCountDownTimer != null) {
			mRecordCountDownTimer.cancel();
			mRecordCountDownTimer = null;
		}
		MediaRecordFunc.getInstance().stopRecordAndFile();
	}

	private String getrecordFilePath(int index, int paragraphIndex) {
		String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
		String filePath = recordDir + "/" + String.format("temp_practice_%d_%d.amr", index, paragraphIndex);
		return filePath;
	}

	private void clearTempRecordings() {
		if (mPractice != null && mParagraphsInfoList.size() > 0) {
			for (int i = 0; i < mParagraphsInfoList.size(); i++) {
				File tempFile = new File(getrecordFilePath(mPractice.getIndex(), i));
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}
		}
	}

	private void showUnfinishPreParagraphToast() {
		Toast toast = Toast.makeText(App.ctx, R.string.practice_finish_pre_paragraph, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(getApplicationContext());
		imageCodeProject.setImageResource(R.drawable.practice_answer);
		toastView.addView(imageCodeProject, 0);
		toast.show();
	}
	
	private void mergeRecordings(){
		String[] pathArray=new String[mParagraphsInfoList.size()];
		for (int i = 0; i < mParagraphsInfoList.size(); i++) {
			String path=getrecordFilePath(mPractice.getIndex(), i);
			pathArray[i]=path;
		}
		String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
		mMergedAudioPath = recordDir + "/" +System.currentTimeMillis() + ".amr";
		//保存录音记录
		if(AudioUtils.uniteAMRFile(pathArray, mMergedAudioPath)){
			PracticeRecord record=new PracticeRecord();
			record.PracticeIndex=mPractice.getIndex();
			record.UserObject=UserInfo.getCurrentUser().getObjectId();
			record.RecordFilePath=mMergedAudioPath;
			record.StartTime=System.currentTimeMillis();
			record.RecordingSeconds=AudioUtils.getAudioSeconds(mMergedAudioPath);
			
			PracticeRecordDB db=new PracticeRecordDB(this);
			db.save(record);
			
			//备份到服务器
			uploadRecording();
		}else{
			Utils.toast(R.string.practice_merge_recording_failed);
		}
		
		//清除临时文件
		clearTempRecordings();
	} 
	
	
	private void showMergeProgressDialog(){
		mMergeProgressDlg = DialogUtils.getCircleProgressDialog(this, getString(R.string.practice_merge_recording));
		mMergeProgressDlg.setCancelable(false);
		mMergeProgressDlg.show();
	}
	
	private void closeMergeDialog(){
		if (mMergeProgressDlg != null) {
			mMergeProgressDlg.dismiss();
			mMergeProgressDlg = null;
			
			mViewRecordingContent.setVisibility(View.GONE);
			mViewMyRecordingContent.setVisibility(View.VISIBLE);
		}
	}
	
	private void uploadRecording(){
		if(mUploadRequestHandle!=null&&!mUploadRequestHandle.isFinished()){
			return;
		}
		try {
			RequestParams requestParams=new RequestParams();
			requestParams.put("practice", mPractice.getObjectId());
			requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
			requestParams.put("audio", new File(mMergedAudioPath));

			showMergeProgressDialog();
			mUploadRequestHandle=mHttpRequest.startRequest(this, ApiUrls.PRACTICE_SAVE, requestParams, mUploadRecordingCallback, BaseHttpRequest.RequestType.POST);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "uploadMyRecording FileNotFoundException : " + mMergedAudioPath);
		}
	}

	private HttpRequestCallback mUploadRecordingCallback=new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			closeMergeDialog();
		}
		@Override
		public void onRequestFailedNoNetwork() {
			closeMergeDialog();
		}
		@Override
		public void onRequestCanceled() {
			closeMergeDialog();
		}
		@Override
		public void onRequestSucceeded(String content) {
			closeMergeDialog();
			BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
			if(baseBean!=null&&baseBean.getStatus()==1){
				Utils.toast(baseBean.getMessage());
			}
		}
	};




	/***
	 * 翻译显示的弹出框
	 */
	private void intiPopMenu() {
		/* 设置显示menu布局 view子VIEW */
		// View sub_view = inflater.inflate(R.layout.pop_memu_height, null);
		/* 第一个参数弹出显示view 后两个是窗口大小 */
		mPopupWindow = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		/* 设置背景显示 */
		//int bgColor=getResources().getColor(R.color.main_background);
		int bgColor=0xff9e9c9c;
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(bgColor));
		/* 设置触摸外面时消失 */
		mPopupWindow.setOutsideTouchable(true);
		/* 设置系统动画 */
		mPopupWindow.setAnimationStyle(R.style.pop_menu_bottom_animation);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
		mPopupWindow.setFocusable(true);
		
		mDismissTranWindowHandler=new DismissTranWindowHandler();

	}
	
	// 显示菜单
	private void showPopMenu() {
		if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			/* 最重要的一步：弹出显示 在指定的位置(parent) 最后两个参数 是相对于 x / y 轴的坐标 */
			mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
			
			mDismissTranWindowHandler.removeMessages(DismissTranWindowHandler.MSG_DISMISS_WINDOW);
			mDismissTranWindowHandler.sendEmptyMessageDelayed(DismissTranWindowHandler.MSG_DISMISS_WINDOW, 3*1000);
			mPopupWindow.setOnDismissListener(new OnDismissListener(){
				@Override
				public void onDismiss() {
					mDismissTranWindowHandler.removeMessages(DismissTranWindowHandler.MSG_DISMISS_WINDOW);
				}
			});
		}
	}

	// 关闭PopUpWindow
	private boolean closePopWin() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}
	
	private class DismissTranWindowHandler extends Handler{
		public final static int MSG_DISMISS_WINDOW=10001;
		@Override
		public void handleMessage(Message msg) {
			closePopWin();
		}
	}
	
	private void showTranPopWindow(final WordTrans wordTrans){
		//View menuContent = getLayoutInflater().inflate(R.layout.pop_memu_choose_exam_time, null);
		TextView tv=new TextView(this);
		tv.setMaxLines(2);
		//tv.setEllipsize(TextUtils.TruncateAt.END);
		tv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		tv.setPadding(10, 32, 10, 32);
		tv.setTextColor(Color.WHITE);
		
		if(wordTrans==null){
			tv.setText(R.string.practice_un_found_mean);
			
		}else{
			
			if(wordTrans.contents.size()>0){
				String pron=wordTrans.contents.get(0).pron;
				if(!TextUtils.isEmpty(pron)){
					tv.setText(pron+" ");
				}
				
				for(Content content:wordTrans.contents){
					tv.append(content.type);
					for(Tran tran:content.trans){
						tv.append(";"+tran.mean);
					}
					tv.append(";");
				}
			}
			tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent it=new Intent(PracticeDetailsActivity.this,TansDetailsActivity.class);
					it.putExtra(TansDetailsActivity.EXT_KEY_WORD, wordTrans.word);
					startActivity(it);
					closePopWin();

					StatisticUtils.onEvent(getString(R.string.statistic_practice_details), getString(R.string.practice_trans));
				}
			});
		}
		
		mPopupWindow.setContentView(tv);
		showPopMenu();
	}
	
	
	public class PracticeTransformer implements PageTransformer {
		
		/**
		 * position参数指明给定页面相对于屏幕中心的位置。它是一个动态属性，会随着页面的滚动而改变。当一个页面填充整个屏幕是，它的值是0，
		 * 当一个页面刚刚离开屏幕的右边时，它的值是1。当两个也页面分别滚动到一半时，其中一个页面的位置是-0.5，另一个页面的位置是0.5。基于屏幕上页面的位置
		 * ，通过使用诸如setAlpha()、setTranslationX()、或setScaleY()方法来设置页面的属性，来创建自定义的滑动动画。
		 */
		@Override
		public void transformPage(View view, float position) {
			int height=PixelUtils.dp2px(24);
			
			//左边
			if (position <= 0) {
				//从右向左滑动为当前View
				
				//设置旋转中心点；
				/*ViewHelper.setPivotX(view, view.getMeasuredWidth());
				ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);*/
				
				/*view.setPivotX(view.getMeasuredWidth());
				view.setPivotY(view.getMeasuredHeight() * 0.5f);*/
				
				//只在Y轴做旋转操作
				//ViewHelper.setRotationY(view, 90f * position);
				
				/*view.setRotationY(90f * position);*/
				
				float tranY=height*(-position);
				
				view.setTranslationY(tranY);
				
			} else if (position <= 1) {//右边
				//从左向右滑动为当前View
				/*ViewHelper.setPivotX(view, 0);
				ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
				ViewHelper.setRotationY(view, 90f * position);*/
				
				/*view.setPivotX( 0);
				view.setPivotY( view.getMeasuredHeight() * 0.5f);
				view.setRotationY(90f * position);*/
				
				float tranY=height*position;
				
				view.setTranslationY(tranY);
			}
		}
	}

}
