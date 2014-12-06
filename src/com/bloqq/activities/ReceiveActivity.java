package com.bloqq.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bloqq.BloqqWebView;
import com.bloqq.R;
import com.bloqq.cache.App;
import com.bloqq.edits.AddressUrlEdit;
import com.bloqq.sqlite.UpdateHistDbTask;

public class ReceiveActivity extends ActionBarActivity {

	private BloqqWebView mWebView;
	private String mCurrentUrl;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getIntent().getAction() != null) {
			if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
				Intent data = getIntent();
				mCurrentUrl = data.getData().toString();
				Log.i("DEBUG", "URL: " + mCurrentUrl);
			}
		}

		mWebView = (BloqqWebView) findViewById(R.id.webPage);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				App app = (App) getApplication();
				if (!app.isOnline()) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Internet connection problems", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				if (url.contains("oauth_verifier")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.setData(uri);
					startActivity(intent);
					finish();
					return true;
				} else if (url.contains("denied")) {
					Toast.makeText(getApplicationContext(),
							"Access to account denied", Toast.LENGTH_LONG)
							.show();
					finish();
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mCurrentUrl = url;
				super.onPageFinished(view, url);
				findViewById(R.id.main_layout).requestFocus();
				new UpdateHistDbTask(getApplicationContext()).execute(mWebView
						.copyBackForwardList());
			}
		});

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFixedFontSize(10);
		final Activity activity = this;
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				activity.setProgress(newProgress * 100);
			}
		});

		mWebView.loadUrl(mCurrentUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_go);
		MenuItemCompat.setOnActionExpandListener(item,
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {

						final View edit = MenuItemCompat.getActionView(item);
						if (edit.getClass().getSimpleName()
								.equals(AddressUrlEdit.class.getSimpleName())) {
							AddressUrlEdit uInput = (AddressUrlEdit) edit;
							uInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									if (actionId == EditorInfo.IME_ACTION_GO) {
										String url = ((AddressUrlEdit) edit)
												.getText().toString();
										if (url.startsWith("http://")
												|| url.startsWith("https://")) {
											mCurrentUrl = url;
											mWebView.loadUrl(mCurrentUrl);
										} else {
											mCurrentUrl = "http://google.com/search?q="
													+ url;
											mWebView.loadUrl(mCurrentUrl);
										}
										return true;
									}
									return false;
								}
							});
							uInput.setOnFocusChangeListener(new OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (v.getId() == R.id.action_go
											&& !hasFocus) {
										InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
										imm.hideSoftInputFromWindow(
												edit.getWindowToken(), 0);
									}
								}
							});
							((AddressUrlEdit) edit).setText(mCurrentUrl);
							edit.clearFocus();
						}

						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						return true;
					}
				});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.go_button) {
			View go = findViewById(R.id.action_go);
			if (go instanceof EditText) {
				String url = ((AddressUrlEdit) go).getText().toString();
				if (url.startsWith("http://") || url.startsWith("https://")) {
					mCurrentUrl = url;
					mWebView.loadUrl(mCurrentUrl);
				} else {
					mCurrentUrl = "http://google.com/search?q=" + url;
					mWebView.loadUrl(mCurrentUrl);
				}
			} else {
				mWebView.loadUrl(mCurrentUrl);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
