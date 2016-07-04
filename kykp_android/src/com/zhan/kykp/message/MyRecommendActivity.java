package com.zhan.kykp.message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.MyRecommendBean;
import com.zhan.kykp.network.bean.MyRecommendBean.DatasEntity;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.spokenSquare.SpokenSquareUtils;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.RatingBar;

public class MyRecommendActivity extends BaseActivity implements OnCompletionListener{

	//Views
	private PullToRefreshListView mPullRefreshListView ;
	
	//Tools
	private RecommendAdapter mAdapter ;
	private LayoutInflater mInflater;
	private MediaPlayerHelper mMediaPlayerHelper;
	private DisplayImageOptions options;
	private Dialog mProgressDlg;
	
	//Datas
	private List<RecommendInfo> mRecommendResults = new LinkedList<RecommendInfo>();

	//Params
	private MyRecommendBean mRecommendBean ;
	private RecommendInfo itemInfo ;
	private RecommendInfo mPlayingAudioItem ;
	private int mMorePage = 1 ;
	
	//Network
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle; 
	private RequestHandle mDownloadRequestHandle ;//下載
	private RequestHandle mUpdateStatusRequestHandle; 
	
	//Network request params
	private final static String PAGE = "page"; 
	private final static String USERID = "user";  
	
	//释放资源
	@Override
	protected void onDestroy() {
		mMediaPlayerHelper.releaseMedia();
		release(mRequestHandle);
		release(mDownloadRequestHandle);
		release(mUpdateStatusRequestHandle);
		
		super.onDestroy();
	}
	private void release(RequestHandle request) {
		if(request!=null && !request.isFinished()){
			request.cancel(true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend_mine);
		
		mInflater=getLayoutInflater();
		mMediaPlayerHelper = new MediaPlayerHelper(this);
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();
		
		initView();
		
		showProgressDialog();
		queryDate(0);
		
		// 下拉刷新
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), 
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				mMorePage = 1;
				queryDate(0);
			}
		});
		// 上拉加载更多
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				queryDate(mMorePage++);
			}
		});
		updatePrivateMsgStatus();
	}
	
	
	private void initView() {
		mAdapter = new RecommendAdapter();
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.myrecomend_list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setAdapter(mAdapter);
		
	}

	private void queryDate(int mPage) {
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		
		requestParams.put(PAGE, mPage);
		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.TOPIC_PRAISELIST, requestParams, new RequestCallback(mPage), RequestType.GET);
	}
	
	private class RequestCallback implements HttpRequestCallback{

		private int page ;
		public RequestCallback(int mPage) {
			page = mPage ;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			Utils.toast(errorMsg);
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			Utils.toast(R.string.network_error);
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
			Gson gson = new Gson();
			mRecommendBean = gson.fromJson(content, MyRecommendBean.class);
			if(mRecommendBean.getStatus() == 1){
				if(page == 0){
					mRecommendResults.clear();
					mMediaPlayerHelper.stopMedia();
				}
				
				if(mRecommendBean.getDatas().size() != 0){
					for(DatasEntity item:mRecommendBean.getDatas()){
						RecommendInfo data = new RecommendInfo();
						data.audio = item.getRecord().getAudio();
						data.avatar = item.getAvatar();
						data.wcreatedAt = item.getCreatedAt();
						data.createdAt = item.getRecord().getCreatedAt();
						data.mark = item.getRecord().getMark();
						data.markerCount = item.getRecord().getMarkerCount();
						data.nickname = item.getNickname();
						data.objectId = item.getRecord().getObjectId();
						data.praise = item.getRecord().getPraise();
						data.recordTime = item.getRecord().getRecordTime();
						data.topic = item.getTopic();
						data.userId = item.getUserId();
						
						mRecommendResults.add(data);
					}
					mAdapter.notifyDataSetChanged();
				}
				if(page > 2 && mRecommendBean.getDatas().size() == 0){
					Utils.toast(R.string.no_more_data);
				}
				
			}else{
				Utils.toast(R.string.network_query_data_failed);
			}
		}
		
	};

	private class RecommendAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mRecommendResults.size();
		}

		@Override
		public Object getItem(int position) {
			return mRecommendResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder ;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.recommend_list_item, null);
			    viewHolder = new ViewHolder();
			    viewHolder.mContent = (TextView) convertView.findViewById(R.id.recommend_content);
			    viewHolder.nickname = (TextView) convertView.findViewById(R.id.recommend_item_nickname);
			    viewHolder.createdAt = (TextView) convertView.findViewById(R.id.recommend_time);
			    viewHolder.updatedAt = (TextView) convertView.findViewById(R.id.recommend_item_data);
			    viewHolder.recordTime = (TextView) convertView.findViewById(R.id.recordTime);
			    viewHolder.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar_mark);
			    viewHolder.avatar = (ImageView) convertView.findViewById(R.id.recommend_item_head);
			    viewHolder.mAudio = (ImageView) convertView.findViewById(R.id.recommend_play_stop);
			    
                convertView.setTag(viewHolder);			    
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.mContent.setText(mRecommendResults.get(position).topic);
			viewHolder.nickname.setText(mRecommendResults.get(position).nickname);
			
			int time = mRecommendResults.get(position).wcreatedAt + 28800;
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
			String date2 = sdf2.format(new Date(time*1000L));
			viewHolder.createdAt.setText(date2);

			int data = mRecommendResults.get(position).createdAt + 28800 ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date(data*1000L));
			viewHolder.updatedAt.setText(date);
			
			viewHolder.recordTime.setText(mRecommendResults.get(position).recordTime+"'");
			viewHolder.ratingbar.setRating(mRecommendResults.get(position).mark);
			ImageLoader.getInstance().displayImage(mRecommendResults.get(position).avatar,viewHolder.avatar,options);
			
			if(mRecommendResults.get(position).isplayingAudio){
				viewHolder.mAudio.setImageResource(R.drawable.icon_pause);
			}else{
				viewHolder.mAudio.setImageResource(R.drawable.icon_play);
			}
			
			viewHolder.mAudio.setOnClickListener(mOnItemElementClickListener);
			viewHolder.mAudio.setTag(mRecommendResults.get(position));
			
			return convertView;
		}
	}
	
	private OnClickListener mOnItemElementClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			itemInfo = (RecommendInfo) v.getTag();

			switch (v.getId()) {
			case R.id.recommend_play_stop:
				StatisticUtils.onEvent(getTitle().toString(),getString(R.string.statistic_play_stop_audio));

				if (itemInfo == mPlayingAudioItem) {
					if (itemInfo.isplayingAudio) {
						mMediaPlayerHelper.stopMedia();
						itemInfo.isplayingAudio = false;
					} else {
						playDownLoadAudio(itemInfo);
					}
				} else {
					if (mPlayingAudioItem != null) {
						mPlayingAudioItem.isplayingAudio = false;
					}
					playDownLoadAudio(itemInfo);
				}

				mPlayingAudioItem = itemInfo;
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
	private class ViewHolder{
		TextView mContent ;
		TextView nickname ;
		TextView updatedAt ;
		TextView createdAt ;
		TextView recordTime ;
		RatingBar ratingbar;
		ImageView avatar;
		ImageView mAudio;
	}
	
	 private class RecommendInfo{
		String topic;
		int wcreatedAt;
		int createdAt;
		String objectId;
		String nickname;
		String userId;
		String avatar;
		int praise;
		float mark;
		int markerCount;
		int recordTime;
		String audio;
		boolean isplayingAudio = false ;
	 }

	 protected void playDownLoadAudio(RecommendInfo itemInfo) {
			release(mDownloadRequestHandle);
			if(SpokenSquareUtils.hasDownloadAudio(itemInfo.objectId)){
				itemInfo.isplayingAudio = true ;
				mMediaPlayerHelper.play(SpokenSquareUtils.getSpokenSquareAudioPath(itemInfo.objectId));
				mAdapter.notifyDataSetChanged();
			}else{
				String saveFilePath=SpokenSquareUtils.getSpokenSquareAudioPath(itemInfo.objectId);
				String serviceFileUrl= itemInfo.audio; 
				mDownloadRequestHandle = mHttpRequest.downloadFile(getApplicationContext(), serviceFileUrl, saveFilePath, mAudioDownloadListener);
			}
		}
	 
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
	 
	 @Override
	public void onCompletion(MediaPlayer mp) {
		mPlayingAudioItem.isplayingAudio=false;
		mAdapter.notifyDataSetChanged();
	}
	 
	 
	private void updatePrivateMsgStatus() {
		release(mUpdateStatusRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());

		mUpdateStatusRequestHandle = mHttpRequest.startRequest(this, ApiUrls.MESSAGE_UPDATE_PRAISE, requestParams, null, RequestType.POST);
	}
	
	private void showProgressDialog(){
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
//		mProgressDlg.setCancelable(false);
		mProgressDlg.show();
	}

	private void closeDialog() {
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}
	
}
