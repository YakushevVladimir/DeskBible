package com.BibleQuote.utils.ErrorReporter;

import com.BibleQuote.R;

import android.content.Context;
import android.content.Intent;

public class EmailErrorReporter implements IErrorReporter {

	@Override
	public void Send(String report, Context context) {

		String subject = context.getResources().getString(
				R.string.CrashReport_MailSubject);
		String body = report;
		String[] emails = new String[]{context.getResources().getString(
				R.string.app_about_email)};

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_EMAIL, emails);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.setType("message/rfc822");

		context.startActivity(Intent.createChooser(sendIntent, "Title:"));
	}

}
