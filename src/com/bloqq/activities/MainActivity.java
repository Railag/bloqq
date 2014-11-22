package com.bloqq.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bloqq.BlocksAdapter;
import com.bloqq.BloqqWebView;
import com.bloqq.R;
import com.bloqq.cache.App;
import com.bloqq.cache.DiskCache;
import com.bloqq.edits.AddressUrlEdit;
import com.bloqq.edits.BlockUrlEdit;
import com.bloqq.sqlite.UpdateHistDbTask;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends ActionBarActivity {

	// WebView mWebView;
	private BloqqWebView mWebView;
	private String mHomepage;
	private String mCurrentUrl;
	public final static String HOMEPAGE = "homepage";

	public final static int REQUEST_HIST = 0;
	public final static String EXTRA_URL = "url";

	private SlidingMenu mMenu;

	private SharedPreferences mPrefs;

	private BlockUrlEdit mInputUrl;

	private boolean mIsUrlListened;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);// getSharedPreferences("settings",
																		// Context.MODE_PRIVATE);
		mHomepage = mPrefs.getString(HOMEPAGE, "http://google.com");

		// SharedPreferences.Editor editor = mPrefs.edit();
		// editor.putString(HOMEPAGE,
		// "http://developer.android.com/about/versions/lollipop.html");
		// editor.commit();
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		// mWebView = (WebView) findViewById(R.id.webPage);
		mWebView = (BloqqWebView) findViewById(R.id.webPage);
		mWebView.setWebViewClient(new WebViewClient() {

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(getApplicationContext(),
						"Oh no! " + description, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				final View edit = findViewById(R.id.action_go);

				if (edit.getClass().getSimpleName()
						.equals(AddressUrlEdit.class.getSimpleName())) {
					if (!mIsUrlListened) {
						AddressUrlEdit uInput = (AddressUrlEdit) edit;
						uInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_GO) {
									mCurrentUrl = ((AddressUrlEdit) edit)
											.getText().toString();
									mWebView.loadUrl(mCurrentUrl);
									return true;
								}
								return false;
							}
						});
						uInput.setOnFocusChangeListener(new OnFocusChangeListener() {

							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (v.getId() == R.id.action_go && !hasFocus) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											edit.getWindowToken(), 0);
								}
							}
						});
						mIsUrlListened = true;
					}
					((AddressUrlEdit) edit).setText(url);
				}
				mCurrentUrl = url;
				super.onPageFinished(view, url);
				edit.clearFocus();
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
		mCurrentUrl = mHomepage;
		mWebView.loadUrl(mCurrentUrl);

		mMenu = new SlidingMenu(this);
		mMenu.setMode(SlidingMenu.LEFT);
		// mMenu.setMode(SlidingMenu.LEFT_RIGHT);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// menu.setShadowWidthRes(R.dimen.dialog_fixed_width_minor);//
		// shadow_width);
		// menu.setShadowDrawable(R.drawable.abc_ab_bottom_solid_light_holo);
		// menu.setBehindOffsetRes(15);// slidingmenu_offset);
		mMenu.setFadeEnabled(true);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mMenu.setMenu(R.layout.left_sliding_menu);
		mMenu.setSlidingEnabled(true);
		// mMenu.setSecondaryMenu(R.layout.activity_main);

		GridView grid = (GridView) mMenu.findViewById(R.id.menu_grid_layout);
		final BlocksAdapter adapter = new BlocksAdapter(this, mPrefs);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mWebView.loadUrl(v.getTag().toString());
				mMenu.toggle();
				Log.i("DEBUG", v.getTag().toString());
			}
		});
		grid.setOnItemLongClickListener(new OnItemLongClickListener() {
			// Left and Right
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (mInputUrl != null) {
					mInputUrl.setVisibility(View.GONE);
					mInputUrl = null;
				}
				mInputUrl = new BlockUrlEdit(getApplicationContext(), position);

				RelativeLayout layout = (RelativeLayout) findViewById(R.id.menu_relative_layout);
				// p.topMargin = 50;
				// p.addRule(RelativeLayout.ABOVE, R.id.menu_grid_layout);
				layout.addView(mInputUrl);
				// mInputUrl.setInputType(InputType.TYPE_CLASS_TEXT);
				// mInputUrl.setContentDescription("text");

				// inputUrl.setText("http://");

				mInputUrl.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (v.getId() == BlockUrlEdit.ID && !hasFocus) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									mInputUrl.getWindowToken(), 0);
						}
					}
				});

				mInputUrl
						.setOnEditorActionListener(new TextView.OnEditorActionListener() {
							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_DONE) {
									SharedPreferences.Editor editor = mPrefs
											.edit();
									editor.putString(
											"uri" + mInputUrl.getPosition(),
											mInputUrl.getText().toString());
									editor.commit();
									DiskCache cache = ((App) getApplication())
											.getDiskCache();
									cache.remove("uri"
											+ mInputUrl.getPosition());
									adapter.notifyDataSetChanged();
									mInputUrl.setVisibility(View.GONE);
									mInputUrl = null;
									// mCurrentUrl =
									// inputUrl.getText().toString();
									// mWebView.loadUrl(mCurrentUrl);

									return true;
								}
								return false;
							}
						});
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.go_button) {
			View go = findViewById(R.id.action_go);
			if (go instanceof EditText) {
				mCurrentUrl = ((EditText) go).getText().toString();
				mWebView.loadUrl(mCurrentUrl);
			} else {
				mWebView.loadUrl(mCurrentUrl);
			}
			return true;
		} else if (id == R.id.hist_button) {
			Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
			startActivityForResult(intent, REQUEST_HIST);
		} else if (id == R.id.settings_button) {
			Intent intent = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivityForResult(intent, REQUEST_HIST);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
	 * mWebView.goBack(); return true; } return super.onKeyDown(keyCode, event);
	 * }
	 */
	@Override
	public void onBackPressed() {
		if (mMenu.isMenuShowing()) {
			// mMenu.showContent();
			mMenu.toggle(true);
		}
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}
		super.onBackPressed();
	}

	public WebView getWebView() {
		return mWebView;
	}

	public SlidingMenu getSlidingMenu() {
		return mMenu;
	}

	@Override
	protected void onActivityResult(int requestCode, int requestResult,
			Intent intent) {
		if (requestCode == REQUEST_HIST) {
			if (requestResult == RESULT_OK) {
				mWebView.loadUrl(intent.getStringExtra(EXTRA_URL));
			}
		}
	}

}
