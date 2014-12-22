package com.BibleQuote.ui;

import android.os.Bundle;
import android.webkit.WebView;
import com.BibleQuote.ui.base.BibleQuoteActivity;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.R;

public class HelpActivity extends BibleQuoteActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		String helpText = FsUtils.getAssetString(getApplicationContext(), "help.html");
		WebView vWeb = (WebView) findViewById(R.id.helpView);
		vWeb.loadDataWithBaseURL("file:///url_initial_load", helpText, "text/html", "UTF-8", "about:config");
	}
}
