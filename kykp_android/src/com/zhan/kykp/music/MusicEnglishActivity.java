package com.zhan.kykp.music;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.util.StatisticUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/11/19.
 */
public class MusicEnglishActivity extends BaseActivity implements MusicCallBack{

    private MusicListFragment mListFrag ;
    private MusicDetailsFragment mSummaryFragment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mListFrag= new MusicListFragment();
        transaction.replace(R.id.content, mListFrag);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void gotoPosition(int position) {
        StatisticUtils.onEvent(R.string.home_page_music , R.string.home_page_music_detaile );
        mSummaryFragment = new MusicDetailsFragment();
        mSummaryFragment.prepareArguments(position);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content, mSummaryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public List<MusicListInfo> getListCallBack() {
        return mListFrag.getmList();
    }
}
