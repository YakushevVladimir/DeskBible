package com.BibleQuote.activity;

import com.BibleQuote.R;
import com.BibleQuote.utils.FsUtils;

import android.os.Bundle;
import android.webkit.WebView;
import com.actionbarsherlock.app.SherlockActivity;

public class HelpActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		String helpText = FsUtils.getAssetString(getApplicationContext(), "help.html");
		WebView vWeb = (WebView)findViewById(R.id.helpView);
		vWeb.loadDataWithBaseURL("file:///url_initial_load", helpText, "text/html", "UTF-8", "about:config");
	}
}
