package com.zhan.kykp.practice;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.DictionaryDB;
import com.zhan.kykp.db.DictionaryDB.Content;
import com.zhan.kykp.db.DictionaryDB.Example;
import com.zhan.kykp.db.DictionaryDB.Tran;
import com.zhan.kykp.db.DictionaryDB.WordTrans;

public class TansDetailsActivity extends BaseActivity {
	public static final String EXT_KEY_WORD="word";

	private TextView mTVWord;
	private TextView mTVMean;
	private TextView mTVExample;
	
	private DictionaryDB mDictionaryDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trans_details);
		
		mTVWord=(TextView)findViewById(R.id.word);
		mTVMean=(TextView)findViewById(R.id.mean);
		mTVExample=(TextView)findViewById(R.id.example);
		
		mDictionaryDB=new DictionaryDB(this);
		String word=getIntent().getStringExtra(EXT_KEY_WORD);
		WordTrans wordTrans=mDictionaryDB.queryData(word);
		
		mTVWord.append(wordTrans.word);
		
		if(wordTrans.contents.size()>0){
			String pron=wordTrans.contents.get(0).pron;
			if(!TextUtils.isEmpty(pron)){
				SpannableString pronSpan=new SpannableString(pron);
				pronSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, pron.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				mTVWord.append(" "+pronSpan+" :");
			}
			
			int index=0;
			
			for(Content content:wordTrans.contents) {
				SpannableString spannableString = new SpannableString(content.type);
				spannableString.setSpan(new ForegroundColorSpan(Color.argb(255,33,177,217)),0,content.type.indexOf(".")+1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				mTVMean.append(spannableString);
				for (Tran tran : content.trans) {
					mTVMean.append("" + tran.mean);
					for (Example example : tran.examples) {
						if (!TextUtils.isEmpty(example.en)) {
							index++;
							mTVExample.append(index + "." + example.en + "\n");
						}

						if (!TextUtils.isEmpty(example.ch)) {
							mTVExample.append(example.ch + ";\n\n");
						}
					}
				}
				mTVMean.append(";\n\n");
			}
		}
	}
}
