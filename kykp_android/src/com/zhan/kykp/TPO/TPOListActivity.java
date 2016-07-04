package com.zhan.kykp.TPO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.FileDownloadWithProgressListener;
import com.zhan.kykp.network.bean.TpoListBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.ProgressWheel;

public class TPOListActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	private final static String TAG = TPOListActivity.class.getSimpleName();

	private LayoutInflater mInflater;
	private TPOAdapter mAdapter;
	private List<ListItemInfo> mTPOList = new ArrayList<ListItemInfo>();
	private Dialog mProgressDlg;

	private String mTPODirPath;

	//Network
	private BaseHttpRequest mHttpRequest ;
	private RequestHandle mRequestHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tpo_list);
		mInflater = LayoutInflater.from(this);

		mHttpRequest = new BaseHttpRequest();

		File TPODir=PathUtils.getExternalTPOFilesDir();
		mTPODirPath=TPODir.getAbsolutePath();

		ListView listView = (ListView) findViewById(R.id.list);
		mAdapter = new TPOAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		
		loadTOPList();
	}

	@Override
	protected void onDestroy() {
		BaseHttpRequest.releaseRequest(mRequestHandle);
		for(ListItemInfo item:mTPOList){
			BaseHttpRequest.releaseRequest(item.mDownloadRequestHandle);
		}
		super.onDestroy();
	}

	private void loadTOPList() {
		BaseHttpRequest.releaseRequest(mRequestHandle);

		showProgressDialog();
		mRequestHandle=mHttpRequest.startRequest(this, ApiUrls.TPO_LIST, null, new HttpRequestCallback() {
			@Override
			public void onRequestFailed(String errorMsg) {
				closeDialog();
				Utils.toast(errorMsg);
			}

			@Override
			public void onRequestFailedNoNetwork() {
				Utils.toast(R.string.network_disconnect);
				closeDialog();
			}

			@Override
			public void onRequestCanceled() {
				closeDialog();
			}

			@Override
			public void onRequestSucceeded(String content) {
				closeDialog();
				TpoListBean bean=Utils.parseJson(content, TpoListBean.class);
				if(bean!=null){
					for(TpoListBean.DatasEntity tpo:bean.getDatas()){
						ListItemInfo info=new ListItemInfo();
						info.mTPO=tpo;
						mTPOList.add(info);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
		}, BaseHttpRequest.RequestType.GET);
	}

	private void showProgressDialog() {
		mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.loading));
		mProgressDlg.setCancelable(false);
		mProgressDlg.show();
	}

	private void closeDialog() {
		if (mProgressDlg != null) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
	}

	private class TPOAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mTPOList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mTPOList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tpo_list_item, null);
				holder = new ViewHolder();
				holder.mBtnDownload = (Button) convertView.findViewById(R.id.btn_download);
				holder.mProgress = (ProgressWheel) convertView.findViewById(R.id.btn_download_progress);
				holder.mProgress.setPaddingBottom(0);
				holder.mProgress.setPaddingLeft(0);
				holder.mProgress.setPaddingRight(0);
				holder.mProgress.setPaddingTop(0);
				holder.mImgRight = (ImageView) convertView.findViewById(R.id.arrow_right);
				holder.mTitle = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mBtnDownload.setTag(position);
			holder.mBtnDownload.setOnClickListener(TPOListActivity.this);
			
			ListItemInfo itemInfo=mTPOList.get(position);
			
			holder.mTitle.setText(itemInfo.mTPO.getName());
			//有解压文件夹，说明已下载解压
			if(isDownloaded(itemInfo.mTPO.getName())){
				holder.mBtnDownload.setVisibility(View.GONE);
				holder.mImgRight.setVisibility(View.VISIBLE);
				holder.mProgress.setVisibility(View.GONE);
			}else if(itemInfo.mDownloadRequestHandle!=null&&!itemInfo.mDownloadRequestHandle.isFinished()){
				//下载中
				holder.mBtnDownload.setVisibility(View.GONE);
				holder.mImgRight.setVisibility(View.GONE);
				holder.mProgress.setVisibility(View.VISIBLE);
				holder.mProgress.setText(itemInfo.mDownloadProgress+"%");
				holder.mProgress.setProgress((int) (itemInfo.mDownloadProgress/100f*360));
			} else {
				//未下载
				holder.mBtnDownload.setVisibility(View.VISIBLE);
				holder.mImgRight.setVisibility(View.GONE);
				holder.mProgress.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		TextView mTitle;
		ImageView mImgRight;
		ProgressWheel mProgress;
		Button mBtnDownload;
	}

	@Override
	public void onClick(View arg0) {

		StatisticUtils.onEvent(getTitle().toString(), getString(R.string.download));

		Integer position=(Integer)arg0.getTag();

		ListItemInfo itemInfo=mTPOList.get(position);

		BaseHttpRequest.releaseRequest(itemInfo.mDownloadRequestHandle);

		itemInfo.mDownloadRequestHandle=mHttpRequest.downloadFile(this,itemInfo.mTPO.getZipFile(), getZipSavePath(itemInfo.mTPO.getName()),new FileDownloadHandler(itemInfo));
		mAdapter.notifyDataSetChanged();
	}


	private class FileDownloadHandler implements FileDownloadWithProgressListener {
		private ListItemInfo mTpoItemInfo;
		public  FileDownloadHandler(ListItemInfo item){
			mTpoItemInfo=item;
		}

		@Override
		public void onDownloadFailed(String errorMsg) {
			Utils.toast(errorMsg);
			closeDialog();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNoNetwork() {
			Utils.toast(R.string.network_error);
			closeDialog();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onCanceled() {
			closeDialog();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onDownloadSuccess(File downFile) {
			closeDialog();
			unZipFile(downFile, mTpoItemInfo.mTPO.getName());
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onProgress(int bytesWritten, int totalSize) {
			int progress= (int) (bytesWritten*100L/totalSize);
			if(progress!=mTpoItemInfo.mDownloadProgress){
				mTpoItemInfo.mDownloadProgress=progress;
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	
	
	private class ListItemInfo{
		TpoListBean.DatasEntity mTPO;
		RequestHandle mDownloadRequestHandle;
		int mDownloadProgress=0;
	}


	private String getZipSavePath(String fileName){
		String zipFilePath=mTPODirPath + "/" + fileName + ".zip";
		return zipFilePath;
	}

	private void unZipFile(File zipFile,String fileName){
		if(zipFile==null&&zipFile.length()==0){
			return;
		}

		//删除解压文件夹
		String unZipPath=mTPODirPath + "/" + fileName;
		//判断是否存在这个文件，有删除
		PathUtils.recursionDeleteFile(new File(unZipPath));

		try {
			Utils.UnZipFolder(zipFile.getAbsolutePath(), unZipPath);
		} catch (IOException e) {
			Log.e(TAG, "IOException " + e.getMessage());
		}

		//最后删除下载的Zip文件
		if (zipFile.exists()) {
			zipFile.delete();
		}
	}

	
	private boolean isDownloaded(String fileName){
		File unzip=new File(mTPODirPath+"/"+fileName);
		return unzip.exists();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(!isDownloaded(mTPOList.get(arg2).mTPO.getName())){
			return;
		}

		Intent TPOIntent = new Intent(this, TPOActivity.class);
		TPOIntent.putExtra(TPOActivity.EXTRA_KEY_TPO_NAME, mTPOList.get(arg2).mTPO.getName());
		TPOIntent.putExtra(TPOActivity.EXTRA_KEY_TPO_INDEX, mTPOList.get(arg2).mTPO.getIndex());
		startActivity(TPOIntent);

		StatisticUtils.onEvent(getTitle().toString(), "模考详情页");
	}
}
