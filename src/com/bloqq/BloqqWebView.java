package com.bloqq;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;

public class BloqqWebView extends WebView {

	public BloqqWebView(Context context) {
		super(context);
	}

	public BloqqWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Toast.makeText(getContext(), "Back button pressed", Toast.LENGTH_LONG)
				.show();
		if (canGoBack()) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
