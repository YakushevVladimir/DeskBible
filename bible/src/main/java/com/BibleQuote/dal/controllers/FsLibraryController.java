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
 * File: FsLibraryController.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.controllers;

import android.content.Context;

import com.BibleQuote.dal.FsCacheContext;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.domain.LibraryContext;
import com.BibleQuote.domain.controllers.IBookController;
import com.BibleQuote.domain.controllers.IChapterController;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.controllers.old.IModuleController;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.managers.Injector;
import com.BibleQuote.utils.DataConstants;

import java.io.File;

public class FsLibraryController implements ILibraryController {

    private static volatile FsLibraryController instance;

	private IModuleController moduleCtrl;
    private FsLibraryContext libraryContext;
    private IBookController bookCtrl;
	private IChapterController chapterCtrl;

    private FsLibraryController(FsLibraryContext context, FsCacheContext cacheContext) {
        FsLibraryUnitOfWork unit = new FsLibraryUnitOfWork(context, cacheContext);
        libraryContext = context;
		moduleCtrl = new FsModuleController(unit);
		bookCtrl = new FsBookController(unit);
		chapterCtrl = new FsChapterController(unit);
	}

    @Override
    public IChapterController getChapterCtrl() {
        return chapterCtrl;
	}

    @Override
    public IModuleController getModuleCtrl() {
        return moduleCtrl;
	}

    @Override
    public com.BibleQuote.domain.controllers.modules.IModuleController getModuleCtrl(Module module) {
        return Injector.getModuleController(libraryContext, module);
    }

    @Override
    public IBookController getBookCtrl() {
        return bookCtrl;
	}

    public static FsLibraryController getInstance(Context context) {
        if (instance == null) {
            synchronized (FsLibraryController.class) {
                String libraryPath = DataConstants.getLibraryPath();
                File cacheDir = context.getCacheDir();
                FsCacheContext cacheContext = new FsCacheContext(cacheDir, DataConstants.LIBRARY_CACHE);
                FsCacheModuleController<BQModule> cache = new FsCacheModuleController<BQModule>(cacheContext);
                FsLibraryContext libContext = new FsLibraryContext(new File(libraryPath), cache);
                instance = new FsLibraryController(libContext, cacheContext);
            }
		}

		return instance;
	}

    @Override
    public LibraryContext getContext() {
        return libraryContext;
	}
}
