package com.zhan.kykp.speaking;

import java.util.LinkedList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.JiJingBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentJiJingYuCe extends FragmentQuestionList implements OnItemClickListener{
	public final static String TAG = FragmentJiJingYuCe.class.getSimpleName();
	
	private PullToRefreshListView mPullRefreshListView;
	
	private LayoutInflater mInflater;
	private JiJingBean mBean ;

	private List<Question> mQuestionList = new LinkedList<Question>();
	private QuestionAdapter mAdapter;
	
	private View mRootView=null;
	
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;
	
	private int mMorePage = 1 ;
	private final static String PAGE = "page";
	
	private Dialog mProgressDlg;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mRootView==null){
			mInflater=inflater;
			mRootView=mInflater.inflate(R.layout.speaking_jj_yuce_list, null);
			
			mPullRefreshListView = (PullToRefreshListView) mRootView.findViewById(R.id.list);
			mPullRefreshListView.setMode(Mode.PULL_FROM_START);
			mAdapter = new QuestionAdapter();
			mPullRefreshListView.setAdapter(mAdapter);
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
					mMorePage = 1;
					loadJJList(0);
				}
			});
			// 上拉加载更多
			mPullRefreshListView
					.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
						@Override
						public void onLastItemVisible() {
							loadJJList(mMorePage++);
						}
					});
			mPullRefreshListView.setOnItemClickListener(this);

			showProgressDialog();
			
			loadJJList(0);
		}

		
		return mRootView;
	}
	

	@Override
	public void initActionBar() {
		super.initActionBar();
		mCallback.setSpeakingTitle(getString(R.string.speaking_jj_prediction));
	}

	private void loadJJList(int page){
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(PAGE,page);
		mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.SPEAKINGPRACTICE_GETJIJING, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
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
			mBean = Utils.parseJson(content, JiJingBean.class);
			if(mBean != null){
				if(page == 0){
					mQuestionList.clear() ;
				}
				
				if(mBean.getDatas().size() != 0){
					for(JiJingBean.DatasEntity item : mBean.getDatas()){
						Question data = new Question() ;
						data.objectId=item.getObjectId();
						data.Answer = item.getAnswer();
						data.Category = item.getCategory();
						data.ExamDate = item.getExamDate() ;
						data.uid = item.getUid();
						data.Question = item.getQuestion() ;
						data.RepeatCount = item.getRepeatCount() ;
						data.Task = item.getTask() ;
						data.Topic = item.getTopic() ;
						mQuestionList.add(data);
					}
					mAdapter.notifyDataSetChanged();
				}
				/*if(page > 2 && mBean.getDatas().size() == 0){
					Utils.toast(R.string.no_more_data);
				}*/
			}else{
				Utils.toast(R.string.network_query_data_failed);
			}
		}
		
	}


	private class ViewHolder {
		ImageView Icon;
		TextView Content;
		TextView Name;
	}

	private class QuestionAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mQuestionList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mQuestionList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.speaking_lib_list_item, null);
				holder = new ViewHolder();
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Content = (TextView) convertView.findViewById(R.id.content);
				holder.Name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Question question = mQuestionList.get(position);
			holder.Content.setText(question.Question);
			
			String Atopic = question.Topic ;
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

			holder.Name.setText(String.format(getString(R.string.speaking_jj_format_name), question.ExamDate, question.RepeatCount));

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mCallback.gotoPosition(position-1);

		StatisticUtils.onEvent(getActivity().getTitle().toString(),"答题详情页");
	}
	
	@Override
	public List<Question> getQuestionList(){
		return mQuestionList;
	}


	@Override
	public String getQuestionListType() {
		return getString(R.string.speaking_type_jj);
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
	public void onDestroy() {
		closeDialog();
		release(mRequestHandle);
		super.onDestroy();
	}

	private void release(RequestHandle request) {
		if(request!=null && !request.isFinished()){
			request.cancel(true);
		}
	}
	
}
