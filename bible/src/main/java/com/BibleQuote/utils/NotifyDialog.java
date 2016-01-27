package com.BibleQuote.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.BibleQuote.R;

public class NotifyDialog {
	private AlertDialog alertDialog;

	public NotifyDialog(String message, Context context) {
		alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.notify_dialog_title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
	}

	public void show() {
		alertDialog.show();
	}
}
