android_oauth2_webview
======================

This is a library project to render a fragment containing a webview whihc authenticate against the Feedly Cloud API.

See http://developer.android.com/tools/projects/projects-eclipse.html#SettingUpLibraryProject for instructions on adding a library project to your app.

This example assumes you have a feedly sandbox/cloud account configured.



Update xml resources
----------------------

In ```res/values/feedly_strings.xml``` there are some variables which must be overriden:

```feedly_client_id```, ```feedly_client_secret```, ```feedly_redirect_uri```: provided to you when you sign up for a feedly dev account
```feedly_api_url```: This is the api url as specified  here http://developer.feedly.com/v3/.  As of 22-OCT-2013 the production endpoint is ```http://cloud.feedly.com``` and the sandbox endpoint is ```http://sandbox.feedly.com```



Showing the Fragment
--------------------
The fragment with the Authentication WebView can be found at ```com.insp.android.oauth2.AuthenticationFragment```.  Note that this fragment utilizes the v4 compatibility libraries.

Once the user logs in, the access token can be retrieved via ```WebApiHelper.getAccessToken()```.



Using the Feedly API
--------------------

```LoadWebUrlAsyncTask```, ```OnApiRequestListener``` and ```WebApiRequest``` classes are used to create web requests and handle JSON responses.

First you must initialize an ```OnApiRequestListener```.  This class is notified when events in the HTTP request lifecycle occur.  The most common use for this:

```OnStartRequest```: Called when an HTTP request is initiated.  Can be useful for setting ProgressDialogs.

```OnFinishRequest```: Called once the HTTP request successfully completes.  Useful for dismissing ProgressDialogs as well as processing the request Data.

```OnException```: Called if an exception is encountered while a request is in progress.

[Here](https://github.com/infospace/android_oauth2_webview/blob/master/src/com/infospace/android/oauth2/AuthenticationFragment.java#L77) is an example implementation from github.

```
OnApiRequestListener listener = new OnApiRequestListener() {
	
	@Override
	public void onStartRequest()
	{
		showAuthenticationDialog("Loading...");
	}
	
	@Override
	public void onFinishRequest(String response)
	{
		if(WebApiHelper.getInstance().saveFeedlyTokensFromResponseToPreferences(response))
		{
			// do something
		}
		hideAuthenticationDialog();
	}
	
	@Override
	public void onException(Exception ex)
	{
		
	}
};
```

Once you have an OnApiRequestListener, a ```LoadWebUrlAsyncTask``` should be instantationed and assigned the listener:

```
LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask();
getFeedlyAccessTokenAsyncTask.setOnWebRequestCallback(callback);
```

An APIRequest class needs to be instantiated.  There is a small subset of Feedly API calls found in ```com.infospace.feedly.requests```.  Additional API calls may be created using ```com.infospace.android.oauth2.WebApiRequest``` class.

to create a new Feedly API Request, create a new class which extends ```WebApiRequest```.  An example would look something like this:

```
package com.infospace.feedly.requests;

import android.content.Context;

import com.infospace.android.oauth2.WebApiRequest;
import com.insp.android.oauth2.R;

public class SomeNewFeedlyApiMethod extends WebApiRequest
{
	public GetFeedlyCodeRequest(Context context)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "GET", context);
		setMethod(R.string.feedly_api_method_name_in_strings_xml);
		addParam(R.string.feedly_api_param_name, R.string.param_value);
	}
}
```

The new class should call ```super(context.getResources().getString(R.string.feedly_api_url), "GET", context);``` in the constructor.  The "GET" should be replaced with the HTTP Request method as specified by the Feedly API.

The constructor must also call setMethod and provide the Resource ID of the method name in the strings.xml file.

Lastly any parameters must be applied as well.  The first value must be a reference to a string resource.  This string resource must correspond to a parameter name as defined by feedly.  All params as defined by feedly should be accounted for here.  the second value may be either a reference to a string resource or a string.

Once a WebApiRequest has been defined and instantiated, you can start the request using:

```
WebApiRequest request = new RetrieveOAuth2TokenRequest(context, code);
getFeedlyAccessTokenAsyncTask.execute(request);
```

Sample Fragment Implementation:
--------------------------------
In your activity layout, ensure the topmost layout container has an ```android:id="@+fragment_container"```, i.e.:

```
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/fragment_container"/>
```

In the onCreate of your activity:

```
	WebApiHelper.register(getApplicationContext());
```	

To render the fragment into the ```fragment_container```, execute the following snippet of code:
```
	FragmentManager manager = getSupportFragmentManager();
	FragmentTransaction transaction = manager.beginTransaction();
	transaction.replace(R.id.fragment_container, new com.insp.android.oauth2.AuthenticationFragment(), "auth_fragment");
	transaction.addToBackStack("auth_fragment");
	transaction.commit();
```

Note: This code snippet assumes you registered the ```WebApiHelper```.  It also assumes you have a FrameLayout available in your Activity layout with an android:id of ```fragment_container```.
