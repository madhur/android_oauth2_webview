package com.infospace.android.oauth2.tasks;

public interface OnApiRequestListener
{
	public void onStartRequest();
	public void onFinishRequest(String response);
	public void onException(Exception ex);
}
