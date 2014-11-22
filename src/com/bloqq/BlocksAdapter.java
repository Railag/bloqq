package com.bloqq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bloqq.activities.MainActivity;
import com.bloqq.cache.App;
import com.bloqq.cache.DiskCache;

@SuppressWarnings("deprecation")
public class BlocksAdapter extends BaseAdapter {

	private Context mContext;
	private SharedPreferences mPrefs;

	public BlocksAdapter(Context context, SharedPreferences prefs) {
		mContext = context;
		mPrefs = prefs;
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
		Log.i("DEBUG", "position " + position);
		final BlocksAdapter adapter = this;
		String uri = mPrefs.getString("uri" + position, "");
		final DiskCache cache = ((App) mContext.getApplicationContext())
				.getDiskCache();
		// bitmap = cache.getBitmap("uri" + position);
		View blockView = null;
		// if (cache.containsKey("uri" + position)) {
		// cache.remove("uri" + position);
		// }
		if (!cache.containsKey("uri" + position)) {// bitmap == null) {
			// ImageView imageView = new ImageView(mContext);
			blockView = new WebView(mContext);
			((WebView) blockView).setOnTouchListener(new OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (((WebView) v).getBackground() == null) {
						MainActivity activity = (MainActivity) mContext;
						activity.getWebView().loadUrl(v.getTag().toString());
						activity.getSlidingMenu().toggle();
					}
					// MainActivity activity = (MainActivity) mContext;
					// activity.getWebView().loadUrl(v.getTag().toString());
					// activity.getSlidingMenu().toggle();
					return false;
				}
			});

			((WebView) blockView).setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description,
							failingUrl);
					view.setBackgroundColor(Color.RED);
					Toast.makeText(mContext, "Error loading fav#" + position,
							Toast.LENGTH_LONG).show();
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
				 * imageView.setBackground(new BitmapDrawable(bitmap)); } else {
				 * imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
				 * }
				 */
			}
		} else {
			Bitmap bitmap = cache.getBitmap("uri" + position);
			blockView = new ImageView(mContext);
			((ImageView) blockView).setImageBitmap(bitmap);
			((ImageView) blockView)
					.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		blockView.setTag(uri);
		blockView.setLayoutParams(new GridView.LayoutParams(120, 120));

		return blockView;
	}
}
