package com.BibleQuote.utils.ErrorReporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;


public class FileErrorReporter implements IErrorReporter {

    private final DateFormat fileDateFormatter = new SimpleDateFormat("dd-MM-yy");

    @Override
	public void Send(String send, Context context) {
		final Date dumpDate = new Date(System.currentTimeMillis());
		String FileName = String.format("stacktrace_%s.dump", fileDateFormatter.format(dumpDate));
		FileOutputStream trace;
		try {
			trace = context.openFileOutput(FileName, Context.MODE_PRIVATE);
			trace.write(send.getBytes());
			trace.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
