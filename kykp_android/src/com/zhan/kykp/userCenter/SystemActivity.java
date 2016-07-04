package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.zhan.kykp.HomePageActivity;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

public class SystemActivity extends BaseActivity implements View.OnClickListener,View.OnLongClickListener{
	private final static String TAG="SystemActivity";
	private Dialog mClearCacheProgressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        initView();
    }

    private void initView() {
        View index = findViewById(R.id.index);
        index.setOnClickListener(this);
        ((TextView)index.findViewById(R.id.title)).setText(R.string.us_index);
        ((ImageView)index.findViewById(R.id.left_img)).setImageResource(R.drawable.icon_index);

        View grade = findViewById(R.id.grade);
        grade.setOnClickListener(this);
        ((TextView)grade.findViewById(R.id.title)).setText(R.string.us_grade);
        ((ImageView)grade.findViewById(R.id.left_img)).setImageResource(R.drawable.icon_pf);

        View cache = findViewById(R.id.cache);
        cache.setOnClickListener(this);
        ((TextView)cache.findViewById(R.id.title)).setText(R.string.us_clear);
        //cache.setVisibility(View.GONE);
        ((ImageView)cache.findViewById(R.id.left_img)).setImageResource(R.drawable.icon_clear);

        View about = findViewById(R.id.about);
        about.setOnClickListener(this);
        ((TextView)about.findViewById(R.id.title)).setText(R.string.us_about);
        ((ImageView)about.findViewById(R.id.left_img)).setImageResource(R.drawable.icon_about);

        View link = findViewById(R.id.link);
        link.setPadding(0, link.getPaddingTop(), link.getPaddingRight(), link.getPaddingBottom());
        ((TextView)link.findViewById(R.id.title)).setText(R.string.us_link);
        link.findViewById(R.id.right_img).setVisibility(View.GONE);

        View wechat = findViewById(R.id.wechat);
		wechat.setOnLongClickListener(this);
        wechat.setPadding(0, wechat.getPaddingTop(), wechat.getPaddingRight(), wechat.getPaddingBottom());
        ((TextView)wechat.findViewById(R.id.title)).setText(R.string.us_wechat);
        wechat.findViewById(R.id.right_img).setVisibility(View.GONE);

        TextView version = (TextView) findViewById(R.id.version);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version.setText("V "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            version.setText("V 1.0");
        }
        //退出
        findViewById(R.id.exit_account).setOnClickListener(this);
    }

    @Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.index:
			StatisticUtils.onEvent(R.string.system, R.string.us_index);
			startActivity(new Intent(this, IndexActivity.class));
			break;
		case R.id.cache:
			StatisticUtils.onEvent(R.string.system, R.string.us_clear);
			showClearCacheProgressDialog();
			File tempFile=PathUtils.getExternalTempFilesDir();
			long filesSize=PathUtils.recursionDeleteFile(tempFile);
			Log.i(TAG, "Delete files size = "+filesSize);
			String chacheStr=getString(R.string.clear_cache_finished);
			Utils.toast(String.format(chacheStr, PathUtils.bytes2kb(filesSize)));
			closeClearCacheProgressDialog();
			break;
		case R.id.grade:
			StatisticUtils.onEvent(R.string.system, R.string.us_grade);
			try {
				Uri uri = Uri.parse("market://search?q=pname:" + getPackageName());
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			} catch (Exception e) {
				Toast.makeText(SystemActivity.this, R.string.us_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.exit_account:
			StatisticUtils.onEvent(R.string.system, R.string.exit_account);
			logout();
			break;
			case R.id.about:
				StatisticUtils.onEvent(R.string.system, R.string.about_us);
				Intent aboutIntent = new Intent(this, aboutActivity.class);
				startActivity(aboutIntent);
				break;

		}
	}

	private void logout(){
		UserInfo user = UserInfo.getCurrentUser();
		SHARE_MEDIA share_media=null;
		switch(user.getUserType()){
			case LoginType.WEIXIN:
				share_media=SHARE_MEDIA.WEIXIN;
				break;
			case LoginType.QQ:
				share_media=SHARE_MEDIA.QQ;
				break;
			case LoginType.SINA:
				share_media=SHARE_MEDIA.SINA;
				break;
		}
		if(share_media!=null){
			UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
			mController.deleteOauth(this, share_media,
					new SocializeListeners.SocializeClientListener() {
						@Override
						public void onStart() {
						}
						@Override
						public void onComplete(int status, SocializeEntity entity) {
							Log.i(TAG,"deleteOauth status = "+status);
						}
					});
		}

		UserInfo.logout();
		Utils.toast(R.string.exit_account_success);
		JPushInterface.setAlias(SystemActivity.this, "", null);

		Intent homePageIntent = new Intent(this, HomePageActivity.class);
		homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		homePageIntent.putExtra(HomePageActivity.EXT_KEY_SHOW_HOME_PAGE,true);
		startActivity(homePageIntent);
	}

	private void showClearCacheProgressDialog() {
		mClearCacheProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.clearing_cache));
		mClearCacheProgressDlg.setCancelable(false);
		mClearCacheProgressDlg.show();
	}

	private void closeClearCacheProgressDialog() {
		if (mClearCacheProgressDlg != null) {
			mClearCacheProgressDlg.dismiss();
			mClearCacheProgressDlg = null;
		}
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
			case R.id.wechat:
				StatisticUtils.onEvent(R.string.system, R.string.us_wechat);
				ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText(null, "youkaowei"));
				Utils.toast(R.string.toast_copyed);
				break;
		}
		return false;
	}
}
