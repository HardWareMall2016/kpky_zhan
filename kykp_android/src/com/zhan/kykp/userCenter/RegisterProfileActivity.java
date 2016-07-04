package com.zhan.kykp.userCenter;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.HomePageActivity;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.LoginResult;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class RegisterProfileActivity extends BaseActivity implements OnClickListener {
    public final static String EXTRA_KEY_PHONE_NUMBER = "phone_number";

    private EditText mEditNickname;
    private EditText mEditPasswaord;
    private EditText mEditRePasswaord;

    private String mPhoneNumber;
    private String mStrNickName;
    private String mStrPassword;

    private Dialog mProgressDlg;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRegisterHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);
        if (!checkPhoneNumber()) {
            finish();
            return;
        }
        mHttpRequest = new BaseHttpRequest();
        initView();
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mRegisterHandle);
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_finish:
                if (checkNickname() && checkPassword()) {
                    register();
                }
                break;
        }
    }

    private void initView() {
        mEditNickname = (EditText) findViewById(R.id.nickname);
        mEditPasswaord = (EditText) findViewById(R.id.password);
        mEditRePasswaord = (EditText) findViewById(R.id.re_password);
        findViewById(R.id.btn_finish).setOnClickListener(this);
    }

    private void register() {
        if(mRegisterHandle!=null&&!mRegisterHandle.isFinished()){
            return;
        }

        showProgressDialog();

        RequestParams requestParams = new RequestParams();
        requestParams.put("nickname", mStrNickName);
        requestParams.put("password", mStrPassword);
        requestParams.put("mobile", mPhoneNumber);

        mRegisterHandle = mHttpRequest.startRequest(this, ApiUrls.USER_REGISTER, requestParams, new HttpRequestCallback() {
            @Override
            public void onRequestFailed(String errorMsg) {
                closeDialog();
                Utils.toast(errorMsg);
            }

            @Override
            public void onRequestFailedNoNetwork() {
                closeDialog();
                Utils.toast(R.string.network_disconnect);
            }

            @Override
            public void onRequestCanceled() {
                closeDialog();
            }

            @Override
            public void onRequestSucceeded(String content) {
                closeDialog();
                BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
                if (baseBean.getStatus() == 0) {
                    Utils.toast(baseBean.getMessage());
                } else {
                    LoginResult result = Utils.parseJson(content, LoginResult.class);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setObjectId(result.getDatas().getUserInfo().getObjectId());
                    userInfo.setFollower(result.getDatas().getUserInfo().getFollower());
                    userInfo.setNickname(result.getDatas().getUserInfo().getNickname());
                    userInfo.setUsername(result.getDatas().getUserInfo().getUsername());
                    userInfo.setApplicationLimit(result.getDatas().getUserInfo().getApplicationLimit());
                    userInfo.setEmailVerified(result.getDatas().getUserInfo().getEmailVerified());
                    userInfo.setMobilePhoneNumber(result.getDatas().getUserInfo().getMobilePhoneNumber());
                    userInfo.setAvatar(result.getDatas().getUserInfo().getAvatar());
                    userInfo.setFollowee(result.getDatas().getUserInfo().getFollowee());
                    userInfo.setAuthData(result.getDatas().getUserInfo().getAuthData());
                    userInfo.setMobilePhoneVerified(result.getDatas().getUserInfo().getMobilePhoneVerified());
                    userInfo.setUserType(result.getDatas().getUserInfo().getIsBBSUser());
                    userInfo.setCredit(result.getDatas().getUserInfo().getCredit());
                    userInfo.setLevel(result.getDatas().getUserInfo().getLevel());
                    userInfo.setCurrentIntegral(result.getDatas().getUserInfo().getCurrent_Integral());
                    userInfo.setMaximumIntegral(result.getDatas().getUserInfo().getMaximum_Integral());
                    userInfo.setAge(result.getDatas().getUserInfo().getAge()+"");
                    userInfo.setSex(result.getDatas().getUserInfo().getSex());
                    UserInfo.saveLoginUserInfo(userInfo);

                    Intent homePageIntent = new Intent(RegisterProfileActivity.this, HomePageActivity.class);
                    homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homePageIntent);
                    Utils.toast(R.string.register_success);
                }
            }
        }, BaseHttpRequest.RequestType.POST);
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.registering));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    private boolean checkPhoneNumber() {
        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra(EXTRA_KEY_PHONE_NUMBER);
        if (TextUtils.isEmpty(mPhoneNumber)) {
            Utils.toast(R.string.phone_number_empty);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkNickname() {
        mStrNickName = mEditNickname.getText().toString().trim();
        if (TextUtils.isEmpty(mStrNickName)) {
            Utils.toast(R.string.input_nickname);
            return false;
        } else if (mStrNickName.length() > 12) {
            Utils.toast(R.string.nick_name_too_long);
            return false;
        }
        return true;
    }

    private boolean checkPassword() {
        mStrPassword = mEditPasswaord.getText().toString();

        String rePwd = mEditRePasswaord.getText().toString();

        if (TextUtils.isEmpty(mStrPassword)) {
            Utils.toast(R.string.pwd_empty);
            return false;
        }

        if (TextUtils.isEmpty(rePwd)) {
            Utils.toast(R.string.reinput_pwd);
            return false;
        }

        if (mStrPassword.length() < 6 || mStrPassword.length() > 16) {
            Utils.toast(R.string.pwd_length_error);
            return false;
        }

        if (!mStrPassword.equals(rePwd)) {
            Utils.toast(R.string.re_pwd_not_equal);
            return false;
        }
        return true;
    }
}
