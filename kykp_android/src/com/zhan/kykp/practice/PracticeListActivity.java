package com.zhan.kykp.practice;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.PracticeListBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class PracticeListActivity extends BaseActivity implements OnItemClickListener{
	// View
	private PullToRefreshListView mPullRefreshListView ;
	private Dialog mProgressDlg;
	// Data and tools
	private PracticeAdapter mAdapter;
	private List<PracticeInfo> mPracticeList = new LinkedList<>();

	//Network
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;
	private final static String PAGE = "page";
	private int mCurrentPage = 0 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_list);

		mHttpRequest = new BaseHttpRequest();

		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.list);
		mAdapter = new PracticeAdapter();
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(this);

		//下拉刷新
		mPullRefreshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						queryData(0);
					}
				});
		//上拉加载更多
		mPullRefreshListView
				.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						queryData(mCurrentPage+1);
					}
				});

		showProgressDialog();
		queryData(0);
	}
	
	@Override
	protected void onDestroy() {
		closeDialog();
		BaseHttpRequest.releaseRequest(mRequestHandle);
		super.onDestroy();
	}


	private class PracticeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mPracticeList.size();
		}

		@Override
		public Object getItem(int position) {
			return mPracticeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.practice_list_item, null);
				holder = new ViewHolder();
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Title = (TextView) convertView.findViewById(R.id.title);
				holder.SubTitle = (TextView) convertView.findViewById(R.id.sub_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PracticeInfo question = mPracticeList.get(position);
			holder.Title.setText(question.getTitle());
			holder.SubTitle.setText(question.getSubTitle());

			holder.Icon.setImageResource(R.drawable.sample1);

			if (question.getIndex() % 7 == 0) {
				holder.Icon.setImageResource(R.drawable.sample1);
			} else if (question.getIndex() % 7 == 1) {
				holder.Icon.setImageResource(R.drawable.sample2);
			} else if (question.getIndex() % 7 == 2) {
				holder.Icon.setImageResource(R.drawable.sample3);
			} else if(question.getIndex() % 7 == 3){
				holder.Icon.setImageResource(R.drawable.sample7);
			}else if(question.getIndex() % 7 == 4){
				holder.Icon.setImageResource(R.drawable.sample5);
			}else if(question.getIndex() % 7 == 5){
				holder.Icon.setImageResource(R.drawable.sample6);
			}else if(question.getIndex() % 7 == 6){
				holder.Icon.setImageResource(R.drawable.sample4);
			}else {
				holder.Icon.setImageResource(R.drawable.sample12);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView Icon;
		TextView Title;
		TextView SubTitle;
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
	
	private void queryData(int page){
		BaseHttpRequest.releaseRequest(mRequestHandle);
		RequestParams requestParams=new RequestParams();
		requestParams.put(PAGE, page);
		mRequestHandle = mHttpRequest.startRequest(this, ApiUrls.PRACTICE_LISTS, requestParams, new RequestCallback(page), BaseHttpRequest.RequestType.GET);

	}

	private class RequestCallback implements HttpRequestCallback{

		private int page ;
		public RequestCallback(int page) {
			this.page = page ;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			mPullRefreshListView.onRefreshComplete();
				closeDialog();
		}

		@Override
		public void onRequestFailedNoNetwork() {
			mPullRefreshListView.onRefreshComplete();
				closeDialog();
				Utils.toast(R.string.network_disconnect);
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
			PracticeListBean bean = Utils.parseJson(content, PracticeListBean.class);
			if (bean != null) {
				if(page == 0){
					mPracticeList.clear();
				}

				for (PracticeListBean.DatasEntity item : bean.getDatas()) {
					PracticeInfo info = new PracticeInfo();
					info.setObjectId(item.getObjectId());
					info.setTitle(item.getTitle());
					info.setSubTitle(item.getSubTitle());
					info.setIndex(item.getIndex());
					info.setZipFile(item.getZipFile());
					mPracticeList.add(info);
				}

				//是否有数据更新
				if(page==0||(bean.getDatas()!=null&&bean.getDatas().size()>0)){
					mAdapter.notifyDataSetChanged();
					mCurrentPage =page;
				}
			}else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent practiceIntent = new Intent(this, PracticeDetailsActivity.class);
		practiceIntent.putExtra(PracticeDetailsActivity.EXT_KEY_PRACTICE_OBJ, mPracticeList.get((int) id));
		startActivity(practiceIntent);

		StatisticUtils.onEvent(getTitle().toString(), "答题详情页");
	}
}
