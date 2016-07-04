package com.zhan.kykp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.zhan.kykp.Receiver.PushMsgBean;
import com.zhan.kykp.base.App;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.message.MessageFragment;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.AppClinetUpgradeBean;
import com.zhan.kykp.network.bean.MessageStatusBean;
import com.zhan.kykp.userCenter.IndexActivity;
import com.zhan.kykp.userCenter.LoginActivity;
import com.zhan.kykp.userCenter.UserCenterFragment;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.ShareUtil;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.RedTipTextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class HomePageActivity extends Activity implements OnClickListener{
	//私信推送相关
	public final static String ACTION_RECEIVER_PUSH_MSG ="action.receiver.private.message";
	public final static String EXT_KEY_MSG = "pushed_msg";

	public final static String EXT_KEY_SHOW_HOME_PAGE = "show_home_page";
	//Views
	private TextView mTVHomePage;
	private RedTipTextView mTVMessage;
	private TextView mTVMy;
	
	private final static String TAG_HOME_PAGE="HomePage";
	private final static String TAG_MESSAGEE="Message";
	private final static String TAG_USER_CENTER="UserCenter";

	private String mCurrentTAG =TAG_HOME_PAGE;
	private MessageFragment mMessageFragment;
	
	private List<Page> mPageList;
	private String mVersionName;

	//Network
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mUpgradeHandle;
	private RequestHandle mMessageStatusHandle;

	//广播接收
	private PrivateMsgReceiver mPrivateMsgReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initWindow();
		
		setContentView(R.layout.activity_home_page);

		mHttpRequest=new BaseHttpRequest();

		getVersionNameAndCheckFirstLogin();
		//Utils.checkUpdate(this);
		checkUpgrade();

		mTVHomePage=(TextView)findViewById(R.id.home_page);
		mTVMessage=(RedTipTextView)findViewById(R.id.message);
		mTVMy=(TextView)findViewById(R.id.my);
		
		mTVHomePage.setOnClickListener(this);
		mTVMessage.setOnClickListener(this);
		mTVMy.setOnClickListener(this);

		initReceiver();

		initPages();
	}
	
	private void initWindow() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏  
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏 
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			// Translucent status bar
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}*/
	}

	/*@Override
	protected boolean displayBackIcon() {
		return false;
	}*/

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean homePage = intent.getBooleanExtra(EXT_KEY_SHOW_HOME_PAGE, false);
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0 && homePage) {
			//退出跳转到主页
			showPage(TAG_HOME_PAGE);
		}
	}

	/**
	 * 页面统计
	 */

	protected void onResume() {
		super.onResume();
		getMessageStatus();
		//友盟统计
		MobclickAgent.onPageStart(getString(R.string.home_page));
		MobclickAgent.onResume(this);

		//极光推送注册别名
		final UserInfo userInfo=UserInfo.getCurrentUser();
		//用户存在并且现在保存的别名和用户ID不是同一个
		if(userInfo!=null&&!userInfo.getObjectId().equals(ShareUtil.getValue(this,ShareUtil.JPUSH_ALIAS))){
			JPushInterface.setAlias(this, userInfo.getObjectId(), new TagAliasCallback(){
				@Override
				public void gotResult(int responseCode, String alias, Set<String> tags) {
					switch (responseCode) {
						case 0:
							Log.i("HomePageActivity", "alias success");
							ShareUtil.setValue(App.ctx,ShareUtil.JPUSH_ALIAS,userInfo.getObjectId());
							break;
						case 6002:
							Log.i("HomePageActivity", "Failed to set alias and tags due to timeout. Try again after 60s.");
							break;
						default:
							Log.e("HomePageActivity", "Failed with errorCode = " + responseCode);
							break;
					}
				}
			});
		}
	}

	protected void onPause() {
		super.onPause();
		//友盟统计
		MobclickAgent.onPageEnd(getString(R.string.home_page));
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		BaseHttpRequest.releaseRequest(mUpgradeHandle);
		BaseHttpRequest.releaseRequest(mMessageStatusHandle);

		releaseReceiver();

		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		if (!checkUser()) {
			return;
		}

		switch (view.getId()) {
		case R.id.home_page:
			showPage(TAG_HOME_PAGE);
			StatisticUtils.onEvent(R.string.home_page);
			break;
		case R.id.message:
			showPage(TAG_MESSAGEE);
			StatisticUtils.onEvent(R.string.my_msg);
			break;
		case R.id.my:
			showPage(TAG_USER_CENTER);
			StatisticUtils.onEvent(R.string.personal_center);
			break;
		}
	}

	private boolean checkUser() {
		if (UserInfo.getCurrentUser() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return false;
		}
		return true;
	}
	
	private void initPages(){
		mPageList=new ArrayList<Page>();
		//首页
		Page page=new Page();
		page.TAG=TAG_HOME_PAGE;
		page.PageFragment= new HomePageFragment();
		page.FocusIconResId=R.drawable.iocn_home_focused;
		page.UnFocusIconResId=R.drawable.iocn_home_un_focused;
		page.BottomTitle=mTVHomePage;
		mPageList.add(page);
		
		//消息
		mMessageFragment=new MessageFragment();
		page=new Page();
		page.TAG=TAG_MESSAGEE;
		page.PageFragment= mMessageFragment;
		page.FocusIconResId=R.drawable.icon_message_focused;
		page.UnFocusIconResId=R.drawable.icon_message_un_focused;
		page.BottomTitle=mTVMessage;
		mPageList.add(page);

		//我的
		page=new Page();
		page.TAG=TAG_USER_CENTER;
		page.PageFragment= new UserCenterFragment();
		page.FocusIconResId=R.drawable.icon_my_focused;
		page.UnFocusIconResId=R.drawable.icon_my_un_focused;
		page.BottomTitle=mTVMy;
		mPageList.add(page);

		showPage(TAG_HOME_PAGE);
	}
	
	
	private void showPage(String tag){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		for(Page page : mPageList){
			if(page.TAG.equals(tag)){
				//transaction.show(page.PageFragment);
				transaction.replace(R.id.content, page.PageFragment);
				page.BottomTitle.setCompoundDrawablesWithIntrinsicBounds(0, page.FocusIconResId, 0, 0);
				page.BottomTitle.setTextColor(getResources().getColor(R.color.dark_red));
			}else{
				//transaction.hide(page.PageFragment);
				page.BottomTitle.setCompoundDrawablesWithIntrinsicBounds(0, page.UnFocusIconResId, 0, 0);
				page.BottomTitle.setTextColor(getResources().getColor(R.color.text_color_content));
			}
		}
		transaction.commit();
		mCurrentTAG =tag;
	}

	private void getVersionNameAndCheckFirstLogin() {
		try {
			mVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			if (!ShareUtil.getValue(HomePageActivity.this, ShareUtil.VERSION).equals(mVersionName)) {
				startActivity(new Intent(HomePageActivity.this, IndexActivity.class));
				ShareUtil.setValue(HomePageActivity.this,ShareUtil.VERSION, mVersionName);
			}

		} catch (PackageManager.NameNotFoundException e) {
		}
	}

	private void checkUpgrade(){
		BaseHttpRequest.releaseRequest(mUpgradeHandle);

		//只在WIFI下更新
		if(!Connectivity.isConnectedWifi(this)){
			return;
		}

		RequestParams requestParams=new RequestParams();
		requestParams.put("type","android");
		requestParams.put("version", mVersionName);
		requestParams.put("name", getString(R.string.app_name));
		mUpgradeHandle=mHttpRequest.startRequest(this, ApiUrls.APPCLINE_UPGRADE,requestParams,new HttpRequestCallback(){

			@Override
			public void onRequestFailed(String errorMsg) {

			}

			@Override
			public void onRequestFailedNoNetwork() {

			}

			@Override
			public void onRequestCanceled() {

			}

			@Override
			public void onRequestSucceeded(String content) {
				final AppClinetUpgradeBean bean=Utils.parseJson(content, AppClinetUpgradeBean.class);
				/*final AppClinetUpgradeBean  bean=new AppClinetUpgradeBean();
				bean.setDatas(new AppClinetUpgradeBean.DatasEntity());
				bean.getDatas().setForcedUpdate(0);
				bean.getDatas().setUrl("http://beikao.zhan.com/download/android.apk");*/
				if(bean!=null&&bean.getDatas()!=null){
					AlertDialog.Builder dlgBuilder=new AlertDialog.Builder(HomePageActivity.this);

					dlgBuilder.setTitle(R.string.new_version).
							setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Utils.installApp(bean.getDatas().getUrl());
									dialog.dismiss();
								}
							});

					if(bean.getDatas().getForcedUpdate()==1){
						dlgBuilder.setCancelable(false);
					}else{
						dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					}
					dlgBuilder.show();
				}
			}
		}, BaseHttpRequest.RequestType.POST);

	}

	private void getMessageStatus(){
		if (UserInfo.getCurrentUser() == null) {
			return;
		}

		BaseHttpRequest.releaseRequest(mMessageStatusHandle);

		RequestParams requestParams=new RequestParams();
		requestParams.put("user",UserInfo.getCurrentUser().getObjectId());
		mMessageStatusHandle=mHttpRequest.startRequest(this, ApiUrls.MESSAGE_STATUS,requestParams,new HttpRequestCallback(){
			@Override
			public void onRequestFailed(String errorMsg) {
			}

			@Override
			public void onRequestFailedNoNetwork() {
			}

			@Override
			public void onRequestCanceled() {
			}

			@Override
			public void onRequestSucceeded(String content) {
				mTVMessage.setRedTipVisibility(RedTipTextView.RED_TIP_INVISIBLE);
				MessageStatusBean msgStatusBean=Utils.parseJson(content,MessageStatusBean.class);
				if(msgStatusBean!=null&&msgStatusBean.getDatas()!=null){
					int msgStatus=msgStatusBean.getDatas().getStatus();
					if(msgStatus==1){
						mTVMessage.setRedTipVisibility(RedTipTextView.RED_TIP_VISIBLE);
					}
				}
			}
		}, BaseHttpRequest.RequestType.GET);
	}
	
	private class Page{
		String TAG;
		Fragment PageFragment;
		int FocusIconResId;
		int UnFocusIconResId;
		TextView BottomTitle;
	}


	//私信推送

	private class PrivateMsgReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(ACTION_RECEIVER_PUSH_MSG.equals(intent.getAction())){
				PushMsgBean bean=intent.getParcelableExtra(EXT_KEY_MSG);
				mTVMessage.setRedTipVisibility(RedTipTextView.RED_TIP_VISIBLE);
				if(TAG_MESSAGEE.equals(mCurrentTAG)){
					mMessageFragment.addNewPushedMsg(bean);
				}
			}
		}
	}

	private void initReceiver(){
		mPrivateMsgReceiver=new PrivateMsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_RECEIVER_PUSH_MSG);
		registerReceiver(mPrivateMsgReceiver, intentFilter);
	}

	private void releaseReceiver(){
		unregisterReceiver(mPrivateMsgReceiver);
	}
}
