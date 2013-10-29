package com.insp.android.oauth2;


import com.insp.android.oauth2.WebApiHelper;
import com.insp.android.oauth2.tasks.OnApiRequestListener;
import com.insp.feedly.requests.GetFeedlyCodeRequest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthenticationFragment extends Fragment implements OnApiRequestListener
{	
	
	private ProgressDialog authenticationDialog;
	
	@Override
	public void onException(Exception ex)
	{
		
	}
	
	@Override
	public void onFinishRequest()
	{
		if (authenticationDialog == null)
		{
			return;
		}
		authenticationDialog.dismiss();
		authenticationDialog = null;
	}
	
	@Override
	public void onStartRequest()
	{
		if (authenticationDialog == null)
		{
			authenticationDialog = new ProgressDialog(getActivity());
		}
		authenticationDialog.dismiss();
		authenticationDialog.setTitle("Requesting...");
		authenticationDialog.show();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.feedly_oauth2_browser, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initializeViews(savedInstanceState);
	}

	private WebChromeClient getWebChromeClient()
	{
		return new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int progress)
			{
				if(getActivity() != null)
				{
					getActivity().setProgress(progress * 100);
				}
			}
		};
	}
	
	public WebViewClient getWebViewClient()
	{
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Loading Website... Please Wait");
		return new WebViewClient()
		{
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				super.onPageStarted(view, url, favicon);
				updateProgressBarVisibility(true);
				dialog.show();
			}
			
			@Override
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);
				updateProgressBarVisibility(false);
				dialog.hide();
			}
			
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				if (WebApiHelper.getInstance().handleFeedlyAuthenticationResponse(url, AuthenticationFragment.this))
				{
					return true;
				}
        		return super.shouldOverrideUrlLoading(view, url);
        	}
		};
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initializeViews(Bundle savedInstanceState)
	{
		if(savedInstanceState == null && getActivity() != null)
		{
			boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
			WebSettings.ZoomDensity zoomDensity = isTablet ? WebSettings.ZoomDensity.MEDIUM : WebSettings.ZoomDensity.FAR;

			WebView description = (WebView)getView().findViewById(R.id.description);
			description.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			description.getSettings().setJavaScriptEnabled(true);
			//description.getSettings().setPluginState(WebSettings.PluginState.ON);
			description.getSettings().setDefaultTextEncodingName("utf-8");
			description.getSettings().setLoadWithOverviewMode(true);
			description.getSettings().setDefaultZoom(zoomDensity);
			description.getSettings().setSupportZoom(true);
			description.getSettings().setBuiltInZoomControls(true);
			description.requestFocus(View.FOCUS_DOWN);
			description.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
			description.getSettings().setUseWideViewPort(isTablet);
			description.setWebChromeClient(this.getWebChromeClient());
			description.setWebViewClient(this.getWebViewClient());
			GetFeedlyCodeRequest request = new GetFeedlyCodeRequest(getActivity());
			description.loadUrl(request.getEncodedUrl());
		}
	}
	
	private void updateProgressBarVisibility(boolean visible)
	{
		if(this.getActivity() != null)
		{
			this.getActivity().setProgressBarVisibility(visible);
		}
	}
	
}
