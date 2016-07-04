package com.zhan.kykp.practice;

import android.os.Bundle;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;

public class PracticeResolvingIdea extends BaseActivity {
	public final static String EXT_KEY_DIFFCULTY="Diffculty";
	public final static String EXT_KEY_HINT="Hint";
	
	private TextView mTVPracticeDiffculty;
	private TextView mTVPracticeHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_resolving_idea);
		mTVPracticeDiffculty = (TextView) findViewById(R.id.practice_diffculty);
		mTVPracticeHint = (TextView) findViewById(R.id.practice_hint);
		
		String diffculty=getIntent().getStringExtra(EXT_KEY_DIFFCULTY);
		String hint=getIntent().getStringExtra(EXT_KEY_HINT);
		
		mTVPracticeDiffculty.setText(diffculty);
		mTVPracticeHint.setText(hint);
	}

}
