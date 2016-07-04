package com.zhan.kykp;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.MXK.MXKActivity;
import com.zhan.kykp.TPO.TPOListActivity;
import com.zhan.kykp.abroad.AbroadStrategyActivity;
import com.zhan.kykp.celebrityEnglish.CelebrityActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.music.MusicEnglishActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.ExamBookingBean;
import com.zhan.kykp.network.bean.ExamTimeListBean;
import com.zhan.kykp.network.bean.UserIsSignBean;
import com.zhan.kykp.practice.PracticeListActivity;
import com.zhan.kykp.speaking.SpeakingMainActivity;
import com.zhan.kykp.spokenSquare.SpokenSquareActivity;
import com.zhan.kykp.subscribe.SubscribeActivity;
import com.zhan.kykp.userCenter.LoginActivity;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.DailyView;
import com.zhan.kykp.widget.PullToZoom.PullToZoomScrollViewEx;
import com.zhan.kykp.widget.wheel.ArrayWheelAdapter;
import com.zhan.kykp.widget.wheel.WheelView;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomePageFragment extends Fragment implements OnClickListener {

    // pop menu
    private PopupWindow mPopupWindow;
    private LayoutInflater mInflater;

    //View
    private DailyView mHeaderView;
    private View mSetExamTimeContent;
    private View mUpdateExamTimeContent;
    private TextView mTVDaysLeftFromExam;
    private View mBtnOrderExam;

    //Network
    private BaseHttpRequest mHttpRequest;
    private RequestHandle mRequestHandle;
    private RequestHandle mIsSignHandle;
    private RequestHandle mSetExamTimeHandle;
    private RequestHandle mExamBookingHandle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	PullToZoomScrollViewEx contentView =(PullToZoomScrollViewEx)inflater.inflate(R.layout.frag_layout_home_page, null);

        mHeaderView = (DailyView)inflater.inflate(R.layout.home_page_header, null);
    	View pageContentView = inflater.inflate(R.layout.home_page_content, null);
    	ImageView zoomView = new ImageView(getActivity());

        Calendar cal = Calendar.getInstance();//当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = 5 * 60 ;// 起始时间 5:00的分钟数
        final int end = 12 * 60;// 结束时间 12:00的分钟数
        final int three = 19 * 60 ; //结束时间 19:00的分钟数

        if (minuteOfDay >= start && minuteOfDay <= end) {
            zoomView.setImageResource(R.drawable.bg_daily_view_one);
        } else if(minuteOfDay >= end && minuteOfDay <= three){
            zoomView.setImageResource(R.drawable.bg_daily_view_two);
        }else{
            zoomView.setImageResource(R.drawable.bg_daily_view_three);
        }

    	zoomView.setScaleType(ScaleType.CENTER_CROP);

        mHttpRequest=new BaseHttpRequest();
        mInflater=inflater;

        intiPopMenu();
    	
    	contentView.setHeaderView(mHeaderView);
    	contentView.setScrollContentView(pageContentView);
    	contentView.setZoomView(zoomView);
    	
        View btnSpeaking = pageContentView.findViewById(R.id.ll_speaking);
        View btnPractice = pageContentView.findViewById(R.id.ll_practice);
        View btnTPO = pageContentView.findViewById(R.id.ll_tpo);
        View btnSpokenSquare = pageContentView.findViewById(R.id.rl_spoken_square);
        View btnMXK = pageContentView.findViewById(R.id.rl_mxk);
        View btnPersonEnglish = pageContentView.findViewById(R.id.home_page_person_english);
        View btnAbroadStrategy = pageContentView.findViewById(R.id.home_page_abroad_strategy);
        View btnMusicEnglish = pageContentView.findViewById(R.id.rl_music);

        mBtnOrderExam = pageContentView.findViewById(R.id.order_exam);

        mSetExamTimeContent=pageContentView.findViewById(R.id.set_exam_time_content);
        mUpdateExamTimeContent=pageContentView.findViewById(R.id.update_exam_time_content);

        View btnSetExamTime = pageContentView.findViewById(R.id.set_exam_time);

        View btnUpdateExamTime = pageContentView.findViewById(R.id.update_exam_time);
        btnUpdateExamTime.setOnClickListener(this);

        mTVDaysLeftFromExam = (TextView)pageContentView.findViewById(R.id.days_left_from_exam);

        btnMXK.setOnClickListener(this);
        btnSpeaking.setOnClickListener(this);
        btnPractice.setOnClickListener(this);
        btnTPO.setOnClickListener(this);
        btnSpokenSquare.setOnClickListener(this);
        btnPersonEnglish.setOnClickListener(this);
        btnAbroadStrategy.setOnClickListener(this);
        btnSetExamTime.setOnClickListener(this);
        btnMusicEnglish.setOnClickListener(this);
        mBtnOrderExam.setOnClickListener(this);

        //检查是否打开考位预定功能
        checkExamBookingFunction();

    	return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryAndRefreshViews();
    }

    public void queryAndRefreshViews(){
        queryIsSign();
    }

    @Override
    public void onClick(View v) {
        if (!checkUser()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_speaking:
                StatisticUtils.onEvent(R.string.home_page, R.string.person_speaking);
                Intent speakingIntent = new Intent(getActivity(), SpeakingMainActivity.class);
                startActivity(speakingIntent);
                break;
            case R.id.ll_practice:
                StatisticUtils.onEvent(R.string.home_page, R.string.practice);
                Intent practiceIntent = new Intent(getActivity(), PracticeListActivity.class);
                startActivity(practiceIntent);
                break;
            case R.id.ll_tpo:
                StatisticUtils.onEvent(R.string.home_page, R.string.tpo);
                Intent tpoIntent = new Intent(getActivity(), TPOListActivity.class);
                startActivity(tpoIntent);
                break;
            case R.id.rl_mxk:
                StatisticUtils.onEvent(R.string.home_page, R.string.MXKActivity);
                Intent mxkIntent = new Intent(getActivity(), MXKActivity.class);
                startActivity(mxkIntent);
                break;
            case R.id.rl_spoken_square:
                StatisticUtils.onEvent(R.string.home_page, R.string.spoken_square_title);
            	Intent spokenSquareIntent = new Intent(getActivity(), SpokenSquareActivity.class);
                startActivity(spokenSquareIntent);
                break;
            case R.id.home_page_person_english:
                StatisticUtils.onEvent(R.string.home_page, R.string.home_page_person_eng);
            	Intent personIntent = new Intent(getActivity(), CelebrityActivity.class);
                startActivity(personIntent);
            	break;
            case R.id.home_page_abroad_strategy:
                StatisticUtils.onEvent(R.string.home_page, R.string.home_page_abord_strategy);
                Intent abroadIntent = new Intent(getActivity(), AbroadStrategyActivity.class);
                startActivity(abroadIntent);
                break;
            case R.id.order_exam:
                StatisticUtils.onEvent(R.string.home_page, R.string.home_page_test_order);
                Intent subscribeIntent = new Intent(getActivity(), SubscribeActivity.class);
                startActivity(subscribeIntent);
                break;
            case R.id.set_exam_time:
            case R.id.update_exam_time:
                StatisticUtils.onEvent(R.string.home_page, R.string.exam_time_set);
                showChooseTimeMenu();
                break;
            case R.id.rl_music:
                StatisticUtils.onEvent(R.string.home_page, R.string.home_page_music);
                Intent musicIntent = new Intent(getActivity(), MusicEnglishActivity.class);
                startActivity(musicIntent);
                break;
        }
    }

    private boolean checkUser() {
        if (UserInfo.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        BaseHttpRequest.releaseRequest(mRequestHandle);
        BaseHttpRequest.releaseRequest(mSetExamTimeHandle);
        BaseHttpRequest.releaseRequest(mIsSignHandle);
        BaseHttpRequest.releaseRequest(mExamBookingHandle);
        super.onDestroyView();
    }

    /***
     *
     */
    private void checkExamBookingFunction(){
        BaseHttpRequest.releaseRequest(mExamBookingHandle);

        mExamBookingHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.APPCLINE_KAOWEI, null,new HttpRequestCallback(){
            @Override
            public void onRequestFailed(String errorMsg) {

            }

            @Override
            public void onRequestFailedNoNetwork() {

            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                ExamBookingBean bean=Utils.parseJson(content, ExamBookingBean.class);
                if(bean!=null&&bean.getDatas()!=null&&bean.getDatas().getIsOpen()==1){
                    mBtnOrderExam.setVisibility(View.VISIBLE);
                }else{
                    mBtnOrderExam.setVisibility(View.GONE);
                }
            }
        }, BaseHttpRequest.RequestType.GET);
    }

    private void intiPopMenu() {
		/* 设置显示menu布局 view子VIEW */
        // View sub_view = inflater.inflate(R.layout.pop_memu_height, null);
		/* 第一个参数弹出显示view 后两个是窗口大小 */
        mPopupWindow = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		/* 设置背景显示 */
        // mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.agold_menu_bg));
        int bgColor=getResources().getColor(R.color.main_background);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(bgColor));
		/* 设置触摸外面时消失 */
        mPopupWindow.setOutsideTouchable(true);
		/* 设置系统动画 */
        mPopupWindow.setAnimationStyle(R.style.pop_menu_animation);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
        mPopupWindow.setFocusable(true);
    }

    //考试日期
    private final static int EXAM_TYPE_UNKONW=-1;//未知
    private final static int EXAM_TYPE_IELTS=1;//托福
    private final static int EXAM_TYPE_TOEFL=2;//雅思

    private int mExamType=EXAM_TYPE_UNKONW;
    private String mExamTime;
    private int mSelectedTye=EXAM_TYPE_IELTS;
    //Data
    private ExamTimeListBean mExamTimeListBean;
    //View
    private View mExamTimePopMenuContent;
    private TextView mBtnIelts;
    private TextView mBtnToefl;
    private WheelView mWVExamTimes;

    private void showChooseTimeMenu() {
        mExamTimePopMenuContent = mInflater.inflate(R.layout.pop_memu_choose_exam_time, null);
        mPopupWindow.setContentView(this.mExamTimePopMenuContent);

        View btnCancel=mExamTimePopMenuContent.findViewById(R.id.exam_time_cancel);
        btnCancel.setOnClickListener(mOnExamTimeClickListener);
        View btnFinish=mExamTimePopMenuContent.findViewById(R.id.exam_time_finish);
        btnFinish.setOnClickListener(mOnExamTimeClickListener);
        mBtnIelts=(TextView)mExamTimePopMenuContent.findViewById(R.id.exam_time_ielts);
        mBtnIelts.setOnClickListener(mOnExamTimeClickListener);
        mBtnToefl=(TextView)mExamTimePopMenuContent.findViewById(R.id.exam_time_toefl);
        mBtnToefl.setOnClickListener(mOnExamTimeClickListener);

        mWVExamTimes=(WheelView)mExamTimePopMenuContent.findViewById(R.id.time_wheel);
        mWVExamTimes.setCyclic(false);

        mExamTimeListBean=null;
        queryExamTimeList();
        refreshPopmenuContent(EXAM_TYPE_IELTS);

        showPopMenu();

    }

    private void refreshPopmenuContent(int examType){
        mSelectedTye=examType;
        String[] values = new String[0];
        int selectIndex=0;

        if(examType==EXAM_TYPE_IELTS){
            mBtnIelts.setTextColor(getResources().getColor(R.color.dark_red));
            mBtnIelts.setBackgroundResource(R.drawable.bg_dark_red_underline);

            mBtnToefl.setTextColor(getResources().getColor(R.color.text_color_content));
            mBtnToefl.setBackgroundColor(Color.WHITE);

            //绑定滚轮
            if(mExamTimeListBean!=null&&mExamTimeListBean.getDatas()!=null&&mExamTimeListBean.getDatas().getIelts()!=null){
                ExamTimeListBean.DatasEntity.IeltsEntity ieltsEntity= mExamTimeListBean.getDatas().getIelts();
                int size =ieltsEntity.getExam().size();
                values = new String[size];
                for (int i=0;i<size;i++){
                    values[i]=ieltsEntity.getExam().get(i).getTime();
                    if(mExamType==EXAM_TYPE_IELTS&&values[i].equals(mExamTime)){
                        selectIndex=i;
                    }
                }
            }

        }else{
            mBtnIelts.setTextColor(getResources().getColor(R.color.text_color_content));
            mBtnIelts.setBackgroundColor(Color.WHITE);

            mBtnToefl.setTextColor(getResources().getColor(R.color.dark_red));
            mBtnToefl.setBackgroundResource(R.drawable.bg_dark_red_underline);

            //绑定滚轮
            if(mExamTimeListBean!=null&&mExamTimeListBean.getDatas()!=null &&mExamTimeListBean.getDatas().getToefl()!=null){
                ExamTimeListBean.DatasEntity.ToeflEntity toeflEntity= mExamTimeListBean.getDatas().getToefl();

                final int size =toeflEntity.getExam().size();
                values = new String[size];
                for (int i=0;i<size;i++){
                    values[i]=toeflEntity.getExam().get(i).getTime();
                    if(mExamType==EXAM_TYPE_TOEFL&&values[i].equals(mExamTime)){
                        selectIndex=i;
                    }
                }
            }
        }

        ArrayWheelAdapter<String> adapter=new ArrayWheelAdapter<>(values);
        mWVExamTimes.setAdapter(adapter);
        mWVExamTimes.setCurrentItem(selectIndex);
    }

    private void queryExamTimeList(){
        BaseHttpRequest.releaseRequest(mRequestHandle);

        mRequestHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.EXAM_GET_EXAM_TIME, null,new HttpRequestCallback(){
            @Override
            public void onRequestFailed(String errorMsg) {

            }

            @Override
            public void onRequestFailedNoNetwork() {

            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                mExamTimeListBean=Utils.parseJson(content, ExamTimeListBean.class);
                if(mExamType==EXAM_TYPE_UNKONW){
                    mExamType=EXAM_TYPE_IELTS;
                }
                refreshPopmenuContent(mExamType);
            }
        }, BaseHttpRequest.RequestType.GET);
    }

    private void queryIsSign(){
        if (UserInfo.getCurrentUser() == null) {
            return;
        }

        BaseHttpRequest.releaseRequest(mIsSignHandle);

        mExamType=EXAM_TYPE_UNKONW;
        mExamTime=null;

        RequestParams requestParams=new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());

        mIsSignHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.USER_IS_SIGN, requestParams,new HttpRequestCallback(){
            @Override
            public void onRequestFailed(String errorMsg) {

            }

            @Override
            public void onRequestFailedNoNetwork() {

            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                UserIsSignBean bean=Utils.parseJson(content, UserIsSignBean.class);
                if(bean!=null&&bean.getDatas()!=null&&bean.getDatas().getExam()!=null){
                    String typeStr=bean.getDatas().getExam().getType();
                    mExamType=Integer.parseInt(typeStr);
                    mExamTime= bean.getDatas().getExam().getExam();
                }
                if(bean!=null&&bean.getDatas()!=null){
                    mHeaderView.setIsSigned(bean.getDatas().getIsSign()==1);
                }

                refreshExamTime();
            }
        }, BaseHttpRequest.RequestType.GET);
    }

    private void refreshExamTime(){
        SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd" );

        int dayLeftFromExam=-1;
        try {
            if(!TextUtils.isEmpty(mExamTime)){
                Date examDate = format.parse(mExamTime);

                Calendar examCalendar = Calendar.getInstance();
                examCalendar.setTime(examDate);
                examCalendar.set(Calendar.HOUR_OF_DAY, 0);
                examCalendar.set(Calendar.MINUTE, 0);
                examCalendar.set(Calendar.SECOND, 0);
                examCalendar.set(Calendar.MILLISECOND, 0);

                Calendar curCalendar = Calendar.getInstance();
                curCalendar.set(Calendar.HOUR_OF_DAY, 0);
                curCalendar.set(Calendar.MINUTE, 0);
                curCalendar.set(Calendar.SECOND, 0);
                curCalendar.set(Calendar.MILLISECOND, 0);

                dayLeftFromExam=(int) ((examCalendar.getTime().getTime() - curCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));

            }
        } catch (ParseException e) {

        }

        //无效日期
        if(dayLeftFromExam<0){
            mSetExamTimeContent.setVisibility(View.VISIBLE);
            mUpdateExamTimeContent.setVisibility(View.GONE);
        }else {
            mSetExamTimeContent.setVisibility(View.GONE);
            mUpdateExamTimeContent.setVisibility(View.VISIBLE);
            setDaysLeftFromExam(dayLeftFromExam);
        }
    }

    //设置距离考试日期
    private void setDaysLeftFromExam(int days){
        if(days==0){
            SpannableString spanString = getSpannableString(getString(R.string.is_exam_time),0xff494949,12);
            mTVDaysLeftFromExam.setText(spanString);
            return;
        }
        SpannableString spanString = getSpannableString(getString(R.string.upto_exam_time),0xff494949,12);
        mTVDaysLeftFromExam.setText(spanString);
        spanString = getSpannableString(String.valueOf(days),0xfff56374,15);
        mTVDaysLeftFromExam.append(spanString);
        spanString = getSpannableString(getString(R.string.unit_day),0xffaaaaaa,10);
        mTVDaysLeftFromExam.append(spanString);
    }

    private SpannableString getSpannableString(String textStr,int color,int textDpSize) {
        SpannableString spannableString = new SpannableString(textStr);
        //颜色
        ForegroundColorSpan colorSpanspan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpanspan, 0, textStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //大小
        AbsoluteSizeSpan sizSpan = new AbsoluteSizeSpan(textDpSize,true);
        spannableString.setSpan(sizSpan, 0, textStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void setExamTime(){
        //考试时间
        String examTime=null;
        int curItem=mWVExamTimes.getCurrentItem();
        if(mSelectedTye==EXAM_TYPE_IELTS){
            if(mExamTimeListBean!=null&&mExamTimeListBean.getDatas()!=null&&mExamTimeListBean.getDatas().getIelts()!=null){
                if(mExamTimeListBean.getDatas().getIelts().getExam().size()>curItem){
                    examTime=mExamTimeListBean.getDatas().getIelts().getExam().get(curItem).getTime();
                }
            }
        }else{
            if(mExamTimeListBean !=null&&mExamTimeListBean.getDatas()!=null &&mExamTimeListBean.getDatas().getToefl()!=null){
                if(mExamTimeListBean.getDatas().getToefl().getExam().size()>curItem){
                    examTime=mExamTimeListBean.getDatas().getToefl().getExam().get(curItem).getTime();
                }
            }
        }

        if(TextUtils.isEmpty(examTime)){
            Utils.toast(R.string.exam_time_not_set_time);
            return;
        }

        BaseHttpRequest.releaseRequest(mSetExamTimeHandle);

        RequestParams requestParams=new RequestParams();
        requestParams.put("user", UserInfo.getCurrentUser().getObjectId());
        requestParams.put("type", mSelectedTye);
        requestParams.put("examtime", examTime);

        final String finalExamTime = examTime;
        mSetExamTimeHandle=mHttpRequest.startRequest(getActivity(), ApiUrls.EXAM_SET_UPTIME, requestParams,new HttpRequestCallback(){
            @Override
            public void onRequestFailed(String errorMsg) {

            }

            @Override
            public void onRequestFailedNoNetwork() {

            }

            @Override
            public void onRequestCanceled() {

            }

            @Override
            public void onRequestSucceeded(String content) {
                BaseBean bean= Utils.parseJson(content, BaseBean.class);
                if(bean.getStatus()==1){
                    mExamType=mSelectedTye;
                    mExamTime= finalExamTime;
                }
                refreshExamTime();
                closePopWin();
            }
        }, BaseHttpRequest.RequestType.POST);
    }


    // 显示菜单
    public void showPopMenu() {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			/* 最重要的一步：弹出显示 在指定的位置(parent) 最后两个参数 是相对于 x / y 轴的坐标 */
            mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            backgroundAlpha(0.7f);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                    BaseHttpRequest.releaseRequest(mRequestHandle);
                }
            });
        }
    }

    // 关闭PopUpWindow
    public boolean closePopWin() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private OnClickListener mOnExamTimeClickListener=new OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.exam_time_cancel:
                    StatisticUtils.onEvent(R.string.home_page, R.string.exam_time_set,R.string.cancel);
                    closePopWin();
                    break;
                case R.id.exam_time_finish:
                    StatisticUtils.onEvent(R.string.home_page, R.string.exam_time_set,R.string.finish);
                    setExamTime();
                    break;
                case R.id.exam_time_ielts:
                    StatisticUtils.onEvent(R.string.home_page, R.string.exam_time_set,R.string.exam_time_ielts);
                    refreshPopmenuContent(EXAM_TYPE_IELTS);
                    break;
                case R.id.exam_time_toefl:
                    StatisticUtils.onEvent(R.string.home_page, R.string.exam_time_set,R.string.exam_time_toefl);
                    refreshPopmenuContent(EXAM_TYPE_TOEFL);
                    break;
            }
        }
    };
}
