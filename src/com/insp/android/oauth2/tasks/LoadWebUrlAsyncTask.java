package com.insp.android.oauth2.tasks;

import java.io.*;
import java.net.*;

import com.insp.android.oauth2.WebApiRequest;

import android.os.AsyncTask;
import android.text.TextUtils;

public class LoadWebUrlAsyncTask extends AsyncTask<WebApiRequest, Void, String>
{
	private OnApiRequestListener apiListener;
	
	public void setOnWebRequestCallback(OnApiRequestListener callback)
	{
		this.apiListener = callback;
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (apiListener != null)
		{
			apiListener.onStartRequest();
		}
	}
	
	@Override
	protected String doInBackground(WebApiRequest... request)
	{
		try
		{
			if (request == null)
			{
				return null;
			}
			if (request.length != 1)
			{
				return null;
			}
			URL url = new URL(request[0].getEncodedUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(request[0].getRequestMethod());
			if (!TextUtils.isEmpty(request[0].getOAuthToken()))
			{
				conn.setRequestProperty("Authorization", "OAuth " + request[0].getOAuthToken());
			}
			return readStream(conn.getInputStream());
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			callWebRequestException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			callWebRequestException(e);
		}
		return null;
	}
	
	private String readStream(InputStream in)
	{
		BufferedReader reader = null;
		String page = "";
		try
		{
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null)
			{
				page += line;
				line = reader.readLine();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			callWebRequestException(ex);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					callWebRequestException(ex);
				}
			}
		}
		return page;
		
	}
	
	private void callWebRequestException(Exception ex)
	{
		if (apiListener == null)
		{
			return;
		}
		apiListener.onException(ex);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (apiListener != null)
		{
			apiListener.onFinishRequest(result);
		}
	}
}
