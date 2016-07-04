package com.zhan.kykp.Receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhan.kykp.HomePageActivity;
import com.zhan.kykp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

            String msg=bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + msg);

            sendMsg(context, msg);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            String msg=bundle.getString(JPushInterface.EXTRA_EXTRA);

            Log.i(TAG, "收到通知 : " + msg);
            try {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                JSONObject msgJson=json.optJSONObject("extras");
                if(msgJson!=null){
                    sendMsg(context, msgJson.toString());
                }
            } catch (JSONException e) {

            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            Intent i = new Intent(context, HomePageActivity.class);
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void sendMsg(Context context, String msgConetnt) {
        if(!TextUtils.isEmpty(msgConetnt)){
            Gson gson = new Gson();
            try {
                PushMsgBean bean= gson.fromJson(msgConetnt, PushMsgBean.class);
                Intent sendIntent = new Intent(HomePageActivity.ACTION_RECEIVER_PUSH_MSG);
                sendIntent.putExtra(HomePageActivity.EXT_KEY_MSG,bean);
                context.sendBroadcast(sendIntent);
            }catch (JsonSyntaxException exp){
                Log.e(TAG, "json String error : " + exp.getMessage());
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(Context context, String message) {
        ///// 第一步：获取NotificationManager
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        ///// 第二步：定义Notification
        Intent intent = new Intent(context, HomePageActivity.class);
        //PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher)).setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

        /////第三步：启动通知栏，第一个参数是一个通知的唯一标识
        nm.notify(0, notification);
    }
}
