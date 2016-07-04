package com.zhan.kykp.speakingIelts;

import java.util.List;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.base.BaseActivity;


public class SpeakingIeltsListActivity extends BaseActivity implements ISpeakingIeltsCallback{

	private FragmentSpeakingIelts mFragMain ;
	//Main Fragment
	private boolean mShowMainFrag=true;
	private Dialog mDialogBackConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED  
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON  
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		init();
	}
	
	private void init() {
		mFragMain = new FragmentSpeakingIelts();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, mFragMain);
		transaction.commit();
		
		initBackConfirmDlg();
	}

	@Override
	public LinearLayout getMenuContent() {
		return getActionBarRightContent();
	}

	@Override
	public void gotoPosition(int position) {
		FragmentPracticeIelts fragPractice = new FragmentPracticeIelts();
		fragPractice.prepareArguments(position-1);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, fragPractice);
		transaction.addToBackStack(null);
		transaction.commit();

		mShowMainFrag=false;
		
		refreshActionBar();
	}

	@Override
	public void setSpeakingIeltsTitle(String title) {
		setTitle(title);
	}

	@Override
	public List<IeltsData> getIeltsList() {
		return mFragMain.getIeltsList();
	}
	
	private void refreshActionBar(){
		if(!mShowMainFrag){
			getActionBarRightContent().removeAllViews();
			//setTitle(mFragMain.getQuestionListType());
		}else{
			mFragMain.initActionBar();
		}
	}

	@Override
	public void onBackPressed() {
		//在子Frag中回退到main frag
		/*if(!mShowMainFrag){
			mDialogBackConfirm.show();
		}else{
			super.onBackPressed();
		}*/
		//判断是否在录音，录音时弹出dialog
		if(mFragMain != null){
			if(MediaRecordFunc.getInstance().isRecording()){
				mDialogBackConfirm.show();
			}else{
				super.onBackPressed();
				mShowMainFrag=true;
				refreshActionBar();
			}
		}
	}
	
	private void initBackConfirmDlg(){
		LayoutInflater inflater=getLayoutInflater();
		mDialogBackConfirm = new Dialog(this, R.style.Dialog);
		View dlgContent = inflater.inflate(R.layout.confirm_dialog_layout, null);
		TextView title = (TextView) dlgContent.findViewById(R.id.title);
		title.setText(R.string.speaking_exting_practice);
		
		TextView message = (TextView) dlgContent.findViewById(R.id.message);
		message.setText(R.string.speaking_exit_confirm_msg);
		
		TextView confirm = (TextView) dlgContent.findViewById(R.id.confirm);
		confirm.setText(R.string.speaking_exit_confirm);
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
				
				SpeakingIeltsListActivity.super.onBackPressed();
				mShowMainFrag=true;
				refreshActionBar();
			}
		});
		
		TextView cancel = (TextView) dlgContent.findViewById(R.id.cancel);
		cancel.setText(R.string.speaking_exit_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mDialogBackConfirm.dismiss();
			}
		});
		
		mDialogBackConfirm.setContentView(dlgContent);
	}
}