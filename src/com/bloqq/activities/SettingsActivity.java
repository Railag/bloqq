package com.bloqq.activities;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.bloqq.R;
import com.bloqq.sqlite.BloqqDBHelper;

public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// getSupportFragmentManager().beginTransaction()
		// .replace(android.R.id.content, new SettingFragment()).commit();
		EditTextPreference homePage = (EditTextPreference) findPreference("homepage");
		homePage.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String newUrl = null;
				String url = newValue.toString();
				Log.i("DEBUG", url);
				if (url.startsWith("http://") || url.startsWith("https://")) {
					newUrl = url;
				} else {
					newUrl = "http://" + url;
				}
				Editor editor = preference.getEditor();
				editor.putString("homepage", newUrl);
				editor.commit();
				return false;
			}
		});

		Preference clearHistButton = findPreference("button_clear_hist");
		clearHistButton
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference pref) {
						getContentResolver().delete(
								BloqqDBHelper.Hist.CONTENT_URI_HIST_DB,
								"1 = 1", null);
						return true;
					}
				});
		Preference clearFavButton = findPreference("button_clear_fav");
		clearFavButton
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference pref) {
						getContentResolver().delete(
								BloqqDBHelper.Fav.CONTENT_URI_FAV_DB, "1 = 1",
								null);
						return true;
					}
				});
	}
}
