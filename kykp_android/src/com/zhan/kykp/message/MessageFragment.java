package com.zhan.kykp.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import com.zhan.kykp.Receiver.PushMsgBean;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.AllMessageListBean;
import com.zhan.kykp.network.bean.AllMessageListBean.DatasEntity.PrivateEntity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.RedDotView;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/***
 * 最新消息为当天的显示时间，其他情况显示日期
 * 
 * @author Administrator
 * 
 */
public class MessageFragment extends Fragment implements OnItemClickListener{
	
	private final static String TAG=MessageFragment.class.getSimpleName();

	//Views
	private PullToRefreshListView mPullRefreshListView;
	private Dialog mProgressDlg;

	//Tools
	private LayoutInflater mInflater;
	private MessageAdapter mMessageAdapter;
	
	//Params
	private int mCurrentPage=0;
	
	//Datas
	private List<ItemInfo> mItemInfoList = new ArrayList<ItemInfo>();
	
	//Network
	private BaseHttpRequest mHttpRequest;
	private DisplayImageOptions options;
	private RequestHandle mQueryRequestHandle;
	
	//Type
	private class ItemInfo {
		String title;
		MessageStatus ststus;
		String summary;
		MessageType type;
		long time;
		
		//以下仅私信使用
		String avatarUrl;
		String userObjectId;
	}
	
	// 系统，私信,赞
	private enum MessageType {
		SYSTEM, PRIVATE, PRAISE
	};
	// 系统，私信,赞
	private enum MessageStatus {
		HAS_READ, UN_READ, NO_MSG
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mInflater = inflater;
		LinearLayout contentView = (LinearLayout)inflater.inflate(R.layout.frag_layout_mesage_list, null);
		
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(0, Utils.getStatusBarHeight(this.getActivity()), 0, 0);
			contentView.setLayoutParams(lp);
		}*/
		
		mHttpRequest=new BaseHttpRequest();
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();

		mItemInfoList.clear();
		
		mMessageAdapter = new MessageAdapter();
		mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setAdapter(mMessageAdapter);
		mPullRefreshListView.setOnItemClickListener(this);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), 
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				queryMsgList(0);
			}
		});
		
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){
			@Override
			public void onLastItemVisible() {
				queryMsgList(mCurrentPage+1);
			}
		});
		
		/*//如果已经有消息，就不请求数据了
		if(mItemInfoList.size()==0){
			queryMsgList(0);
		}else{
			mMessageAdapter.notifyDataSetChanged();
		}*/

		showProgressDialog();
		queryMsgList(0);
		
		return contentView;
	}

	@Override
	public void onDestroyView() {
		releaseRequest(mQueryRequestHandle);
		mPullRefreshListView.onRefreshComplete();
		super.onDestroyView();
	}

	private class ViewHolder {
		ImageView imgIcon;
		ImageView avatar;
		TextView title;
		TextView summary;
		RedDotView redDotView;
		TextView time;
	}

	/***
	 * 消息类型分类：系统消息(公告、通知等)、私信(私信聊天记录)、赞(我收到的赞) (系统消息置顶，赞排第二，其余私信按照时间排序
	 * 
	 * @author Administrator
	 * 
	 */
	private class MessageAdapter extends BaseAdapter {
		private final SimpleDateFormat mFullDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private final SimpleDateFormat mShotDateFormat = new SimpleDateFormat("MM-dd");
		private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
		
		@Override
		public int getCount() {
			return mItemInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.message_list_item, null);
				holder=new ViewHolder();
				holder.avatar=(ImageView)convertView.findViewById(R.id.avatar);
				holder.imgIcon=(ImageView)convertView.findViewById(R.id.icon);
				holder.title=(TextView)convertView.findViewById(R.id.title);
				holder.summary=(TextView)convertView.findViewById(R.id.summary);
				holder.redDotView=(RedDotView)convertView.findViewById(R.id.icon_read);
				holder.time=(TextView)convertView.findViewById(R.id.time);
				
				convertView.setTag(holder);
			} else {
				holder=(ViewHolder)convertView.getTag();
			}
			
			ItemInfo itemInfo=mItemInfoList.get(position);
			switch (itemInfo.type) {
			case SYSTEM:
				holder.avatar.setVisibility(View.GONE);
				holder.imgIcon.setVisibility(View.VISIBLE);
				holder.imgIcon.setImageResource(R.drawable.news);
				break;
			case PRIVATE:
				holder.avatar.setVisibility(View.VISIBLE);
				holder.imgIcon.setVisibility(View.GONE);
				ImageLoader.getInstance().displayImage(itemInfo.avatarUrl,holder.avatar,options);
				break;
			case PRAISE:
				holder.avatar.setVisibility(View.GONE);
				holder.imgIcon.setVisibility(View.VISIBLE);
				holder.imgIcon.setImageResource(R.drawable.icon_msg_praise);
				break;
			}
			
			holder.title.setText(itemInfo.title);
			holder.summary.setText(itemInfo.summary);
			if(itemInfo.ststus==MessageStatus.UN_READ){
				holder.redDotView.setVisibility(View.VISIBLE);
			}else{
				holder.redDotView.setVisibility(View.INVISIBLE);
			}
			
			holder.time.setText(getTimeStr(itemInfo.time));

			return convertView;
		}
		
		
		private String getTimeStr(long time){
			//是否是当天
			String curDateStr=getFormatTimeStr(mFullDateFormat,System.currentTimeMillis());
			String timeStr=getFormatTimeStr(mFullDateFormat,time);
			
			//当天
			if(curDateStr.equals(timeStr)){
				return getFormatTimeStr(mTimeFormat,time);
			}else{
				return getFormatTimeStr(mShotDateFormat,time);
			}
		}
		
		private String getFormatTimeStr(SimpleDateFormat format,long time){
			String timeStr = format.format(time);
			return timeStr;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ItemInfo itemInfo=mItemInfoList.get((int) id);
		itemInfo.ststus=MessageStatus.HAS_READ;
		mMessageAdapter.notifyDataSetChanged();
		
		switch (itemInfo.type) {
		case SYSTEM:
			StatisticUtils.onEvent(R.string.my_msg, R.string.sys_msg);
			Intent sysMsgDetailsIntent=new Intent(getActivity(),SystemMsgDetailsActivity.class);
			this.startActivity(sysMsgDetailsIntent);
			break;
		case PRIVATE:
			StatisticUtils.onEvent(R.string.my_msg, R.string.statistic_private_letter);
			Intent privateLetterIntent=new Intent(getActivity(),PrivateLetterActivity.class);
			privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_ID, itemInfo.userObjectId);
			privateLetterIntent.putExtra(PrivateLetterActivity.EXT_KET_TO_USER_NAME, itemInfo.title);
			startActivity(privateLetterIntent);
			break;
		case PRAISE:
			StatisticUtils.onEvent(R.string.my_msg, R.string.sys_praise);
			startActivity(new Intent(getActivity(), MyRecommendActivity.class));
			break;
		}
	}
	
	/***
	 * Network
	 */
	private final static String NET_PARAM_USER="user";//用户objectId
	private final static String NET_PARAM_PAGE="page";//页数
	
	private void queryMsgList(int page){
		if(mQueryRequestHandle!=null&&!mQueryRequestHandle.isFinished()){
			return;
		}

		RequestParams requestParams=new RequestParams();
		requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_PAGE, page);
		
		mQueryRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.MESSAGE_ALL_LIST, requestParams, new QueryMsgCallback(page), RequestType.GET);
	}
	
	private class QueryMsgCallback implements HttpRequestCallback{
		private int mPage;
		
		public QueryMsgCallback(int page){
			mPage=page;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
		}

		@Override
		public void onRequestCanceled() {
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
		}

		@Override
		public void onRequestSucceeded(String content) {
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
			
			AllMessageListBean allMessageBean=Utils.parseJson(content, AllMessageListBean.class);
			//0已读;1未读;2无消息
			final int STATUS_HAS_READ=0;
			final int STATUS_UN_READ=1;
			final int STATUS_NO_MSG=2;
			
			if(allMessageBean!=null){
				
				if (mPage == 0) {
					mItemInfoList.clear();
					// 系统消息
					ItemInfo itemInfo = new ItemInfo();
					itemInfo.title = getString(R.string.sys_msg);
					itemInfo.summary = allMessageBean.getDatas().getSystem().getContent();
					switch (allMessageBean.getDatas().getSystem().getStatus()) {
					case STATUS_HAS_READ:
						itemInfo.ststus = MessageStatus.HAS_READ;
						break;
					case STATUS_UN_READ:
						itemInfo.ststus = MessageStatus.UN_READ;
						break;
					case STATUS_NO_MSG:
						itemInfo.ststus = MessageStatus.NO_MSG;
						break;
					}
					itemInfo.type = MessageType.SYSTEM;
					itemInfo.time = Utils.getCurrentTimeZoneUnixTime(allMessageBean.getDatas().getSystem().getUpdatedAt() );
					mItemInfoList.add(itemInfo);

					// 点赞消息
					itemInfo = new ItemInfo();
					switch (allMessageBean.getDatas().getPraise().getStatus()) {
					case STATUS_HAS_READ:
						itemInfo.ststus = MessageStatus.HAS_READ;
						break;
					case STATUS_UN_READ:
						itemInfo.ststus = MessageStatus.UN_READ;
						break;
					case STATUS_NO_MSG:
						itemInfo.ststus = MessageStatus.NO_MSG;
						break;
					}
					
					if(itemInfo.ststus!=MessageStatus.NO_MSG){
						itemInfo.title = getString(R.string.sys_praise);
						itemInfo.summary = allMessageBean.getDatas().getPraise().getContent();
						itemInfo.type = MessageType.PRAISE;
						itemInfo.time = Utils.getCurrentTimeZoneUnixTime(allMessageBean.getDatas().getPraise().getUpdatedAt());
						mItemInfoList.add(itemInfo);
					}

					//小站客服
					AllMessageListBean.DatasEntity.FeedbackEntity  feedback=allMessageBean.getDatas().getFeedback();
					if(feedback!=null){
						itemInfo = new ItemInfo();
						itemInfo.title = feedback.getNickname();
						itemInfo.summary = feedback.getContent();
						switch(Integer.valueOf(feedback.getStatus())){
							case STATUS_HAS_READ:
								itemInfo.ststus=MessageStatus.HAS_READ;
								break;
							case STATUS_UN_READ:
								itemInfo.ststus=MessageStatus.UN_READ;
								break;
						}
						itemInfo.type = MessageType.PRIVATE;
						itemInfo.time =Utils.getCurrentTimeZoneUnixTime(feedback.getUpdatedAt());

						itemInfo.userObjectId=feedback.getOthersId();
						itemInfo.avatarUrl=feedback.getFormUserUrl();

						mItemInfoList.add(itemInfo);
					}
				}

				
				//私信消息列表
				List<PrivateEntity> privateMsg=allMessageBean.getDatas().getPrivateMsg();
				for(PrivateEntity msg:privateMsg){
					ItemInfo itemInfo = new ItemInfo();
					itemInfo.title = msg.getNickname();
					itemInfo.summary = msg.getContent();
					switch(Integer.valueOf(msg.getStatus())){
					case STATUS_HAS_READ:
						itemInfo.ststus=MessageStatus.HAS_READ;
						break;
					case STATUS_UN_READ:
						itemInfo.ststus=MessageStatus.UN_READ;
						break;
					}
					itemInfo.type = MessageType.PRIVATE;
					itemInfo.time =Utils.getCurrentTimeZoneUnixTime(msg.getUpdatedAt());
					
					itemInfo.userObjectId=msg.getOthersId();
					itemInfo.avatarUrl=msg.getFormUserUrl();
					
					mItemInfoList.add(itemInfo);
				}
				
				if(privateMsg.size()>0){
					mCurrentPage=mPage;
				}
				
				mMessageAdapter.notifyDataSetChanged();
			}else{
				Utils.toast(R.string.network_query_data_failed);
			}
		}
	}

	/***
	 * 新数据放在私信第一个，且在小站客服后面
	 * @param bean
	 */
	public void addNewPushedMsg(PushMsgBean bean){
		if(bean.getType()==3){
			AddPrivateMsg(bean);
		}else if(bean.getType()==0){
			AddSysMsg(bean);
		}

		mMessageAdapter.notifyDataSetChanged();
	}

	private void AddSysMsg(PushMsgBean bean) {

		ItemInfo itemInfo=null;
		if(mItemInfoList.size()>0&&mItemInfoList.get(0).type==MessageType.SYSTEM){
			itemInfo=mItemInfoList.remove(0);
		}else{
			itemInfo = new ItemInfo();
			itemInfo.title = getString(R.string.sys_msg);
			itemInfo.type = MessageType.SYSTEM;
		}

		itemInfo.summary = bean.getContent();
		itemInfo.ststus= MessageStatus.UN_READ;
		itemInfo.time =Utils.getCurrentTimeZoneUnixTime(bean.getUpdatedAt());

		if(mItemInfoList.size()>0){
			mItemInfoList.add(0,itemInfo);
		}else{
			mItemInfoList.add(itemInfo);
		}
	}

	private void AddPrivateMsg(PushMsgBean bean) {
		int insertPosition=-1;
		ItemInfo insertItem = null;

		for(int i=0;i<mItemInfoList.size();i++){
			ItemInfo item=mItemInfoList.get(i);

			//找到第一个私信的位置
			if(insertPosition==-1&&item.type== MessageType.PRIVATE){
				insertPosition=i;
			}

			//找到已存在的记录
			if(item.type== MessageType.PRIVATE&&item.userObjectId.equals(bean.getFromUser())){
				insertItem=mItemInfoList.remove(i);

				insertItem.summary = bean.getContent();
				insertItem.ststus= MessageStatus.UN_READ;
				insertItem.time = Utils.getCurrentTimeZoneUnixTime(bean.getUpdatedAt());
				break;
			}
		}

		if(insertItem!=null){
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.title = bean.getNickname();
			itemInfo.summary = bean.getContent();
			itemInfo.ststus= MessageStatus.UN_READ;
			itemInfo.type = MessageType.PRIVATE;
			itemInfo.time =Utils.getCurrentTimeZoneUnixTime(bean.getUpdatedAt());
			itemInfo.userObjectId=bean.getFromUser();
			itemInfo.avatarUrl=bean.getFormUserUrl();
		}

		if(insertPosition!=-1&&insertPosition<mItemInfoList.size()-1){
			mItemInfoList.add(insertPosition,insertItem);
		}else{
			mItemInfoList.add(insertItem);
		}
	}

	private void releaseRequest(RequestHandle requestHandle){
		if(requestHandle!=null&&!requestHandle.isFinished()){
			requestHandle.cancel(true);
		}
	}
	
	private void showProgressDialog() {
		mProgressDlg = DialogUtils.getProgressDialog(getActivity(), getString(R.string.loading));
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
