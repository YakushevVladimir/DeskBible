/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.controllers;

import android.content.Context;
import android.os.Environment;

import com.BibleQuote.dal.CacheContext;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.dal.LibraryContext;
import com.BibleQuote.dal.LibraryUnitOfWork;
import com.BibleQuote.entity.modules.FsModule;
import com.BibleQuote.utils.DataConstants;

import java.io.File;

public class LibraryController {

	private static volatile LibraryController instance;

	private IModuleController moduleCtrl;
	private LibraryContext libraryContext;
	private IBookController bookCtrl;
	private IChapterController chapterCtrl;
	
	private LibraryController(FsLibraryContext context, CacheContext cacheContext) {
		LibraryUnitOfWork unit = new LibraryUnitOfWork(context, cacheContext);
		libraryContext = context;
		moduleCtrl = new FsModuleController(unit);
		bookCtrl = new FsBookController(unit);
		chapterCtrl = new FsChapterController(unit);
	}

	public IChapterController getChapterCtrl() {
		return chapterCtrl;
	}

	public IModuleController getModuleCtrl() {
		return moduleCtrl;
	}

	public IBookController getBookCtrl() {
		return bookCtrl;
	}

	public static LibraryController getInstance(Context context) {
		if (instance == null) {
			synchronized (LibraryController.class) {
				String libraryPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
						? DataConstants.FS_EXTERNAL_DATA_PATH
						: DataConstants.FS_DATA_PATH;

				CacheContext cacheContext = new CacheContext(context.getCacheDir(), DataConstants.LIBRARY_CACHE);
				CacheModuleController<FsModule> cache = new CacheModuleController<FsModule>(cacheContext);
				FsLibraryContext libContext = new FsLibraryContext(new File(libraryPath), context, cache);
				instance = new LibraryController(libContext, cacheContext);
			}
		}

		return instance;
	}

	public LibraryContext getContext() {
		return libraryContext;
	}
}
