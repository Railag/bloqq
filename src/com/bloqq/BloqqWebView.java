package com.bloqq;

import java.io.File;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebView;

public class BloqqWebView extends WebView {

	private final Context mContext;

	public BloqqWebView(Context context) {
		super(context);
		mContext = context;
		final DownloadManager manager = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		final File destinationDir = new File(
				Environment.getExternalStorageDirectory(), "Bloqq");
		if (!destinationDir.exists()) {
			destinationDir.mkdir();
		}

		this.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Uri source = Uri.parse(url);

				DownloadManager.Request request = new DownloadManager.Request(
						source);
				File destinationFile = new File(destinationDir, source
						.getLastPathSegment());
				request.setDestinationUri(Uri.fromFile(destinationFile));
				manager.enqueue(request);
			}
		});
	}

	public BloqqWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		final DownloadManager manager = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		final File destinationDir = new File(
				Environment.getExternalStorageDirectory(),
				mContext.getPackageName());
		if (!destinationDir.exists()) {
			destinationDir.mkdir();
		}

		this.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				/*
				 * Intent i = new Intent(Intent.ACTION_VIEW);
				 * i.setData(Uri.parse(url)); Log.i("DEBUG", url);
				 * mContext.startActivity(i);
				 */
				Uri source = Uri.parse(url);

				DownloadManager.Request request = new DownloadManager.Request(
						source);
				File destinationFile = new File(destinationDir, source
						.getLastPathSegment());
				request.setDestinationUri(Uri.fromFile(destinationFile));
				manager.enqueue(request);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (canGoBack()) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
