package com.zhan.kykp.TPO;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;


import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.App;
import com.zhan.kykp.db.TPORecordDB;
import com.zhan.kykp.entity.dbobject.TPORecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.widget.ProgressWheel;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentQuestion extends BaseTPOFragment {
    private final static String TAG = FragmentQuestion.class.getSimpleName();

    private ProgressWheel mProgressWheel;
    private ImageView mBtnRecord;
    private int mPrepareSeconds;
    private int mRecordingSeconds;

    private CountDownTimer mPrepareTimer;
    private CountDownTimer mRecordingTimer;

    private Dialog mProgressDlg;

    private int mTask;

    //录音相关
    private boolean mStartRecording = false;
    private long mRecordingStartTime;
    private String mRecordPath;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mUploadRequestHandle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tpo_question_layout, null);
        TextView tvTitle = (TextView) rootView.findViewById(R.id.title);

        mHttpRequest = new BaseHttpRequest();

        mProgressWheel = (ProgressWheel) rootView.findViewById(R.id.btn_progress);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mBtnRecord = (ImageView) rootView.findViewById(R.id.btn_record);
        mBtnRecord.setVisibility(View.INVISIBLE);

        JSONObject tpoJson = mTPOCallback.getTPOJson();

        String jsonAttr = null;
        try {
            if (mQuestionIndex.equals(TPOConstant.Q1)) {
                mTask = 1;
                play(TPOConstant.speaking_question1);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK1;
                mPrepareSeconds = 15;
                mRecordingSeconds = 45;

                StatisticUtils.onEvent("口语模考详情页", "Q1");

            } else if (mQuestionIndex.equals(TPOConstant.Q2)) {
                mTask = 2;
                play(TPOConstant.speaking_question2);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK2;
                mPrepareSeconds = 15;
                mRecordingSeconds = 45;

                StatisticUtils.onEvent("口语模考详情页", "Q2");
            } else if (mQuestionIndex.equals(TPOConstant.Q3)) {
                mTask = 3;
                play(TPOConstant.speaking_question3);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK3_QUESTION;
                mPrepareSeconds = 30;
                mRecordingSeconds = 60;

                StatisticUtils.onEvent("口语模考详情页", "Q3");
            } else if (mQuestionIndex.equals(TPOConstant.Q4)) {
                mTask = 4;
                play(TPOConstant.speaking_question4);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK4_QUESTION;
                mPrepareSeconds = 30;
                mRecordingSeconds = 60;

                StatisticUtils.onEvent("口语模考详情页", "Q4");
            } else if (mQuestionIndex.equals(TPOConstant.Q5)) {
                mTask = 5;
                play(TPOConstant.speaking_question5);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK5_QUESTION;
                mPrepareSeconds = 20;
                mRecordingSeconds = 60;

                StatisticUtils.onEvent("口语模考详情页", "Q5");
            } else if (mQuestionIndex.equals(TPOConstant.Q6)) {
                mTask = 6;
                play(TPOConstant.speaking_question6);
                jsonAttr = TPOConstant.TPO_JSON_ATTR_TASK6_QUESTION;
                mPrepareSeconds = 20;
                mRecordingSeconds = 60;

                StatisticUtils.onEvent("口语模考详情页", "Q6");
            }

            String task = tpoJson.getString(jsonAttr);
            tvTitle.setText(task);
            //tvTitle.setMovementMethod(ScrollingMovementMethod.getInstance());
        } catch (JSONException e) {
            Log.i(TAG, "Error parse json : " + jsonAttr);
        }
        return rootView;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        prepareTask();
    }

    private void prepareTask() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.setSummary("Preparation Time");
        mPrepareTimer = new CountDownTimer(1000 * mPrepareSeconds, 100) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                String title = "" + seconds;
                mProgressWheel.setText(title);
                mProgressWheel.setProgress((int) (360 - millisUntilFinished / (1000f * mPrepareSeconds) * 360));
            }

            public void onFinish() {
                mProgressWheel.setProgress(360);
                showStartRecordingDlg();
                mPrepareTimer = null;
            }
        }.start();
    }

    private void showStartRecordingDlg() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View rootView = inflater.inflate(R.layout.tpo_recording_start_dlg, null);
        final ImageView animImg = (ImageView) rootView.findViewById(R.id.bird_fly);
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);

        dialog.setContentView(rootView);
        dialog.setCancelable(false);
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                final int width = animImg.getWidth();
                final int dlgwidth = rootView.getWidth();

                ObjectAnimator anim = ObjectAnimator.ofFloat(animImg, "zhy", -width, dlgwidth).setDuration(1600);
                anim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float cVal = (Float) animation.getAnimatedValue();
                        animImg.setTranslationX((int) cVal);
                    }
                });
                anim.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationCancel(Animator arg0) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        dialog.dismiss();
                        startAnimatorToRecording();
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override
                    public void onAnimationStart(Animator arg0) {
                    }
                });
                anim.start();
            }
        });
        dialog.show();
    }


    private void startAnimatorToRecording() {
        ValueAnimator animator = ValueAnimator.ofFloat(360, 0);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float value = (Float) animation.getAnimatedValue();
                        mProgressWheel.setProgress((int) value);
                    }
                });
            }
        });
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordingTask();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationStart(Animator arg0) {

            }
        });

        animator.setDuration(600).start();

    }

    @Override
    public void release() {
        super.release();
        if (mPrepareTimer != null) {
            mPrepareTimer.cancel();
            mPrepareTimer = null;
        }
        if (mRecordingTimer != null) {
            mRecordingTimer.cancel();
            mRecordingTimer = null;
        }
        if (mStartRecording) {
            MediaRecordFunc.getInstance().stopRecordAndFile();
            File file = new File(mRecordPath);
            if (file.exists()) {
                file.delete();
            }
            //saveRecordData();
            mStartRecording = false;
        }

    }

    private void recordingTask() {
        //mBtnRecord.setImageResource(R.drawable.recording);
        mBtnRecord.setVisibility(View.VISIBLE);

        mStartRecording = true;
        mRecordingStartTime = System.currentTimeMillis();
        mRecordPath = getrecordFilePath();
        MediaRecordFunc.getInstance().startRecordAndFile(mRecordPath);

        mProgressWheel.setSummary("Recording Time");

        mRecordingTimer = new CountDownTimer(1000 * mRecordingSeconds, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                String title = "" + seconds;
                mProgressWheel.setText(title);
                mProgressWheel.setProgress((int) (360 - millisUntilFinished / (1000f * mRecordingSeconds) * 360));
            }

            @Override
            public void onFinish() {
                mRecordingTimer = null;
                //mBtnRecord.setImageResource(R.drawable.recording);
                mBtnRecord.setVisibility(View.VISIBLE);
                MediaRecordFunc.getInstance().stopRecordAndFile();
                saveRecordData();
                mStartRecording = false;

                showProgressDialog();
                BaseHttpRequest.releaseRequest(mUploadRequestHandle);
                try {
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("tpo", mTPOCallback.getTPOName());
                    requestParams.put("task", mTask);
                    requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
                    requestParams.put("audio", new File(mRecordPath));

                    mUploadRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.TPO_SAVE, requestParams, mUploadRecordingCallback, BaseHttpRequest.RequestType.POST);
                } catch (FileNotFoundException e) {
                    closeDialog();
                    Log.e(TAG, "uploadMyRecording FileNotFoundException : " + mRecordPath);
                }
            }
        }.start();

    }

    private HttpRequestCallback mUploadRecordingCallback=new HttpRequestCallback(){
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
        }
    };

    private void saveRecordData() {
        TPORecord data = new TPORecord();
        data.UserObject = UserInfo.getCurrentUser().getObjectId();
        data.TPOName = mTPOCallback.getTPOName();
        data.TPOIndex = mTPOCallback.getTPOIndex();
        data.QuestionIndex = mQuestionIndex;
        data.StartTime = mRecordingStartTime;
        data.RecordingSeconds = mRecordingSeconds;
        data.RecordFilePath = mRecordPath;

        TPORecordDB TPORecordDB = new TPORecordDB(App.ctx);
        TPORecordDB.save(data);
    }

    private String getrecordFilePath() {
        String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
        String filePath = recordDir + "/" + System.currentTimeMillis() + ".amr";
        return filePath;
    }

    private void showProgressDialog(){
        mProgressDlg = DialogUtils.getCircleProgressDialog(this.getActivity(), getString(R.string.tpo_saving_recording));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeDialog(){
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
            mTPOCallback.gotoNext();
        }
    }
}
