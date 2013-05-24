package com.BibleQuote.ui;

import android.os.Bundle;
import android.webkit.WebView;
import com.BibleQuote.R;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HelpActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		ViewUtils.setActionBarBackground(this);

		String helpText = FsUtils.getAssetString(getApplicationContext(), "help.html");
		WebView vWeb = (WebView) findViewById(R.id.helpView);
		vWeb.loadDataWithBaseURL("file:///url_initial_load", helpText, "text/html", "UTF-8", "about:config");
	}
}
