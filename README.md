android_oauth2_webview
======================

This is a library project to render a fragment containing a webview whihc authenticate against the Feedly Cloud API.

See http://developer.android.com/tools/projects/projects-eclipse.html#SettingUpLibraryProject for instructions on adding a library project to your app.

This example assumes you have a feedly sandbox/cloud account configured.



Update xml resources
----------------------

In ```res/values/feedly_strings.xml``` there are some variables which must be overriden:

```feedly_client_id```, ```feedly_client_secret```, ```feedly_redirect_uri```: provided to you when you sign up for a feedly dev account
```feedly_api_url```: This is the base api as specified  here http://developer.feedly.com/v3/.  As of 22-OCT-2013 the production endpoint is ```http://cloud.feedly.com``` and the sandbox endpoint is ```http://sandbox.feedly.com```



Showing the Fragment
--------------------
The fragment with the Authentication WebView can be found at ```com.insp.android.oauth2.AuthenticationFragment```.  Note that this fragment utilizes the v4 compatibility libraries.

Once the user logs in, the access token can be retrieved via ```WebApiHelper.getAccessToken()```.



Using the Feedly API
--------------------

```
	public void requestFeedlyFeeds()
	{
		LoadWebUrlAsyncTask getFeedlyCategoriesAsyncTask = new LoadWebUrlAsyncTask()
		{
			
			@Override
			public void handleResponse(String response) {
				// this is the json response
			}
		};
		getFeedlyCategoriesAsyncTask.setContext(MainApplication.currentActivity);
		getFeedlyCategoriesAsyncTask.execute("sandbox.feedly.com/v3/subscriptions/", "GET", WebApiHelper.getInstance().getAccessToken());
	}
```
For ```LoadWebUrlAsyncTask```, the first parameter is the url encoded feedly api call.  The second parameter is the http method (i.e. "GET" or "POST") specified to be used by the feedly api.  The third parameter is a valid access token.



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
