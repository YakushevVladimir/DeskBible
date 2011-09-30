package com.jBible.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.jBible.entity.ItemList;
import com.jBible.entity.Module;
import com.jBible.exceptions.CreateModuleErrorException;

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
			File dir_modules = new File(Environment
					.getExternalStorageDirectory().toString()
					+ "/jBible/modules/");
			if (!dir_modules.exists()) {
				Log.i(TAG, "Create directory \"/jBible/modules/\"");
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
		if (currVersion.contains("0.9")) {
			// На более поздних обновления надо будет удалить
			// этот блок обновления, т.к. в данном случае присваиваем
			// меньшую версию
			Settings.edit().remove("Modules").commit();
			Settings.edit().remove("ActiveModulesIndex").commit();
			update = true;
		}
		if (currVersion.contains("0.01.11") || update) {
			// Сменился формат харения данных о цвете текста и фона
			// Удалим старые значения
			Settings.edit().remove("TextBG").commit();
			Settings.edit().remove("TextColor").commit();
			update = true;
		} 
		if (currVersion.contains("0.01.17") || update) {
			UpdateManager.convertFavorities(Settings);
			update = true;
		}
		if (currVersion.contains("0.02.01") || update) {
			Settings.edit().putBoolean("nightMode", false).commit();
			update = true;
		}
		Settings.edit().putString("myversion", myversion).commit();
	}

	public static void convertFavorities(SharedPreferences settings) {
		Byte delimeter1 = (byte) 0xFE;
		Byte delimeter2 = (byte) 0xFF;
		ArrayList<String> oldFavorits = new ArrayList<String>();
		ArrayList<String> newFavorits = new ArrayList<String>();
		
		String fav = settings.getString("Favorits", "");
		
		Log.i(TAG, "oldFav = " + fav);
		
		oldFavorits.addAll(Arrays.asList(fav.split(delimeter1.toString())));
		for (String favItem : oldFavorits) {
			try {
				String[] param = favItem.split(delimeter2.toString());
				
				Log.i(TAG, "favItem = " + favItem);
				Log.i(TAG, "favItem.split(delimeter2).length = " + param.length);
				
				if (param.length == 6) {
					String humanLink = param[0];
					String path = param[1];
					int bookNum = Integer.parseInt(param[2]);
					int chapter = Integer.parseInt(param[3]);
					String verse = param[4];
					
				
					Module mod = new Module(path);
					ArrayList<ItemList> books = mod.getBooksList();
					
					String moduleID = mod.getShortName();
					String bookID = books.get(bookNum).get("ID");
					String linkOSIS = moduleID + "." + bookID + "." + (++chapter) + "." + verse;
					
					Log.i(TAG, "newItem = " + humanLink + " : " + linkOSIS);
					newFavorits.add(humanLink + delimeter2 + linkOSIS); 
				} else {
					param = favItem.split("\\.");
					if (param.length == 4) {
						String humanLink = param[0] + ": " + param[1] + " " + param[2] + ":" + param[3];
					Log.i(TAG, "newItem = " + humanLink + " : " + favItem);
						newFavorits.add(humanLink + delimeter2 + favItem); 
					} else {
						Log.i(TAG, "favItem.split(.).length = " + param.length);
						Log.i(TAG, "Error convert bookmark");
					}
				}
				
			} catch (CreateModuleErrorException e) {
				// Модуль либо отсутствует, либо перемещён, либо ещё какая-то ошибка
				Log.e(TAG, e);
				continue;
			} catch (NumberFormatException e) {
				// Не удалось преобразовать к числу номер книги или главы
				Log.e(TAG, e);
				continue;
			}
		}
		
		fav = "";
		for (String favItem : newFavorits) {
			fav += favItem + delimeter1;
		}
		
		Log.i(TAG, "newFav = " + fav);
		settings.edit().putString("Favorits", fav).commit();
	}

}
