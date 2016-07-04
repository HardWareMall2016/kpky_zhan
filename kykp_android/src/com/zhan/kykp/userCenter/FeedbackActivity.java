package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.base.EnvConfig;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

/**
 * Created by WuYue on 2015/10/19.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private final static String NET_PARAM_FROM_USER = "formUser";//发送用户objectId
    private final static String NET_PARAM_TO_USER = "toUser";//接收用户objectId
    private final static String NET_PARAM_MSG_CONTENT = "message";//信息内容

    //View
    private Button mBtnSubmit;
    private EditText mETFeedback;
    private Dialog mProgressDialog;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mSendMsgRequestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mBtnSubmit = (Button) findViewById(R.id.submit);
        mBtnSubmit.setOnClickListener(this);
        mETFeedback = (EditText) findViewById(R.id.feedback_content);

        mHttpRequest = new BaseHttpRequest();
    }

    @Override
    protected void onDestroy() {
        releaseRequest(mSendMsgRequestHandle);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        //数据有效性验证
        String msg = mETFeedback.getText().toString();
        if(TextUtils.isEmpty(msg)){
            Utils.toast(R.string.feedback_empty);
            return;
        }

        //防止重复请求
        if (mSendMsgRequestHandle != null && !mSendMsgRequestHandle.isFinished()) {
            return;
        }

        showProgressDialog();

        RequestParams requestParams = new RequestParams();
        requestParams.put(NET_PARAM_FROM_USER, UserInfo.getCurrentUser().getObjectId());
        requestParams.put(NET_PARAM_TO_USER, EnvConfig.CUSTOMER_SERVICE_ID);
        requestParams.put(NET_PARAM_MSG_CONTENT, msg);
        mSendMsgRequestHandle = mHttpRequest.startRequest(this, ApiUrls.MESSAGE_SEND_PRIV_MSG, requestParams, new SendPrivateMsgCallback(), BaseHttpRequest.RequestType.POST);
    }

    private class SendPrivateMsgCallback implements HttpRequestCallback {
        @Override
        public void onRequestFailed(String errorMsg) {
            Utils.toast(R.string.send_msg_failed);
            closeProgressDialog();
        }

        @Override
        public void onRequestFailedNoNetwork() {
            Utils.toast(R.string.send_msg_network_error);
            closeProgressDialog();
        }

        @Override
        public void onRequestCanceled() {
            closeProgressDialog();
        }

        @Override
        public void onRequestSucceeded(String content) {
            closeProgressDialog();
            BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
            if (baseBean != null) {
                Utils.toast(R.string.feedback_submitted_success);
                FeedbackActivity.this.finish();
            } else {
                Utils.toast(R.string.send_msg_failed);
            }
        }
    }

    //Tools
    private void showProgressDialog() {
        mProgressDialog = DialogUtils.getCircleProgressDialog(this, getString(R.string.feedback_submiting));
        //mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /***
     * Network
     */
    private void releaseRequest(RequestHandle request) {
        if (request != null && !request.isFinished()) {
            request.cancel(true);
        }
    }

}
