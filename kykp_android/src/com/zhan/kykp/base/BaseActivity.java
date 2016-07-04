package com.zhan.kykp.base;


import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import com.zhan.kykp.R;
import com.zhan.kykp.util.PageAnalyticsHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class BaseActivity extends Activity {
	private FrameLayout mCustomerContent;
	private RelativeLayout mDefaultContent;
	
	private TextView mTVTitle;
	private TextView mTVRightMenu;
	private LinearLayout mRightContent;
	private int mPaddingTop=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏  
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			// Translucent status bar
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			
			// create our manager instance after the content view is set
		    SystemBarTintManager tintManager = new SystemBarTintManager(this);
		    // enable status bar tint
		    tintManager.setStatusBarTintEnabled(true);
		    // enable navigation bar tint
		    tintManager.setNavigationBarTintEnabled(true);
		    // set a custom tint color for all system bars
		    tintManager.setTintColor( getResources().getColor(R.color.action_bar_bg_color));
		    
		    SystemBarConfig config = tintManager.getConfig();
		    
		    mPaddingTop=config.getPixelInsetTop(true);
		}*/
		initActionbar();
		
		//对MiUI 6修改颜色
		//StatusBarUtils.setStatusBarTextColor(this, 1);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		View rootView = createRootView();
		setInnerContentView(rootView, layoutResID);
		super.setContentView(rootView);

	}

	@Override
	public void setContentView(View view) {
		View rootView = createRootView();
		setInnerContentView(rootView, view);
		super.setContentView(rootView);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		View rootView = createRootView();
		setInnerContentView(rootView,view);
		super.setContentView(rootView, params);
	}
	
	private View createRootView(){
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout rootView = (LinearLayout)inflater.inflate(R.layout.translucent_status_activity_layout, null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			rootView.setPadding(0, mPaddingTop, 0, 0);
		}
		return rootView;
	}
	
	private void setInnerContentView(View rootView,View contentView){
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
		int mainBgColor=getResources().getColor(R.color.main_background);
		contentView.setBackgroundColor(mainBgColor);
		FrameLayout mainContent=(FrameLayout)rootView.findViewById(R.id.root_view);
		mainContent.addView(contentView, lp);
	}
	
	private void setInnerContentView(View rootView,int layoutResID){
		LayoutInflater inflater = getLayoutInflater();
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
		View content = inflater.inflate(layoutResID, null);
		FrameLayout mainContent=(FrameLayout)rootView.findViewById(R.id.root_view);
		mainContent.addView(content, lp);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if (mTVTitle != null) {
			mTVTitle.setText(title);
		}
	}

	protected boolean displayBackIcon() {
		return true;
	}
	
	protected boolean displayActionbarUnderline() {
		return true;
	}

	private void initActionbar() {
		View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.action_bar_main, null);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(MATCH_PARENT, MATCH_PARENT);
		actionBar.setCustomView(actionbarLayout, lp);

		mTVTitle = (TextView) actionbarLayout.findViewById(R.id.title);
		mTVTitle.setText(getTitle());

		mTVRightMenu = (TextView) actionbarLayout.findViewById(R.id.right_menu);

		ImageView imgBack = (ImageView) actionbarLayout.findViewById(R.id.img_back);
		if (displayBackIcon()) {
			imgBack.setVisibility(View.VISIBLE);
		} else {
			imgBack.setVisibility(View.INVISIBLE);
		}

		imgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseActivity.this.onBackPressed();
			}
		});
		
		View underline = actionbarLayout.findViewById(R.id.underline);
		if(displayActionbarUnderline()){
			underline.setVisibility(View.VISIBLE);
		}else{
			underline.setVisibility(View.GONE);
		}

		mRightContent = (LinearLayout) actionbarLayout.findViewById(R.id.right_content);
		
		mCustomerContent = (FrameLayout) actionbarLayout.findViewById(R.id.customer_content);
		mDefaultContent= (RelativeLayout) actionbarLayout.findViewById(R.id.def_content);
	}

	/**
	 * 页面统计
	 */

	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onPageStart(PageAnalyticsHelper.getPageName(this));
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		//友盟统计
		MobclickAgent.onPageEnd(PageAnalyticsHelper.getPageName(this));
		MobclickAgent.onPause(this);
	}

	protected LinearLayout getActionBarRightContent() {
		return mRightContent;
	}

	protected TextView getActionBarRightMenu() {
		return mTVRightMenu;
	}
	
	protected FrameLayout getCustomerActionBarLayout() {
		return mCustomerContent;
	}
	
	protected RelativeLayout getActionDefLayout() {
		return mDefaultContent;
	}
}
