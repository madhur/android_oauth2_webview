package com.infospace.android.oauth2;

import java.net.*;
import java.security.InvalidParameterException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.*;

import com.infospace.android.oauth2.tasks.LoadWebUrlAsyncTask;
import com.infospace.android.oauth2.tasks.OnApiRequestListener;
import com.infospace.feedly.requests.RefreshTokenRequest;
import com.infospace.feedly.requests.RetrieveOAuth2TokenRequest;
import com.insp.android.oauth2.R;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class WebApiHelper
{

	private static WebApiHelper instance;
	
	private final Context context;
	
	private WebApiHelper(Context context)
	{
		if (context == null)
		{
			throw new InvalidParameterException();
		}
		this.context = context;
	}
	
	public static void register(Context context)
	{
		instance = new WebApiHelper(context);
	}
	
	public static WebApiHelper getInstance()
	{
		return instance;
	}
	
	public boolean handleFeedlyAuthenticationResponse(String url, OnApiRequestListener callback)
	{
		if (!url.startsWith(getResourceString(R.string.feedly_redirect_uri)))
		{
			return false;
		}
		
		String code = getCodeFromUrl(url);
		if (code == null)
		{
			return false;
		}
		
		LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask();
		getFeedlyAccessTokenAsyncTask.setOnWebRequestCallback(callback);
		WebApiRequest request = new RetrieveOAuth2TokenRequest(context, code);
		getFeedlyAccessTokenAsyncTask.execute(request);
		return true;
	}
	
	public void refreshAccessTokenIfNeeded()
	{
		if (shouldRefreshAccesToken())
		{
			refreshAccessToken();
		}
	}
	
	public boolean shouldRefreshAccesToken()
	{
		try
		{
			long expirationDelta = Long.parseLong(getSharedPreferenceValue(R.string.feedly_api_expires_in));
			long timestamp = Long.parseLong(getSharedPreferenceValue(R.string.feedly_api_timestamp));
			long currentTime = System.currentTimeMillis()/1000;
			if (currentTime > timestamp + expirationDelta)
			{
				return true;
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	public void refreshAccessToken()
	{
		String refreshToken = getSharedPreferenceValue(R.string.feedly_api_refresh_token);
		if (TextUtils.isEmpty(refreshToken))
		{
			return;
		}
		LoadWebUrlAsyncTask refreshFeedlyAcessTokensAsyncTask = new LoadWebUrlAsyncTask();
		OnApiRequestListener requestListener = new OnApiRequestListener() {
			
			@Override
			public void onStartRequest()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinishRequest(String response)
			{
				if(response!=null)
					saveFeedlyRefreshTokenFromResponseToPreferences(response);
			}
			
			@Override
			public void onException(Exception ex)
			{
				// TODO Auto-generated method stub
				
			}
		};
		
		refreshFeedlyAcessTokensAsyncTask.setOnWebRequestCallback(requestListener);
		WebApiRequest request = new RefreshTokenRequest(context, refreshToken);
		refreshFeedlyAcessTokensAsyncTask.execute(request);
	}
	
	private boolean saveFeedlyRefreshTokenFromResponseToPreferences(String response)
	{
		try
		{
			JSONObject json = new JSONObject(response);
			String accessToken = json.getString(getResourceString(R.string.feedly_api_access_token));
			String userId = json.getString(getResourceString(R.string.feedly_api_user_id));
			String expiresIn = json.getString(getResourceString(R.string.feedly_api_expires_in));
			String timestamp = Long.toString(System.currentTimeMillis()/1000);
			saveToSharedPreferences(R.string.feedly_api_access_token, accessToken);
			saveToSharedPreferences(R.string.feedly_api_user_id, userId);
			saveToSharedPreferences(R.string.feedly_api_expires_in, expiresIn);
			saveToSharedPreferences(R.string.feedly_api_timestamp, timestamp);
			return true;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	// TODO: error checking here for invalid json, connection issues, invalid auth token, 404, etc
	public boolean saveFeedlyTokensFromResponseToPreferences(String response)
	{
		try
		{
			JSONObject json = new JSONObject(response);
			String accessToken = json.getString(getResourceString(R.string.feedly_api_access_token));
			String refreshToken = json.getString(getResourceString(R.string.feedly_api_refresh_token));
			String userId = json.getString(getResourceString(R.string.feedly_api_user_id));
			String expiresIn = json.getString(getResourceString(R.string.feedly_api_expires_in));
			String timestamp = Long.toString(System.currentTimeMillis()/1000);
			saveToSharedPreferences(R.string.feedly_api_access_token, accessToken);
			saveToSharedPreferences(R.string.feedly_api_refresh_token, refreshToken);
			saveToSharedPreferences(R.string.feedly_api_user_id, userId);
			saveToSharedPreferences(R.string.feedly_api_expires_in, expiresIn);
			saveToSharedPreferences(R.string.feedly_api_timestamp, timestamp);
			return true;

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private void saveToSharedPreferences(int prefKeyId, String value)
	{
		SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor currentEditor = currentPreferences.edit();
		currentEditor.putString(getResourceString(prefKeyId), value);
		currentEditor.commit();
	}

    private String getCodeFromUrl(String url)
    {
    	try
    	{
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
			String paramName = getResourceString(R.string.feedly_api_param_code);
	    	for (NameValuePair param : params)
	    	{
	    		if (param.getName().equals(paramName))
	    		{
	    			return param.getValue();
	    		}
	    	}
		}
    	catch (URISyntaxException e)
    	{
			// TODO do something??
		}
    	return null;
    }
    
	private String getResourceString(int resourceId)
	{
		return context.getResources().getString(resourceId);
	}
	
	private String getSharedPreferenceValue(int resourceKeyId)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String preferenceValue = preferences.getString(context.getResources().getString(resourceKeyId), "");
		return preferenceValue;
	}
}
