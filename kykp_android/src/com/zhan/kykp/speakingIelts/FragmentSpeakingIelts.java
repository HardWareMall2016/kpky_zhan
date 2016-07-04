package com.zhan.kykp.speakingIelts;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.SpeakingIeltsListsBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class FragmentSpeakingIelts extends FragmentIeltsList implements OnItemClickListener {

	private LayoutInflater mInflater ;
	private PullToRefreshListView mPullRefreshListView;
	private Dialog mProgressDlg;
	private IeltsAdapter mAdapter ;
	private View rootView = null;
	// pop menu
    private PopupWindow mPopupWindow;
    //侧边栏按钮
  	private Button mSelectedBtnAnswerType;
  	
  	private String mQueryTopic="";
  	private int mMorePage = 1 ;
  	
	private boolean mHasMoreData=true;
	private List<IeltsData> mSearchIelts = new LinkedList<IeltsData>();
	
	private final static String TYPE = "type";
	private final static String PAGE = "page";
	private SpeakingIeltsListsBean mBean ;
	
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;
	
	@Override
	public List<IeltsData> getIeltsList() {
		return mSearchIelts;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		if(rootView == null){
			 mInflater = inflater;
			    rootView = mInflater.inflate(R.layout.speaking_jj_yuce_list, null);
				mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.list);
				//下拉刷新
				mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
						if(mHasMoreData){
							mMorePage = 1;
							queryData(mQueryTopic,0) ;
						}else{
							mPullRefreshListView.postDelayed(new Runnable(){
								@Override
								public void run() {
									mPullRefreshListView.onRefreshComplete();
								}
							}, 100);
						}
					}
				});
				//上拉加载更多
				mPullRefreshListView
						.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
							@Override
							public void onLastItemVisible() {
								queryData(mQueryTopic,mMorePage++);
							}
						});
				
				mPullRefreshListView.setOnItemClickListener(this);
				
				mAdapter = new IeltsAdapter();
				mPullRefreshListView.setAdapter(mAdapter);
				
				intiPopMenu();
				
				showProgressDialog();
				
				queryData(mQueryTopic,0);
		}
       
		return rootView ;
	}
	
	private void intiPopMenu() {
		int screenWidth = DialogUtils.getScreenWidth(getActivity());
		mPopupWindow = new PopupWindow(screenWidth * 3 / 4, WindowManager.LayoutParams.MATCH_PARENT);
		/* 设置背景显示 */
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
		View conetnt = mInflater.inflate(R.layout.speaking_ielts_left_menu_layout, null);
		initItemClickEvent(conetnt);
		mPopupWindow.setContentView(conetnt);
		mSelectedBtnAnswerType = (Button)conetnt.findViewById(R.id.btn_all_question);
		mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_dark_red_border);
		mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.dark_red));
		
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

	@Override
	public void initActionBar() {
		ImageView imgKind = new ImageView(getActivity());
		imgKind.setImageResource(R.drawable.kind_selector);
		imgKind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopMenu();
			}
		});

		mCallback.getMenuContent().removeAllViews();
		mCallback.getMenuContent().addView(imgKind);
		mCallback.setSpeakingIeltsTitle(getString(R.string.speaking_ielts_library));
	}
	protected void showPopMenu() {
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

	private void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getActivity().getWindow().setAttributes(lp);
	}

	protected void onLeftMenuItemClick(View view) {
		Button selectedBtn = (Button)view ;
		
		switch(view.getId()){
		case R.id.btn_all_question:
			//选择全部
			mQueryTopic = "";
			mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnAnswerType = selectedBtn;
			break;
		default:
			mQueryTopic=(String) selectedBtn.getText();
			mSelectedBtnAnswerType.setBackgroundResource(R.drawable.bg_grey_border);
			mSelectedBtnAnswerType.setTextColor(getResources().getColor(R.color.text_color_main));
			
			selectedBtn.setBackgroundResource(R.drawable.bg_dark_red_border);
			selectedBtn.setTextColor(getResources().getColor(R.color.dark_red));
			mSelectedBtnAnswerType = selectedBtn;
		}
		mMorePage = 1;
		queryData(mQueryTopic,0);

		//统计使用
		String conetType=getString(R.string.speaking_all_question);
		if(!TextUtils.isEmpty(mQueryTopic)){
			conetType=mQueryTopic;
		}
		StatisticUtils.onEvent(getActivity().getTitle().toString(), getString(R.string.speaking_selelct_question), conetType);

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
	
	
	private void queryData(String type, int page){
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();

		requestParams.put(TYPE, type);
		requestParams.put(PAGE,page);
		mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.SPEAKINGPRACTICE_GETIELTS, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
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
			mBean = Utils.parseJson(content, SpeakingIeltsListsBean.class);
			if(mBean != null){
				if(page == 0){
					mSearchIelts.clear();
				}
				if(mBean.getDatas().size() != 0){
					for(SpeakingIeltsListsBean.DatasEntity item:mBean.getDatas()){
						IeltsData data = new IeltsData();
						data.Book = item.getBook();
						data.createdAt = item.getCreatedAt();
						data.index = item.getIndex();
						data.objectId = item.getObjectId();
						data.Part = item.getPart();
						data.Question = item.getQuestion();
						data.QuestionTitle = item.getQuestionTitle();
						data.QuestionType = item.getQuestionType();
						data.Test = item.getTest();
						data.updatedAt = item.getUpdatedAt();
						mSearchIelts.add(data);
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
		
	};

	
	private class ViewHolder{
		ImageView Icon ;
		TextView Content ;
		TextView part ;
	}
	
	private class IeltsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mSearchIelts.size();
		}

		@Override
		public Object getItem(int position) {
			return mSearchIelts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder ;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.speaking_ielts_list_item, null);
				holder = new ViewHolder();
				holder.Icon = (ImageView) convertView.findViewById(R.id.img_question);
				holder.Content = (TextView) convertView.findViewById(R.id.content);
				holder.part = (TextView) convertView.findViewById(R.id.part);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			IeltsData ieltsData = mSearchIelts.get(position);
            holder.Content.setText(ieltsData.getQuestion());
            holder.part.setText(ieltsData.getBook()+" Test "+ ieltsData.getTest()+" Part "+ ieltsData.getPart());
            
            if(ieltsData.getTest().equals("1")){
            	holder.Icon.setImageResource(R.drawable.sample1);
            }else if(ieltsData.getTest().equals("2")){
            	holder.Icon.setImageResource(R.drawable.sample2);
            }else if(ieltsData.getTest().equals("3")){
            	holder.Icon.setImageResource(R.drawable.sample3);
            }else if(ieltsData.getTest().equals("4")){
            	holder.Icon.setImageResource(R.drawable.sample4);
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

	//点击事件调用SpeakingIeltsListActivity的回调函数gotoPosition（）
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mCallback.gotoPosition(position);

		StatisticUtils.onEvent(getActivity().getTitle().toString(), "答题详情页");
	}

	
}
