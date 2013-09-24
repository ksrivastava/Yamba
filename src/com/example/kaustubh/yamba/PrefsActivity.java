package com.example.kaustubh.yamba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity {
	/*
	 * Every new screen is an Activity - this is a preference-aware Activity
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs); // Instead of setContent()
	}
	
}
