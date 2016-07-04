package com.zhan.kykp.music;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RelativeLayout;
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
 * Created by Administrator on 2015/11/19.
 */
public class MusicListFragment extends Fragment  {

    private PullToRefreshListView mPullRefreshListView;
    private Dialog mProgressDlg;
    //Tools
    private LayoutInflater mInflater;
    private DisplayImageOptions options;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;

    private MusicAdapter mAdapter ;
    private MusicCallBack mCallback ;
    private List<MusicListInfo> mList = new LinkedList<MusicListInfo>();
    private int mCurrentPage=0;

    @Override
    public void onAttach(Activity activity) {
        mCallback = (MusicCallBack) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.music_english_list,null);

        mInflater=inflater;
        mHttpRequest=new BaseHttpRequest();
        options= buldDisplayImageOptions();
        mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.music_englist_list);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                queryList(0);
            }
        });
        // 上拉加载更多
        mPullRefreshListView
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        queryList(mCurrentPage + 1);
                    }
                });

        showProgressDialog();
        queryList(0);

        mAdapter = new MusicAdapter();
        mPullRefreshListView.setAdapter(mAdapter);
        return rootView;
    }

    private void queryList(int page) {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        RequestParams requestParams=new RequestParams();
        requestParams.put("page", page);
        mRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.CELEBRITY_LISTS, requestParams,new RequestCallback(page), BaseHttpRequest.RequestType.GET);
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

            CelebrityListBean bean=Utils.parseJson(content, CelebrityListBean.class);
            if(bean != null){
                if(page == 0){
                    mList.clear();
                }
                for (CelebrityListBean.DatasEntity item:bean.getDatas()){
                    MusicListInfo info = new MusicListInfo();
                    info.setTitle(item.getTitle());
                    info.setContent(item.getContent());
                    //info.setImage("http://bbsapp.static.tpooo.net/2015-10-28/563035aa69348.gif");
                    info.setNumber("123");
                    info.setObjectId(item.getObjectId());
                    mList.add(info);
                }

            }else{
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    };

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


    public DisplayImageOptions buldDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.music_def_icon)
                .showImageForEmptyUri(R.drawable.music_def_icon)
                .showImageOnFail(R.drawable.music_def_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();
    }

    private class MusicAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.music_english_list_item, null);
                viewHolder.content_one = (TextView) convertView.findViewById(R.id.music_content_1);
                viewHolder.content_two = (TextView) convertView.findViewById(R.id.music_content_2);

                viewHolder.title_one = (TextView) convertView.findViewById(R.id.music_title_1);
                viewHolder.title_two = (TextView) convertView.findViewById(R.id.music_title_2);

                viewHolder.image_one = (ImageView) convertView.findViewById(R.id.music_imageView_1);
                viewHolder.image_two = (ImageView) convertView.findViewById(R.id.music_imageView_2);

                viewHolder.number_one = (TextView) convertView.findViewById(R.id.music_number_1);
                viewHolder.number_two = (TextView) convertView.findViewById(R.id.music_number_2);

                viewHolder.rl_music_one = (RelativeLayout) convertView.findViewById(R.id.rl_music_one);
                viewHolder.rl_music_two = (RelativeLayout) convertView.findViewById(R.id.rl_music_two);

                viewHolder.rl_music_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.gotoPosition((int) position);
                    }
                });
                viewHolder.rl_music_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.gotoPosition((int) position);
                    }
                });

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag() ;
            }
            //if(position % 2 == 0){
                viewHolder.title_one.setText(mList.get(position % 2).getTitle());
                viewHolder.content_one.setText(mList.get(position % 2).getContent());
                viewHolder.number_one.setText("0" + "人喜欢");
               // ImageLoader.getInstance().displayImage(mList.get(position).getImage(), viewHolder.image_one, options);
            //}else if(position %2 == 1){
                viewHolder.title_two.setText(mList.get(position % 2 + 1).getTitle());
                viewHolder.content_two.setText(mList.get(position % 2 + 1).getContent());
                viewHolder.number_two.setText("1" + "人喜欢");
               // ImageLoader.getInstance().displayImage(mList.get(position).getImage(), viewHolder.image_two, options);
          //  }


            return convertView;
        }

    }

    static class ViewHolder {
        RelativeLayout rl_music_one,rl_music_two ;
        TextView title_one ,title_two;
        TextView content_one ,content_two;
        ImageView image_one,image_two ;
        TextView number_one,number_two ;
    }

    public List<MusicListInfo> getmList(){
        return mList;
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
