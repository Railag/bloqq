package com.bloqq.edits;

import com.bloqq.activities.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class AddressUrlEdit extends EditText {

	public AddressUrlEdit(Context context) {
		super(context);
		this.setImeActionLabel("Go", EditorInfo.IME_ACTION_GO);
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		this.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_URI);

		this.setShadowLayer(4.0f, 1.0f, 1.0f, Color.WHITE);
		this.setTextColor(0xff000000);

		SharedPreferences prefs = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		this.setText(prefs
				.getString(MainActivity.HOMEPAGE, "http://google.com"));
	}

	public AddressUrlEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setImeActionLabel("Go", EditorInfo.IME_ACTION_GO);
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		this.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_URI);
	}
}
