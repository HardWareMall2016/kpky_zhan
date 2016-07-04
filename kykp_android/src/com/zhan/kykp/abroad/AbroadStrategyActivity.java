package com.zhan.kykp.abroad;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.AbroadStrategyBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/23.
 */
public class AbroadStrategyActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private PullToRefreshListView mPullRefreshListView;
    private RequestHandle mRequestHandle;
    private BaseHttpRequest mHttpRequest;

    private AbroadStrategyAdapter mAdapter ;
    private LayoutInflater mInflater;
    private Dialog mProgressDlg;
    private AbroadStrategyBean mBean ;
    private List<ItemInfo> mList = new ArrayList<ItemInfo>();
    private DisplayImageOptions options;

    private final static String PAGE = "page";

    private int mMorePage = 1 ;

    @Override
    protected void onDestroy() {
        release(mRequestHandle);
        super.onDestroy();
    }

    private void release(RequestHandle request) {
        if (request != null && !request.isFinished()) {
            request.cancel(true);
        }
    }
    @Override
    protected void onResume() {
        //点赞之后返回到这个页面，页面刷新
        mPullRefreshListView.setRefreshing();
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_abroad_strategy);
        mInflater = getLayoutInflater() ;
        options= buldDisplayImageOptions() ;

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.abroad_strategy_list);
        mAdapter = new AbroadStrategyAdapter();
        mPullRefreshListView.setAdapter(mAdapter);

        //下拉刷新
        mPullRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                getApplicationContext(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);
                        mMorePage = 1;
                        queryDate(0);
                    }
                });
        //上拉加载更多
        mPullRefreshListView
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        queryDate(mMorePage++);
                    }
                });
        showProgressDialog();
        queryDate(0);
        mPullRefreshListView.setOnItemClickListener(this);

    }

    private DisplayImageOptions buldDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.person_english_default)
                .showImageForEmptyUri(R.drawable.person_english_default)
                .showImageOnFail(R.drawable.person_english_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();
    }

    private void queryDate(int page) {
        mHttpRequest = new BaseHttpRequest();
        RequestParams requestParams=new RequestParams();
        requestParams.put(PAGE, page);
        mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.RAIDERS_GETRAIDERS, requestParams, new RequestCallback(page), BaseHttpRequest.RequestType.GET);
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
            mBean = Utils.parseJson(content,AbroadStrategyBean.class);
           if (mBean != null) {
                if (page == 0) {
                    mList.clear();
                }
               if(mBean.getDatas().size() != 0){
                   for(AbroadStrategyBean.DatasEntity item : mBean.getDatas()){
                       ItemInfo info = new ItemInfo();
                       info.content = item.getContent();
                       info.createdAt = item.getCreatedAt();
                       info.image = item.getImage();
                       info.objectId = item.getObjectId();
                       info.praise = item.getPraise();
                       info.title = item.getTitle();
                       long time = Utils.getCurrentTimeZoneUnixTime(item.getUpdatedAt());
                       info.updatedAt = Utils.getDateFormateStr(time);
                       mList.add(info);
                   }
                   mAdapter.notifyDataSetChanged();
               }

                if(page > 2 && mBean.getDatas().size() == 0){
                    Utils.toast(R.string.no_more_data);
                }
            }else {
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StatisticUtils.onEvent("留学攻略", "留学攻略详情");
        Intent abradIntent = new Intent(getApplicationContext(),AbroadStrategyInfoActivity.class);
        abradIntent.putExtra(AbroadStrategyInfoActivity.OBJECT,mList.get((int)id).objectId);
        startActivity(abradIntent);
    }

    private class ItemInfo {
         String title;
         String image;
         String content;
         String updatedAt;
         int createdAt;
         int praise;
         String objectId;
    }
    private class AbroadStrategyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if(convertView == null){
                viewHolder = new ViewHolder() ;
                convertView = mInflater.inflate(R.layout.celebrity_list_item,null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.person_item_title);
                viewHolder.content = (TextView) convertView.findViewById(R.id.person_item_content);
                viewHolder.numpeople = (TextView) convertView.findViewById(R.id.person_item_time);
                viewHolder.data = (TextView) convertView.findViewById(R.id.person_item_data);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_person_eng_item);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(mList.get(position).title);
            viewHolder.content.setText(mList.get(position).content);
            viewHolder.numpeople.setText(mList.get(position).praise+" 人喜欢");
            viewHolder.data.setVisibility(View.INVISIBLE);

            String avatarPath=mList.get(position).image;
            ImageLoader.getInstance().displayImage(avatarPath,viewHolder.imageView,options);
            return convertView;
        }
    }

    static class ViewHolder{
        TextView title ;
        TextView content ;
        TextView numpeople ;
        TextView data ;
        ImageView imageView ;
    }

    private void showProgressDialog(){
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
//		mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

}
