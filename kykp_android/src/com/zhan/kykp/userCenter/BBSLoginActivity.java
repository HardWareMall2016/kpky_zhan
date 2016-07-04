package com.zhan.kykp.userCenter;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


import com.loopj.android.http.AsyncHttpResponseHandler;

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
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.HttpClientUtils;
import com.zhan.kykp.util.Utils;

public class BBSLoginActivity extends BaseActivity implements OnClickListener, HttpRequestCallback {
    private final static String TAG = "BBSLoginActivity";

    private final static String BBS_URL = "http://toefl.api.tpooo.com/user/login";

    private EditText mEditUserName;
    private EditText mEditPwd;
    private Dialog mProgressDlg;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mThirdLoginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_login);

        mHttpRequest = new BaseHttpRequest();

        mEditUserName = (EditText) findViewById(R.id.user_name);
        mEditPwd = (EditText) findViewById(R.id.passwd);

        View btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mThirdLoginHandler);
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_login:
                if (checkUserName() && checkPwd()) {
                    String userName = mEditUserName.getText().toString();
                    String pwd = mEditPwd.getText().toString();

                    checkWithBBS(userName, pwd);
                }
                break;
        }
    }

    private boolean checkUserName() {
        String userName = mEditUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Utils.toast(R.string.hint_user_name);
            return false;
        }

        return true;
    }

    private boolean checkPwd() {
        String pwd = mEditPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            Utils.toast(R.string.pwd_empty);
            return false;
        }
        return true;
    }

    private void checkWithBBS(String userName, String pwd) {
        if (!Connectivity.isConnected(this)) {
            Utils.toast(R.string.network_disconnect);
            return;
        }
        showProgressDlg();
        String url = String.format("%s?username=%s&password=%s", BBS_URL, userName, pwd);
        HttpClientUtils.get(url, mResponseHandler);
    }

    AsyncHttpResponseHandler mResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
            if (arg2 != null) {
                String content = new String(arg2);
                Log.i(TAG, "onFailure responseBody = " + content);
            }

            Log.i(TAG, "onFailure statusCode = " + arg0);

            closeProgressDlg();
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            String content = new String(arg2);

            try {
                JSONObject json = new JSONObject(content);
                int status = json.getInt("status");
                if (status == 1) {
                    JSONObject jsonDate = json.getJSONObject("datas");

                    String uid = jsonDate.getString("uid");
                    String nickname = jsonDate.getString("nickname");

                    String username = jsonDate.getString("username");
                    String mobile = jsonDate.getString("mobile");

                    String avatarUrl = jsonDate.getString("avatar");

                    thirdLogin(nickname, uid, LoginType.BBS, avatarUrl);

                } else {
                    closeProgressDlg();
                    String message = json.getString("message");
                    Utils.toast(message);
                }

            } catch (JSONException e) {

            }

            Log.i(TAG, "onSuccess statusCode = " + arg0);
            Log.i(TAG, "onSuccess responseBody = " + content);
        }
    };


    private void thirdLogin(String nickname, String uid, int userType, String avatar) {
        BaseHttpRequest.releaseRequest(mThirdLoginHandler);

        RequestParams requestParam = new RequestParams();
        requestParam.put("nickname", nickname);
        requestParam.put("username", uid);
        requestParam.put("isBBSUser", userType);
        requestParam.put("avatar", avatar);
        mThirdLoginHandler = mHttpRequest.startRequest(this, ApiUrls.USER_THIRD_LOGIN, requestParam, this, BaseHttpRequest.RequestType.POST);
    }


    private void showProgressDlg() {
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.logining));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeProgressDlg() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    @Override
    public void onRequestFailed(String errorMsg) {
        closeProgressDlg();
        Utils.toast(errorMsg);
    }

    @Override
    public void onRequestFailedNoNetwork() {
        closeProgressDlg();
        Utils.toast(R.string.network_disconnect);
    }

    @Override
    public void onRequestCanceled() {
        closeProgressDlg();
    }

    @Override
    public void onRequestSucceeded(String content) {
        closeProgressDlg();
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

            Intent homePageIntent = new Intent(this, HomePageActivity.class);
            homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homePageIntent);
            Utils.toast(R.string.login_success);
        }
    }
}
