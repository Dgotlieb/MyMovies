package com.example.daniel.mymovies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {


	//I am using this PreferenceActivity on top of the regular shared prefrences for color and language, because this one is deprecated
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options);
	}

}
