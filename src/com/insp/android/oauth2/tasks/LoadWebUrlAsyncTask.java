package com.insp.android.oauth2.tasks;

import java.io.*;
import java.net.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class LoadWebUrlAsyncTask extends AsyncTask<String, Void, String>
{

	private ProgressDialog dialog;
	
	public void setContext(Context context)
	{
		dialog = new ProgressDialog(context);
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		dialog.setTitle("Authorizing...");
		dialog.show();
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		try
		{
			URL url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(params[1]);
			if (params.length == 3)
			{
				conn.setRequestProperty("Authorization", "OAuth " + params[2]);
			}
			return readStream(conn.getInputStream());
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
				}
			}
		}
		return page;
		
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		handleResponse(result);
		dialog.dismiss();
	}
	
	public abstract void handleResponse(String response);
}
