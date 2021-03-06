package com.infospace.feedly.requests;

import android.content.Context;

import com.infospace.android.oauth2.WebApiRequest;
import com.insp.android.oauth2.R;

public class RefreshTokenRequest extends WebApiRequest
{
	public RefreshTokenRequest(Context context, String refreshToken)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "POST", context);
		setMethod(R.string.feedly_api_exchange_code_for_tokens);
		addParam(R.string.feedly_api_refresh_token, refreshToken);
		addParam(R.string.feedly_api_param_client_id, R.string.feedly_client_id);
		addParam(R.string.feedly_api_param_client_secret, R.string.feedly_client_secret);
		addParam(R.string.feedly_api_param_grant_type, R.string.feedly_api_refresh_token);
	}
}
