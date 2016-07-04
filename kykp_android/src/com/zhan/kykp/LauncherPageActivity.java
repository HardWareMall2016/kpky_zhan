package com.zhan.kykp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LauncherPageActivity extends Activity {

    private Handler mHandler = new Handler();
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView img = new ImageView(this);
        /*String publishChannel = getPublishChannel();
        if (getString(R.string.publish_channel_360).equals(publishChannel)) {
			img.setImageResource(R.drawable.launch_page_360);
		} else if (getString(R.string.publish_channel_vmail).equals(publishChannel)) {
			img.setImageResource(R.drawable.launch_page_huawei);
		} else if (getString(R.string.publish_channel_lenovo).equals(publishChannel)) {
			img.setImageResource(R.drawable.launch_page_lenovo);
		} else {
			img.setImageResource(R.drawable.launch_page_default);
		}*/

        img.setImageResource(R.drawable.launch_page_default);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img.setScaleType(ScaleType.FIT_XY);
        setContentView(img, lp);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent launcherPageIntent = new Intent(LauncherPageActivity.this, HomePageActivity.class);
                startActivity(launcherPageIntent);
                finish();
            }
        }, 2 * 1000);
    }

    @Override
    public void onBackPressed() {

    }

    private String getPublishChannel() {
        Bundle metaData = null;
        String apiKey = null;

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString("leancloud");
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }
}
