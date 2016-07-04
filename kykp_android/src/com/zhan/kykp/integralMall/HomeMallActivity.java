package com.zhan.kykp.integralMall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.GoodsListBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WuYue on 2015/11/19.
 */
public class HomeMallActivity extends BaseActivity implements View.OnClickListener{

    private final static int REQUEST_CODE_EXCHANGE=100;

    //View 相关
    private PullToRefreshListView mPullRefreshListView;
    private Dialog mProgressDlg;

    //Data
    private List<GoodsInfo> mGoodsList=new LinkedList<>();
    private int mCurrentPage=0;

    //Tools
    private MallAdapter mAdapter;
    private LayoutInflater mInflater;
    private DisplayImageOptions options;
    private DecimalFormat mDigitFormat = new DecimalFormat("###,###");//使用系统默认的格式

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mall);

        mHttpRequest=new BaseHttpRequest();

        mInflater=getLayoutInflater();

        options= buldDisplayImageOptions();

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                queryGoodsList(0);
            }
        });
        // 上拉加载更多
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                queryGoodsList(mCurrentPage + 1);
            }
        });


        mAdapter=new MallAdapter();
        mPullRefreshListView.setAdapter(mAdapter);

        initActionBarMenu();

        showProgressDialog();
        queryGoodsList(0);
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        super.onDestroy();
    }

    private void initActionBarMenu(){
        getActionBarRightMenu().setText(R.string.integral_mall_exchange_records);
        getActionBarRightMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMallActivity.this,ExchangeRecordsActivity.class));
            }
        });
    }


    private class ViewHolder{
        ImageView goodsPic;
        TextView goodsName;
        TextView goodsNum;
        TextView scholarshipNum;
        Button btnExchange;
    }

    private class MallAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mGoodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mGoodsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView= mInflater.inflate(R.layout.mall_list_item,null);
                holder=new ViewHolder();
                holder.goodsPic=(ImageView)convertView.findViewById(R.id.goods_pic);
                holder.goodsName=(TextView)convertView.findViewById(R.id.goods_name);
                holder.goodsNum=(TextView)convertView.findViewById(R.id.goods_num);
                holder.scholarshipNum=(TextView)convertView.findViewById(R.id.scholarship_num);
                holder.btnExchange=(Button)convertView.findViewById(R.id.btn_exchange);
                convertView.setTag(holder);
                holder.btnExchange.setOnClickListener(HomeMallActivity.this);
            }else {
                holder=(ViewHolder)convertView.getTag();
            }

            GoodsInfo goods= mGoodsList.get(position);

            ImageLoader.getInstance().displayImage(goods.getImage(), holder.goodsPic, options);
            holder.goodsName.setText(goods.getTitle());
            holder.goodsNum.setText(getString(R.string.integral_mall_exchange_remain_num)+goods.getNumberRemaining());
            holder.scholarshipNum.setText(mDigitFormat.format(goods.getScholarship()));

            if(goods.getNumberRemaining()>0){
                holder.btnExchange.setEnabled(true);
                holder.btnExchange.setText(R.string.integral_mall_exchange_go);
            }else{
                holder.btnExchange.setEnabled(false);
                holder.btnExchange.setText(R.string.integral_mall_exchange_none);
            }
            holder.btnExchange.setTag(goods);

            return convertView;
        }
    }


    @Override
    public void onClick(View v) {
        GoodsInfo goods=(GoodsInfo)v.getTag();

        Intent intent=new Intent(this, ExchangeConfirmActivity.class);
        intent.putExtra(ExchangeConfirmActivity.EXT_KEY_GOODS_INFO,goods);
        startActivityForResult(intent, REQUEST_CODE_EXCHANGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_EXCHANGE&&RESULT_OK==resultCode){
            String goodId=data.getStringExtra(ExchangeConfirmActivity.RESULT_EXT_KEY_GOODS_ID);
            int goodNum=data.getIntExtra(ExchangeConfirmActivity.RESULT_EXT_KEY_GOODS_NUM,0);
            for (GoodsInfo goods:mGoodsList){
                if(goods.getId().equals(goodId)){
                    goods.setNumberRemaining(goodNum);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void queryGoodsList(int page){
        BaseHttpRequest.releaseRequest(mRequestHandle);
        RequestParams requestParams=new RequestParams();
        requestParams.put("page", page);
        mRequestHandle=mHttpRequest.startRequest(this, ApiUrls.GOODS_GET_LIST, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
    }

    private class RequestCallback implements HttpRequestCallback {
        private int mPage;

        public RequestCallback(int page) {
            mPage = page;
        }

        @Override
        public void onRequestFailed(String errorMsg) {
            closeDialog();
            mPullRefreshListView.onRefreshComplete();
            Utils.toast(errorMsg);
        }

        @Override
        public void onRequestFailedNoNetwork() {
            closeDialog();
            mPullRefreshListView.onRefreshComplete();
            Utils.toast(R.string.network_error);
        }

        @Override
        public void onRequestCanceled() {
            closeDialog();
            mPullRefreshListView.onRefreshComplete();
        }

        @Override
        public void onRequestSucceeded(String content) {
            closeDialog();
            mPullRefreshListView.onRefreshComplete();

            GoodsListBean bean=Utils.parseJson(content,GoodsListBean.class);
            if(bean!=null){
                if(mPage==0){
                    mGoodsList.clear();
                }

                if(bean.getDatas()!=null){
                    for (GoodsListBean.DatasEntity data:bean.getDatas()){
                        GoodsInfo goods=new GoodsInfo();
                        goods.setId(data.get_id());
                        goods.setTitle(data.getTitle());
                        goods.setImage(data.getImage());
                        goods.setNumberRemaining(data.getRealnum());
                        int scholarship=0;
                        if(Utils.isNumeric(data.getScholarship())){
                            scholarship=Integer.parseInt(data.getScholarship());
                        }
                        goods.setScholarship(scholarship);

                        mGoodsList.add(goods);
                    }
                }

                //是否有数据更新
                if(mPage==0||(bean.getDatas()!=null&&bean.getDatas().size()>0)){
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage=mPage;
                }
            }else{
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    }

    private void showProgressDialog(){
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    public DisplayImageOptions buldDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.mall_def_product)
                .showImageForEmptyUri(R.drawable.mall_def_product)
                .showImageOnFail(R.drawable.mall_def_product)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();
    }

}
