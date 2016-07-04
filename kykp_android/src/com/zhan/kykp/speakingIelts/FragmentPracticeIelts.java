package com.zhan.kykp.speakingIelts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.base.App;
import com.zhan.kykp.db.SpeakingRecordDB;
import com.zhan.kykp.entity.dbobject.SpeakingRecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.NoScrollViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPracticeIelts extends Fragment implements OnPageChangeListener, OnClickListener, OnCompletionListener {

    public final static String TAG = "FragmentPracticeIelts";
    protected static final String ARG_POSITION_KEY = "position";

    private ISpeakingIeltsCallback mCallback;

    // ViewPager 相关
    private int mCurPosition;
    private LayoutInflater mLayoutInflater;
    private List<IeltsData> mList = new ArrayList<IeltsData>();
    private NoScrollViewPager mViewPager;
    private SeakingIeltsAdapter mAdapter;

    private Handler mHandler;

    // Views
    private View mViewContentRecording;
    private View mViewContentBeforeRecording;
    private TextView mTVRecordingTime;

    //录音相关
    private boolean mRecording = false;
    private String mRecordFilePath;
    private Timer mTimer;
    private int mRecordingSecoinds;
    private long mRecordingStartTime;
    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestUpdateAnswer;

    private MediaPlayer mMediaPlayer;
    private AnimationDrawable mAnimSound;

    public void prepareArguments(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_KEY, position);
        setArguments(args);
    }

    @Override
    public void onAttach(Activity activity) {
        mCallback = (ISpeakingIeltsCallback) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurPosition = args.getInt(ARG_POSITION_KEY);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View rootView = mLayoutInflater.inflate(R.layout.speaking_practice_ietls, null);

        mHandler = new Handler();

        mHttpRequest=new BaseHttpRequest();

        mList = mCallback.getIeltsList();//回调中获取list
        mCallback.setSpeakingIeltsTitle(mList.get(mCurPosition).getBook() + " Test" + mList.get(mCurPosition).getTest());//调用回调设置title的默认值

        mViewPager = (NoScrollViewPager) rootView.findViewById(R.id.pager);
        mAdapter = new SeakingIeltsAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurPosition);
        mViewPager.setOnPageChangeListener(this);

        rootView.findViewById(R.id.goto_left).setOnClickListener(this);
        rootView.findViewById(R.id.recording).setOnClickListener(this);
        rootView.findViewById(R.id.goto_right).setOnClickListener(this);

        mTVRecordingTime = (TextView) rootView.findViewById(R.id.recording_time);
        rootView.findViewById(R.id.cancel_recording).setOnClickListener(this);
        rootView.findViewById(R.id.finish_recording).setOnClickListener(this);

        mViewContentBeforeRecording = rootView.findViewById(R.id.content_before_recording);
        mViewContentRecording = rootView.findViewById(R.id.content_recording);

        mViewContentBeforeRecording.setVisibility(View.VISIBLE);
        mViewContentRecording.setVisibility(View.GONE);

        initMediaPlayer();
        mAnimSound = (AnimationDrawable) getResources().getDrawable(R.drawable.sound_anim_right);
        return rootView;
    }

    //退出时资源释放（这里是音频释放）
    @Override
    public void onDestroyView() {
        release();
        super.onDestroyView();
    }


    private void release() {
        // 为完成的录音，清除数据
        cancelRecording();
        releaseMedia();
        BaseHttpRequest.releaseRequest(mRequestUpdateAnswer);
    }


    private void releaseMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException : " + e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException : " + e.getMessage());
        }
    }


    private class SeakingIeltsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(View container, int position) {

            View view = mLayoutInflater.inflate(R.layout.speaking_practice_ielts_layout, null, false);
            View content = view.findViewById(R.id.content);
            switch (position % 3) {
                case 0:
                    content.setBackgroundResource(R.drawable.ielts_card1);
                    break;
                case 1:
                    content.setBackgroundResource(R.drawable.ielts_card2);
                    break;
                case 2:
                    content.setBackgroundResource(R.drawable.ielts_card3);
                    break;
            }

            ((ViewPager) container).addView(view);

            TextView topic = (TextView) view.findViewById(R.id.topic);
            TextView question = (TextView) view.findViewById(R.id.question);
            question.setMovementMethod(ScrollingMovementMethod.getInstance());

            topic.setText(mList.get(position).getQuestionType());
            question.setText(mList.get(position).getQuestion());

            return view;
        }
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mCurPosition = arg0;
        mCallback.setSpeakingIeltsTitle(mList.get(mCurPosition).getBook() + " Test" + mList.get(mCurPosition).getTest());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_left:
                if (mCurPosition > 0) {
                    mViewPager.setCurrentItem(mCurPosition - 1);
                }
                StatisticUtils.onEvent("雅思题库答题页", "上一题");
                break;
            case R.id.goto_right:
                if (mCurPosition < mList.size() - 1) {
                    mViewPager.setCurrentItem(mCurPosition + 1);
                }
                StatisticUtils.onEvent("雅思题库答题页", "下一题");
                break;
            case R.id.recording:
                startRecording();
                StatisticUtils.onEvent("雅思题库答题页", "开始录音");
                break;
            case R.id.cancel_recording:
                cancelRecording();
                StatisticUtils.onEvent("雅思题库答题页", "取消录音");
                break;
            case R.id.finish_recording:
                StatisticUtils.onEvent("雅思题库答题页", "完成录音");
                stopRecording();
                break;
        }

    }

    private void startRecording() {
        mViewContentBeforeRecording.setVisibility(View.GONE);
        mViewContentRecording.setVisibility(View.VISIBLE);
        mViewPager.setNoScroll(true);
        mRecording = true;
        mRecordingStartTime = System.currentTimeMillis();
        mRecordFilePath = getrecordFilePath();
        MediaRecordFunc.getInstance().startRecordAndFile(mRecordFilePath);
        mTimer = new Timer(true);
        mRecordingSecoinds = 0;
        TimerTask timerTask = new TimerTask() {
            public void run() {
                mRecordingSecoinds++;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTVRecordingTime.setText(String.valueOf(mRecordingSecoinds));
                    }
                });

                //超过45秒自动停止
                if (mRecordingSecoinds == 45) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopRecording();
                        }
                    });
                }
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
    }

    private void stopRecording() {
        mViewPager.setNoScroll(false);
        mViewContentBeforeRecording.setVisibility(View.VISIBLE);
        mViewContentRecording.setVisibility(View.GONE);
        mRecording = false;
        MediaRecordFunc.getInstance().stopRecordAndFile();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mRecordingSecoinds < 35) {
            File recordFile = new File(mRecordFilePath);
            if (recordFile.exists()) {
                recordFile.delete();
            }
            Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
            dialog.setContentView(R.layout.speaking_dialog_invalid_recording_view);
            dialog.show();
        } else {

            //更新为已回答
            updateAnswerStatus();

            final Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
            View dlgContent = mLayoutInflater.inflate(R.layout.speaking_dialog_result_view, null);
            TextView tvRecordingSeconds = (TextView) dlgContent.findViewById(R.id.recording_seconds);

            ImageView imgSound = (ImageView) dlgContent.findViewById(R.id.img_sound);
            imgSound.setBackground(mAnimSound);

            //保存录音
            tvRecordingSeconds.setText(String.format("%s\"", mRecordingSecoinds));
            dlgContent.findViewById(R.id.save_recording).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    stopMedia();
                    saveRecordData();
                    Utils.toast(R.string.speaking_save_success);

                    StatisticUtils.onEvent("雅思题库答题页", "保存录音");
                }
            });

            //取消录音
            Button button = (Button) dlgContent.findViewById(R.id.apply_for_correcting);
            button.setText(R.string.cancel);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    File recordFile = new File(mRecordFilePath);
                    if (recordFile.exists()) {
                        recordFile.delete();
                    }
                    dialog.dismiss();

                    StatisticUtils.onEvent("雅思题库答题页", "取消保存录音");
                }
            });

            //播放录音
            dlgContent.findViewById(R.id.recording_info_content).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //播放/停止录音
                    if (mMediaPlayer.isPlaying()) {
                        stopMedia();
                    } else {
                        play(mRecordFilePath);
                    }

                    StatisticUtils.onEvent("雅思题库答题页", "播放/停止答题录音");
                }
            });

            dialog.setContentView(dlgContent);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    protected void saveRecordData() {

        IeltsData ieltsData = mList.get(mCurPosition);

        SpeakingRecord data = new SpeakingRecord();
        data.questionObjectId = ieltsData.objectId;
        data.userObjectId = UserInfo.getCurrentUser().getObjectId();
        data.imgKeyWord = ieltsData.Test;
        data.title = ieltsData.Question;

        data.subTitle = ieltsData.getBook() + " Test " + ieltsData.getTest() + " Part " + ieltsData.getPart();

        data.questionType = SpeakingRecord.TYPE_Ielts;
        data.recordingFilePath = mRecordFilePath;
        data.recordingSeconds = mRecordingSecoinds;
        data.startTime = mRecordingStartTime;

        SpeakingRecordDB speakingRecordDB = new SpeakingRecordDB(App.ctx);
        speakingRecordDB.save(data);
    }

    private void play(String audioFile) {
        mAnimSound.start();
        try {
            mMediaPlayer.setDataSource(audioFile);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException : " + e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException : " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException : " + e.getMessage());
        }
    }

    protected void stopMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        mAnimSound.stop();
    }

    private void cancelRecording() {
        mViewPager.setNoScroll(false);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mRecording) {
            mViewContentBeforeRecording.setVisibility(View.VISIBLE);
            mViewContentRecording.setVisibility(View.GONE);

            MediaRecordFunc.getInstance().stopRecordAndFile();
            mRecording = false;
            File recordFile = new File(mRecordFilePath);
            if (recordFile.exists()) {
                recordFile.delete();
            }
        }
    }

    private String getrecordFilePath() {
        String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
        String filePath = recordDir + "/" + System.currentTimeMillis() + ".amr";
        return filePath;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mAnimSound.stop();
    }

    private void updateAnswerStatus() {
        if(mRequestUpdateAnswer!=null&&!mRequestUpdateAnswer.isFinished()){
            return;
        }
        Log.i(TAG, "updateAnswerStatus");

        RequestParams requestParams = new RequestParams();
        requestParams.put("user",  UserInfo.getCurrentUser().getObjectId());
        requestParams.put("type", 0 );//0雅思;1托福;2机经
        requestParams.put("speakingid", mList.get(mCurPosition).objectId);

        mRequestUpdateAnswer = mHttpRequest.startRequest(getActivity(), ApiUrls.SPEAKINGPRACTICE_ANSWER_STATUS, requestParams, null, BaseHttpRequest.RequestType.POST);
    }
}
