package com.zhan.kykp.abroad;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.AbroadStrategyBean;
import com.zhan.kykp.network.bean.AbroadStrategyInfoBean;
import com.zhan.kykp.network.bean.AbroadStrategyPraiseBean;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/23.
 */
public class AbroadStrategyInfoActivity extends BaseActivity implements View.OnClickListener {

    public final static String OBJECT = "objectId";

    private final static String PAGE = "page";
    private final static String USERID =  "user";

    private RequestHandle mRequestHandle;
    private RequestHandle mPraiseRequestHandle;
    private BaseHttpRequest mHttpRequest;

    private TextView mTitle ;
    private TextView mContent ;
    private TextView mPriceNum ;
    private ImageView mPrice ;
    private String objectId ;
    private String userObjectId ;

    private AbroadStrategyInfoBean mPraiseBean ;
    private AbroadStrategyPraiseBean mBean ;
    private List<ItemInfo> mList = new ArrayList<ItemInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abroad_strategy_info);

        objectId = getIntent().getStringExtra(OBJECT);
        userObjectId = UserInfo.getCurrentUser().getObjectId();

        initView();
        query(0);
    }

    @Override
    protected void onDestroy() {
        release(mRequestHandle);
        release(mPraiseRequestHandle);
        super.onDestroy();
    }

    private void release(RequestHandle request) {
        if (request != null && !request.isFinished()) {
            request.cancel(true);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        mTitle = (TextView)findViewById(R.id.abroad_strategy_info_title);
        mContent = (TextView)findViewById(R.id.abroad_strategy_info_content);
        mPrice = (ImageView)findViewById(R.id.iv_abroad_praise);
        mPrice.setOnClickListener(this);
        mPriceNum = (TextView)findViewById(R.id.tv_abroad_praise_num);
    }

    private void query(int page) {
        mHttpRequest = new BaseHttpRequest();
        RequestParams requestParams=new RequestParams();
        requestParams.put(OBJECT, objectId);
        requestParams.put(USERID, userObjectId);
        requestParams.put(PAGE, page);
        mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.RAIDERS_GETRAIDERS, requestParams, new RequestCallback(), BaseHttpRequest.RequestType.GET);
    }

    private class RequestCallback implements HttpRequestCallback {

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
            mBean = Utils.parseJson(content, AbroadStrategyPraiseBean.class);
            if (mBean != null) {
                if (mBean.getDatas().size() != 0) {
                    for(AbroadStrategyPraiseBean.DatasEntity item : mBean.getDatas()){
                        ItemInfo info = new ItemInfo();
                        info.content = item.getContent();
                        info.praise = item.getPraise();
                        info.title = item.getTitle();
                        info.status = item.getStatus();
                        mList.add(info);
                    }
                    mPriceNum.setText(mBean.getDatas().get(0).getPraise() + "");
                    mTitle.setText(mBean.getDatas().get(0).getTitle());
                    mContent.setText(mBean.getDatas().get(0).getContent());

                    if(mList.get(0).status == 1){
                        mPrice.setImageResource(R.drawable.abroad_strategy_praiseed);
                    }else{
                        mPrice.setImageResource(R.drawable.abroad_strategy_praise);
                    }
                }
            }else{
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    };

    private class ItemInfo {
        String title;
        String content;
        int praise;
        int status ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_abroad_praise:
                StatisticUtils.onEvent("留学攻略","留学攻略详情","点赞");
                if(mList.get(0).status == 0){
                    praise(objectId);
                }else{
                    Utils.toast("已经点过赞了");
                }
                break;
        }
    }

    private void praise(String objectId) {
        mHttpRequest = new BaseHttpRequest();
        RequestParams requestParams= new RequestParams();
        requestParams.put("user", userObjectId);
        requestParams.put("raiders", objectId);

        mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.RAIDERS_PRAISERAIDERS, requestParams, new PraiseRequestCallback(), BaseHttpRequest.RequestType.POST);
    }

    private class PraiseRequestCallback implements HttpRequestCallback {

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
            mPraiseBean = Utils.parseJson(content,AbroadStrategyInfoBean.class);
            if(mBean != null){
                if(mBean.getStatus() == 1){
                    mPrice.setImageResource(R.drawable.abroad_strategy_praiseed);
                    mList.get(0).status = 1 ;
                    query(0);
                }else{
                    Utils.toast("点赞失败");
                }

            }else{
                Utils.toast(R.string.network_query_data_failed);
            }

        }
    }
}
