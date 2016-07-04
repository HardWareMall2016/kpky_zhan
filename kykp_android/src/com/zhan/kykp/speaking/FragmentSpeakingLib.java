package com.zhan.kykp.speaking;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
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
import com.zhan.kykp.util.PixelUtils;
import com.zhan.kykp.util.ShareUtil;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView.OnEditorActionListener;

public class FragmentSpeakingLib extends FragmentQuestionList implements OnClickListener, OnItemClickListener{
	
	//搜索&&历史记录
	private LinearLayout mSearchContent ;
	private TextView mTVCancelSearch;
	private EditText mSearchInputView ;
	private boolean mIsSearchMode=false;
	private ListView mListViewSearchHistory;
	private BaseAdapter mSearchHistoryAdapter;
	private ArrayList<String> mSearchHistoryList=new ArrayList<String>();
	
	// Views
	private LinearLayout mSpeakingContent ;
	private TextView mTVTPOSepaking;
	private TextView mTVNewGoldSepaking;
	private TextView mTVJJSepaking;

	private Dialog mProgressDlg;

	// pop menu
	private PopupWindow mPopupWindow;

	private LayoutInflater mInflater;
	private Resources mRes;
	private PullToRefreshListView mPullRefreshListView;
	private QuestionAdapter mAdapter;
	/*private QuestionLibDB mQuestionLibDB;
	private SpeakingRecordDB mSpeakingRecordDB;*/
	
	//侧边栏按钮
	private Button mSelectedBtnAnswerType;
	private Button mSelectedBtnTopic;

	private Type mSpeakingType = Type.TPO;
	private String mQueryTopic="";
	private boolean mQueryExcludeAnswered=false;
	
	List<Question> mQuestionList = new ArrayList<Question>();
	

	//查询参数
	private static String mSearchKeyword = "";//关键字搜索
	private static int mAnswerType = 0;//0全部;1未答题
	private static String mContentType = "0";//内容分类 0全部;其他直接传内容分类
	private int mCurrentPage = 0 ;

	//Network
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater=inflater;
		
		View rootView=mInflater.inflate(R.layout.speaking_library, null);

		init();

		initView(rootView);
		
		intiPopMenu();
		
		showProgressDialog();

		parseQueryParamAndRefreshTab();

		queryData(0);
		
		return rootView;
	}

	@Override
	public void onDestroyView() {
		closeDialog();
		releaseRequest(mRequestHandle);
		super.onDestroyView();
	}

	private void init(){
		mRes = getResources();
		mHttpRequest = new BaseHttpRequest();
	}

	private void initView(View rootView) {
		mTVTPOSepaking = (TextView) rootView.findViewById(R.id.tpo_speaking);
		mTVNewGoldSepaking = (TextView) rootView.findViewById(R.id.new_gold_speaking);
		mTVJJSepaking = (TextView) rootView.findViewById(R.id.jj_speaking);
		mTVTPOSepaking.setOnClickListener(this);
		mTVNewGoldSepaking.setOnClickListener(this);
		mTVJJSepaking.setOnClickListener(this);

		//搜索模块
		mSearchContent = (LinearLayout) rootView.findViewById(R.id.speaking_search_content);
		mSpeakingContent = (LinearLayout) rootView.findViewById(R.id.speaking_content);
		mListViewSearchHistory= (ListView) rootView.findViewById(R.id.search_history_list);
		mListViewSearchHistory.setOnItemClickListener(mOnSearchItemClickListener);
		mSearchHistoryAdapter=new SearchHistoryAdapter();
		mListViewSearchHistory.setAdapter(mSearchHistoryAdapter);

		/*mQuestionLibDB = new QuestionLibDB(getActivity());
		mSpeakingRecordDB= new SpeakingRecordDB(getActivity());*/

		mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.speaking_list);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		//刷新
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				mCurrentPage=0;
				queryData(mCurrentPage);

			}
		});
		//加载更多
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				queryData(mCurrentPage+1);
			}
		});

		mAdapter = new QuestionAdapter();
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(this);
	}

	/***
	 * 获取请求参数并发现TAB
	 */
	private void parseQueryParamAndRefreshTab(){
		switch (mSpeakingType) {
			case TPO:
				mTVTPOSepaking.setBackgroundResource(R.drawable.bg_dark_red_underline);
				mTVTPOSepaking.setTextColor(mRes.getColor(R.color.dark_red));
				mTVNewGoldSepaking.setBackgroundColor(Color.WHITE);
				mTVNewGoldSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				mTVJJSepaking.setBackgroundColor(Color.WHITE);
				mTVJJSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				break;
			case NEW_GOLD:
				mTVTPOSepaking.setBackgroundColor(Color.WHITE);
				mTVTPOSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				mTVNewGoldSepaking.setBackgroundResource(R.drawable.bg_dark_red_underline);
				mTVNewGoldSepaking.setTextColor(mRes.getColor(R.color.dark_red));
				mTVJJSepaking.setBackgroundColor(Color.WHITE);
				mTVJJSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				break;
			case JJ:
				mTVTPOSepaking.setBackgroundColor(Color.WHITE);
				mTVTPOSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				mTVNewGoldSepaking.setBackgroundColor(Color.WHITE);
				mTVNewGoldSepaking.setTextColor(mRes.getColor(R.color.text_color_content));
				mTVJJSepaking.setBackgroundResource(R.drawable.bg_dark_red_underline);
				mTVJJSepaking.setTextColor(mRes.getColor(R.color.dark_red));
				break;
		}

		if(mQueryExcludeAnswered == false){
			mAnswerType = 0;
		}else{
			mAnswerType = 1;
		}
		if(!TextUtils.isEmpty(mQueryTopic)){
			mContentType = mQueryTopic ;
		}else{
			mContentType = "0" ;
		}
	}

	/**
	 * 查询请求数据
	 */
	private void queryData(int page){
		releaseRequest(mRequestHandle);

		RequestParams requestParams = new RequestParams();
		requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
		requestParams.put("question", mSearchKeyword);
		requestParams.put("category", SpeakingTools.getTypeParam(getActivity(), mSpeakingType));//托福分类 tpo,TPO口语;new,新黄金口语;jijing,机经口语
		requestParams.put("answer_type", mAnswerType);
		requestParams.put("content_type", mContentType);
		requestParams.put("page",page);
		mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.SPEAKINGPRACTICE_GETTOEFL, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
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
						data.Inscription=item.getInscription();
								
					    mQuestionList.add(data);
					}
				}

				mAdapter.notifyDataSetChanged();
				
				/*if(page > 2 && bean.getDatas().size() == 0){
					Utils.toast(R.string.no_more_data);
				}*/
			}else{
				Utils.toast(R.string.network_query_data_failed);
			}
		}
		
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tpo_speaking:
			mSpeakingType = Type.TPO;
			break;
		case R.id.new_gold_speaking:
			mSpeakingType = Type.NEW_GOLD;
			break;
		case R.id.jj_speaking:
			mSpeakingType = Type.JJ;
			break;
		}
		parseQueryParamAndRefreshTab();
		mPullRefreshListView.setRefreshing();

		StatisticUtils.onEvent(getActivity().getTitle().toString(), SpeakingTools.getTitleString(App.ctx, mSpeakingType));
	}
	
	@Override
	public void initActionBar() {
		super.initActionBar();
		
		//搜索相关
		ImageView imgsearch = new ImageView(getActivity());
		imgsearch.setImageResource(R.drawable.search_selector);
		imgsearch.setPadding(0, 10, 10, 10);
		imgsearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsSearchMode=true;
				refreshSearchContentVisibility();
			}
		});
		mCallback.getMenuContent().addView(imgsearch);
		
		
		LayoutInflater inflater=getActivity().getLayoutInflater();
		View searchLayout= inflater.inflate(R.layout.speaking_action_bar_search_layout, null);
		mCallback.getCustomerActionBarLayout().addView(searchLayout);
		
		mSearchInputView = (EditText) searchLayout.findViewById(R.id.speaking_search);
		mSearchInputView.setOnEditorActionListener(mOnStartSearchListener);
		mTVCancelSearch = (TextView) searchLayout.findViewById(R.id.speaking_cancel);
		mTVCancelSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mIsSearchMode=false;
				refreshSearchContentVisibility();
				Utils.hideSoftInputFromWindow(mSearchInputView);

				StatisticUtils.onEvent(getActivity().getTitle().toString(), "搜索");
			}
		});
		
		
		//分类
		ImageView imgKind = new ImageView(getActivity());
		imgKind.setImageResource(R.drawable.kind_selector);
		imgKind.setPadding(0, 10, 0, 10);
		imgKind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopMenu();
				StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.speaking_selelct_question));
			}
		});
		mCallback.getMenuContent().addView(imgKind);
		mCallback.setSpeakingTitle(getString(R.string.speaking_library));
	}
	
	private OnEditorActionListener mOnStartSearchListener=new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			if (arg1 == EditorInfo.IME_ACTION_SEARCH) {  
				String queryStr=mSearchInputView.getText().toString();
				if(!TextUtils.isEmpty(queryStr)){
					//保存历史
					saveSearchHistory(queryStr);
					mCallback.showSearchResult(queryStr);
					Utils.hideSoftInputFromWindow(mSearchInputView);
				}else{
					Utils.toast(R.string.speaking_search);
				}
			}
			return false;
		}
	};
	
	
	public void onLeftMenuItemClick(View view) {

		Button selectedBtn=(Button)view;
		
		switch (view.getId()) {
		case R.id.btn_answered_type_all:
			//所有答题分类
			mQueryExcludeAnswered=false;
			mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnAnswerType=selectedBtn;
			break;
		case R.id.btn_un_answered:
			//未答题分类
			mQueryExcludeAnswered=true;
			mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnAnswerType=selectedBtn;
			break;
		case R.id.btn_all_question:
			mQueryTopic="";
			mSelectedBtnTopic.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnTopic.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnTopic=selectedBtn;
			break;
		default:
			mQueryTopic=(String) selectedBtn.getText();
			mSelectedBtnTopic.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnTopic.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnTopic=selectedBtn;
		}

		//统计使用
		String answerType=getString(R.string.speaking_all_question);
		if(mQueryExcludeAnswered){
			answerType=getString(R.string.speaking_un_answered);
		}
		String conetType=getString(R.string.speaking_all_question);
		if(!TextUtils.isEmpty(mQueryTopic)){
			conetType=mQueryTopic;
		}
		StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.speaking_selelct_question),answerType,conetType);

		parseQueryParamAndRefreshTab();
		mPullRefreshListView.setRefreshing();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mCallback.gotoPosition((int) arg3);
		StatisticUtils.onEvent(getString(R.string.speaking_library), SpeakingTools.getTitleString(App.ctx, mSpeakingType),"答题详情页");
	}
	
	@Override
	public List<Question> getQuestionList(){
		return mQuestionList;
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
			
			if (mSpeakingType == Type.TPO) {
				if(TextUtils.isEmpty(question.Inscription)){
					holder.Name.setText(String.format("%s(%s)", question.Name, question.Task));
				}else{
					holder.Name.setText(question.Inscription);
				}
			} else if (mSpeakingType == Type.NEW_GOLD) {
				holder.Name.setText(String.format(getString(R.string.speaking_new_gold_format_name), position + 1));
			} else {
				holder.Name.setText(String.format(getString(R.string.speaking_jj_format_name), question.ExamDate, question.RepeatCount));
			}

			return convertView;
		}
	}
	
	
	//搜索相关
	private void refreshSearchContentVisibility(){
		if(mIsSearchMode){
			mCallback.getCustomerActionBarLayout().setVisibility(View.VISIBLE);
			mCallback.getActionBarDefaultLayout().setVisibility(View.GONE);
			//显示搜索栏
			mSearchContent.setVisibility(View.VISIBLE);
			mSpeakingContent.setVisibility(View.GONE);
			//显示搜索历史
			updateSearchHistoryList();
		}else{
			mCallback.getCustomerActionBarLayout().setVisibility(View.GONE);
			mCallback.getActionBarDefaultLayout().setVisibility(View.VISIBLE);
			//显示搜索栏
			mSearchContent.setVisibility(View.GONE);
			mSpeakingContent.setVisibility(View.VISIBLE);
		}
	}
	
	
	private void updateSearchHistoryList() {
		mSearchHistoryList.clear();
		String history = ShareUtil.getValue(getActivity(), ShareUtil.SPEAKING_SEARCH_HISTORY);
		// 从保存的字符串中截取出来
		if (!TextUtils.isEmpty(history)) {
			String[] historyarr =TextUtils.split(history, "\\|");
			for (String item : historyarr) {
				if (!TextUtils.isEmpty(item)) {
					mSearchHistoryList.add(item);
				}
			}
		}
		
		mSearchHistoryAdapter.notifyDataSetChanged();
	}
	
	private void saveSearchHistory(String curQuery){
		// 历史记录中最多保留10条
		String history = "";
		boolean exists = false;
		
		for (int i = 0; i < mSearchHistoryList.size(); i++) {
			if (mSearchHistoryList.size() == 10 && i == mSearchHistoryList.size()-1) {
				break;
			}
			history += mSearchHistoryList.get(i) + "|";
			if (mSearchHistoryList.get(i).equals(curQuery)) {
				exists = true;
			}
		}
		if (!exists) {
			if(TextUtils.isEmpty(history)){
				history = curQuery;
			}else{
				history = curQuery+"|"+history;
			}
			
			ShareUtil.setValue(getActivity(), ShareUtil.SPEAKING_SEARCH_HISTORY, history);
		}
	}
	
	private  OnItemClickListener mOnSearchItemClickListener =new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mCallback.showSearchResult(mSearchHistoryList.get(position));
			Utils.hideSoftInputFromWindow(mSearchInputView);
		}
	};
	
	
	private class SearchHistoryAdapter extends BaseAdapter{
		private class ViewHolder{
			TextView tvItem;
		}
		
		@Override
		public int getCount() {
			return mSearchHistoryList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mSearchHistoryList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder;
			if(arg1==null){
				holder=new ViewHolder();
				holder.tvItem=new TextView(getActivity());
				holder.tvItem.setMinHeight(PixelUtils.dp2px(40));
				holder.tvItem.setTextSize(20);
				holder.tvItem.setPadding(PixelUtils.dp2px(12), 0, 0, 0);
				holder.tvItem.setGravity(Gravity.CENTER_VERTICAL);
				holder.tvItem.setTextColor(mRes.getColor(R.color.text_color_title));
				
				arg1=holder.tvItem;
				
				arg1.setTag(holder);
			}else{
				holder=(ViewHolder)arg1.getTag();
			}
			
			String itemStr=mSearchHistoryList.get(arg0);
			
			holder.tvItem.setText(itemStr);
			
			return arg1;
		}
	}

	// 初始化
	private void intiPopMenu() {
		/* 设置显示menu布局 view子VIEW */
		// View sub_view = inflater.inflate(R.layout.pop_memu_height, null);
		/* 第一个参数弹出显示view 后两个是窗口大小 */
		int screenWidth = DialogUtils.getScreenWidth(getActivity());
		mPopupWindow = new PopupWindow(screenWidth * 3 / 4, WindowManager.LayoutParams.MATCH_PARENT);
		/* 设置背景显示 */
		// mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.agold_menu_bg));
		int bgColor = getResources().getColor(R.color.main_background);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(bgColor));
		/* 设置触摸外面时消失 */
		mPopupWindow.setOutsideTouchable(true);
		/* 设置系统动画 */
		mPopupWindow.setAnimationStyle(R.style.pop_menu_left_animation);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
		mPopupWindow.setFocusable(true);

		// 布局
		View conetnt = mInflater.inflate(R.layout.speaking_lib_left_menu_layout, null);
		initItemClickEvent(conetnt);
		mPopupWindow.setContentView(conetnt);
		mSelectedBtnAnswerType=(Button)conetnt.findViewById(R.id.btn_answered_type_all);
		mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_dark_red_border);
		mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.dark_red));
		
		mSelectedBtnTopic=(Button)conetnt.findViewById(R.id.btn_all_question);
		mSelectedBtnTopic.setBackgroundResource(R.drawable.bg_dark_red_border);
		mSelectedBtnTopic.setTextColor(getResources().getColor(R.color.dark_red));
	}
	
	private void initItemClickEvent(View conetnt) {
		LinearLayout container = (LinearLayout)conetnt.findViewById(R.id.container);
		for (int i = 0; i < container.getChildCount(); i++) {
			View child = container.getChildAt(i);
			if (child instanceof LinearLayout) {
				LinearLayout itemContainer = (LinearLayout) child;
				for (int j = 0; j < itemContainer.getChildCount(); j++) {
					View itemContainerChild = itemContainer.getChildAt(j);
					if (itemContainerChild instanceof Button) {
						itemContainerChild.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								onLeftMenuItemClick(arg0);
							}
						});
					}
				}
			}
		}
	}

	// 显示菜单
	public void showPopMenu() {
		if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			/* 最重要的一步：弹出显示 在指定的位置(parent) 最后两个参数 是相对于 x / y 轴的坐标 */
			mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.LEFT, 0, 0);
			backgroundAlpha(0.5f);
			mPopupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					backgroundAlpha(1f);
				}
			});
		}
	}

	// 关闭PopUpWindow
	public boolean closePopWin() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

	private void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getActivity().getWindow().setAttributes(lp);
	}


	@Override
	public String getQuestionListType() {
		return SpeakingTools.getTitleString(App.ctx, mSpeakingType);
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

	/***
	 * Network
	 */
	private void releaseRequest(RequestHandle request) {
		if (request != null && !request.isFinished()) {
			request.cancel(true);
		}
	}
}

