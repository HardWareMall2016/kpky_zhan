package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SnsAccount;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
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
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity implements OnClickListener ,HttpRequestCallback{
	private final static String TAG="LoginActivity";

    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    //Views
	private EditText mEditPhoneNumber;
	private EditText mEditPwd;
	private Dialog mProgressDlg;

    //Network
    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mLoginHandler;
    private RequestHandle mThirdLoginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHttpRequest = new BaseHttpRequest();
        // wx qq初始化
        initQZoneQQPlatform();
        initWeChatPlatform();

        findViewById(R.id.qq).setOnClickListener(this);
        findViewById(R.id.weixin).setOnClickListener(this);
        findViewById(R.id.weibo).setOnClickListener(this);

        mEditPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mEditPwd = (EditText) findViewById(R.id.passwd);

        View forgetPwd = findViewById(R.id.forget_pwd);
        forgetPwd.setOnClickListener(this);

        View btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        View btnRegister = findViewById(R.id.btn_next);
        btnRegister.setOnClickListener(this);

        TextView rightMenu = getActionBarRightMenu();
        rightMenu.setOnClickListener(this);
        rightMenu.setText(R.string.bbs_login);
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mLoginHandler);
        BaseHttpRequest.releaseRequest(mThirdLoginHandler);
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.right_menu:
                Intent bbsIntent = new Intent(this, BBSLoginActivity.class);
                startActivity(bbsIntent);

                StatisticUtils.onEvent(getTitle().toString(), getString(R.string.bbs_login));
                break;
            case R.id.forget_pwd:
                Intent forgetPwdIntent = new Intent(this, ForgetPasswordActivity.class);
                startActivity(forgetPwdIntent);

                StatisticUtils.onEvent(getTitle().toString(), getString(R.string.forget_pwd));
                break;
            case R.id.btn_login:
                if (checkPhoneNumber() && checkPwd()) {
                    String phoneNumber = mEditPhoneNumber.getText().toString();
                    String pwd = mEditPwd.getText().toString();
                    login(phoneNumber,pwd);
                }

                StatisticUtils.onEvent(getTitle().toString(), getString(R.string.login));
                break;
            case R.id.btn_next:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);

                StatisticUtils.onEvent(getTitle().toString(), getString(R.string.register));
                break;
            case R.id.weibo:
                login(SHARE_MEDIA.SINA);

                StatisticUtils.onEvent(getTitle().toString(), "微博");
                break;
            case R.id.qq:
                login(SHARE_MEDIA.QQ);

                StatisticUtils.onEvent(getTitle().toString(), "QQ");
                break;
            case R.id.weixin:
                login(SHARE_MEDIA.WEIXIN);

                StatisticUtils.onEvent(getTitle().toString(), "微信");
                break;
        }
    }

    private boolean checkPhoneNumber() {
        String phoneNumber = mEditPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            Utils.toast(R.string.phone_number_empty);
            return false;
        }

        if (!Utils.checkMobilePhoneNumber(phoneNumber)) {
            Utils.toast(R.string.phone_number_invalid);
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

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                showProgressDlg(getString(R.string.processing));
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                closeProgressDlg();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                closeProgressDlg();
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                closeProgressDlg();
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
	private void getUserInfo(final SHARE_MEDIA platform) {
		mController.getPlatformInfo(this, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (info != null) {
                    Log.e(TAG, "" + info.toString());
                }
                mController.getUserInfo(LoginActivity.this, new SocializeListeners.FetchUserListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int i, SocializeUser socializeUser) {

                        for (SnsAccount s : socializeUser.mAccounts) {
                            switch (platform) {
                                case QQ:
                                    Log.i(TAG, "QQ");
                                    if (s.getPlatform().equals("qq")) {
                                        thirdLogin(s.getUserName(), s.getUsid(), LoginType.QQ, s.getAccountIconUrl());
                                    }
                                    break;
                                case SINA:
                                    Log.i(TAG, "SINA");
                                    if (s.getPlatform().equals("sina")) {
                                        thirdLogin(s.getUserName(), s.getUsid(), LoginType.SINA, s.getAccountIconUrl());
                                    }
                                    break;
                                case WEIXIN:
                                    Log.i(TAG, "WEIXIN");
                                    if (s.getPlatform().equals("wxsession")) {
                                        thirdLogin(s.getUserName(), s.getUsid(), LoginType.WEIXIN, s.getAccountIconUrl());
                                    }
                                    break;
                            }
                        }
                    }
                });
            }
        });
	}

    private void initQZoneQQPlatform() {
        String appId = "1104742693";
        String appKey = "nYut8JjiEBlsST8c";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void initWeChatPlatform() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, "wx601c58a5f91ef83f", "7e182ac7d696166462627bce71d6a78f");
        wxHandler.addToSocialSDK();
    }

    @Override
    public void onBackPressed() {
        Intent homePageIntent = new Intent(LoginActivity.this, HomePageActivity.class);
        homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePageIntent);
    }

    private void login(String mobile,String password) {

        BaseHttpRequest.releaseRequest(mLoginHandler);

        showProgressDlg(getString(R.string.logining));

        RequestParams requestParam = new RequestParams();
        requestParam.put("mobile", mobile);
        requestParam.put("password", password);

        mLoginHandler=mHttpRequest.startRequest(this, ApiUrls.USER_LOGIN, requestParam, this, BaseHttpRequest.RequestType.POST);
    }

    private  void thirdLogin(String nickname,String uid,int userType,String avatar){
        BaseHttpRequest.releaseRequest(mThirdLoginHandler);
        showProgressDlg(getString(R.string.logining));

        RequestParams requestParam = new RequestParams();
        requestParam.put("nickname", nickname);
        requestParam.put("username", uid);
        requestParam.put("isBBSUser", userType);
        requestParam.put("avatar", avatar);
        mThirdLoginHandler=mHttpRequest.startRequest(this, ApiUrls.USER_THIRD_LOGIN, requestParam, this, BaseHttpRequest.RequestType.POST);
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
            LoginResult result=Utils.parseJson(content, LoginResult.class);
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

            Intent homePageIntent = new Intent(LoginActivity.this, HomePageActivity.class);
            homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homePageIntent);
            Utils.toast(R.string.login_success);
        }
    }

    private void showProgressDlg(String msg) {
        mProgressDlg = DialogUtils.getCircleProgressDialog(this, msg);
        mProgressDlg.setCancelable(true);
        mProgressDlg.show();
    }

    private void closeProgressDlg() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }
}
