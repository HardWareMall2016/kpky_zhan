package com.zhan.kykp.spokenSquare;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.BaseHttpRequest.RequestType;
import com.zhan.kykp.network.bean.TopicHistoryBean;
import com.zhan.kykp.network.bean.TopicHistoryBean.DatasEntity;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class TopicHistoryActivity extends BaseActivity implements OnItemClickListener {
	
	private PullToRefreshListView mPullRefreshListView;
	private LayoutInflater mInflater;
	private HistoryAdapter mAdapter ;
	private RequestHandle mRequestHandle;
	private ImageView mEmptyView ;
	
    private TopicHistoryBean mHistoryBean ;
    private List<DatasEntity> mResults = new ArrayList<DatasEntity>();
	
	private final static String PAGE = "page";
	private int mMorePage = 1 ;
	private Dialog mProgressDlg;
	
	@Override
	protected void onDestroy() {
		if(mRequestHandle!=null && !mRequestHandle.isFinished()){
			mRequestHandle.cancel(true);
		}
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_history);
		
		mInflater=getLayoutInflater();
		
		mAdapter = new HistoryAdapter();
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.myfocus_list);
		
		mEmptyView = new ImageView(getApplicationContext());
		mEmptyView.setImageResource(R.drawable.topic_history_none);
		mEmptyView.setScaleType(ImageView.ScaleType.CENTER);
		mPullRefreshListView.setEmptyView(mEmptyView);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setAdapter(mAdapter);
		
		getActionBarRightMenu().setText(getString(R.string.tody_topic));
		getActionBarRightMenu().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),SpokenSquareActivity.class));

				StatisticUtils.onEvent(getString(R.string.spoken_square_title), getTitle().toString(), getString(R.string.tody_topic));
			}
		});
		
		showProgressDialog();
		queryDate(0);
		
		//下拉刷新
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				mMorePage = 1;
				queryDate(0);
			}
		});
		//上拉加载更多
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){
			@Override
			public void onLastItemVisible() {
				queryDate(mMorePage++);
			}
		});
		
		mPullRefreshListView.setOnItemClickListener(this);
		
	}
	
	private void queryDate(int page) {
		BaseHttpRequest httpRequest = new BaseHttpRequest();
		String url=String.format("%s?%s=%d", ApiUrls.TOPIC_TOPICHISTORY,PAGE,page);
		
		mRequestHandle= httpRequest.startRequest(getApplicationContext(), url, null, new HistoryCallBack(page), RequestType.GET);
	}

	private class HistoryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mResults.size();
		}

		@Override
		public Object getItem(int position) {
			return mResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder ;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.history_list_item, null);
				holder = new ViewHolder() ;
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Content = (TextView) convertView.findViewById(R.id.content);
				holder.data = (TextView) convertView.findViewById(R.id.data);
				holder.time = (TextView) convertView.findViewById(R.id.history_item_data);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			DatasEntity results = mResults.get(position);
			holder.Content.setText(results.getQuestion());
			holder.data.setText(results.getAnswerCount()+"人"+"  已练习");
			holder.time.setText(results.getTopicDate());
			
			String Atopic = results.getTopic() ;
			String topic = Atopic.toLowerCase();
			
			if(topic.equals("activity")){
				holder.Icon.setImageResource(R.drawable.sample8);
			}else if (topic.equals("advice")){
				holder.Icon.setImageResource(R.drawable.sample2);
			}else if(topic.equals("education")){
				holder.Icon.setImageResource(R.drawable.sample7);
			}else if(topic.equals("experience")){
				holder.Icon.setImageResource(R.drawable.sample3);
			}else if(topic.equals("job")){
				holder.Icon.setImageResource(R.drawable.sample5);
			}else if(topic.equals("leisure time")){
				holder.Icon.setImageResource(R.drawable.sample1);
			}else if(topic.equals("life")){
				holder.Icon.setImageResource(R.drawable.sample9);
			}else if(topic.equals("media")){
				holder.Icon.setImageResource(R.drawable.sample10);
			}else if(topic.equals("modern tech.")){
				holder.Icon.setImageResource(R.drawable.sample6);
			}else if(topic.equals("object")){
				holder.Icon.setImageResource(R.drawable.sample11);
			}else if(topic.equals("person")){
				holder.Icon.setImageResource(R.drawable.sample4);
			}else if(topic.equals("place")){
				holder.Icon.setImageResource(R.drawable.sample12);
			}else if(topic.equals("random")){
				holder.Icon.setImageResource(R.drawable.sample13);
			}else if(topic.equals("skill")){
				holder.Icon.setImageResource(R.drawable.sample14);
			}else if(topic.equals("money")){
				holder.Icon.setImageResource(R.drawable.sample15);
			}
			
			
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		ImageView Icon ;
		TextView Content ;
		TextView data ;
		TextView time ;
	}

	private class HistoryCallBack implements HttpRequestCallback{

		private int mPage;
		
		public HistoryCallBack(int page) {
			mPage=page;
		}

		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();
		}

		@Override
		public void onRequestSucceeded(String jsonStr) {
			closeDialog();
			mPullRefreshListView.onRefreshComplete();

			Gson gson = new Gson();
			mHistoryBean = gson.fromJson(jsonStr, TopicHistoryBean.class);

			if (mHistoryBean.getStatus() == 1) {
				if (mPage == 0) {
					mResults.clear();
				}
				if (mHistoryBean.getDatas().size() != 0) {
					mResults.addAll(mHistoryBean.getDatas());
				}
				if (mPage > 2 && mHistoryBean.getDatas().size() == 0) {
					Utils.toast(R.string.no_more_data);
				}
				
				mAdapter.notifyDataSetChanged();
			} else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, SpokenSquareActivity.class);
		intent.putExtra(SpokenSquareActivity.EXT_KEY_TOPIC_OBJECT_ID, mResults.get((int) id).getObjectId());
		String dateStr=mResults.get((int) id).getTopicDate();
		if(!TextUtils.isEmpty(dateStr)){
			dateStr=dateStr.replace("/","-");
		}
		intent.putExtra(SpokenSquareActivity.EXT_KEY_TOPIC_TIME,dateStr);
		startActivity(intent);

		StatisticUtils.onEvent(getString(R.string.spoken_square_title) , getTitle().toString(), "详情页");
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
