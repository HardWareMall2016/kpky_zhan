package com.zhan.kykp.speaking;//package com.
import java.util.List;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.TPO.MediaRecordFunc;
import com.zhan.kykp.base.BaseActivity;

public class SpeakingActivity extends BaseActivity implements ISpeakingCallback {

	public final static String EXTRA_KEY_QUESTION_TYPE= "question_type";
	
	public final static int QUESTION_TYPE_LIB=1;
	public final static int QUESTION_TYPE_JJ_YUCE=2;
	
	
	private int mQuestionType=QUESTION_TYPE_LIB;
	
	//Main Fragment
	private FragmentQuestionList mFragMain;
	
	private FragmentSearchresult mFragmentSearchresult;
	private FragmentPractice mFragmentPractice;
	
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

	@Override
	public LinearLayout getMenuContent() {
		return getActionBarRightContent();
	}
	
	@Override
	public FrameLayout getCustomerActionBarLayout() {
		return super.getCustomerActionBarLayout();
	}
	
	@Override
	public View getActionBarDefaultLayout() {
		return getActionDefLayout();
	}

	@Override
	public void gotoPosition(int position) {
		mFragmentPractice = new FragmentPractice();
		mFragmentPractice.prepareArguments(position);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, mFragmentPractice);
		transaction.addToBackStack(null);
		transaction.commit();
		
		refreshActionBar();
	}
	
	@Override
	public void showSearchResult(String keyword) {
		mFragmentSearchresult = new FragmentSearchresult();
		mFragmentSearchresult.prepareArguments(keyword);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, mFragmentSearchresult);
		transaction.addToBackStack(null);
		transaction.commit();

		refreshActionBar();
	}

	private void init() {
		
		mQuestionType=getIntent().getIntExtra(EXTRA_KEY_QUESTION_TYPE, QUESTION_TYPE_LIB);
		
		if(mQuestionType==QUESTION_TYPE_LIB){
			mFragMain = new FragmentSpeakingLib();
		}else if(mQuestionType==QUESTION_TYPE_JJ_YUCE){
			mFragMain = new FragmentJiJingYuCe();
		}
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content, mFragMain);
		transaction.commit();
		
		initBackConfirmDlg();
	}
	
	@Override
	public void onBackPressed() {
		// 退出练习详情界面
		if (mFragmentPractice != null) {
			//在对话框中处理退出练习详情界面
			if(MediaRecordFunc.getInstance().isRecording()){
				mDialogBackConfirm.show();
			}else{
				super.onBackPressed();
				mFragmentPractice=null;
				refreshActionBar();
			}
		} else if (mFragmentSearchresult != null) {
			super.onBackPressed();
			// 退出搜索结果界面
			mFragmentSearchresult=null;
			refreshActionBar();
		}else{
			super.onBackPressed();
		}
	}

	private void refreshActionBar(){
		//练习详情界面
		if(mFragmentPractice!=null){
			getActionBarRightContent().removeAllViews();
			//如果是搜索界面过来的
			if(mFragmentSearchresult!=null){
				setTitle(mFragmentSearchresult.getQuestionListType());
			}else{
				setTitle(mFragMain.getQuestionListType());
			}
			
		}else if(mFragmentSearchresult!=null){
			//搜索结果界面
			getActionBarRightContent().removeAllViews();
			setTitle(mFragmentSearchresult.getQuestionListType());
		}else{
			//住界面
			mFragMain.initActionBar();
		}
	}

	@Override
	public List<Question> getQuestionList() {
		if(mFragmentSearchresult!=null){
			return mFragmentSearchresult.getQuestionList();
		}else{
			return mFragMain.getQuestionList();
		}
	}

	@Override
	public void setSpeakingTitle(String title) {
		setTitle(title);
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
				
				//退出练习界面
				SpeakingActivity.super.onBackPressed();
				mFragmentPractice=null;
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
