/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        if (currVersionCode < 46) {
            Log.i(TAG, "Update to version 0.06.05");
            updateExternalModule(context);
            moveHistoryFile(context);
        }


        try {
			int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			Settings.edit().putInt("versionCode", versionCode).commit();
		} catch (NameNotFoundException e) {
			Settings.edit().putInt("versionCode", 39).commit();
		}
	}

    private static void moveHistoryFile(Context context) {
        File historyFile = new File(context.getCacheDir(), "history.dat");
        if (!historyFile.exists()) return;
        historyFile.renameTo(new File(DataConstants.FS_HISTORY_PATH, "history.dat"));
    }

    private static void updateExternalModule(Context context) {
        File bModule = new File(DataConstants.FS_EXTERNAL_DATA_PATH, "bible.zip");
        if (bModule.exists()) {
            bModule.delete();
            saveExternalModule(context);
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
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
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
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
