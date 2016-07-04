package com.zhan.kykp.celebrityEnglish;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.util.StatisticUtils;

import java.util.List;

/**
 * Created by WuYue on 2015/10/29.
 */
public class CelebrityActivity extends BaseActivity implements ICelebrityCallback{

    //Frags
    private CelebrityListFragment mListFrag;
    private CelebrityDetailsFragment mDetailsFrag;

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
        mListFrag=new CelebrityListFragment();
        transaction.replace(R.id.content, mListFrag);
        transaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void gotoPosition(int position) {

        StatisticUtils.onEvent(R.string.home_page_person_eng, R.string.statistic_goto_details_page);

        mDetailsFrag = new CelebrityDetailsFragment();
        mDetailsFrag.prepareArguments(position);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content, mDetailsFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public List<CelebritySummaryInfo> getCelebrityList() {
        return mListFrag.getCelebrityList();
    }
}
