package com.zhan.kykp.integralMall;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.ExchangeRecordsBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class ExchangeRecordsActivity extends BaseActivity {
    //View 相关
    private PullToRefreshListView mPullRefreshListView;
    private ImageView emptyView ;
    //Tools
    private MallAdapter mAdapter;
    private LayoutInflater mInflater;

    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mOrderHandler;
    private Dialog mProgressDlg;

    private DisplayImageOptions options;

    private List<ExchangeRecordInfo> mOrderList =new LinkedList<ExchangeRecordInfo>();
    private int mCurrentPage = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_records);
        mHttpRequest = new BaseHttpRequest();
        mInflater=getLayoutInflater();
        options= buldDisplayImageOptions();

        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.exchange_relist);
        emptyView = new ImageView(getApplicationContext());
        emptyView.setImageResource(R.drawable.exchange_relist_null);
        emptyView.setScaleType(ImageView.ScaleType.CENTER);
        mPullRefreshListView.setEmptyView(emptyView);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                queryOrderList(0);
            }
        });
        // 上拉加载更多
        mPullRefreshListView
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        queryOrderList(mCurrentPage + 1);
                    }
                });

        mAdapter=new MallAdapter();
        mPullRefreshListView.setAdapter(mAdapter);

        if(mOrderList.size()==0){
            showProgressDialog();
            queryOrderList(0);
        }
    }

    public DisplayImageOptions buldDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.celebrity_def_icon)
                .showImageForEmptyUri(R.drawable.celebrity_def_icon)
                .showImageOnFail(R.drawable.celebrity_def_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();
    }

    @Override
    protected void onDestroy() {
        BaseHttpRequest.releaseRequest(mOrderHandler);
        super.onDestroy();
    }

    private void queryOrderList(int page) {
        if(mOrderHandler!=null&&!mOrderHandler.isFinished()){
            return;
        }
        RequestParams requestParams=new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("page", page);
        mOrderHandler = mHttpRequest.startRequest(this, ApiUrls.GOODS_GET_ORDER, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
    }

    private class RequestCallback implements HttpRequestCallback {

        private int page ;
        public RequestCallback(int page) {
            this.page = page ;
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
            ExchangeRecordsBean mBean = Utils.parseJson(content, ExchangeRecordsBean.class);
            if(mBean != null){
                if(page==0){
                    mOrderList.clear();
                }
                if(mBean.getDatas()!=null){
                    for(ExchangeRecordsBean.DatasEntity item : mBean.getDatas()){
                        ExchangeRecordInfo info = new ExchangeRecordInfo();
                        info.set_id(item.get_id());
                        info.setCreatedAt(item.getCreatedAt());
                        info.setExchange(item.getExchange());
                        info.setImage(item.getImage());
                        info.setStatus(item.getStatus());
                        info.setUpdatedAt(item.getUpdatedAt());
                        info.setTitle(item.getTitle());
                        info.setRemark(item.getRemark());
                        mOrderList.add(info);
                    }
                }
                if(page==0||(mBean.getDatas()!=null&&mBean.getDatas().size()>0)){
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage=page;
                }
            }else{
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    }

    private class ViewHolder{
        ImageView image;
        TextView title;
        TextView exchangeNum;
        TextView status ;
        View remarkContent;
        TextView remarkInfo;
    }

    private class MallAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOrderList.size();
        }

        @Override
        public Object getItem(int position) {
            return mOrderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView= mInflater.inflate(R.layout.exchange_recording_item,null);
                holder=new ViewHolder();
                holder.image=(ImageView)convertView.findViewById(R.id.goods_pic);
                holder.title=(TextView)convertView.findViewById(R.id.goods_name);
                holder.exchangeNum=(TextView)convertView.findViewById(R.id.scholarship_num);
                holder.status = (TextView)convertView.findViewById(R.id.scholarship_need);
                holder.remarkContent=convertView.findViewById(R.id.remark_content);
                holder.remarkInfo=(TextView)convertView.findViewById(R.id.remark_info);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder)convertView.getTag();
            }

            ExchangeRecordInfo info = mOrderList.get(position);
            holder.title.setText(info.getTitle());
            holder.exchangeNum.setText(info.getExchange()+"");
            if(info.getStatus() == 0){
                holder.status.setText("未发货");
            }else if(info.getStatus() == 1){
                holder.status.setText("已发货");
            }else{
                holder.status.setText("虚拟商品");
            }

            if(TextUtils.isEmpty(info.getRemark())){
                holder.remarkContent.setVisibility(View.GONE);
            }else{
                holder.remarkContent.setVisibility(View.VISIBLE);
                holder.remarkInfo.setText(info.getRemark());
            }

            ImageLoader.getInstance().displayImage(info.getImage(), holder.image, options);
            return convertView;
        }
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }
}
