package com.BibleQuote.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.BibleQuote.R;
import com.BibleQuote.exceptions.BQUniversalException;
import com.BibleQuote.exceptions.ExceptionHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml.Encoding;

public class UpdateManager {
	
	private final static String TAG = "UpdateManager";
	
	static public void Init(Context context) {

		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);

		// Инициализация каталога программы
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File dir_modules = new File(DataConstants.FS_EXTERNAL_DATA_PATH);
			if (!dir_modules.exists()) {
				Log.i(TAG, String.format("Create directory %1$s", dir_modules));
				dir_modules.mkdirs();
			}
		}

		int currVersionCode = Settings.getInt("versionCode", 0);
		
		if (currVersionCode == 0 && Environment.MEDIA_MOUNTED.equals(state)) {
			Log.i(TAG, "Copying built-in module the Bible on external storage");
			saveExternalModule(context);
		}
		if (currVersionCode < 30) {
			Log.i(TAG, "Update to version 0.04.05");
			Settings.edit().remove("myversion");
			File cacheDir = context.getCacheDir();
			for (File currFile : cacheDir.listFiles()) {
				android.util.Log.i(TAG, String.format("Delete library cache file %1$s", currFile.getName()));
				currFile.delete();
			}
		}
		
		if (currVersionCode < 39) {
			Log.i(TAG, "Update to version 0.05.01");
			saveTSK(context);
		}
		
		try {
			int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			Settings.edit().putInt("versionCode", versionCode).commit();
		} catch (NameNotFoundException e) {
			Settings.edit().putInt("versionCode", 39).commit();
		}
	}

	private static void saveExternalModule(Context context) {
		try {
			InputStream moduleStream = context.getResources().openRawResource(R.raw.bible);
			File moduleDir = new File(DataConstants.FS_EXTERNAL_DATA_PATH);
			OutputStream newModule = new FileOutputStream(new File(moduleDir, "bible.zip"));
			byte[] buf = new byte[1024];
			int len;
			while ((len = moduleStream.read(buf)) > 0) {
				newModule.write(buf, 0, len);
			}
			moduleStream.close();
			newModule.close();
		} catch (FileNotFoundException e) {
			ExceptionHelper.onException(new BQUniversalException("Error import internal bibles module!"), context, TAG);
		} catch (IOException e) {
			ExceptionHelper.onException(new BQUniversalException("Error import internal bibles module!"), context, TAG);
		}
	}

	private static void saveTSK(Context context) {
		try {
			InputStream tskStream = context.getResources().openRawResource(R.raw.tsk);
			ZipInputStream zStream = new ZipInputStream(tskStream);
			
			InputStreamReader isReader = null;
			ZipEntry entry;
			while ((entry = zStream.getNextEntry()) != null) {
				String entryName = entry.getName().toLowerCase();
				if (entryName.contains(File.separator)) {
					entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1);
				}
				if (entryName.equalsIgnoreCase("tsk.xml")) {
					isReader = new InputStreamReader(zStream, Encoding.UTF_8.toString());
					break;
				};
			}
			if (isReader == null) {
				return;
			}
			BufferedReader tsk_br = new BufferedReader(isReader);
			
			File tskFile = new File(DataConstants.FS_APP_DIR_NAME, "tsk.xml");
			if (tskFile.exists()) {
				tskFile.delete();
			}			
			BufferedWriter tsk_bw = new BufferedWriter(new FileWriter(tskFile));
			
			char[] buf = new char[1024];
			int len;
			while ((len = tsk_br.read(buf)) > 0) {
				tsk_bw.write(buf, 0, len);
			}
			tsk_bw.flush();
			tsk_bw.close();
			tsk_br.close();
		} catch (FileNotFoundException e) {
			ExceptionHelper.onException(new BQUniversalException("Error import cross-references library!"), context, TAG);
		} catch (IOException e) {
			ExceptionHelper.onException(new BQUniversalException("Error import cross-references library!"), context, TAG);
		}
	}
}
