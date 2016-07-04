package com.zhan.kykp.userCenter;

import java.util.List;

import android.app.Dialog;
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

import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

public class ForgetPasswordActivity extends BaseActivity implements OnClickListener {
	private static final String TAG=ForgetPasswordActivity.class.getSimpleName();
	// Views
	private EditText mEditPhoneNumber;
	private EditText mEditSmsCode;
	private EditText mEditPasswaord;
	private EditText mEditRePasswaord;
	private Button mBtnSendSmsCode;

	//
	private String mPhoneNumber;
	private String mStrPassword;
	private ChangePwdHandler mHandler;
	private Dialog mProgressDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

		initView();

		mHandler=new ChangePwdHandler();
	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(ChangePwdHandler.MSG_REACH_ONE_SECOND);
		super.onDestroy();
	}

	private void initView() {
		mEditPhoneNumber = (EditText) findViewById(R.id.phone_number);
		mEditSmsCode = (EditText) findViewById(R.id.sms_code);

		mEditPasswaord = (EditText) findViewById(R.id.passwd);
		mEditRePasswaord = (EditText) findViewById(R.id.re_passwd);

		mBtnSendSmsCode = (Button) findViewById(R.id.btn_get_sms_code);
		mBtnSendSmsCode.setOnClickListener(this);

		findViewById(R.id.btn_finish).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// 检查网络连接
		if (!Connectivity.isConnected(this)) {
			Utils.toast(R.string.network_disconnect);
			return;
		}
		// 验证手机号
		if (!checkPhoneNumber()) {
			return;
		}

		switch (arg0.getId()) {
			case R.id.btn_get_sms_code:
				mBtnSendSmsCode.setEnabled(false);
				requestSMSCode();
				break;
			case R.id.btn_finish:
				if(checkSmsCode()&&checkPassword()){
					verifyCode();
				}
				break;
		}
	}
	private void requestSMSCode() {
		RequestParams requestParams = new RequestParams();
		//requestParams.put("usr", UserInfo.getCurrentUser().getObjectId());
		requestParams.put("mobile", mPhoneNumber);
		new BaseHttpRequest().startRequest(ForgetPasswordActivity.this, ApiUrls.USER_SEND_FORGET_CODE, requestParams, new HttpRequestCallback() {
			@Override
			public void onRequestFailed(String errorMsg) {
				mHandler.removeMessages(ChangePwdHandler.MSG_REACH_ONE_SECOND);
				Utils.toast(errorMsg);
			}

			@Override
			public void onRequestFailedNoNetwork() {

			}

			@Override
			public void onRequestCanceled() {

			}

			@Override
			public void onRequestSucceeded(String content) {
				BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
				if (baseBean.getStatus() == 1) {
					mHandler.sendEmptyMessage(ChangePwdHandler.MSG_START_TIMER);
				} else {
					mBtnSendSmsCode.setEnabled(true);
					Utils.toast(baseBean.getMessage());
				}

			}
		}, BaseHttpRequest.RequestType.POST);
	}

	private class ChangePwdHandler extends Handler {
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
					}else{
						String formatStr= getString(R.string.re_get_time_left);
						mBtnSendSmsCode.setText(String.format(formatStr, mReVerifyLeftSecond));
						sendEmptyMessageDelayed(MSG_REACH_ONE_SECOND, 1000);
						mBtnSendSmsCode.setBackgroundResource(R.drawable.bg_dark_grey_rounded);
						mBtnSendSmsCode.setTextColor(Color.rgb(102, 102, 102));
					}
					break;
			}
		}
	}

	private void showProgressDialog(){
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.submitting));
		mProgressDlg.setCancelable(false);
		mProgressDlg.show();
	}

	private void closeDialog(){
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}

	private void verifyCode() {
		String smsCode = mEditSmsCode.getText().toString();
		showProgressDialog();

		RequestParams requestParams = new RequestParams();
		requestParams.put("mobile",mEditPhoneNumber.getText().toString().trim());
		requestParams.put("password",mEditPasswaord.getText().toString().trim());
		requestParams.put("verifyCode",mEditSmsCode.getText().toString().trim());
		new BaseHttpRequest().startRequest(ForgetPasswordActivity.this, ApiUrls.USER_FORGET_PASSWORD, requestParams, new HttpRequestCallback() {
			@Override
			public void onRequestFailed(String errorMsg) {
				Utils.toast(errorMsg);
			}

			@Override
			public void onRequestFailedNoNetwork() {
				closeDialog();

			}

			@Override
			public void onRequestCanceled() {

			}

			@Override
			public void onRequestSucceeded(String content) {
				BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
				if (baseBean.getStatus() == 1) {
					Utils.toast(R.string.set_new_pwd_success);
					finish();
				} else {
					Utils.toast(baseBean.getMessage());
					closeDialog();
				}

			}
		}, BaseHttpRequest.RequestType.POST);

	}


	private boolean checkPhoneNumber() {
		mPhoneNumber = mEditPhoneNumber.getText().toString();
		if (TextUtils.isEmpty(mPhoneNumber)) {
			Utils.toast(R.string.phone_number_empty);
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

	private boolean checkPassword(){
		mStrPassword=mEditPasswaord.getText().toString();

		String rePwd=mEditRePasswaord.getText().toString();

		if(TextUtils.isEmpty(mStrPassword)){
			Utils.toast(R.string.pwd_empty);
			return false;
		}

		if(TextUtils.isEmpty(rePwd)){
			Utils.toast(R.string.re_input_new_pwd);
			return false;
		}

		if(mStrPassword.length()<6||mStrPassword.length()>16){
			Utils.toast(R.string.pwd_length_error);
			return false;
		}

		if(!mStrPassword.equals(rePwd)){
			Utils.toast(R.string.re_pwd_not_equal);
			return false;
		}

		return true;

	}

}