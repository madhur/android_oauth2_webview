package com.insp.feedly.requests;

import android.content.Context;

import com.insp.android.oauth2.R;
import com.insp.android.oauth2.WebApiRequest;

public class RetrieveSubscriptionsRequest extends WebApiRequest
{
	public RetrieveSubscriptionsRequest(Context context)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "GET", context);
		setMethod(R.string.feedly_api_get_subscriptions);
	}
}
