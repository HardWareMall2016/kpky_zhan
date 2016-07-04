package com.zhan.kykp.network;

import java.io.File;

public interface FileDownloadListener {
	public void onDownloadFailed(String errorMsg);
	public void onNoNetwork();
	public void onCanceled();
	public void onDownloadSuccess(File downFile);
}
