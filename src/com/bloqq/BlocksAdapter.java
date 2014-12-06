package com.bloqq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bloqq.activities.MainActivity;
import com.bloqq.cache.App;
import com.bloqq.cache.DiskCache;

@SuppressWarnings("deprecation")
public class BlocksAdapter extends BaseAdapter {

	private Context mContext;
	private SharedPreferences mPrefs;
	private int mBlockSizePortrait;
	private int mBlockSizeLandscape;

	@SuppressLint("NewApi")
	public BlocksAdapter(Context context, SharedPreferences prefs) {
		mContext = context;
		mPrefs = prefs;
		Display display = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		int width, height;
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point point = new Point();
			display.getSize(point);
			width = point.x;
			height = point.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}

		mBlockSizePortrait = width / 4;
		mBlockSizeLandscape = height / 4;
	}

	@Override
	public int getCount() {
		return 9;
	}

	@Override
	public Object getItem(int position) {
		return this.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		WebIconDatabase.getInstance().open(
				mContext.getDir("icons", Context.MODE_PRIVATE).getPath());
		final BlocksAdapter adapter = this;
		String uri = mPrefs.getString("uri" + position, "");
		final DiskCache cache = ((App) mContext.getApplicationContext())
				.getDiskCache();
		View blockView = null;
		if (cache != null) {
			if (!cache.containsKey("uri" + position)) {
				blockView = new WebView(mContext);

				((WebView) blockView).setOnTouchListener(new OnTouchListener() {

					@SuppressLint("ClickableViewAccessibility")
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// if (((WebView) v).getBackground() == null) {
						// Log.i("DEBUG", ((WebView) v).getUrl());
						// MainActivity activity = (MainActivity) mContext;
						// if (activity.getSlidingMenu().isMenuShowing()) {
						// activity.getSlidingMenu().toggle();
						// }
						// activity.getWebView()
						// .loadUrl(v.getTag().toString());
						// Log.i("DEBUG", "dwwd");
						// }
						if (((WebView) v).getUrl() != null) {
							if (((WebView) v).getBackground() == null) {
								Log.i("DEBUG", "qwerty");
								Bitmap errorIcon = BitmapFactory.decodeResource(
										mContext.getResources(),
										android.R.drawable.ic_menu_close_clear_cancel);
								cache.put("uri" + position, errorIcon);
								v.setBackgroundDrawable(new BitmapDrawable(
										errorIcon));
								adapter.notifyDataSetChanged();
							}
							Log.i("DEBUG", ((WebView) v).getUrl());
							MainActivity activity = (MainActivity) mContext;
							if (activity.getSlidingMenu().isMenuShowing()) {
								activity.getSlidingMenu().toggle();
							}
							activity.getWebView().loadUrl(
									((WebView) v).getUrl());
						}
						return false;
					}
				});
				//
				// ((WebView) blockView).setOnClickListener(new
				// OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // if (((WebView) v).getBackground() != null) {
				// // if (((WebView) v).getBackground() != mContext
				// // .getResources().getDrawable(
				//
				// // android.R.drawable.gallery_thumb)) {
				//
				// }
				// // }
				// }
				// });
				((WebView) blockView).setWebViewClient(new WebViewClient() {
					@Override
					public void onReceivedError(WebView view, int errorCode,
							String description, String failingUrl) {
						super.onReceivedError(view, errorCode, description,
								failingUrl);
						view.setBackgroundColor(Color.RED);
						Log.i("DEBUG", "Error loading fav#" + position);
						Bitmap errorIcon = BitmapFactory.decodeResource(
								mContext.getResources(),
								android.R.drawable.ic_menu_close_clear_cancel);
						cache.put("uri" + position, errorIcon);
						adapter.notifyDataSetChanged();
					}
				});

				((WebView) blockView).setWebChromeClient(new WebChromeClient() {
					@Override
					public void onReceivedIcon(WebView view, Bitmap icon) {
						if (icon != null) {
							Log.i("DEBUG", "trewq");
							Log.i("DEBUG", "FAVICON != NULL HERE");
							// if (cache.containsKey("uri" + position)) {
							// cache.remove("uri" + position);
							// }
							cache.put("uri" + position, icon);
							view.setBackgroundDrawable(new BitmapDrawable(icon));
							adapter.notifyDataSetChanged();
						}
						super.onReceivedIcon(view, icon);
					}
				});

				// imageView.setInitialScale(2);
				// imageView.getSettings().setLoadWithOverviewMode(true);
				if ("".equals(uri)) {
					blockView
							.setBackgroundResource(android.R.drawable.gallery_thumb);
				} else {
					// new LogoTask(imageView).execute(uri);
					((WebView) blockView).loadUrl(uri);
					// } else {
					/*
					 * if (android.os.Build.VERSION.SDK_INT >=
					 * android.os.Build.VERSION_CODES.JELLY_BEAN) {
					 * imageView.setBackground(new BitmapDrawable(bitmap)); }
					 * else { imageView.setBackgroundDrawable(new
					 * BitmapDrawable(bitmap)); }
					 */
				}
			} else {
				Bitmap bitmap = cache.getBitmap("uri" + position);
				blockView = new ImageView(mContext);
				((ImageView) blockView).setImageBitmap(bitmap);
				((ImageView) blockView)
						.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		} else {
			blockView = new ImageView(mContext);
			blockView.setBackgroundResource(android.R.drawable.gallery_thumb);
		}
		blockView.setTag(uri);

		// blockView.setLayoutParams(new GridView.LayoutParams(120, 120));

		int rotation = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			blockView.setLayoutParams(new GridView.LayoutParams(
					mBlockSizeLandscape, mBlockSizeLandscape));
		} else {
			blockView.setLayoutParams(new GridView.LayoutParams(
					mBlockSizePortrait, mBlockSizePortrait));
		}
		return blockView;
	}
}
