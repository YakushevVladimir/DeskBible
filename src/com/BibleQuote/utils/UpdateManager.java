package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml.Encoding;
import com.BibleQuote.R;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.managers.bookmarks.repository.dbBookmarksRepository;
import com.BibleQuote.managers.bookmarks.repository.prefBookmarksRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

		boolean updateModules = false;
		if (currVersionCode == 0 && Environment.MEDIA_MOUNTED.equals(state)) {
			updateModules = true;
		}

		if (currVersionCode < 39) {
			Log.i(TAG, "Update to version 0.05.02");
			saveTSK(context);
		}

		if (currVersionCode < 53) {
			updateModules = true;
		}

		if (currVersionCode < 59) {
			convertBookmarks_59();
		}

		if (updateModules) {
			Log.i(TAG, "Update built-in modules on external storage");
			updateBuiltInModules(context);
		}

		try {
			int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			Settings.edit().putInt("versionCode", versionCode).commit();
		} catch (NameNotFoundException e) {
			Settings.edit().putInt("versionCode", 39).commit();
		}
	}

	private static void convertBookmarks_59() {
		Log.d(TAG, "Convert bookmarks");
		BookmarksManager newBM = new BookmarksManager(new dbBookmarksRepository());
		ArrayList<Bookmark> bookmarks = new BookmarksManager(new prefBookmarksRepository()).getAll();
		for (Bookmark curr : bookmarks) {
			newBM.add(curr.OSISLink, curr.humanLink);
		}
	}

	private static void updateBuiltInModules(Context context) {
		try {
			saveBuiltInModule(context, "bible_rst.zip", R.raw.bible_rst);
			saveBuiltInModule(context, "bible_kjv.zip", R.raw.bible_kjv);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private static void saveBuiltInModule(Context context, String fileName, int rawId) throws IOException {
		File moduleDir = new File(DataConstants.FS_EXTERNAL_DATA_PATH);
		InputStream moduleStream = context.getResources().openRawResource(rawId);
		OutputStream newModule = new FileOutputStream(new File(moduleDir, fileName));
		byte[] buf = new byte[1024];
		int len;
		while ((len = moduleStream.read(buf)) > 0) {
			newModule.write(buf, 0, len);
		}
		moduleStream.close();
		newModule.close();
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
				}
				;
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
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
