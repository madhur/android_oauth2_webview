package com.insp.feedly.requests;

import android.content.Context;

import com.insp.android.oauth2.R;
import com.insp.android.oauth2.WebApiRequest;

public class RetrieveOAuth2TokenRequest extends WebApiRequest
{
	public RetrieveOAuth2TokenRequest(Context context, String feedlyCode)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "POST", context);
		setMethod(R.string.feedly_api_exchange_code_for_tokens);
		addParam(R.string.feedly_api_param_client_id, R.string.feedly_client_id);
		addParam(R.string.feedly_api_param_redirect_uri, R.string.feedly_redirect_uri);
		addParam(R.string.feedly_api_param_code, feedlyCode);
		addParam(R.string.feedly_api_param_client_secret, R.string.feedly_client_secret);
		addParam(R.string.feedly_api_param_grant_type, R.string.feedly_api_param_grant_type_default_val);
	}
}
