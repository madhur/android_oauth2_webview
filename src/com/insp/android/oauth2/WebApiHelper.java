package com.insp.android.oauth2;

import java.net.*;
import java.security.InvalidParameterException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.*;

import com.insp.android.oauth2.tasks.LoadWebUrlAsyncTask;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

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
	
	private WebApiRequest getRequest()
	{
		WebApiRequest request = new WebApiRequest(context.getResources().getString(R.string.feedly_api_url), context);
		return request;
	}
	
	public boolean handleFeedlyAuthenticationResponse(String url, final Context context)
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
		
		LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask()
		{
			@Override
			public void handleResponse(String response)
			{
				if(saveFeedlyTokensFromResponseToPreferences(response))
				{
					onRetrievedOAuthTokens();
				}
			}
		};
		getFeedlyAccessTokenAsyncTask.setContext(context);
		getFeedlyAccessTokenAsyncTask.execute(WebApiHelper.getInstance().getFeedlyAccessTokenUrl(code), "POST");
		return true;
	}
	
	public void refreshAccessTokenIfNeeded(Context activityContext)
	{
		if (shouldRefreshAccesToken())
		{
			refreshAccessToken(activityContext);
			onRefreshedTokens();
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
	
	public void refreshAccessToken(final Context context)
	{
		LoadWebUrlAsyncTask refreshFeedlyAcessTokensAsyncTask = new LoadWebUrlAsyncTask() {
			
			@Override
			public void handleResponse(String response) {
				saveFeedlyRefreshTokenFromResponseToPreferences(response);
			}
		};
		refreshFeedlyAcessTokensAsyncTask.setContext(context);
		refreshFeedlyAcessTokensAsyncTask.execute(getFeedlyRefreshTokenUrl(getResourceString(R.string.feedly_api_refresh_token)), "POST");
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
	
	private void onRetrievedOAuthTokens()
	{
		// TODO: put code here on what to do after authentication
	}
	
	private void onRefreshedTokens()
	{
		
	}

	// TODO: error checking here for invalid json, connection issues, invalid auth token, 404, etc
	private boolean saveFeedlyTokensFromResponseToPreferences(String response)
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
	
	
	public String getAccessToken()
	{
		return getSharedPreferenceValue(R.string.feedly_api_access_token);
	}
	
	private String getSharedPreferenceValue(int resourceKeyId)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String preferenceValue = preferences.getString(context.getResources().getString(resourceKeyId), "");
		return preferenceValue;
	}
	
	public String getFeedlyRefreshTokenUrl(String refreshToken)
	{
		WebApiRequest request = getRequest();
		request.setMethod(R.string.feedly_api_exchange_code_for_tokens);
		request.addParam(R.string.feedly_api_refresh_token, refreshToken);
		request.addParam(R.string.feedly_api_param_client_id, R.string.feedly_client_id);
		request.addParam(R.string.feedly_api_param_client_secret, R.string.feedly_client_secret);
		request.addParam(R.string.feedly_api_param_grant_type, R.string.feedly_api_refresh_token);
		return request.getEncodedUrl();
	}

	public String getFeedlyAccessTokenUrl(String accessToken)
	{
		WebApiRequest request = getRequest();
		request.setMethod(R.string.feedly_api_exchange_code_for_tokens);
		request.addParam(R.string.feedly_api_param_client_id, R.string.feedly_client_id);
		request.addParam(R.string.feedly_api_param_redirect_uri, R.string.feedly_redirect_uri);
		request.addParam(R.string.feedly_api_param_code, accessToken);
		request.addParam(R.string.feedly_api_param_client_secret, R.string.feedly_client_secret);
		request.addParam(R.string.feedly_api_param_grant_type, R.string.feedly_api_param_grant_type_default_val);
		return request.getEncodedUrl();
	}
	
	public String getFeedlyLoginUrl()
	{
		WebApiRequest request = getRequest();
		request.setMethod(R.string.feedly_api_authenticate_user);
		request.addParam(R.string.feedly_api_param_response_type, R.string.feedly_api_param_response_type_default_val);
		request.addParam(R.string.feedly_api_param_client_id, R.string.feedly_client_id);
		request.addParam(R.string.feedly_api_param_redirect_uri, R.string.feedly_redirect_uri);
		request.addParam(R.string.feedly_api_param_scope, R.string.feedly_api_param_scope_default_val);
		return request.getEncodedUrl();
	}
}
