package com.zhan.kykp.celebrityEnglish;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.CelebrityDetails;
import com.zhan.kykp.network.bean.CelebrityPraise;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.MediaPlayerHelper;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.SampleProgressView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityDetailsFragment extends Fragment implements MediaPlayer.OnCompletionListener, View.OnClickListener {
    protected static final String ARG_POSITION_KEY = "position";

    private ICelebrityCallback mCallback;
    private int mCurPosition;

    //View 相关
    private ScrollView mScrollView;
    private TextView mTVTitle;
    private TextView mTvContent;
    private TextView mTvAudioTotalTime;
    private TextView mTvAudioPlayPosition;
    private ImageView mImgAudioPlayStop;
    private ImageView mImgPreArticle;
    private ImageView mImgNextArticle;
    private SampleProgressView mAudioProgress;
    //赞
    private View mPraiseConetnt;
    private ImageView mImgIconPraise;
    private TextView mTvPraiseNumber;
    private Dialog mProgressDlg;
    private Dialog mDownloaddProgressDlg;

    //Data
    private CelebrityDetails.DatasEntity mArticle;

    //Tools
    private MediaPlayerHelper mMediaPlayerHelper;
    private Timer mPlayAudioTimer;
    private TimerTask mPlayAudioTask;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;
    private RequestHandle mDownloadRequestHandle;
    private RequestHandle mPraiseHandle;

    public void prepareArguments(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_KEY, position);
        setArguments(args);
    }

    @Override
    public void onAttach(Activity activity) {
        mCallback = (ICelebrityCallback) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurPosition = args.getInt(ARG_POSITION_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.celebrity_frag_details, null);
        mScrollView=(ScrollView)rootView.findViewById(R.id.scroll_content);
        mTVTitle = (TextView) rootView.findViewById(R.id.title);
        mTvContent = (TextView) rootView.findViewById(R.id.content);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "timesi.ttf");
        mTvContent.setTypeface(typeface);

        mTvAudioPlayPosition = (TextView) rootView.findViewById(R.id.audio_play_position);
        mAudioProgress = (SampleProgressView) rootView.findViewById(R.id.audio_progress_view);
        mTvAudioTotalTime = (TextView) rootView.findViewById(R.id.audio_total_time);
        mImgAudioPlayStop = (ImageView) rootView.findViewById(R.id.aduio_play_stop);
        mImgPreArticle = (ImageView) rootView.findViewById(R.id.pre_article);
        mImgNextArticle = (ImageView) rootView.findViewById(R.id.next_article);
        mImgAudioPlayStop.setOnClickListener(this);
        mImgPreArticle.setOnClickListener(this);
        mImgNextArticle.setOnClickListener(this);

        mPraiseConetnt = rootView.findViewById(R.id.praise_conetnt);
        mPraiseConetnt.setOnClickListener(this);
        mImgIconPraise = (ImageView) rootView.findViewById(R.id.icon_praise);
        mTvPraiseNumber = (TextView) rootView.findViewById(R.id.praise_number);

        mHttpRequest = new BaseHttpRequest();
        mMediaPlayerHelper = new MediaPlayerHelper(this);

        queryDetails(mCurPosition);

        return rootView;
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

    private void releaseAllRequest(){
        BaseHttpRequest.releaseRequest(mRequestHandle);
        BaseHttpRequest.releaseRequest(mDownloadRequestHandle);
        BaseHttpRequest.releaseRequest(mPraiseHandle);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aduio_play_stop:
                StatisticUtils.onEvent(R.string.home_page_person_eng, R.string.statistic_goto_details_page,R.string.statistic_play_stop_audio);
                //暂停状态
                if(mMediaPlayerHelper.isPaused()){
                    replayAudio();
                }else if (mMediaPlayerHelper.isPlaying()) {
                    pauseAudio();
                } else {
                    //停止状态,开始播放
                    downAndPlayAudio();
                }
                break;
            case R.id.pre_article:
                gotoPreviousArticle();
                break;
            case R.id.next_article:
                gotoNextArticle();
                break;
            case R.id.praise_conetnt:
                StatisticUtils.onEvent(R.string.home_page_person_eng, R.string.statistic_goto_details_page,R.string.statistic_praise);
                praiseArticle();
                break;
        }
    }

    private void queryDetails(int position) {
        //停止录音
        stopAudio();

        //释放所有请求
        releaseAllRequest();

        //显示加载对话框
        showProgressDialog();

        //开始请求详情
        CelebritySummaryInfo summaryInfo = mCallback.getCelebrityList().get(position);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("celebrity", summaryInfo.getObjectId());

        mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.CELEBRITY_DETAIL, requestParams, new DetailsHttpRequestCallback(position), BaseHttpRequest.RequestType.GET);
    }

    private class DetailsHttpRequestCallback implements HttpRequestCallback {
        private int mPosition;

        public DetailsHttpRequestCallback(int position) {
            mPosition = position;
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
                populateView();
            } else {
                Utils.toast(R.string.network_query_data_failed);
            }
        }
    }

    private void gotoNextArticle() {
        if (mCurPosition < mCallback.getCelebrityList().size() - 1) {
            queryDetails(mCurPosition + 1);
        }
    }

    private void gotoPreviousArticle() {
        if (mCurPosition > 0) {
            queryDetails(mCurPosition - 1);
        }
    }

    private void populateView() {
        mTVTitle.setText(mArticle.getTitle());
        mTvContent.setText(mArticle.getContent());
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
    public void onCompletion(MediaPlayer mp) {
        stopAudio();
    }

    private void downAndPlayAudio() {
        if (mArticle == null) {
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

    /***
     * 点赞文章
     */
    private void praiseArticle(){

        if (mArticle == null) {
            return;
        }

        if(mArticle.getIspraise()==1){
            Utils.toast(R.string.celebrity_has_praised);
            return;
        }

        if(mPraiseHandle!=null&&!mPraiseHandle.isFinished()){
            return;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("celebrity", mArticle.getObjectId());

        mPraiseHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.CELEBRITY_PRAISE, requestParams, new HttpRequestCallback(){

            @Override
            public void onRequestFailed(String errorMsg) {
                Utils.toast(errorMsg);
            }

            @Override
            public void onRequestFailedNoNetwork() {
                Utils.toast(R.string.no_network_error);
            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                BaseBean baseBean=Utils.parseJson(content, BaseBean.class);
                if(baseBean!=null&&baseBean.getStatus()==1){
                    CelebrityPraise bean=Utils.parseJson(content, CelebrityPraise.class);

                    Utils.toast(R.string.celebrity_praise_success);

                    mArticle.setIspraise(1);
                    mArticle.setPraise(bean.getDatas().getPraise());

                    mImgIconPraise.setImageResource(R.drawable.celebrity_praised);
                    mTvPraiseNumber.setText(String.valueOf(bean.getDatas().getPraise()));
                }else if(baseBean!=null){
                    Utils.toast(baseBean.getMessage());
                }
            }
        }, BaseHttpRequest.RequestType.POST);
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

    private void startAudioPlayTimer(){
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

    private void pauseAudio(){
        mMediaPlayerHelper.pause();
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_play_audio_selector);
        releaseMyRecordingTimer();
    }

    private void replayAudio(){
        mMediaPlayerHelper.replay();
        mImgAudioPlayStop.setImageResource(R.drawable.celebrity_pause_audio_selector);
        startAudioPlayTimer();
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
        String celebrityPath = PathUtils.getExternalCelebrityFilesDir().getAbsolutePath();
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
