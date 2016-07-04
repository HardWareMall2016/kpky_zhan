package com.zhan.kykp.userCenter;

import com.zhan.kykp.base.BaseActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class ModifyPhoneActivity extends BaseActivity implements View.OnClickListener {

	private Button mBtnSendSmsCode;
	private EditText mEditPhoneNumber;
	private EditText mEditSmsCode;
	private ImageView mImgAvatar;

	private String mPhoneNumber;

	private RegisterHandler mHandler;
	private UserInfo mUser;
	//network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mSendCodeHandle;
	private RequestHandle mModifyPhoneNumberHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_phone);

		mHttpRequest=new BaseHttpRequest();

		mUser = UserInfo.getCurrentUser();

		if(TextUtils.isEmpty(mUser.getMobilePhoneNumber())){
			setTitle(R.string.user_info_binding_phone);
		}else{
			setTitle(R.string.title_activity_modify_phone);
		}

		mHandler = new RegisterHandler();

		mEditPhoneNumber = (EditText) findViewById(R.id.phone_number);
		mEditSmsCode = (EditText) findViewById(R.id.sms_code);

		mBtnSendSmsCode = (Button) findViewById(R.id.btn_get_sms_code);
		mBtnSendSmsCode.setOnClickListener(this);

		View btnNext = findViewById(R.id.btn_next);
		btnNext.setOnClickListener(this);

		mImgAvatar = (ImageView) findViewById(R.id.avatar);
		setAvatar();
	}

	@Override
	protected void onDestroy() {
		BaseHttpRequest.releaseRequest(mSendCodeHandle);
		BaseHttpRequest.releaseRequest(mModifyPhoneNumberHandle);

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
				StatisticUtils.onEvent(R.string.title_activity_modify_phone, R.string.get_sms_code);
				if (!checkPhoneNumber()) {
					return;
				}
				requestSMSCode();
				break;
			case R.id.btn_next:
				StatisticUtils.onEvent(R.string.title_activity_modify_phone, R.string.finish);
				if (checkPhoneNumber() && checkSmsCode()) {
					modifyPhone();
				}
				break;
		}
	}

	private void requestSMSCode() {
		RequestParams requestParams = new RequestParams();
		requestParams.put("mobile", mPhoneNumber);
		mSendCodeHandle=mHttpRequest.startRequest(ModifyPhoneActivity.this, ApiUrls.USER_SEND_MOBILE_CODE, requestParams, new HttpRequestCallback() {
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
				BaseBean bean=Utils.parseJson(content, BaseBean.class);
				if(bean.getStatus()==1){
					mHandler.sendEmptyMessage(RegisterHandler.MSG_START_TIMER);
				}else{
					Utils.toast(bean.getMessage());
				}
			}
		}, BaseHttpRequest.RequestType.POST);
	}

	/**
     * 修改验证!!
     */
    private void modifyPhone() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("mobile",mEditPhoneNumber.getText().toString().trim());
        requestParams.put("verifyCode",mEditSmsCode.getText().toString().trim());
		mModifyPhoneNumberHandle=mHttpRequest.startRequest(ModifyPhoneActivity.this, ApiUrls.USER_UPDATE_MOBILE, requestParams, new HttpRequestCallback() {
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
				BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
				if (baseBean.getStatus() == 1) {
					if(TextUtils.isEmpty(mUser.getMobilePhoneNumber())){
						Utils.toast(R.string.binding_phone_success);
					}else{
						Utils.toast(R.string.modify_phone_success);
					}
					//保存手机号到本地
					mUser.setMobilePhoneNumber(mEditPhoneNumber.getText().toString().trim());
					UserInfo.saveLoginUserInfo(mUser);

					finish();
				} else {
					Utils.toast(baseBean.getMessage());
				}
			}
		}, BaseHttpRequest.RequestType.POST);
    }

	private void setAvatar() {
		UserInfo user = UserInfo.getCurrentUser();
		String avatarPath = user.getAvatar();
		ImageLoader.getInstance().displayImage(avatarPath, mImgAvatar,
				PhotoUtils.buldDisplayImageOptionsForAvatar());
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
		// Log.i(TAG, "mPhoneNumber = "+mPhoneNumber);
		if (TextUtils.isEmpty(mPhoneNumber)) {
			Utils.toast(R.string.phone_number_empty);
			// Log.i(TAG, "mPhoneNumber is empty");
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
