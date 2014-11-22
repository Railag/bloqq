package com.bloqq.edits;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloqq.R;
import com.bloqq.activities.MainActivity;

public class BlockUrlEdit extends EditText {
	private int mPosition = -1;

	public final static int ID = 1;

	public BlockUrlEdit(Context context, int position) {
		super(context);
		this.setImeActionLabel("Set", EditorInfo.IME_ACTION_DONE);
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		this.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_URI);
		this.setPosition(position);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 70);
		this.setLayoutParams(p);
		this.setBackgroundColor(Color.LTGRAY);

		// this.setShadowLayer(2.0f, 0.0f, 0.0f, 0xffff77ff);
		// this.setTextColor(0xffffffff);
		this.setShadowLayer(4.0f, 2.0f, 2.0f, 0xff000000);
		this.setTextColor(0xffffffff);

		// this.setTextColor(Color.BLACK);

		Field f = null;
		try {
			f = TextView.class.getDeclaredField("mCursorDrawableRes");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		f.setAccessible(true);
		try {
			f.set(this, R.drawable.cursor);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.requestFocus();

		// MainActivity ma = ((MainActivity)context).getHomepage();
		SharedPreferences prefs = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		this.setText(prefs.getString("uri" + mPosition, "http://"));
		this.setId(ID);
	}

	public BlockUrlEdit(Context context, AttributeSet attrs, int position) {
		super(context, attrs);
		this.setImeActionLabel("Set", EditorInfo.IME_ACTION_DONE);
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		this.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_URI);
		this.setPosition(position);
		SharedPreferences prefs = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		this.setText(prefs
				.getString(MainActivity.HOMEPAGE, "http://google.com"));
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public int getPosition() {
		return mPosition;
	}
}
