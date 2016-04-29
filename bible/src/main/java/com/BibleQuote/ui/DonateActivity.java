package com.BibleQuote.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.BibleQuote.R;
import com.BibleQuote.ui.base.BibleQuoteActivity;

/**
 *
 */
public class DonateActivity extends BibleQuoteActivity {

    private static final String DONATE_URL = "http://scripturesoftware.org/donate/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        Button donate = (Button) findViewById(R.id.btn_donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donate();
            }
        });
    }

    private void donate() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_URL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
