package com.BibleQuote.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NotifyDialog {
	private AlertDialog alertDialog;

	public NotifyDialog(String message, Context context) {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Oops!");
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}

	public void show() {
		alertDialog.show();
	}
}
