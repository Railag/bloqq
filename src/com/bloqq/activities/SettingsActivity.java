package com.bloqq.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bloqq.R;

public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// getSupportFragmentManager().beginTransaction()
		// .replace(android.R.id.content, new SettingFragment()).commit();

	}
}
