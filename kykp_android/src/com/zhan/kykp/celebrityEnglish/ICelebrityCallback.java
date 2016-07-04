package com.zhan.kykp.celebrityEnglish;

import java.util.List;

/**
 * Created by WuYue on 2015/10/29.
 */
public interface ICelebrityCallback {
    void gotoPosition(int position);
    List<CelebritySummaryInfo> getCelebrityList();
}
