package com.infospace.feedly.requests;

import android.content.Context;

import com.infospace.android.oauth2.WebApiRequest;
import com.insp.android.oauth2.R;

public class RetrieveSubscriptionsRequest extends WebApiRequest
{
	public RetrieveSubscriptionsRequest(Context context)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "GET", context);
		setMethod(R.string.feedly_api_get_subscriptions);
	}
}
