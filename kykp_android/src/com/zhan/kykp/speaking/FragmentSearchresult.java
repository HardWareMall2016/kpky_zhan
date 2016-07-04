package com.zhan.kykp.speaking;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.zhan.kykp.base.App;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.SpeakingLibBean;
import com.zhan.kykp.speaking.SpeakingTools.Type;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class FragmentSearchresult extends FragmentQuestionList implements OnItemClickListener{

	public final static String ARG_SEARCH_KEY_WORD = "KEY_WORD";

	List<Question> mQuestionList = new ArrayList<Question>();

	private LayoutInflater mInflater;

	private QuestionAdapter mAdapter;

	// Views
	//private TextView mTvSearchResult;
	private PullToRefreshListView mPullRefreshListView;

	private Dialog mProgressDlg;

	// 搜索关键字
	private String mKeyWord;
	private int mCurrentPage = 0 ;

	//Network
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;

	public void prepareArguments(String keyword) {
		Bundle args = new Bundle();
		args.putString(ARG_SEARCH_KEY_WORD, keyword);
		setArguments(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mKeyWord = getArguments().getString(ARG_SEARCH_KEY_WORD);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;

		mHttpRequest = new BaseHttpRequest();

		View contentView = mInflater.inflate(R.layout.speaking_search_result, null);

		//mTvSearchResult = (TextView) contentView.findViewById(R.id.search_result_title);
		mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.speaking_list);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mPullRefreshListView.setOnItemClickListener(this);
		mAdapter=new QuestionAdapter();
		mPullRefreshListView.setAdapter(mAdapter);

		//刷新
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mCurrentPage=0;
				queryData(mCurrentPage);

			}
		});
		//加载更多
		mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				queryData(mCurrentPage + 1);
			}
		});


		//String resultFormat=getString(R.string.speaking_search_result);
		//mTvSearchResult.setText(String.format(resultFormat, mQuestionList.size()));

		showProgressDialog();
		mCurrentPage=0;
		queryData(mCurrentPage);

		return contentView;
	}

	@Override
	public void onDestroyView() {
		closeDialog();
		releaseRequest(mRequestHandle);
		super.onDestroyView();
	}
	@Override
	public List<Question> getQuestionList() {
		return mQuestionList;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mCallback.gotoPosition(position);

		StatisticUtils.onEvent(getActivity().getTitle().toString(), "搜索结果", "答题详情页");
	}
	
	@Override
	public String getQuestionListType() {
		return App.ctx.getString(R.string.speaking);
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
			

			if (question.Category.equals(SpeakingTools.getTypeString(getActivity(), Type.TPO))) {
				holder.Name.setText(String.format("%s(%s)", question.Name, question.Task));
			} else if (question.Category.equals(SpeakingTools.getTypeString(getActivity(), Type.NEW_GOLD))) {
				holder.Name.setText(String.format(getString(R.string.speaking_new_gold_format_name), position + 1));
			} else {
				holder.Name.setText(String.format(getString(R.string.speaking_jj_format_name), question.ExamDate, question.RepeatCount));
			}

			return convertView;
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
	/**
	 * 查询请求数据
	 */
	private void queryData(int page){
		releaseRequest(mRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
		requestParams.put("question", mKeyWord);
		requestParams.put("page",page);
		mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.SPEAKINGPRACTICE_GETTOEFL, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
	}

	private class RequestCallback implements HttpRequestCallback {

		private int page ;
		public RequestCallback(int page) {
			this.page = page ;
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
			SpeakingLibBean bean = Utils.parseJson(content, SpeakingLibBean.class);
			if(bean != null){

				if(page == 0){
					mQuestionList.clear();
				}
				if(bean.getDatas().size() != 0){
					mCurrentPage=page;

					for(SpeakingLibBean.DatasEntity item : bean.getDatas()){
						Question data = new Question() ;
						data.Answer = item.getAnswer();
						data.Category = item.getCategory();
						data.ExamDate = item.getExamDate();
						data.Name = item.getName() ;
						data.Question = item.getQuestion() ;
						data.RepeatCount = item.getRepeatCount();
						data.Task = item.getTask();
						data.Topic = item.getTopic();
						data.uid = item.getUid();
						data.objectId=item.getObjectId();

						mQuestionList.add(data);
					}
				}

				mAdapter.notifyDataSetChanged();

				if(page > 2 && bean.getDatas().size() == 0){
					Utils.toast(R.string.no_more_data);
				}
			}else{
				Utils.toast(R.string.network_query_data_failed);
			}
		}

	};
	/***
	 * Network
	 */
	private void releaseRequest(RequestHandle request) {
		if (request != null && !request.isFinished()) {
			request.cancel(true);
		}
	}
}
