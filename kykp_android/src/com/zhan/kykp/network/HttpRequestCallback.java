package com.zhan.kykp.network;

public interface HttpRequestCallback {
	public void onRequestFailed(String errorMsg);
	public void onRequestFailedNoNetwork();
	public void onRequestCanceled();
	public void onRequestSucceeded(String content);
}
