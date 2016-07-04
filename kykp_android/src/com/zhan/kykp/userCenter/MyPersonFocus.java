package com.zhan.kykp.userCenter;


import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.MyPersonRefreshBean;
import com.zhan.kykp.network.bean.MyPersonRefreshBean.DatasEntity;
import com.zhan.kykp.network.bean.MyPersonRefreshBean.DatasEntity.UserInfoEntity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class MyPersonFocus extends BaseActivity implements OnItemClickListener{
	public static final String EXT_KEY_PERSONOBJECT = "userobject_id";
	public static final String EXT_KEY_TYPE = "type";
	
	private PullToRefreshListView mPullRefreshListView ;
	private FocusAdapter mAdapter ;
	private LayoutInflater mInflater;
	private RequestHandle mRequestHandle;
	private String mUserobjectId ;
	private String mType ;
	private TextView emptyView ;
	
	private BaseHttpRequest mHttpRequest ;
	private MyPersonRefreshBean mRefreshBean ;
	private DisplayImageOptions options;
	private Dialog mProgressDlg;
	
	private final static String PAGE = "page";
	private final static String USERID = "user"; 
	private final static String TYPE = "type"; 
	
	private int mMorePage = 1 ;
	
	
	private List<DataItemInfo> userInfo = new LinkedList<DataItemInfo>();
	
	@Override
	protected void onDestroy() {
		release(mRequestHandle);
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
		setContentView(R.layout.activity_person_focus);
		
		mInflater=getLayoutInflater();
		options=PhotoUtils.buldDisplayImageOptionsForAvatar();
		
		Intent intent = this.getIntent();        
		Bundle bundle = intent.getExtras();      
		mUserobjectId = bundle.getString(EXT_KEY_PERSONOBJECT); 
		mType = bundle.getString(EXT_KEY_TYPE); 
		
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.myfocus_list);
		emptyView = new TextView(getApplicationContext());
		if (mType.equals("0")) {
			this.setTitle(getString(R.string.person_title_fans));
			emptyView.setText(getString(R.string.person_fans_none));
		} else {
			this.setTitle(getString(R.string.person_title_focus));
			emptyView.setText(getString(R.string.person_focus_none));
		}
		emptyView.setTextColor(Color.parseColor("#666666"));  
		emptyView.setTextSize(20);
		emptyView.setGravity(Gravity.CENTER);  
		mPullRefreshListView.setEmptyView(emptyView);
		
		mAdapter = new FocusAdapter();
		mPullRefreshListView.setAdapter(mAdapter);
		
		//下拉刷新
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						mMorePage = 1;
						queryDate(0);
					}
				});
		//上拉加载更多
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						queryDate(mMorePage++);
					}
				});
		showProgressDialog();
		queryDate(0);
		
		mPullRefreshListView.setOnItemClickListener(this);
		
	}
	
	private void queryDate(int page) {
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(USERID, mUserobjectId);
		requestParams.put(TYPE, mType);
		requestParams.put(PAGE, page);
		mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.FOLLOWE_GETFOLLOWELIST, requestParams, new RequestCallback(page), RequestType.GET);
	}
	
	private class RequestCallback implements HttpRequestCallback{

		private int page ;
		
		public RequestCallback(int page) {
			this.page = page ;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
			Utils.toast(R.string.network_error);
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
			mRefreshBean = Utils.parseJson(content, MyPersonRefreshBean.class);
			if(mRefreshBean != null){
				if(page == 0){
					userInfo.clear();
				}
				if(mRefreshBean.getDatas().size() != 0){
					for(DatasEntity item:mRefreshBean.getDatas()){
						DataItemInfo data = new DataItemInfo();
						data.createdAt = item.getCreatedAt();
						data.objectId = item.getObjectId();
						data.updatedAt = item.getUpdatedAt();
						data.userInfo = item.getUserInfo();
						userInfo.add(data);
					}
					mAdapter.notifyDataSetChanged();
				}else{
					mPullRefreshListView.setEmptyView(emptyView);
				}
				
				if(page > 2 && mRefreshBean.getDatas().size() == 0){
					Utils.toast(R.string.no_more_data);
				}
				
			}else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
		
	}
	

	private class FocusAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return userInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return userInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder ;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.myfocus_list_item, null);
			    holder.head = (ImageView) convertView.findViewById(R.id.focus_item_head);
			    holder.nickname = (TextView) convertView.findViewById(R.id.focus_item_nickname);
			    convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.nickname.setText(userInfo.get(position).userInfo.getNickname());
			ImageLoader.getInstance().displayImage(userInfo.get(position).userInfo.getUrl(),holder.head,options);
			
			return convertView;
		}
		
		
	}

	private class DataItemInfo{
		 int createdAt;
         int updatedAt;
         String objectId;
         UserInfoEntity userInfo;
	}
	static class ViewHolder{
		ImageView head ;
		TextView nickname ;
	}
	//点击进入他的个人主页
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		StatisticUtils.onEvent(R.string.person_title_focus, R.string.person_title_list);
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(),PersonCenterActivity.class);
		Bundle bundle = new Bundle();                           
		bundle.putString(PersonCenterActivity.EXT_KEY_USEROBJECT, userInfo.get((int)id).userInfo.getObjectId());    
		intent.putExtras(bundle); 
		startActivity(intent);
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
