package com.zhan.kykp.subscribe;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.util.DialogUtils;

/**
 * Created by Qs on 15/8/30.
 */
public class SubscribeActivity extends BaseActivity {
    public final String LOAD_URL = "http://youkaowei.zhan.com/ykw/subscribe.html?userId=%s&nickname=%s";
    //public final String LOAD_URL = "http://192.168.4.241:8080/ykw/subscribe.html?userId=%s&nickname=%s";
    private WebView webView;
    private Dialog mProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        TextView actionBarRightMenu=getActionBarRightMenu();
        actionBarRightMenu.setText(R.string.close);
        actionBarRightMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showProgressDialog();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);

        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                String url1 = view.getUrl();
                if (url1 != null && url1.contains("#/back")) {
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                closeDialog();
                super.onPageFinished(view, url);
            }
        });
        UserInfo userInfo=UserInfo.getCurrentUser();
        webView.loadUrl(String.format(LOAD_URL,userInfo.getObjectId(),userInfo.getNickname()));
    }

    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(SubscribeActivity.this, getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
