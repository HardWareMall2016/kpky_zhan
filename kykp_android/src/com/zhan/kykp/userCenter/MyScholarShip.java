package com.zhan.kykp.userCenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.integralMall.HomeMallActivity;
import com.zhan.kykp.util.PhotoUtils;

/**
 * Created by Administrator on 2015/10/28.
 */
public class MyScholarShip extends BaseActivity{
    private TextView mTvScholarship;
    private ImageView mImgAvatar ;
    private WebView mWebView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_scholarship);

        mTvScholarship=(TextView) findViewById(R.id.scholarship);

        findViewById(R.id.integral_mall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyScholarShip.this,HomeMallActivity.class));
            }
        });

        mImgAvatar = (ImageView) findViewById(R.id.scholship_avatar);
        mWebView = (WebView)findViewById(R.id.scholarship_webView);
        mWebView.loadUrl("file:///android_asset/index.html");
        setAvatar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvScholarship.setText(String.valueOf(UserInfo.getCurrentUser().getCredit()));
    }

    private void setAvatar() {
        UserInfo user = UserInfo.getCurrentUser();
        String avatarPath = user.getAvatar();
        ImageLoader.getInstance().displayImage(avatarPath, mImgAvatar,
                PhotoUtils.buldDisplayImageOptionsForAvatar());
    }
}
