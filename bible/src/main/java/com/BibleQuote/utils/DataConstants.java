/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: DataConstants.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.utils;

import android.os.Environment;

import java.io.File;

public class DataConstants {

	private static final String APP_PACKAGE_NAME = "com.BibleQuote";
	private static final String APP_DIR_NAME = "BibleQuote";
	private static final String FS_DATA_DIR_NAME = "modules";
	private static final String DB_DATA_DIR_NAME = "data";
	private static final String FONT_DIR_NAME = "fonts";

	public static final String DB_LIBRARY_NAME = "library.db";
	public static final String LIBRARY_CACHE = "library.cache";

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

	public static final String FONT_DIR = Environment.getDataDirectory() + File.separator
			+ "data" + File.separator + DataConstants.APP_PACKAGE_NAME + File.separator + FONT_DIR_NAME;


	private DataConstants() {
    }

    public static String getLibraryPath() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ? FS_EXTERNAL_DATA_PATH
                : FS_DATA_PATH;
    }
}
