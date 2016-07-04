package com.zhan.kykp.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.JijingTitleBean;
import com.zhan.kykp.speakingIelts.SpeakingIeltsListActivity;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class SpeakingMainActivity extends BaseActivity implements OnClickListener {

    private TextView mTextView;

    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestJijingTitleHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_main);

        findViewById(R.id.speaking_lib).setOnClickListener(this);
        findViewById(R.id.jj_yuce).setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.tv_jj_yuce);
        findViewById(R.id.speaking_ielts).setOnClickListener(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mRequestJijingTitleHandler);
        super.onDestroy();
    }

    private void initView() {
        if (!Connectivity.isConnected(getApplicationContext())) {
            return;
        }
        mHttpRequest=new BaseHttpRequest();

        mRequestJijingTitleHandler=mHttpRequest.startRequest(this, ApiUrls.SPEAKINGPRACTICE_GETJIJING_TITLE, new RequestParams(), new HttpRequestCallback() {
            @Override
            public void onRequestFailed(String errorMsg) {

            }

            @Override
            public void onRequestFailedNoNetwork() {

            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                JijingTitleBean jijingTitleBean = Utils.parseJson(content, JijingTitleBean.class);
                if (jijingTitleBean == null) return;
                mTextView.setText(jijingTitleBean.getDatas().getTitle());
            }
        }, BaseHttpRequest.RequestType.GET);
    }

    @Override
    public void onClick(View arg0) {
        BaseHttpRequest.releaseRequest(mRequestJijingTitleHandler);
        Intent intent = new Intent(this, SpeakingActivity.class);
        switch (arg0.getId()) {
            case R.id.speaking_lib:
                StatisticUtils.onEvent(getTitle().toString(), "托福题库");

                intent.putExtra(SpeakingActivity.EXTRA_KEY_QUESTION_TYPE, SpeakingActivity.QUESTION_TYPE_LIB);
                break;
            case R.id.jj_yuce:
                StatisticUtils.onEvent(getTitle().toString(), getString(R.string.speaking_jj_prediction));

                intent.putExtra(SpeakingActivity.EXTRA_KEY_QUESTION_TYPE, SpeakingActivity.QUESTION_TYPE_JJ_YUCE);
                break;
            case R.id.speaking_ielts:
                StatisticUtils.onEvent(getTitle().toString(), "雅思题库");

                intent = new Intent(this, SpeakingIeltsListActivity.class);
                break;
        }
        startActivity(intent);
    }
}
