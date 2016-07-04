package com.zhan.kykp.userCenter;

import android.os.Bundle;
import android.webkit.WebView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;

public class aboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");
    }

}
