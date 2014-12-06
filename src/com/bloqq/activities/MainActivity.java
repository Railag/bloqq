package com.bloqq.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.bloqq.SwipeTouchListener;
import com.bloqq.cache.App;
import com.bloqq.cache.DiskCache;
import com.bloqq.edits.AddressUrlEdit;
import com.bloqq.edits.BlockUrlEdit;
import com.bloqq.sqlite.UpdateFavDbTask;
import com.bloqq.sqlite.UpdateHistDbTask;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends ActionBarActivity {

	private BloqqWebView mWebView;
	private String mHomepage;
	private String mCurrentUrl;
	public final static String HOMEPAGE = "homepage";

	public final static int REQUEST_HIST = 0;
	public final static int REQUEST_FAV = 1;
	public final static String EXTRA_URL = "url";

	private SlidingMenu mMenu;

	private SharedPreferences mPrefs;

	private BlockUrlEdit mInputUrl;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mHomepage = mPrefs.getString(HOMEPAGE, "http://google.com");

		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

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
		if (mPrefs.getBoolean("js_enabled", true)) {
			mWebView.getSettings().setJavaScriptEnabled(true);
		}
		mWebView.getSettings().setBuiltInZoomControls(true);

		try {
			int fontSize = Integer.parseInt(mPrefs
					.getString("font_size", "100"));
			mWebView.getSettings().setTextZoom(fontSize);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		}

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
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mMenu.setFadeEnabled(true);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mMenu.setMenu(R.layout.left_sliding_menu);
		mMenu.setSlidingEnabled(true);

		View menuLayout = findViewById(R.id.menu_relative_layout);
		menuLayout.setOnTouchListener(new SwipeTouchListener(this) {

			@Override
			public void onSwipeRight() {
			}

			@Override
			public void onSwipeLeft() {
				mMenu.toggle();
			}
		});

		GridView grid = (GridView) mMenu.findViewById(R.id.menu_grid_layout);
		final BlocksAdapter adapter = new BlocksAdapter(this, mPrefs);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mWebView.loadUrl(v.getTag().toString());
				if (mMenu.isShown()) {
					mMenu.toggle();
				}
				Log.i("DEBUG", v.getTag().toString());
			}
		});
		grid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (mInputUrl != null) {
					mInputUrl.setVisibility(View.GONE);
					mInputUrl = null;
				}
				mInputUrl = new BlockUrlEdit(getApplicationContext(), position);

				if (!v.getTag().toString().isEmpty()) {
					mInputUrl.setText(v.getTag().toString());
				}

				RelativeLayout layout = (RelativeLayout) findViewById(R.id.menu_relative_layout);

				layout.addView(mInputUrl);

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
									String url = mInputUrl.getText().toString();
									if (url.startsWith("http://")
											|| url.startsWith("https://")) {
										mCurrentUrl = url;
									} else {
										mCurrentUrl = "http://" + url;
									}

									editor.putString(
											"uri" + mInputUrl.getPosition(),
											mCurrentUrl);
									editor.commit();
									DiskCache cache = ((App) getApplication())
											.getDiskCache();
									cache.remove("uri"
											+ mInputUrl.getPosition());
									adapter.notifyDataSetChanged();
									mInputUrl.setVisibility(View.GONE);
									mInputUrl = null;
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
			startActivity(intent);
		} else if (id == R.id.fav_add_button) {
			new UpdateFavDbTask(this).execute(mWebView.getTitle(),
					mWebView.getUrl());
		} else if (id == R.id.fav_button) {
			Intent intent = new Intent(MainActivity.this,
					FavoriteActivity.class);
			startActivityForResult(intent, REQUEST_FAV);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mMenu.isMenuShowing()) {
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
		} else if (requestCode == REQUEST_FAV) {
			if (requestResult == RESULT_OK) {
				mWebView.loadUrl(intent.getStringExtra(EXTRA_URL));
			}
		}
	}
}
