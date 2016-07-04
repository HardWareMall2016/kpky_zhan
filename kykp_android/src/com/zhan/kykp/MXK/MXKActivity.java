package com.zhan.kykp.MXK;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.util.DialogUtils;

/**
 * Created by Qs on 15/8/30.
 */
public class MXKActivity extends Activity {
    public final String loadUrl = "http://www.zhan.com/api/mingxiaoku/build/index.html";

    private WebView webView;
    private Dialog mProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mxk);
        showProgressDialog();

        webView = (WebView) findViewById(R.id.webView2);
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
                //?????
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
//        webView.addon
        webView.loadUrl(loadUrl);
    }
    private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(MXKActivity.this, getString(R.string.loading));
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    /**
     * 页面统计
     */

    protected void onResume() {
        super.onResume();
        //友盟统计
        MobclickAgent.onPageStart(getString(R.string.MXKActivity));
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPageEnd(getString(R.string.MXKActivity));
        MobclickAgent.onPause(this);
    }
}
