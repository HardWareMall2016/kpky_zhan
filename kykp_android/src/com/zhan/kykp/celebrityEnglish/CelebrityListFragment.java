package com.zhan.kykp.celebrityEnglish;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.CelebrityListBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private final static String TAG="CelebrityListFragment";

    //View 相关
    private PullToRefreshListView mPullRefreshListView;
    private Dialog mProgressDlg;

    private CelebrityAdapter mAdapter;

    //Data
    private List<CelebritySummaryInfo> mCelebrityList=new LinkedList<>();
    private int mCurrentPage=0;

    //Tools
    private LayoutInflater mInflater;
    private ICelebrityCallback mCallback;
    private DisplayImageOptions options;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;

    //Type
    class ViewHolder {
        TextView title ;
        TextView content ;
        TextView data ;
        TextView time ;
        ImageView image;
    }

    @Override
    public void onAttach(Activity activity) {
        mCallback = (ICelebrityCallback) activity;
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mInflater=inflater;
        mHttpRequest=new BaseHttpRequest();
        options= buldDisplayImageOptions();

        View rootView=inflater.inflate(R.layout.celebrity_frag_list,null);
        mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.person_englist_list);


        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                queryCelebrityList(0);
            }
        });
        // 上拉加载更多
        mPullRefreshListView
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        queryCelebrityList(mCurrentPage + 1);
                    }
                });


        mAdapter=new CelebrityAdapter();
        mPullRefreshListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnItemClickListener(this);

        if(mCelebrityList.size()==0){
            showProgressDialog();
            queryCelebrityList(0);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallback.gotoPosition((int) id);
    }

    public List<CelebritySummaryInfo> getCelebrityList(){
        return mCelebrityList;
    }

    private void queryCelebrityList(int page){
        BaseHttpRequest.releaseRequest(mRequestHandle);
        RequestParams requestParams=new RequestParams();
        requestParams.put("page", page);
        mRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.CELEBRITY_LISTS, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
    }

    private class RequestCallback implements HttpRequestCallback{
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

            CelebrityListBean bean=Utils.parseJson(content, CelebrityListBean.class);
            if(bean!=null){
                if(page==0){
                    mCelebrityList.clear();
                }

                String showTimeFormat=getString(R.string.celebrity_show_time);

               if(bean.getDatas()!=null){
                   for (CelebrityListBean.DatasEntity item:bean.getDatas()){
                       CelebritySummaryInfo data=new CelebritySummaryInfo();

                       data.setTitle(item.getTitle());
                       data.setObjectId(item.getObjectId());
                       data.setContent(item.getContent());
                       int seconds=0;
                       try{
                           seconds=Integer.parseInt(item.getAudioTime());
                       }catch (NumberFormatException ex){
                           Log.e(TAG,"parseInt item.getAudioTime() Error : "+item.getAudioTime());
                       }
                       data.setAudioTime(String.format(showTimeFormat, seconds / 60, seconds % 60));
                       long time=Utils.getCurrentTimeZoneUnixTime(item.getPlanDate());
                       data.setPlanDate(Utils.getDateFormateStr(time));
                       data.setImgUrl(item.getImage());

                       mCelebrityList.add(data);
                   }

               }

                //是否有数据更新
                if(page==0||(bean.getDatas()!=null&&bean.getDatas().size()>0)){
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage=page;
                }

            } else{
                Utils.toast(R.string.network_query_data_failed);
            }
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


    private class CelebrityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCelebrityList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCelebrityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder ;
            if(convertView == null){
                holder = new ViewHolder() ;
                convertView = mInflater.inflate(R.layout.celebrity_list_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.person_item_title);
                holder.content = (TextView) convertView.findViewById(R.id.person_item_content);
                holder.data = (TextView) convertView.findViewById(R.id.person_item_data);
                holder.time = (TextView) convertView.findViewById(R.id.person_item_time);
                holder.image=(ImageView) convertView.findViewById(R.id.img_person_eng_item);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            CelebritySummaryInfo data=mCelebrityList.get(position);

            holder.title.setText(data.getTitle());
            holder.content.setText(data.getContent());
            holder.time.setText(data.getAudioTime());
            holder.data.setText(data.getPlanDate());
            ImageLoader.getInstance().displayImage(data.getImgUrl(), holder.image, options);

            return convertView;
        }
    }


    private void showProgressDialog(){
        mProgressDlg = DialogUtils.getProgressDialog(getActivity(), getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }
}
