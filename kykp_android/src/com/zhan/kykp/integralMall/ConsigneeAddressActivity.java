package com.zhan.kykp.integralMall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.ConsigneeAddressBean;
import com.zhan.kykp.network.bean.ConsigneeAddressResultBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

public class ConsigneeAddressActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG="ConsigneeAddressActivity";

    public final static String RESULT_DATA_ADDRESS="ConsigneeAddress";

    private EditText mName ;
    private EditText mPhoneNumber;
    private EditText mCity ;
    private EditText mDetailsAddress ;
    private EditText mPostcode ;
    private EditText mRemark ;
    private Button mBtnComplete ;
    private String name ,phoneNum,city,address,postcode,remark;

    private Dialog mProgressDlg;

    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mCompleteHandler;
    private RequestHandle mGetAddressHandler ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignee_address);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mHttpRequest = new BaseHttpRequest();
        initView();
        getAddress();
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mCompleteHandler);
        BaseHttpRequest.releaseRequest(mGetAddressHandler);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        getAddress();
        super.onResume();
    }

    private void getAddress() {
        if(mGetAddressHandler!=null&&!mGetAddressHandler.isFinished()){
            return;
        }
        showProgressDialog();
        RequestParams requestParams=new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        mGetAddressHandler = mHttpRequest.startRequest(this, ApiUrls.GOODS_GET_ADDR,requestParams,new HttpRequestCallback(){

            @Override
            public void onRequestFailed(String errorMsg) {
                closeDialog();
                Utils.toast(errorMsg);
            }

            @Override
            public void onRequestFailedNoNetwork() {
                closeDialog();
            }

            @Override
            public void onRequestCanceled() {
                closeDialog();
            }

            @Override
            public void onRequestSucceeded(String content) {
                closeDialog();
                ConsigneeAddressBean mBean = Utils.parseJson(content, ConsigneeAddressBean.class);
                if(mBean!=null&&mBean.getStatus()==1){
                    mName.setText(mBean.getDatas().getUsername());
                    mPhoneNumber.setText(mBean.getDatas().getMobile());
                    mCity.setText(mBean.getDatas().getCity());
                    mDetailsAddress.setText(mBean.getDatas().getAddress());
                    if(mBean.getDatas().getCode() != null){
                        mPostcode.setText(mBean.getDatas().getCode()+"");
                    }
                    mRemark.setText(mBean.getDatas().getRemark());
                } else {
                    Utils.toast(mBean.getMessage());
                }
            }
        }, BaseHttpRequest.RequestType.GET);
    }

    private void initView() {
        mName = (EditText) findViewById(R.id.name);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        if(UserInfo.getCurrentUser().getMobilePhoneNumber() != null){
            mPhoneNumber.setText(UserInfo.getCurrentUser().getMobilePhoneNumber());
        }
        mCity = (EditText) findViewById(R.id.city);
        mDetailsAddress = (EditText) findViewById(R.id.details_address);
        mPostcode = (EditText) findViewById(R.id.postcode);
        mRemark = (EditText) findViewById(R.id.remark);
        mBtnComplete =(Button) findViewById(R.id.btn_complete) ;
        mBtnComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_complete:
                if (check()) {
                    complete();
                }
                break;
        }
    }

    private void complete() {

        if(mCompleteHandler!=null&&!mCompleteHandler.isFinished()){
            return;
        }
        showProgressDialog();
        RequestParams requestParams=new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("username", name);
        requestParams.put("mobile", phoneNum);
        requestParams.put("city", city);
        requestParams.put("address", address);
        requestParams.put("code", postcode);
        requestParams.put("remark", remark);

        mCompleteHandler = mHttpRequest.startRequest(this, ApiUrls.GOODS_SAVE_ADDR,requestParams,new HttpRequestCallback(){

            @Override
            public void onRequestFailed(String errorMsg) {
                closeDialog();
                Utils.toast(errorMsg);
            }

            @Override
            public void onRequestFailedNoNetwork() {
                closeDialog();
            }

            @Override
            public void onRequestCanceled() {
                closeDialog();
            }

            @Override
            public void onRequestSucceeded(String content) {
                closeDialog();
                BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
                if(baseBean!=null&&baseBean.getStatus()==1){

                    ConsigneeAddressResultBean bean=Utils.parseJson(content, ConsigneeAddressResultBean.class);

                    Utils.toast(R.string.integral_mall_consignee_save_success);

                    String detailAddr=String.format("%s %s",city,address);
                    AddressInfo  addressInfo=new AddressInfo();
                    addressInfo.setId(bean.getDatas().get_id());
                    addressInfo.setConsignee(name);
                    addressInfo.setDetailsAddr(detailAddr);
                    addressInfo.setPostCode(postcode);
                    addressInfo.setPhone(phoneNum);
                    addressInfo.setRemark(remark);

                    Intent result=new Intent();
                    result.putExtra(RESULT_DATA_ADDRESS,addressInfo);
                    setResult(RESULT_OK,result);
                    finish();
                }else{
                    Utils.toast(baseBean.getMessage());
                }

            }
        }, BaseHttpRequest.RequestType.POST);

    }


    private boolean check() {
         name = mName.getText().toString();
         phoneNum = mPhoneNumber.getText().toString() ;
         city = mCity.getText().toString();
         address = mDetailsAddress.getText().toString();
         postcode = mPostcode.getText().toString();
         remark = mRemark.getText().toString() ;

        if(TextUtils.isEmpty(name)){
            Utils.toast(R.string.integral_mall_consignee_name);
            return false;
        }
        if(TextUtils.isEmpty(phoneNum)){
            Utils.toast(R.string.integral_mall_consignee_phonenumber);
            return false;
        }
        if (!Utils.checkMobilePhoneNumber(phoneNum)) {
            Utils.toast(R.string.phone_number_invalid);
            return false;
        }
        if(TextUtils.isEmpty(city)){
            Utils.toast(R.string.integral_mall_consignee_city);
            return false;
        }

        if(TextUtils.isEmpty(address)){
            Utils.toast(R.string.integral_mall_consignee_address);
            return false;
        }
        if(TextUtils.isEmpty(postcode)){
            Utils.toast(R.string.integral_mall_consignee_postcode);
            return false;
        }

        if(!Utils.checkPostCodeNumber(postcode)){
            Utils.toast(R.string.integral_mall_consignee_six_postcode);
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getCircleProgressDialog(this, getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }


}
