package com.BibleQuote.utils;

import java.io.File;

import android.os.Environment;

public class DataConstants {

   private static final String APP_PACKAGE_NAME = "com.biblequote";
   private static final String APP_DIR_NAME = "BibleQuote";
   private static final String FS_DATA_DIR_NAME = "modules";
   private static final String DB_DATA_DIR_NAME = "databases";
   
   public  static final String DB_LIBRARY_NAME = "library.db";
   public  static final String LIBRARY_CACHE = "library.cache";
   public  static final String DEFAULT_INI_FILE_NAME = "bibleqt.ini";
   
   public static final String FS_DATA_PATH = Environment.getDataDirectory() + File.separator 
		   + "data" + File.separator + DataConstants.APP_PACKAGE_NAME + File.separator + FS_DATA_DIR_NAME;
   
   public static final String DB_DATA_PATH = Environment.getDataDirectory() + File.separator 
		   + "data" + File.separator + DataConstants.APP_PACKAGE_NAME + File.separator + DB_DATA_DIR_NAME;
   
   public static final String FS_EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + File.separator 
		   + APP_DIR_NAME + File.separator + DataConstants.FS_DATA_DIR_NAME;

   public static final String DB_EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + File.separator 
		   + APP_DIR_NAME + File.separator + DataConstants.DB_DATA_DIR_NAME;


   public static final String ORDER_BY_NAME_ASC = "book.name COLLATE NOCASE asc";
   public static final String ORDER_BY_NAME_DESC = "book.name COLLATE NOCASE desc";

   public static final String MODULE_TABLE = "module";
   public static final String BOOK_TABLE = "book";

   private DataConstants() {
   }
}
