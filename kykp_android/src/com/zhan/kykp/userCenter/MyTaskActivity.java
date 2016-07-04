package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.HomePageActivity;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.TPOListActivity;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.TaskBean;
import com.zhan.kykp.practice.PracticeListActivity;
import com.zhan.kykp.speaking.SpeakingMainActivity;
import com.zhan.kykp.spokenSquare.SpokenSquareActivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.MyTaskListView;

import java.util.ArrayList;
import java.util.List;

public class MyTaskActivity extends BaseActivity {

	private RequestHandle mRequestHandle;
	private BaseHttpRequest mHttpRequest;
	private Dialog mProgressDlg;
	private final static String USER = "user";

	private LayoutInflater mInflater;
	private TaskBean mBean ;
	private TextView mSpokenSquare ;
	private TextView mBeikaoTask ;
	private TextView mOtherEntity ;

	private MyTaskListView myTaskSquareListView ;
	private SquareAdapter mAdapter ;
	private MyTaskListView myTaskBKListView ;
	private BeiKaoAdapter beiKaoAdapter ;
	private MyTaskListView myTaskOTListView ;
	private OtherAdapter otherAdapter ;


	private List<TopicInfo> mTopList = new ArrayList<TopicInfo>();
	private List<BeiKaoInfo> mBeiKaoList = new ArrayList<BeiKaoInfo>();
	private List<OtherInfo> mOtherList = new ArrayList<OtherInfo>();

	private enum ItemType {SPEAKING,PRACTICE,TPO};

	@Override
	protected void onResume() {
		showProgressDialog();
		queryDate(UserInfo.getCurrentUser().getObjectId());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		release(mRequestHandle);
		super.onDestroy();
	}

	private void release(RequestHandle request) {
		if (request != null && !request.isFinished()) {
			request.cancel(true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_task);
		mInflater=getLayoutInflater();
		initView();
	}


	private void queryDate(String objectId) {
		StatisticUtils.onEvent("我的任务", "用户任务列表");
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(USER, objectId);
		mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_USERTASKLIST, requestParams, new RequestCallback(), BaseHttpRequest.RequestType.GET);
	}

	private class RequestCallback implements HttpRequestCallback {

		@Override
		public void onRequestFailed(String errorMsg) {
			closeDialog();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			closeDialog();
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeDialog();
			mBean = Utils.parseJson(content, TaskBean.class);
			mTopList.clear();
			mBeiKaoList.clear();
			mOtherList.clear();
			if(mBean != null){
				mSpokenSquare.setText(mBean.getDatas().getTopic().getName());
				mBeikaoTask.setText(mBean.getDatas().getBeikao().getName());
				mOtherEntity.setText(mBean.getDatas().getOther().getName());
				if(mBean.getDatas().getTopic() != null){
					for(TaskBean.DatasEntity.TopicEntity.DataEntity item : mBean.getDatas().getTopic().getData()){
						TopicInfo info = new TopicInfo();
						info.type = item.getType();
						info.name = item.getName();
						info.countNumber = item.getCountNumber();
						info.needNumber = item.getNeedNumber();
						info.credit = item.getCredit();
						info.status = item.getStatus();
						info.nowNumber = item.getNowNumber();
						mTopList.add(info);
					}
				}
				if(mBean.getDatas().getBeikao() != null){
					for(TaskBean.DatasEntity.BeikaoEntity.DataEntity item :mBean.getDatas().getBeikao().getData()){
						BeiKaoInfo info = new BeiKaoInfo();
						info.name = item.getName();
						info.type = item.getType();
						info.countNumber = item.getCountNumber();
						info.needNumber = item.getNeedNumber();
						info.credit = item.getCredit();
						info.period = item.getPeriod();
						info.status = item.getStatus();
						info.nowNumber = item.getNowNumber();
						mBeiKaoList.add(info);
					}
				}

				if(mBean.getDatas().getOther() != null){
					for(TaskBean.DatasEntity.OtherEntity.DataEntity item : mBean.getDatas().getOther().getData()){
						OtherInfo info = new OtherInfo();
						info.name = item.getName();
						info.type= item.getType();
						info.countNumber = item.getCountNumber();
						info.needNumber = item.getNeedNumber();
						info.credit = item.getCredit();
						info.period = item.getPeriod();
						info.status = item.getStatus();
						info.nowNumber = item.getNowNumber();
						info.stage = item.getStage();
						info.lastTime = item.getLastTime();
						mOtherList.add(info);
					}
				}

				mAdapter.notifyDataSetChanged();
				beiKaoAdapter.notifyDataSetChanged();
				otherAdapter.notifyDataSetChanged();
			}else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
	};

	private class TopicInfo{
		String name;
		String type;
		int countNumber;
		int needNumber;
		int credit;
		int period;
		int status;
		int nowNumber;
	}
	private class BeiKaoInfo{
		String name;
		String type;
		int countNumber;
		int needNumber;
		int credit;
		int period;
		int status;
		int nowNumber;
	}

	private class OtherInfo{
		String name;
		String type;
		int countNumber;
		int needNumber;
		int credit;
		int period;
		int status;
		int nowNumber;
		int stage;
		int lastTime;
	}


	private void initView() {

		mSpokenSquare = (TextView)findViewById(R.id.mytask_spokensquare);
		mBeikaoTask = (TextView)findViewById(R.id.mytask_beikao_task);
		mOtherEntity = (TextView)findViewById(R.id.mytask_otherentity);

		mAdapter = new SquareAdapter();
		myTaskSquareListView = (MyTaskListView)findViewById(R.id.mytask_square_listview);
		myTaskSquareListView.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(myTaskSquareListView);

		beiKaoAdapter = new BeiKaoAdapter();
		myTaskBKListView = (MyTaskListView)findViewById(R.id.mytask_beikao_listview);
		myTaskBKListView.setAdapter(beiKaoAdapter);
		setListViewHeightBasedOnChildren(myTaskBKListView);

		otherAdapter = new OtherAdapter();
		myTaskOTListView = (MyTaskListView)findViewById(R.id.mytask_other_listview);
		myTaskOTListView.setAdapter(otherAdapter);
		setListViewHeightBasedOnChildren(myTaskOTListView);

	}

	private class OtherAdapter extends  BaseAdapter{

		@Override
		public int getCount() {
			return mOtherList.size();
		}
		@Override
		public Object getItem(int position) {
			return mOtherList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final OTViewHolder viewHolder;
			if(convertView == null){
				viewHolder = new OTViewHolder();
				convertView = mInflater.inflate(R.layout.mytask_item, null);
				viewHolder.mTitleName = (TextView) convertView.findViewById(R.id.mytask_title);
				viewHolder.mItemCredit = (TextView) convertView.findViewById(R.id.mytask_mark);
				viewHolder.mNowNumber = (TextView) convertView.findViewById(R.id.mytask_scale);
				viewHolder.mStatus = (TextView) convertView.findViewById(R.id.mytask_unfinish);
				viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.mytask_left_img);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (OTViewHolder) convertView.getTag();
			}
			viewHolder.mTitleName.setText(mOtherList.get(position).name);
			viewHolder.mItemCredit.setText("+"+mOtherList.get(position).credit+"");
			viewHolder.mNowNumber.setText(mOtherList.get(position).nowNumber+"/"+mOtherList.get(position).countNumber+"");
			if(mOtherList.get(position).status == 0){
				viewHolder.mStatus.setText(R.string.mytask_unfinish);
			}else{
				viewHolder.mStatus.setText(R.string.mytask_finish);
				viewHolder.mStatus.setBackgroundResource(R.drawable.bg_task_item_finish);
			}
			if(position == 0){
				viewHolder.itemIcon.setImageResource(R.drawable.mytask_icon_frend);
			}else if(position == 1){
				viewHolder.itemIcon.setImageResource(R.drawable.mytask_icon_data);

			}else{
				viewHolder.itemIcon.setImageResource(R.drawable.mytask_icon_phone);
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mOtherList.get(position).name.equals("手机绑定")){
						StatisticUtils.onEvent("我的任务", "其他任务", "手机绑定");
						Intent phoneIntent = new Intent(getApplicationContext(), ModifyPhoneActivity.class);
						startActivity(phoneIntent);
					}else if(mOtherList.get(position).name.equals("填写考试日期")){
						StatisticUtils.onEvent("我的任务", "其他任务", "填写考试日期");
						Intent homePageIntent = new Intent(getApplicationContext(), HomePageActivity.class);
						homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						homePageIntent.putExtra(HomePageActivity.EXT_KEY_SHOW_HOME_PAGE, true);
						startActivity(homePageIntent);
					}else{
						StatisticUtils.onEvent("我的任务", "其他任务", "积累5个好友");
						startActivity(new Intent(getApplicationContext(), SpokenSquareActivity.class));
					}
				}
			});
			return convertView;
		}
	}

	private class OTViewHolder {
		TextView mTitleName ;
		ImageView itemIcon ;
		TextView mItemCredit ;
		TextView mNowNumber ;
		TextView mStatus ;
	}

	private class BeiKaoAdapter extends  BaseAdapter{

		@Override
		public int getCount() {
			return mBeiKaoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBeiKaoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			BKViewHolder viewHolder;
			if(convertView == null){
				viewHolder = new BKViewHolder();
				convertView = mInflater.inflate(R.layout.mytask_item, null);
				viewHolder.mTitleName = (TextView) convertView.findViewById(R.id.mytask_title);
				viewHolder.mItemCredit = (TextView) convertView.findViewById(R.id.mytask_mark);
				viewHolder.mNowNumber = (TextView) convertView.findViewById(R.id.mytask_scale);
				viewHolder.mStatus = (TextView) convertView.findViewById(R.id.mytask_unfinish);
				viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.mytask_left_img);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (BKViewHolder) convertView.getTag();
			}

			viewHolder.mTitleName.setText(mBeiKaoList.get(position).name);
			viewHolder.mItemCredit.setText("+"+mBeiKaoList.get(position).credit+"");
			viewHolder.mNowNumber.setText(mBeiKaoList.get(position).nowNumber+"/"+mBeiKaoList.get(position).countNumber+"");
			if(mBeiKaoList.get(position).status == 0){
				viewHolder.mStatus.setText(R.string.mytask_unfinish);
			}else{
				viewHolder.mStatus.setText(R.string.mytask_finish);
				viewHolder.mStatus.setBackgroundResource(R.drawable.bg_task_item_finish);
			}
			if(position == 0){
				viewHolder.itemIcon.setImageResource(R.drawable.icon_speaking);
				viewHolder.tag = ItemType.SPEAKING ;
			}else if(position == 1){
				viewHolder.itemIcon.setImageResource(R.drawable.icon_practice);
				viewHolder.tag = ItemType.PRACTICE;
			}else{
				viewHolder.itemIcon.setImageResource(R.drawable.icon_tpo);
				viewHolder.tag = ItemType.TPO ;
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					BKViewHolder viewHolder1 = (BKViewHolder) v.getTag();
					switch (viewHolder1.tag){
						case SPEAKING:
							StatisticUtils.onEvent("我的任务", "备考任务", "口语练习");
							startActivity(new Intent(getApplicationContext(), SpeakingMainActivity.class));
							break;
						case PRACTICE:
							StatisticUtils.onEvent("我的任务", "备考任务", "口语跟读");
							startActivity(new Intent(getApplicationContext(), PracticeListActivity.class));
							break;
						case TPO:
							StatisticUtils.onEvent("我的任务", "备考任务", "口语模考");
							startActivity(new Intent(getApplicationContext(), TPOListActivity.class));
							break;
					}
				}
			});
			return convertView;
		}
	}

	private class BKViewHolder{
		TextView mTitleName ;
		ImageView itemIcon ;
		TextView mItemCredit ;
		TextView mNowNumber ;
		TextView mStatus ;
		ItemType tag ;
	}


	private class SquareAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mTopList.size();
		}

		@Override
		public Object getItem(int position) {
			return mTopList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.mytask_item, null);
				viewHolder.mTitleName = (TextView) convertView.findViewById(R.id.mytask_title);
				viewHolder.mItemCredit = (TextView) convertView.findViewById(R.id.mytask_mark);
				viewHolder.mNowNumber = (TextView) convertView.findViewById(R.id.mytask_scale);
				viewHolder.mStatus = (TextView) convertView.findViewById(R.id.mytask_unfinish);
				viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.mytask_left_img);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mTitleName.setText(mTopList.get(position).name);
			viewHolder.mItemCredit.setText("+"+mTopList.get(position).credit+"");
			viewHolder.mNowNumber.setText(mTopList.get(position).nowNumber+"/"+mTopList.get(position).countNumber+"");
			if(mTopList.get(position).status == 0){
				viewHolder.mStatus.setText(R.string.mytask_unfinish);
			}else{
				viewHolder.mStatus.setText(R.string.mytask_finish);
				viewHolder.mStatus.setBackgroundResource(R.drawable.bg_task_item_finish);
			}
			viewHolder.itemIcon.setImageResource(R.drawable.mytask_spoken_square);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (position == 0) {
						StatisticUtils.onEvent("我的任务", "口语广场","口语广场答题");
						startActivity(new Intent(getApplicationContext(), SpokenSquareActivity.class));
					} else {
						StatisticUtils.onEvent("我的任务", "口语广场","口语广场评分");
						Intent intent = new Intent(getApplicationContext(), SpokenSquareActivity.class);
						intent.putExtra(SpokenSquareActivity.EXT_KEY_SHOW_TODAY,false);
						startActivity(intent);
					}
				}
			});

			return convertView;
		}
	}

	private class ViewHolder{
		TextView mTitleName ;
		ImageView itemIcon ;
		TextView mItemCredit ;
		TextView mNowNumber ;
		TextView mStatus ;
	}

	private void setListViewHeightBasedOnChildren(MyTaskListView myTaskSquareListView) {
		ListAdapter listAdapter = myTaskSquareListView.getAdapter();
		if(listAdapter == null){
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, myTaskSquareListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = myTaskSquareListView.getLayoutParams();
		params.height = totalHeight
				+ (myTaskSquareListView.getDividerHeight() * (listAdapter.getCount() - 1));
		myTaskSquareListView.setLayoutParams(params);
	}

	private void showProgressDialog(){
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
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
