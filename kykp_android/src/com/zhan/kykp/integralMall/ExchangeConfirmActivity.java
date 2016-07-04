package com.zhan.kykp.integralMall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.ConsigneeAddressBean;
import com.zhan.kykp.network.bean.MallExchangeResult;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

import java.text.DecimalFormat;

/**
 * Created by WuYue on 2015/11/20.
 */
public class ExchangeConfirmActivity extends BaseActivity implements View.OnClickListener {
    public final static String EXT_KEY_GOODS_INFO = "goods_info";

    public final static String RESULT_EXT_KEY_GOODS_NUM = "goods_number";
    public final static String RESULT_EXT_KEY_GOODS_ID = "goods_id";

    private final static int REQUEST_CODE=100;

    //View
    private TextView mInputLocation;
    private View mLocationDetails;
    private TextView mName;
    private TextView mPhone;
    private TextView mDetailsAddr;
    private TextView mPostcode;
    private TextView mRemark;

    private ImageView mGoodsPic;
    private TextView mGoodsName;
    private TextView mScholarshipNeed;
    private TextView mScholarshipTotal;

    private Dialog mDialogConfirm;
    private Dialog mProgressDlg;

    //Data
    private GoodsInfo mGoodsInfo;
    private AddressInfo mAddressInfo;

    //Tools
    private DisplayImageOptions options;
    private DecimalFormat mDigitFormat = new DecimalFormat("###,###");//使用系统默认的格式

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mGetAddrRequestHandle;
    private RequestHandle mSaveOrderRequestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_confirm);

        options = buldDisplayImageOptions();

        mHttpRequest = new BaseHttpRequest();

        initView();

        initBackConfirmDlg();

        processExtraData();

        populateViews();

        queryAddress();
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mGetAddrRequestHandle);
        BaseHttpRequest.releaseRequest(mSaveOrderRequestHandle);
        super.onDestroy();
    }

    private void processExtraData() {
        Intent intent = getIntent();
        mGoodsInfo = (GoodsInfo) intent.getParcelableExtra(EXT_KEY_GOODS_INFO);
    }

    private void initView() {
        mInputLocation = (TextView) findViewById(R.id.input_location);

        mLocationDetails =  findViewById(R.id.location_details);
        mName = (TextView) findViewById(R.id.name);
        mPhone = (TextView) findViewById(R.id.phone);
        mDetailsAddr = (TextView) findViewById(R.id.details_address);
        mPostcode= (TextView) findViewById(R.id.postcode);
        mRemark= (TextView) findViewById(R.id.remark);

        mGoodsPic = (ImageView) findViewById(R.id.goods_pic);
        mGoodsName = (TextView) findViewById(R.id.goods_name);
        mScholarshipNeed = (TextView) findViewById(R.id.scholarship_need);
        mScholarshipTotal = (TextView) findViewById(R.id.scholarship_total);

        findViewById(R.id.location_content).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    private void populateViews() {
        ImageLoader.getInstance().displayImage(mGoodsInfo.getImage(), mGoodsPic, options);
        mGoodsName.setText(mGoodsInfo.getTitle());
        mScholarshipNeed.setText(mDigitFormat.format(mGoodsInfo.getScholarship()));
        UserInfo user = UserInfo.getCurrentUser();
        mScholarshipTotal.setText(mDigitFormat.format(user.getCredit()));
    }

    private void populateAddress(){
        if(mAddressInfo==null){
            mLocationDetails.setVisibility(View.GONE);
            mInputLocation.setVisibility(View.VISIBLE);
        }else{
            mLocationDetails.setVisibility(View.VISIBLE);
            mInputLocation.setVisibility(View.GONE);

            mName.setText(getString(R.string.integral_mall_addr_consignee) + mAddressInfo.getConsignee());
            mPhone.setText(mAddressInfo.getPhone());

            mDetailsAddr.setText(getString(R.string.integral_mall_addr_detail)+mAddressInfo.getDetailsAddr());
            mPostcode.setText(getString(R.string.integral_mall_addr_postcode) + mAddressInfo.getPostCode());
            if(TextUtils.isEmpty(mAddressInfo.getRemark())){
                mRemark.setVisibility(View.GONE);
            }else{
                mRemark.setVisibility(View.VISIBLE);
                mRemark.setText(getString(R.string.integral_mall_addr_remark) + mAddressInfo.getRemark());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_content:
                startActivityForResult(new Intent(this, ConsigneeAddressActivity.class),REQUEST_CODE);
                break;
            case R.id.btn_confirm:
                mDialogConfirm.show();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            if(resultCode==RESULT_OK){
                mAddressInfo=data.getParcelableExtra(ConsigneeAddressActivity.RESULT_DATA_ADDRESS);
                populateAddress();
            }
        }
    }

    private void initBackConfirmDlg() {
        LayoutInflater inflater = getLayoutInflater();
        mDialogConfirm = new Dialog(this, R.style.Dialog);
        View dlgContent = inflater.inflate(R.layout.mall_confirm_dialog_layout, null);

        TextView message = (TextView) dlgContent.findViewById(R.id.message);
        message.setText(R.string.integral_mall_dlg_exchange_confirm);

        TextView confirm = (TextView) dlgContent.findViewById(R.id.confirm);
        confirm.setText(R.string.integral_mall_dlg_exchange_now);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveOrder();
            }
        });

        TextView cancel = (TextView) dlgContent.findViewById(R.id.cancel);
        cancel.setText(R.string.integral_mall_dlg_exchange_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDialogConfirm.dismiss();
            }
        });

        mDialogConfirm.setContentView(dlgContent);
    }

    private void queryAddress() {
        BaseHttpRequest.releaseRequest(mGetAddrRequestHandle);
        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        mGetAddrRequestHandle = mHttpRequest.startRequest(this, ApiUrls.GOODS_GET_ADDR, requestParams, new HttpRequestCallback() {
            @Override
            public void onRequestFailed(String errorMsg) {
                Utils.toast(errorMsg);
            }

            @Override
            public void onRequestFailedNoNetwork() {
                Utils.toast(R.string.integral_mall_nonetwork_for_addr);
            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
                if (baseBean != null && baseBean.getStatus() == 1) {
                    ConsigneeAddressBean bean = Utils.parseJson(content, ConsigneeAddressBean.class);
                    if(bean.getDatas()!=null&&bean.getDatas().get_id()!=null){
                        String detailAddr=String.format("%s %s",bean.getDatas().getCity(),bean.getDatas().getAddress());
                        mAddressInfo=new AddressInfo();
                        mAddressInfo.setId(bean.getDatas().get_id());
                        mAddressInfo.setConsignee(bean.getDatas().getUsername());
                        mAddressInfo.setDetailsAddr(detailAddr);
                        mAddressInfo.setPostCode(bean.getDatas().getCode());
                        mAddressInfo.setPhone(bean.getDatas().getMobile());
                        mAddressInfo.setRemark(bean.getDatas().getRemark());
                    }else{
                        mAddressInfo=null;
                    }

                    populateAddress();

                } else if (baseBean != null && baseBean.getStatus() == 0) {
                    Utils.toast(baseBean.getMessage());
                }
            }

        }, BaseHttpRequest.RequestType.GET);
    }

    private void saveOrder(){
        if(mAddressInfo==null){
            Utils.toast(R.string.integral_mall_exchange_no_location);
            return;
        }

        if(mSaveOrderRequestHandle!=null&&!mSaveOrderRequestHandle.isFinished()){
            return;
        }

        showProgressDialog();

        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("goodsId", mGoodsInfo.getId());
        requestParams.put("addressId", mAddressInfo.getId());

        mSaveOrderRequestHandle=mHttpRequest.startRequest(this, ApiUrls.GOODS_SAVE_ORDER, requestParams, new HttpRequestCallback() {
            @Override
            public void onRequestFailed(String errorMsg) {
                Utils.toast(errorMsg);
                closeDialog();
            }

            @Override
            public void onRequestFailedNoNetwork() {
                Utils.toast(R.string.integral_mall_nonetwork_for_addr);
                closeDialog();
            }

            @Override
            public void onRequestCanceled() {
                closeDialog();
            }

            @Override
            public void onRequestSucceeded(String content) {
                closeDialog();
                BaseBean bean=Utils.parseJson(content,BaseBean.class);
                if(bean.getStatus()==1){
                    mDialogConfirm.dismiss();

                    MallExchangeResult resultBean=Utils.parseJson(content,MallExchangeResult.class);

                    UserInfo userInfo= UserInfo.getCurrentUser();
                    userInfo.setCredit(resultBean.getDatas().getScholarship());
                    UserInfo.saveLoginUserInfo(userInfo);

                    String toastStr=getString(R.string.integral_mall_exchange_success)+mDigitFormat.format(resultBean.getDatas().getScholarship());
                    Utils.toastLong(toastStr);

                    //将商品剩余数量返回
                    Intent result=new Intent();
                    result.putExtra(RESULT_EXT_KEY_GOODS_NUM,resultBean.getDatas().getRealnum());
                    result.putExtra(RESULT_EXT_KEY_GOODS_ID,mGoodsInfo.getId());
                    setResult(RESULT_OK,result);

                    finish();
                }else{
                    Utils.toast(bean.getMessage());
                }
            }
        }, BaseHttpRequest.RequestType.POST);
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getCircleProgressDialog(this, getString(R.string.integral_mall_dlg_exchanging));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    public DisplayImageOptions buldDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.mall_def_product)
                .showImageForEmptyUri(R.drawable.mall_def_product)
                .showImageOnFail(R.drawable.mall_def_product)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();
    }
}
