package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
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
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

/**
 * Created by Administrator on 2015/11/25.
 */
public class ModifyAgeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView avatarImage;
    private EditText nameEdit;
    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mRequestHandle;

    private Dialog mProgressDlg;
    private String USERID = "user";
    private String NICKAGE = "age";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick);

        initView();
    }

    private void initView() {
        avatarImage = (ImageView) findViewById(R.id.avatar);
        nameEdit = (EditText) findViewById(R.id.phone_number);
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
        nameEdit.setInputType(inputType);
        nameEdit.setText(UserInfo.getCurrentUser().getAge());
        findViewById(R.id.btn_next).setOnClickListener(this);
        setAvatar();
    }

    private void setAvatar() {
        UserInfo user = UserInfo.getCurrentUser();
        String avatarPath = user.getAvatar();
        ImageLoader.getInstance().displayImage(avatarPath, avatarImage,
                PhotoUtils.buldDisplayImageOptionsForAvatar());
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (checkedUser()) {
            StatisticUtils.onEvent(R.string.title_activity_modify_age, R.string.finish);
            UserInfo user = UserInfo.getCurrentUser();
            user.setAge(nameEdit.getText().toString().trim());
            showProgressDlg();
            querData();
        }
    }

    private void querData() {
        mHttpRequest = new BaseHttpRequest();
        RequestParams requestParams=new RequestParams();
        requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
        requestParams.put(NICKAGE, nameEdit.getText().toString().trim());
        mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_UPDATEAGE, requestParams, requestCallback, BaseHttpRequest.RequestType.POST);
    }

    private HttpRequestCallback requestCallback = new HttpRequestCallback(){

        @Override
        public void onRequestFailed(String errorMsg) {
            closeProgressDlg();
            Utils.toast(errorMsg);
        }

        @Override
        public void onRequestFailedNoNetwork() {
            closeProgressDlg();
            Utils.toast(R.string.network_error);
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
            } else {
                UserInfo userInfo = UserInfo.getCurrentUser();
                userInfo.setAge(nameEdit.getText().toString().trim());
                UserInfo.saveLoginUserInfo(userInfo);
                finish();
            }
            Utils.toast(baseBean.getMessage());
        }
    };
    private boolean checkedUser() {
        String nickAge= nameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(nickAge)) {
            Utils.toast(R.string.input_nickage);
            return false;
        }
        if(Integer.parseInt(nickAge)<1 || Integer.parseInt(nickAge)>100){
            Utils.toast("请输入正确的数字");
            return false;
        }
        return true;
    }

    private void showProgressDlg() {
        mProgressDlg = DialogUtils.getCircleProgressDialog(this, getString(R.string.logining));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeProgressDlg() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }
}
