package com.zhan.kykp.TPO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.util.PathUtils;

public class TPOActivity extends BaseActivity implements TPOCallback {
	private final static String TAG = TPOActivity.class.getSimpleName();
	
	public final static String EXTRA_KEY_TPO_NAME = "TPO_NAME";
	public final static String EXTRA_KEY_TPO_INDEX = "TPO_INDEX";
	
	private String mTPOName;
	private int mTPOIndex;
	private JSONObject mTPOJson;
	private Dialog mDialog;
	
	private List<BaseTPOFragment> mFragList=new ArrayList<BaseTPOFragment>();
	
	private int mCurIndex=0;
	
	private boolean mResumed=true;
	
	//休眠锁
	//private WakeLock mWakeLock = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED  
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON  
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//acquireWakeLock();
		init();
	}
	
	
	@Override
	protected void onResume() {
		mResumed=true;
		super.onResume();
	}

	@Override
	protected void onStop() {
		mResumed=false;
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		mDialog.show();
	}
	
	private void initDlg(){
		LayoutInflater inflater=getLayoutInflater();
		mDialog = new Dialog(this, R.style.Dialog);
		View dlgContent = inflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.tpo_exting_tpo);
		
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setText(R.string.tpo_exit_confirm_msg);
		
		TextView confirm = (TextView) dlgContent.findViewById(R.id.confirm);
		confirm.setText(R.string.tpo_exit_confirm);
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
				finish();
			}
		});
		
		TextView cancel = (TextView) dlgContent.findViewById(R.id.cancel);
		cancel.setText(R.string.tpo_exit_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});
		
		mDialog.setContentView(dlgContent);
	}

	@Override
	public String getTPOName() {
		return mTPOName;
	}

	@Override
	public int getTPOIndex() {
		return mTPOIndex;
	}
	
	private void init(){
		Intent intent = getIntent();
		mTPOName = intent.getStringExtra(EXTRA_KEY_TPO_NAME);
		mTPOIndex = intent.getIntExtra(EXTRA_KEY_TPO_INDEX,0);
		parseTPOJson();
		initRightActionMenu();
		initFragments();
		initDlg();
}
	
	private void initRightActionMenu(){
		TextView menu=getActionBarRightMenu();
		menu.setText(R.string.tpo_ignore);
		menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				gotoNext();
			}
		});
	}
	
	private void initFragments(){
		FragmentReadingDirection fragReadDirect= new FragmentReadingDirection();
		fragReadDirect.prepareArguments(getString(R.string.tpo_title_subfix), true);
		mFragList.add(fragReadDirect);
		
		BaseTPOFragment frag;
		//Q1
		frag = new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q1,true);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q1,false);
		mFragList.add(frag);
		//Q2
		frag = new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q2,true);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q2,false);
		mFragList.add(frag);
		
		//Q3
		frag = new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q3,true);
		mFragList.add(frag);
		frag=new FragmentReading();
		frag.prepareArguments(TPOConstant.Q3,false);
		mFragList.add(frag);
		frag=new FragmentListening();
		frag.prepareArguments(TPOConstant.Q3,false);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q3,false);
		mFragList.add(frag);
		//Q4
		frag = new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q4,true);
		mFragList.add(frag);
		frag=new FragmentReading();
		frag.prepareArguments(TPOConstant.Q4,false);
		mFragList.add(frag);
		frag=new FragmentListening();
		frag.prepareArguments(TPOConstant.Q4,false);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q4,false);
		mFragList.add(frag);
		
		//Q5
		frag= new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q5,true);
		mFragList.add(frag);
		frag=new FragmentListening();
		frag.prepareArguments(TPOConstant.Q5,false);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q5,false);
		mFragList.add(frag);
		//Q6
		frag= new FragmentIntro();
		frag.prepareArguments(TPOConstant.Q6,true);
		mFragList.add(frag);
		frag=new FragmentListening();
		frag.prepareArguments(TPOConstant.Q6,false);
		mFragList.add(frag);
		frag=new FragmentQuestion();
		frag.prepareArguments(TPOConstant.Q6,false);
		mFragList.add(frag);
		
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, fragReadDirect);
		transaction.commit();
	}

	@Override
	public JSONObject getTPOJson() {
		return mTPOJson;
	}

	@Override
	public void setTPOTitle(String title, boolean skip) {
		setTitle(title);
		TextView actionMenu=getActionBarRightMenu();
		if(skip){
			actionMenu.setVisibility(View.VISIBLE);
		}else{
			actionMenu.setVisibility(View.GONE);
		}
	}

	@Override
	public String getTPOFilePath(String fileSubPath) {
		String TPOPath = PathUtils.getExternalTPOFilesDir().getAbsolutePath();
		String filePath = TPOPath + "/" +mTPOName + fileSubPath;
		return filePath;
	}

	@Override
	public void gotoNext() {
		if(!mResumed){
			mDialog.dismiss();
			finish();
			return;
		}
		BaseTPOFragment curGrag = mFragList.get(mCurIndex);
		curGrag.release();
		mCurIndex++;
		if(mCurIndex<mFragList.size()){
			BaseTPOFragment nextGrag = mFragList.get(mCurIndex);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.content, nextGrag);
			transaction.commit();
		}else{
			mDialog.dismiss();
			finish();
		}
	}
	
	private void parseTPOJson() {
		String tpoJsonPath=getTPOFilePath(TPOConstant.TPO_JSON_PATH);
		StringBuffer sb = new StringBuffer();
		File file = new File(tpoJsonPath);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			
			mTPOJson = new JSONObject(sb.toString());
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException : "+e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : "+e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : "+e.getMessage());
		}
	}
}
