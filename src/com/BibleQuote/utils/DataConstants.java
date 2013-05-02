package com.BibleQuote.utils;

import android.os.Environment;

import java.io.File;

public class DataConstants {

	private static final String APP_PACKAGE_NAME = "com.BibleQuote";
	private static final String APP_DIR_NAME = "BibleQuote";
	private static final String FS_DATA_DIR_NAME = "modules";
	private static final String DB_DATA_DIR_NAME = "data";

	public static final String DB_LIBRARY_NAME = "library.db";
	public static final String LIBRARY_CACHE = "library.cache";
	public static final String DEFAULT_INI_FILE_NAME = "bibleqt.ini";

	public static final String FS_DATA_PATH = Environment.getDataDirectory() + File.separator
			+ "data" + File.separator + DataConstants.APP_PACKAGE_NAME + File.separator + FS_DATA_DIR_NAME;

	public static final String DB_DATA_PATH = Environment.getDataDirectory() + File.separator
			+ "data" + File.separator + DataConstants.APP_PACKAGE_NAME + File.separator + DB_DATA_DIR_NAME;

	public static final String FS_EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + File.separator
			+ APP_DIR_NAME + File.separator + DataConstants.FS_DATA_DIR_NAME;

	public static final String FS_APP_DIR_NAME = Environment.getExternalStorageDirectory() + File.separator
			+ APP_DIR_NAME;

	public static final String DB_EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + File.separator
			+ APP_DIR_NAME + File.separator + DataConstants.DB_DATA_DIR_NAME;


	public static final String MODULE_TABLE = "module";
	public static final String BOOK_TABLE = "book";
	public static final String BOOKMARKS_TABLE = "bookmarks";

	private DataConstants() {
	}
}
