package com.zhan.kykp.music;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.FileDownloadListener;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.CelebrityDetails;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.NoScrollViewPager;
import com.zhan.kykp.widget.SampleProgressView;
import com.zhan.kykp.widget.ViewPagerDotIndicator;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MusicDetailsFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    protected static final String ARG_POSITION_KEY = "position";

    private ViewPagerDotIndicator mViewPagerDotIndicator ;
    private ScrollView mScrollView ;
    private TextView mTVTitle ;
    private TextView mTvContent ;
    private TextView mTvAudioPlayPosition ;
    private TextView mTvAudioTotalTime ;
    private SampleProgressView mAudioProgress ;
    private ImageView mImgAudioPlayStop ;
    private ImageView mImgPreArticle ;
    private ImageView mImgNextArticle ;
    private ImageView mImgIconPraise ;
    private TextView mTvPraiseNumber ;
    // ViewPager 相关
    private NoScrollViewPager mViewPager;
    private MusicViewPagerAdpater mViewPagerAdapter ;
    private int mCurIndex = 0;

    //点赞
    private View mPraiseConetnt ;

    private MusicCallBack mCallback ;
    private int mCurPosition;

    private MediaPlayerHelper mMediaPlayerHelper;
    private Timer mPlayAudioTimer;
    private TimerTask mPlayAudioTask;

    //网络
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;
    private RequestHandle mDownloadRequestHandle ;
    private RequestHandle mPraiseHandle ;

    //list
    private CelebrityDetails.DatasEntity mArticle;

    private Dialog mProgressDlg;
    private Dialog mDownloaddProgressDlg;


    public void prepareArguments(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_KEY, position);
        setArguments(args);
    }

    public void onAttach(Activity activity) {
        mCallback = (MusicCallBack)activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurPosition = args.getInt(ARG_POSITION_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.music_frag_details, null);
        mViewPagerDotIndicator = (ViewPagerDotIndicator) rootView.findViewById(R.id.music_detitle_pager_indicator);
        mScrollView=(ScrollView)rootView.findViewById(R.id.scroll_content);
       // mTVTitle = (TextView) rootView.findViewById(R.id.music_detitle_title);
        //mTvContent = (TextView) rootView.findViewById(R.id.music_detitle_content);
       // Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "timesi.ttf");
       // mTvContent.setTypeface(typeface);

        mViewPager = (NoScrollViewPager) rootView.findViewById(R.id.music_pager);
//        mViewPager.setPageTransformer(true, new PracticeTransformer());
        mViewPagerAdapter = new MusicViewPagerAdpater();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageSelected(int arg0) {
                mCurIndex = arg0;
                mViewPagerDotIndicator.setSelectIndex(mCurIndex);
            }
        });
        mViewPagerAdapter.notifyDataSetChanged();

        mTvAudioPlayPosition = (TextView) rootView.findViewById(R.id.music_audio_play_position);
        mAudioProgress = (SampleProgressView) rootView.findViewById(R.id.music_audio_progress_view);
        mTvAudioTotalTime = (TextView) rootView.findViewById(R.id.music_audio_total_time);
        mImgAudioPlayStop = (ImageView) rootView.findViewById(R.id.music_aduio_play_stop);
        mImgPreArticle = (ImageView) rootView.findViewById(R.id.music_article);
        mImgNextArticle = (ImageView) rootView.findViewById(R.id.music_next_article);

        mImgAudioPlayStop.setOnClickListener(this);
        mImgPreArticle.setOnClickListener(this);
        mImgNextArticle.setOnClickListener(this);

        mPraiseConetnt = rootView.findViewById(R.id.music_praise_conetnt);
        mPraiseConetnt.setOnClickListener(this);

        mImgIconPraise = (ImageView) rootView.findViewById(R.id.music_icon_praise);
        mTvPraiseNumber = (TextView) rootView.findViewById(R.id.music_praise_number);

        mHttpRequest = new BaseHttpRequest();
        mMediaPlayerHelper = new MediaPlayerHelper(this);

        queryDetails(mCurPosition);

        return rootView;
    }

    private class MusicViewPagerAdpater extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.music_viewpage_item, null, false);
            ((ViewPager) container).addView(view);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.music_detitle_title = (TextView)view.findViewById(R.id.music_detitle_title);
            viewHolder.music_detitle_content = (TextView) view.findViewById(R.id.music_detitle_content);

           // viewHolder.music_detitle_title.setText("title测试数据");
           // viewHolder.music_detitle_content.setText("content测试数据");
            //Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "timesi.ttf");
           // viewHolder.music_detitle_content.setTypeface(typeface);
            return view;
        }
    }

    static class ViewHolder {
        TextView music_detitle_title ;
        TextView music_detitle_content ;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayerHelper.isPlaying()) {
            pauseAudio();
        }
    }

    @Override
    public void onDestroyView() {
        releaseAllRequest();
        mMediaPlayerHelper.releaseMedia();
        super.onDestroyView();
    }

    private void releaseAllRequest() {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        BaseHttpRequest.releaseRequest(mDownloadRequestHandle);
        BaseHttpRequest.releaseRequest(mPraiseHandle);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    private void queryDetails(int position) {
        //停止录音
        stopAudio();
        //释放所有请求
        releaseAllRequest();
        //显示加载对话框
        showProgressDialog();
        //开始请求详情
        MusicListInfo info = mCallback.getListCallBack().get(position);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("celebrity", info.getObjectId());
        mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.CELEBRITY_DETAIL, requestParams, new DetailsHttpRequestCallback(position), BaseHttpRequest.RequestType.GET);
    }

    private class DetailsHttpRequestCallback implements HttpRequestCallback {

        private int mPosition ;

        public DetailsHttpRequestCallback(int position) {
            this.mPosition = position;
        }

        @Override
        public void onRequestFailed(String errorMsg) {
            closeDialog();
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

            CelebrityDetails bean = Utils.parseJson(content, CelebrityDetails.class);
            if (bean != null && bean.getDatas() != null) {
                mCurPosition = mPosition;
                mArticle = bean.getDatas();
                mViewPagerDotIndicator.setPageCount(4);//设置圆点数
                mViewPagerAdapter.notifyDataSetChanged();
                populateView();
            } else {
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    }

    private void populateView() {
       // mTVTitle.setText(mArticle.getTitle());
       // mTvContent.setText(mArticle.getContent());
        mScrollView.scrollTo(0, 0);
        mTvAudioTotalTime.setText(String.format("%02d:%02d", mArticle.getAudio().getTime() / 60, mArticle.getAudio().getTime() % 60));
        if(mArticle.getIspraise()==1){
            mImgIconPraise.setImageResource(R.drawable.celebrity_praised);
        }else{
            mImgIconPraise.setImageResource(R.drawable.celebrity_unpraise);
        }
        mTvPraiseNumber.setText(String.valueOf(mArticle.getPraise()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_aduio_play_stop:
                if(mMediaPlayerHelper.isPaused()){
                    replayAudio();
                }else if (mMediaPlayerHelper.isPlaying()) {
                    pauseAudio();
                } else {
                    //停止状态,开始播放
                    downAndPlayAudio();
                }
                break;
            case R.id.music_article:
                gotoPreviousArticle();
                break;
            case R.id.music_next_article:
                gotoNextArticle();
                break;
        }
    }

    private void gotoNextArticle() {
        if (mCurPosition < mCallback.getListCallBack().size() - 1) {
            queryDetails(mCurPosition + 1);
        }
    }

    private void gotoPreviousArticle() {
        if (mCurPosition > 0) {
            queryDetails(mCurPosition - 1);
        }
    }


    private void replayAudio() {
        mMediaPlayerHelper.replay();
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_pause_audio_selector);
        startAudioPlayTimer();
    }
    private void pauseAudio() {
        mMediaPlayerHelper.pause();
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_play_audio_selector);
        releaseMyRecordingTimer();
    }

    private void downAndPlayAudio() {
        if(mArticle == null){
            return;
        }
        if (TextUtils.isEmpty(mArticle.getAudio().getUrl())) {
            Utils.toast(R.string.celebrity_download_audio_path_null);
            return;
        }
        //怎在下载中 不处理
        if(mDownloadRequestHandle!=null&&!mDownloadRequestHandle.isFinished()){
            return;
        }

        //判断文件是否存在，存在播放
        if (hasDownloadAudio(mArticle.getObjectId())) {
            playDownloadedAudio();
        } else {
            stopAudio();
            showDownloadingDialog();
            mDownloadRequestHandle = mHttpRequest.downloadFile(getActivity(), mArticle.getAudio().getUrl(), getAudioPath(mArticle.getObjectId()), mAudioDownloadListener);
        }
    }

    private FileDownloadListener mAudioDownloadListener = new FileDownloadListener() {

        @Override
        public void onDownloadFailed(String errorMsg) {
            closeDownloadingDialog();
            Utils.toast(errorMsg);
            if(mArticle!=null){
                deleteAudioFile(mArticle.getObjectId());
            }
        }

        @Override
        public void onNoNetwork() {
            closeDownloadingDialog();
            Utils.toast(R.string.network_error);
            if(mArticle!=null){
                deleteAudioFile(mArticle.getObjectId());
            }
        }

        @Override
        public void onCanceled() {
            closeDownloadingDialog();
            if(mArticle!=null){
                deleteAudioFile(mArticle.getObjectId());
            }
        }

        @Override
        public void onDownloadSuccess(File downFile) {
            closeDownloadingDialog();
            if (hasDownloadAudio(mArticle.getObjectId())) {
                playDownloadedAudio();
            }
        }
    };

    private void deleteAudioFile(String objectId) {
        File audioFile = new File(getAudioPath(objectId));
        if (audioFile.exists()) {
            audioFile.delete();
        }
    }

    private void playDownloadedAudio() {
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_pause_audio_selector);
        // 开始播放
        mMediaPlayerHelper.play(getAudioPath(mArticle.getObjectId()));
        // 进度
        mAudioProgress.setProgress(0);
        mTvAudioPlayPosition.setText("00:00");
        startAudioPlayTimer();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        stopAudio();
    }

    private void stopAudio() {
        //if (mMediaPlayerHelper.isPlaying()) {
        mMediaPlayerHelper.stopMedia();
        //}
        releaseMyRecordingTimer();
        mAudioProgress.setProgress(0);
        mTvAudioPlayPosition.setText("00:00");
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_play_audio_selector);
    }

    private void startAudioPlayTimer() {
        releaseMyRecordingTimer();
        mPlayAudioTimer = new Timer();
        mPlayAudioTask = new TimerTask() {
            @Override
            public void run() {
                if (!mMediaPlayerHelper.isReleased() && mMediaPlayerHelper.isPlaying()) {
                    int progress = (int) (100f * mMediaPlayerHelper.getCurrentPosition() / mMediaPlayerHelper.getDuration());
                    mAudioProgress.setProgress(progress);

                    //设置当前播放时间
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mMediaPlayerHelper==null||mMediaPlayerHelper.isReleased()){
                                    return;
                                }
                                int seconds = mMediaPlayerHelper.getCurrentPosition() / 1000;
                                mTvAudioPlayPosition.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                            }
                        });
                    }
                }
            }
        };
        mPlayAudioTimer.schedule(mPlayAudioTask, 0, 1000);
    }
    private void releaseMyRecordingTimer() {
        if (mPlayAudioTimer != null) {
            mPlayAudioTimer.cancel();
            mPlayAudioTimer = null;
        }

        if (mPlayAudioTask != null) {
            mPlayAudioTask.cancel();
            mPlayAudioTask = null;
        }
    }

    private String getAudioPath(String objectId) {
        String celebrityPath = PathUtils.getExternalMusicFileDir().getAbsolutePath();
        return celebrityPath + "/" + objectId + ".mp3";
    }

    private boolean hasDownloadAudio(String objectId) {
        File audioFile = new File(getAudioPath(objectId));
        return audioFile.exists();
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(getActivity(), getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    private void showDownloadingDialog() {
        mDownloaddProgressDlg = DialogUtils.getCircleProgressDialog(getActivity(), getString(R.string.celebrity_downloading_audio));
        mDownloaddProgressDlg.show();
        mDownloaddProgressDlg.setCanceledOnTouchOutside(false);
    }

    private void closeDownloadingDialog() {
        if (mDownloaddProgressDlg != null) {
            mDownloaddProgressDlg.dismiss();
            mDownloaddProgressDlg = null;
        }
    }

}
