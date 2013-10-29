package com.infospace.android.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.insp.android.oauth2.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class WebApiRequest
{
	private String apiUrl;
	private String apiMethod;
	private List<NameValuePair> params;
	private Context context;
	private String requestMethod;
	
	public WebApiRequest(String apiUrl, String requestMethod, Context context)
	{
		this.apiUrl = apiUrl;
		this.context = context;
		this.requestMethod = requestMethod;
	}
	
	public String getOAuthToken()
	{
		return getSharedPreferenceValue(R.string.feedly_api_access_token);
	}
	
	private String getSharedPreferenceValue(int resourceKeyId)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String preferenceValue = preferences.getString(context.getResources().getString(resourceKeyId), "");
		return preferenceValue;
	}
	
	public String getRequestMethod()
	{
		return requestMethod;
	}
	
	public WebApiRequest setMethod(int methodStringId)
	{
		if (context == null)
		{
			return this;
		}
		String methodName = context.getString(methodStringId);
		return setMethod(methodName);
	}
	
	public WebApiRequest setMethod(String method)
	{
		apiMethod = method;
		return this;
	}

	public WebApiRequest addParam(int nameId, int valueId)
	{
		if (context == null)
		{
			return this;
		}
		String value = context.getString(valueId);
		return addParam(nameId, value);
	}
	
	public WebApiRequest addParam(int nameId, String value)
	{
		if (context == null)
		{
			return this;
		}
		String name = context.getString(nameId);
		return addParam(name, value);
	}
	
	private WebApiRequest addParam(final String name, final String value)
	{
		NameValuePair nvp = new NameValuePair()
		{
			@Override
			public String getValue()
			{
				return value;
			}
			
			@Override
			public String getName()
			{
				return name;
			}
		};
		return addParam(nvp);
	}
	
	private WebApiRequest addParam(NameValuePair nvp)
	{
		if (params == null)
		{
			params = new ArrayList<NameValuePair>();
		}
		if (!params.contains(nvp))
		{
			params.add(nvp);
		}
		return this;
	}
	
	public String getEncodedUrl()
	{
		StringBuilder sb = new StringBuilder(apiUrl);
		
		if (apiMethod == null)
		{
			return sb.toString();
		}
		sb.append(apiMethod);
		
		if (params == null)
		{
			return sb.toString();
		}
		if (params.isEmpty())
		{
			return sb.toString();
		}
		
		sb.append("?");
		
		for (NameValuePair nvp : params)
		{
			try
			{
				String encodedName = URLEncoder.encode(nvp.getName(), "UTF-8");
				String encodedValue = URLEncoder.encode(nvp.getValue(), "UTF-8");
				sb.append(encodedName).append("=").append(encodedValue).append("&");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
