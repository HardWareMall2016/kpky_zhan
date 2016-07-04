package com.zhan.kykp.widget;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.DailBean;
import com.zhan.kykp.network.bean.UserSignBean;
import com.zhan.kykp.util.ShareUtil;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DailyView extends LinearLayout  {

	private TextView mTVSentence;
	private TextView mTVAuthor;
	private TextView mCnsentence;
	private TextView mDay,mWeek,mMouth,mYear;
	private TextView mMeeting ;

	private String showDate;
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;
	private RequestHandle mUserSingnRequestHandle;
	private DailBean mBean ;
	private UserSignBean mUsersignBean ;

	private Typeface mTypeface;

	private Context mContext ;

	public DailyView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.mContext = context;

		LayoutInflater inflater = LayoutInflater.from(context);
		View content = inflater.inflate(R.layout.daily_view_content, this);
		
		mTypeface = Typeface.createFromAsset(context.getAssets(), "timesi.ttf");

		mDay = (TextView) content.findViewById(R.id.day);
		mWeek = (TextView) content.findViewById(R.id.week);
		mMouth = (TextView) content.findViewById(R.id.mouth);
		mYear = (TextView) content.findViewById(R.id.year);
		mMeeting = (TextView) content.findViewById(R.id.meeting);
		mMeeting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StatisticUtils.onEvent("首页", "签到");
				if(mMeeting.getText().toString().equals("")){
					Utils.toast(R.string.home_page_signed);
				}else{
					mHttpRequest = new BaseHttpRequest();
					RequestParams requestParams=new RequestParams();
					requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
					mUserSingnRequestHandle = mHttpRequest.startRequest(mContext, ApiUrls.USER_USERSIGN, requestParams, usersingrequestCallback, BaseHttpRequest.RequestType.POST);
				}
			}
		});


		
		mTVSentence = (TextView) content.findViewById(R.id.sentence);
		mTVSentence.setTypeface(mTypeface);
		
		mCnsentence = (TextView) content.findViewById(R.id.cnsentence);
		mTVAuthor = (TextView) content.findViewById(R.id.author);
		showDate = Utils.getDateFormateStr(System.currentTimeMillis());
		
		mWeek.setText(Utils.getDateFormateStrWeek(System.currentTimeMillis()));
		mDay.setText(Utils.getDateFormateStrDay(System.currentTimeMillis()));
		mMouth.setText(Utils.getDateFormateStrMouth(System.currentTimeMillis()));
		mYear.setText(Utils.getDateFormateStrYear(System.currentTimeMillis()));

		if (TextUtils.isEmpty(ShareUtil.getValue(getContext(), ShareUtil.EN_SENTENCE))) {
			mTVSentence.setText(context.getString(R.string.home_page_def_en_sentence));
			mCnsentence.setText(context.getString(R.string.home_page_def_ch_sentence));
			mTVAuthor.setText("--"+context.getString(R.string.home_page_def_sentence_author));
		} else {
			mTVSentence.setText(ShareUtil.getValue(getContext(), ShareUtil.EN_SENTENCE));
			mCnsentence.setText(ShareUtil.getValue(getContext(), ShareUtil.CH_SENTENCE));
			mTVAuthor.setText("--"+ShareUtil.getValue(getContext(), ShareUtil.AUTHOR));
		}

		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		mRequestHandle = mHttpRequest.startRequest(context, ApiUrls.PROVERB_GETPROVERB, requestParams, requestCallback, BaseHttpRequest.RequestType.GET);
	}

	/*
	判断是否已经签到
	 */
	public void setIsSigned(boolean isSigned){
		if(isSigned){
			mMeeting.setVisibility(View.VISIBLE);
			mMeeting.setText("");
			//mMeeting.setTextColor(Color.rgb(252, 87, 125));
			//mMeeting.setBackgroundResource(R.drawable.bg_red_border);
			mMeeting.setBackgroundResource(R.drawable.bg_qiandao);
		}else{
			mMeeting.setVisibility(View.VISIBLE);
			mMeeting.setText(R.string.home_page_meeting);
			mMeeting.setBackgroundResource(R.drawable.bg_white_border);
		}
	}
	private HttpRequestCallback usersingrequestCallback = new HttpRequestCallback(){

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String content) {
			mUsersignBean = Utils.parseJson(content,UserSignBean.class);
			if(mUsersignBean != null){
				if(mUsersignBean.getStatus() == 1){
					//mMeeting.setText(R.string.home_page_signed);
					//mMeeting.setTextColor(Color.rgb(252, 87, 125));
					//mMeeting.setBackgroundResource(R.drawable.bg_red_border);
					mMeeting.setText("");
					mMeeting.setBackgroundResource(R.drawable.bg_qiandao);
					Utils.toast("已经连续签到"+mUsersignBean.getDatas().getDays()+"天啦 ！\n获得"+mUsersignBean.getDatas().getCredit()+"奖学金！\n继续努力哟！");
				}
			}else {
				Utils.toast(R.string.network_query_data_failed);
			}
		}
	};

	private HttpRequestCallback requestCallback = new HttpRequestCallback(){
		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}
		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String content) {
			mBean = Utils.parseJson(content,DailBean.class);
			if(mBean != null){

				ShareUtil.setValue(getContext(), ShareUtil.EN_SENTENCE, mBean.getDatas().getContent());
				ShareUtil.setValue(getContext(), ShareUtil.CH_SENTENCE, mBean.getDatas().getContentCN());
				ShareUtil.setValue(getContext(), ShareUtil.AUTHOR, mBean.getDatas().getFrom());

				mTVSentence.setText(ShareUtil.getValue(getContext(), ShareUtil.EN_SENTENCE));
				mCnsentence.setText(ShareUtil.getValue(getContext(), ShareUtil.CH_SENTENCE));
				mTVAuthor.setText("--"+ShareUtil.getValue(getContext(), ShareUtil.AUTHOR));
			}
		}
	};

	@Override
	protected void onDetachedFromWindow() {
		if(mRequestHandle!=null && !mRequestHandle.isFinished()){
			mRequestHandle.cancel(true);
		}
		if(mUserSingnRequestHandle != null && !mUserSingnRequestHandle.isFinished()){
			mUserSingnRequestHandle.cancel(true);
			mMeeting.setVisibility(View.GONE);
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onAttachedToWindow() {
		if(UserInfo.getCurrentUser() == null){
			mMeeting.setVisibility(View.GONE);
			return;
		}else{
			mMeeting.setVisibility(View.VISIBLE);
		}
		super.onAttachedToWindow();
	}

}
