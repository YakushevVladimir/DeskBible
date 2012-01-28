package com.BibleQuote.utils.ErrorReporter;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ExceptionHandler implements UncaughtExceptionHandler {

	private IErrorReporter reporter;
	private Thread.UncaughtExceptionHandler oldHandler;
	private Context context;

	public ExceptionHandler(Context context, IErrorReporter reporter) {
		this.context = context;
		this.reporter = reporter;
		this.oldHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		try {
			StringBuilder report = new StringBuilder();
			report.append(getEnviromentInfo());
			processThrowable(throwable, report);
			reporter.Send(report.toString(), context);
		} catch (Throwable fatality) {
			oldHandler.uncaughtException(thread, throwable);
		}
	}

	private void processThrowable(Throwable throwable, StringBuilder report) {
		if (throwable == null)
			return;
		StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		report
			.append("Exception: ").append(throwable.getClass().getName()).append("\n")
			.append("Message: ").append(throwable.getMessage()).append("\n")
			.append("Stacktrace:\n");

		for (StackTraceElement element : stackTraceElements) {
			report.append("\t").append(element.toString()).append("\n");
		}

		processThrowable(throwable.getCause(), report);
	}

	private Object getEnviromentInfo() {
		StringBuilder enviroment = new StringBuilder();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			
			enviroment.append("Package name: " + pi.packageName + "\n");
			enviroment.append("Version name: " + pi.versionName + "\n");
			enviroment.append("Phone model: " + android.os.Build.MODEL + "\n");
			enviroment.append("Android version: " + android.os.Build.VERSION.RELEASE + "\n");
			enviroment.append("Board: " + android.os.Build.BOARD + "\n");
			enviroment.append("Device: " + android.os.Build.DEVICE + "\n");
			enviroment.append("Display: " + android.os.Build.DISPLAY + "\n");
			enviroment.append("FingerPrint: " + android.os.Build.FINGERPRINT + "\n");
			enviroment.append("Host: " + android.os.Build.HOST + "\n");
			enviroment.append("ID: " + android.os.Build.ID + "\n");
			enviroment.append("Model: " + android.os.Build.MODEL + "\n");
			enviroment.append("Product: " + android.os.Build.PRODUCT + "\n");
			enviroment.append("Tags: " + android.os.Build.TAGS + "\n");
			enviroment.append("Time: " + android.os.Build.TIME + "\n");
			enviroment.append("Type: " + android.os.Build.TYPE + "\n");
			enviroment.append("User: " + android.os.Build.USER + "\n");
		} catch (Exception e) {
			enviroment.append("Error get enviroment info\n");
		} 

		return enviroment.toString();
	}
}
