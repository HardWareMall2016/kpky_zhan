package com.zhan.kykp.music;


import java.util.List;

/**
 * Created by Administrator on 2015/11/19.
 */
public interface MusicCallBack {
    void gotoPosition(int position);
    List<MusicListInfo> getListCallBack();
}
