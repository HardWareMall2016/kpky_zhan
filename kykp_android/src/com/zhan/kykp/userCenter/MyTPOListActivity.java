package com.zhan.kykp.userCenter;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.db.TPORecordDB;
import com.zhan.kykp.entity.dbobject.TPORecord;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.util.StatisticUtils;

public class MyTPOListActivity extends BaseActivity {
	private LayoutInflater mInflater;
	private List<List<TPORecord>> mGroupData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_tpo_list);
		
		mInflater = LayoutInflater.from(this);
		
		TPORecordDB queryDB=new TPORecordDB(this);
		mGroupData=queryDB.queryDataGroupByTPOName(UserInfo.getCurrentUser().getObjectId());
		
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.list);
        expandableListView.setAdapter(mTPOGroupAdapter);
		if (mGroupData.size() == 0) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			imageView.setImageResource(R.drawable.no_answer);
			imageView.setScaleType(ScaleType.CENTER);
			expandableListView.setEmptyView(imageView);
			setContentView(imageView);
		}

		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				StatisticUtils.onEvent(getString(R.string.my_tpo), "详情页");
				Intent intent=new Intent(MyTPOListActivity.this,MyTPODetailsActivity.class);
				TPORecord tpoRecord=(TPORecord)mTPOGroupAdapter.getChild(groupPosition, childPosition);
				intent.putExtra(MyTPODetailsActivity.EXTRA_KEY_TPO_NAME, tpoRecord.TPOName);
				intent.putExtra(MyTPODetailsActivity.EXTRA_KEY_TPO_QUESTION, tpoRecord.QuestionIndex);
				intent.putExtra(MyTPODetailsActivity.EXTRA_KEY_TPO_INDEX, tpoRecord.TPOIndex);
				startActivity(intent);
				return false;
			}
		});
	}

	final ExpandableListAdapter mTPOGroupAdapter = new BaseExpandableListAdapter() {

		@Override
		public TPORecord getChild(int arg0, int arg1) {
			return mGroupData.get(arg0).get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_tpo_child_item, null);
			}
			
			TextView tvTitle=(TextView)convertView.findViewById(R.id.title);	
			tvTitle.setText(getChild(groupPosition, childPosition).QuestionIndex);
			
			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return mGroupData.get(arg0).size();
		}

		@Override
		public List<TPORecord> getGroup(int arg0) {
			 return mGroupData.get(arg0);
		}

		@Override
		public int getGroupCount() {
			 return mGroupData.size();//generalsTypes.length;
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_tpo_group_item, null);
			}
			
			TextView tvTitle=(TextView)convertView.findViewById(R.id.title);	
			tvTitle.setText(getGroup(groupPosition).get(0).TPOName);
			
			ImageView imgRight=(ImageView)convertView.findViewById(R.id.right_img);
			if(isExpanded){
				imgRight.setImageResource(R.drawable.arrow_down);
			}else{
				imgRight.setImageResource(R.drawable.arrow_right);	
			}
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	};
}
