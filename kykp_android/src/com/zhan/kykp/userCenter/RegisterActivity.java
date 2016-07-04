package com.zhan.kykp.userCenter;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = RegisterActivity.class.getSimpleName();

	private Button mBtnSendSmsCode;
	private EditText mEditPhoneNumber;
	private EditText mEditSmsCode;

	private String mPhoneNumber;

	private RegisterHandler mHandler;

	//Network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mRequestSMSCodeHandler;
	private RequestHandle mVerifyCodeHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		mHttpRequest=new BaseHttpRequest();

		mHandler = new RegisterHandler();

		mEditPhoneNumber = (EditText) findViewById(R.id.phone_number);
		mEditSmsCode = (EditText) findViewById(R.id.sms_code);

		mBtnSendSmsCode = (Button) findViewById(R.id.btn_get_sms_code);
		mBtnSendSmsCode.setOnClickListener(this);

		View btnNext = findViewById(R.id.btn_next);
		btnNext.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		BaseHttpRequest.releaseRequest(mRequestSMSCodeHandler);
		BaseHttpRequest.releaseRequest(mVerifyCodeHandler);
		mHandler.removeMessages(RegisterHandler.MSG_REACH_ONE_SECOND);
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		if (!Connectivity.isConnected(this)) {
			Utils.toast(R.string.network_disconnect);
			return;
		}
		switch (arg0.getId()) {
		case R.id.btn_get_sms_code:
			if (!checkPhoneNumber()) {
				return;
			}
			requestSMSCode(mPhoneNumber);
			break;
		case R.id.btn_next:
			if (checkPhoneNumber() && checkSmsCode()) {
				verifyCode();
			}
			break;
		}
	}

	private void requestSMSCode(String phoneNumber) {

		if(mRequestSMSCodeHandler!=null&&!mRequestSMSCodeHandler.isFinished()){
			return;
		}

		RequestParams requestParams=new RequestParams();
		requestParams.put("mobile", phoneNumber);

		mHandler.sendEmptyMessage(RegisterHandler.MSG_START_TIMER);
		mRequestSMSCodeHandler=mHttpRequest.startRequest(this, ApiUrls.USER_SEND_MOBILE_CODE,requestParams,new HttpRequestCallback(){
			@Override
			public void onRequestFailed(String errorMsg) {
				Utils.toast(errorMsg);
				refreshSendStatus();
			}

			@Override
			public void onRequestFailedNoNetwork() {
				refreshSendStatus();
			}

			@Override
			public void onRequestCanceled() {
				refreshSendStatus();
			}

			@Override
			public void onRequestSucceeded(String content) {
				BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
				if(baseBean.getStatus()==0){
					refreshSendStatus();
					Utils.toast(baseBean.getMessage());
				}
			}
		}, BaseHttpRequest.RequestType.POST);
	}

	private void refreshSendStatus(){
		mBtnSendSmsCode.setEnabled(true);
		mBtnSendSmsCode.setText(R.string.get_sms_code);
		mHandler.removeMessages(RegisterHandler.MSG_REACH_ONE_SECOND);
	}


	private void verifyCode() {
		BaseHttpRequest.releaseRequest(mVerifyCodeHandler);

		String code = mEditSmsCode.getText().toString();
		RequestParams requestParams=new RequestParams();
		requestParams.put("mobile", mPhoneNumber);
		requestParams.put("verifyCode", code);

		mVerifyCodeHandler=mHttpRequest.startRequest(this, ApiUrls.USER_VERIFY_CODE,requestParams,new HttpRequestCallback(){
			@Override
			public void onRequestFailed(String errorMsg) {
				Utils.toast(errorMsg);

			}

			@Override
			public void onRequestFailedNoNetwork() {
				Utils.toast(R.string.network_disconnect);
			}

			@Override
			public void onRequestCanceled() {

			}

			@Override
			public void onRequestSucceeded(String content) {
				BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
				if(baseBean.getStatus()==0){
					refreshSendStatus();
					Utils.toast(baseBean.getMessage());
				}else{
					Intent registerProfileIntent = new Intent(RegisterActivity.this, RegisterProfileActivity.class);
					registerProfileIntent.putExtra(RegisterProfileActivity.EXTRA_KEY_PHONE_NUMBER, mPhoneNumber);
					startActivity(registerProfileIntent);
				}
			}
		}, BaseHttpRequest.RequestType.POST);
	}

	private class RegisterHandler extends Handler {
		public final static int MSG_START_TIMER = 1000;
		public final static int MSG_REACH_ONE_SECOND = 1001;
		public int mReVerifyLeftSecond = 60;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_TIMER:
				mReVerifyLeftSecond = 60;
				mBtnSendSmsCode.setEnabled(false);
				sendEmptyMessageDelayed(MSG_REACH_ONE_SECOND, 1000);
				break;
			case MSG_REACH_ONE_SECOND:
				mReVerifyLeftSecond--;
				if (mReVerifyLeftSecond == 0) {
					mBtnSendSmsCode.setEnabled(true);
					mBtnSendSmsCode.setText(R.string.get_sms_code);
					mBtnSendSmsCode.setBackgroundResource(R.drawable.bg_cyan_rounded);
					mBtnSendSmsCode.setTextColor(getResources().getColorStateList(R.drawable.text_color_white_selector));
				} else {
					String formatStr = getString(R.string.re_get_time_left);
					mBtnSendSmsCode.setText(String.format(formatStr, mReVerifyLeftSecond));
					sendEmptyMessageDelayed(MSG_REACH_ONE_SECOND, 1000);
					mBtnSendSmsCode.setBackgroundResource(R.drawable.bg_dark_grey_rounded);
					mBtnSendSmsCode.setTextColor(Color.rgb(102, 102, 102)); 
				}
				break;
			}
		}
	}

	private boolean checkPhoneNumber() {
		mPhoneNumber = mEditPhoneNumber.getText().toString();
		Log.i(TAG, "mPhoneNumber = " + mPhoneNumber);
		if (TextUtils.isEmpty(mPhoneNumber)) {
			Utils.toast(R.string.phone_number_empty);
			Log.i(TAG, "mPhoneNumber is empty");
			return false;
		}

		if (!Utils.checkMobilePhoneNumber(mPhoneNumber)) {
			Utils.toast(R.string.phone_number_invalid);
			return false;
		}
		return true;
	}

	private boolean checkSmsCode() {
		String pwd = mEditSmsCode.getText().toString();
		if (TextUtils.isEmpty(pwd)) {
			Utils.toast(R.string.input_sms_code);
			return false;
		}
		if (pwd.length()!=6) {
			Utils.toast(R.string.check_sms_code);
			return false;
		}
		return true;
	}
}
