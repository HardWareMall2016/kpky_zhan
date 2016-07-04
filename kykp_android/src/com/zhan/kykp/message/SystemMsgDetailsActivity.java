package com.zhan.kykp.message;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.SystemMsgBean;
import com.zhan.kykp.network.bean.SystemMsgBean.DatasEntity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

public class SystemMsgDetailsActivity extends BaseActivity {
	private final static String TAG = "SystemMsgDetailsActivity";

	//Views
	private PullToRefreshListView mPullRefreshListView;
	private Dialog mProgressDlg;
	
	//Tools
	private MessageAdapter mAdapter;
	
	//Data
	private List<SystemMessage> mItemList = new LinkedList<SystemMessage>();

	//Params
	private int mCurrentPage=0;
	private boolean mHasMoreData = true;

	// Network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mQueryMsgRequestHandle;
	private RequestHandle mUpdateRequestHandle;
	
	//Type
	private class SystemMessage{
		String content;
		long time;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_msg_details);

		mHttpRequest = new BaseHttpRequest();

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(SystemMsgDetailsActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (mHasMoreData) {
					int queryPage=mItemList.size()==0?0:(mCurrentPage+1);
					querySystemMsg(queryPage);
				} else {
					mPullRefreshListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							mPullRefreshListView.onRefreshComplete();
						}
					}, 100);
				}
			}
		});

		mAdapter = new MessageAdapter();
		mPullRefreshListView.setAdapter(mAdapter);

		showProgressDialog();
		querySystemMsg(0);
		updateSystemMsg();
	}

	@Override
	protected void onDestroy() {
		releaseRequest(mQueryMsgRequestHandle);
		releaseRequest(mUpdateRequestHandle);
		closeDialog();
		super.onDestroy();
	}


	/*
	 * 倒序显示
	 */
	private class MessageAdapter extends BaseAdapter {
		private class ViewHolder {
			ImageView icon;
			TextView content;
			TextView time;
		}

		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemList.get(mItemList.size() - 1 - position);
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

				convertView = inflater.inflate(R.layout.message_system_list_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.time = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			SystemMessage systemMsg = mItemList.get(mItemList.size() - 1 - position);

			holder.content.setText(systemMsg.content);

			holder.time.setText(Utils.getFullFormateTimeStr(systemMsg.time));

			return convertView;
		}
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

	private void moveListToLastItem() {
		ListView listView = mPullRefreshListView.getRefreshableView();
		int position = mItemList.size() - 1;
		if (position >= 0) {
			listView.setSelection(position);
		}
	}

	/***
	 * Network
	 */

	private void releaseRequest(RequestHandle request) {
		if (request != null && !request.isFinished()) {
			request.cancel(true);
		}
	}

	// Message - 系统消息列表
	private final static String NET_PARAM_USER = "user";// 发送用户objectId
	private final static String NET_PARAM_PAGE = "page";// 页数

	private void querySystemMsg(int page) {
		releaseRequest(mQueryMsgRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(NET_PARAM_PAGE, page);

		mQueryMsgRequestHandle = mHttpRequest.startRequest(this, ApiUrls.MESSAGE_SYS_MSG, requestParams, new QueryMsgCallback(page), RequestType.GET);
	}

	private class QueryMsgCallback implements HttpRequestCallback {
		private int mPage;

		public QueryMsgCallback(int page) {
			mPage = page;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			mPullRefreshListView.onRefreshComplete();
			closeDialog();
			Utils.toast(R.string.network_error);
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
			
			SystemMsgBean systemMsgBean = Utils.parseJson(content, SystemMsgBean.class);

			if (systemMsgBean != null) {
				if(mPage==0){
					mItemList.clear();
				}
				
				if(systemMsgBean.getDatas().size()>0){
					mCurrentPage=mPage;
					mHasMoreData=true;
					
					for(DatasEntity msg:systemMsgBean.getDatas()){
						SystemMessage item=new SystemMessage();
						item.content=msg.getMessage();
						item.time=Utils.getCurrentTimeZoneUnixTime(msg.getCreatedAt());
						mItemList.add(item);
					}
				}else{
					mHasMoreData=false;
					mPullRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel(getString(R.string.no_more_sys_msg)); 
					mPullRefreshListView.getLoadingLayoutProxy(true, false).setReleaseLabel(getString(R.string.no_more_sys_msg));
				}
				
				mAdapter.notifyDataSetChanged();

				if (mPage == 0) {
					moveListToLastItem();
				}
			} else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
	}
	
	private void updateSystemMsg() {
		releaseRequest(mUpdateRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put(NET_PARAM_USER, UserInfo.getCurrentUser().getObjectId());

		mUpdateRequestHandle = mHttpRequest.startRequest(this, ApiUrls.MESSAGE_UPDATE_SYS_MSG, requestParams, null, RequestType.POST);
	}
}
