package com.zhan.kykp.network;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zhan.kykp.R;
import com.zhan.kykp.base.EnvConfig;
import com.zhan.kykp.network.bean.FileDownloadWithProgressListener;
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.HttpClientUtils;
import com.zhan.kykp.util.Utils;

public class BaseHttpRequest {
	private final static String TAG = "BaseHttpRequest";


	//public final static String SERVICE_BASE_URL="http://reference.tpooo.net";
	
	public enum RequestType{GET,POST}
	
	/***
	 * 
	 * @param context
	 * @param apiUrl
	 * @param requestParams 参数组，requestType为get时此参数无效
	 * @param requestCallback
	 * @param requestType  请求类型GET/POST
	 * @return
	 */
	public RequestHandle startRequest(Context context,String apiUrl, RequestParams requestParams, final HttpRequestCallback requestCallback,RequestType requestType) {
		RequestHandle mRequestHandle = null;
		final Resources mResources=context.getResources();
		
		String requestUrl=EnvConfig.SERVICE_BASE_URL + apiUrl;
		
		Log.i(TAG, "requestUrl = "+requestUrl);
		Log.i(TAG, "requestType = "+requestType);
		
		if (!Connectivity.isConnected(context)) {
			if(requestCallback!=null){
				requestCallback.onRequestFailedNoNetwork();
			}
            return null;
        }
		
		AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(responseBody!=null){
					String content=new String(responseBody);
					Log.i(TAG, "onFailure responseBody = "+content);
				}
				
				Log.i(TAG, "onFailure statusCode = "+statusCode);
				if(requestCallback!=null){
					if(statusCode==0){
						requestCallback.onRequestFailedNoNetwork();
					}else{
						requestCallback.onRequestFailed(mResources.getString(R.string.unknow_service_error));
					}
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = new String(responseBody);
				Log.i(TAG, "onSuccess statusCode = " + statusCode);
				Log.i(TAG, "onSuccess responseBody = " + content);
				if(requestCallback!=null){
					requestCallback.onRequestSucceeded(content);
				}
			}
			
			@Override
			public void onCancel() {
				if(requestCallback!=null){
					requestCallback.onRequestCanceled();
				}
			}
		};

		//Header
		Header[] headers=new Header[3];
		headers[0]=new BasicHeader("platform", "Android");
		headers[1]=new BasicHeader("version", getVersion(context));
		headers[2]=new BasicHeader("qAuthor", Utils.getQAuthor(context));

		switch (requestType) {
		case GET:
			//if(requestParams!=null){
				mRequestHandle=HttpClientUtils.get(requestUrl,headers,requestParams,responseHandler);
			/*}else{
				mRequestHandle=HttpClientUtils.get(requestUrl,responseHandler);
			}*/
			break;
		/*case DELETE:
			mRequestHandle=HttpClientUtils.delete(apiUrl,requestParams,responseHandler);
			break;*/
		case POST:
			mRequestHandle=HttpClientUtils.post(requestUrl,headers,requestParams,responseHandler);
			break;
		/*case PUT:
			mRequestHandle=HttpClientUtils.put(apiUrl, requestParams,responseHandler);
			break;*/
		default:
			break;
		}
		
		return mRequestHandle;
	}


	private String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "2.0.0";
		}
	}

	public RequestHandle downloadFile(Context context,String serviceFileUrl,String saveFilePath,final FileDownloadListener callback){
		final Resources mResources=context.getResources();
		Log.i(TAG, "downloadFile = "+serviceFileUrl);
		
		if(TextUtils.isEmpty(serviceFileUrl)){
			callback.onDownloadFailed(mResources.getString(R.string.network_download_path_null));
			 return null;
		}
		
		if (!Connectivity.isConnected(context)) {
			callback.onNoNetwork();callback.onNoNetwork();
            return null;
        }

		FileDownloadWithProgressListener tempCallback=null;
		if(callback instanceof FileDownloadWithProgressListener){
			tempCallback=(FileDownloadWithProgressListener)callback;
		}
		final FileDownloadWithProgressListener progressCallback=tempCallback;

		File saveFile=new File(saveFilePath);
		FileAsyncHttpResponseHandler responseHandler=new FileAsyncHttpResponseHandler(saveFile){
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
				Log.i(TAG, "onFailure statusCode = "+statusCode);
				if(statusCode==0){
					callback.onNoNetwork();
				}else{
					callback.onDownloadFailed(mResources.getString(R.string.download_fialed));
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, File file) {
				Log.i(TAG, "onSuccess statusCode = "+statusCode);
				Log.i(TAG, "onSuccess file length = "+file.length());
				if(file.length()==0){
					callback.onDownloadFailed(mResources.getString(R.string.download_fialed));
				}else{
					callback.onDownloadSuccess(file);
				}
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(progressCallback!=null){
					progressCallback.onProgress(bytesWritten,totalSize);
				}
			}

			@Override
			public void onCancel() {
				callback.onCanceled();
			}
		};
		return HttpClientUtils.get(serviceFileUrl,responseHandler);
	}

	public  static void releaseRequest(RequestHandle request){
		if(request!=null&&!request.isFinished()){
			request.cancel(true);
		}
	}
}
