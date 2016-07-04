package com.zhan.kykp.util;

import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.zhan.kykp.base.App;

/**
 * Created by WuYue on 2015/10/23.
 */
public class StatisticUtils {

    public static void onEvent(String ... eventNames) {
        String eventNameStr="";
        for (int i=0;i<eventNames.length;i++){
            if(i==0){
                eventNameStr=eventNames[i];
            }else{
                eventNameStr+="_"+eventNames[i];
            }
        }

        Log.i("StatisticUtils",eventNameStr);
        MobclickAgent.onEvent(App.ctx, eventNameStr);
    }

    public static void onEvent(int ... eventNameResIds) {
        String eventNameStr="";
        for (int i=0;i<eventNameResIds.length;i++){
            if(i==0){
                eventNameStr=App.ctx.getString(eventNameResIds[i]);
            }else{
                eventNameStr+="_"+App.ctx.getString(eventNameResIds[i]);
            }
        }
        onEvent(eventNameStr);
    }
}
