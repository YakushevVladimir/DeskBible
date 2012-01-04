package com.BibleQuote.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.BibleQuote.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

public class UpdateManager {
	
	private final static String TAG = "UpdateManager";
	
	static public void Init(Context context) {

		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);

		Log.Init(context);

		// Инициализация каталога программы
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String basePath = Environment.getExternalStorageDirectory().toString();
			File dir_modules = new File(basePath + "/jBible");
			if (dir_modules.exists()) {
				Log.i(TAG, "Rename directory \"/jBible\"");
				dir_modules.renameTo(new File(basePath + "/BibleQuote"));
			} else {
				Log.i(TAG, "Create directory \"/BibleQuote/modules\"");
				dir_modules = new File(basePath + "/BibleQuote/modules/");
				dir_modules.mkdirs();
			}
		}

		String myversion;
		try {
			myversion = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			myversion = "0.00.01";
		}

		String currVersion = Settings.getString("myversion", "");
		Log.i(TAG, "Update from version " + currVersion);
		Boolean update = false;

		if (currVersion.contains("0.03.02")
				|| currVersion.length() == 0
				|| update) {
			saveExternalModule(context);
			update = true;
		}
		
		if (currVersion.contains("0.03.04") 
				|| currVersion.contains("0.03.05") 
				|| update) {
			Log.i(TAG, "Delete library cache file");
			File cacheDir = context.getCacheDir();
			File cacheFile = new File(cacheDir, "library.cash");
			if (cacheFile.exists()) {
				android.util.Log.i(TAG, "Delete library cache file");
				cacheFile.delete();
			}
			update = true;
		}
		
		Settings.edit().putString("myversion", myversion).commit();
	}

	private static void saveExternalModule(Context context) {
		try {
			InputStream moduleStream = context.getResources().openRawResource(
					R.raw.rst_strong);
			File moduleDir = new File(Environment.getExternalStorageDirectory().toString() + "/BibleQuote/modules/");
			OutputStream newModule = new FileOutputStream(new File(moduleDir, "rst_strong.zip"));
			byte[] buf = new byte[1024];
			int len;
			while ((len = moduleStream.read(buf)) > 0) {
				newModule.write(buf, 0, len);
			}
			moduleStream.close();
			newModule.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
