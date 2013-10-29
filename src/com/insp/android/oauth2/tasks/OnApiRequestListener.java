package com.insp.android.oauth2.tasks;

public interface OnApiRequestListener
{
	public void onStartRequest();
	public void onFinishRequest();
	public void onException(Exception ex);
}
