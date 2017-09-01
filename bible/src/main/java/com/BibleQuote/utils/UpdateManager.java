/*
 * Copyright (C) 2011 Scripture Software
 *
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
 * Project: BibleQuote-for-Android
 * File: UpdateManager.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Xml.Encoding;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.dal.repository.bookmarks.DbBookmarksTagsRepository;
import com.BibleQuote.dal.repository.bookmarks.DbTagRepository;
import com.BibleQuote.dal.repository.bookmarks.PrefBookmarksRepository;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.bookmarks.BookmarksManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class UpdateManager {

    private final static String TAG = "UpdateManager";

    private UpdateManager() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static void start(Context context, PreferenceHelper prefHelper) {

        Logger.i(TAG, "Start update manager...");

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dirModules = new File(DataConstants.getFsExternalDataPath());
            if (!dirModules.exists() && !dirModules.mkdirs()) {
                Logger.i(TAG, String.format("Fail create module directory %1$s", dirModules));
                return;
            }
        }

        int currVersionCode = prefHelper.getInt("versionCode");

        boolean updateModules = false;
        if (currVersionCode < 53 && Environment.MEDIA_MOUNTED.equals(state)) {
            updateModules = true;
        }

        if (currVersionCode < 39) {
            Logger.i(TAG, "Update to version 0.05.02");
            saveTSK(context);
        }

        if (currVersionCode < 59) {
            convertBookmarks_59(prefHelper);
        } else if (currVersionCode < 63) {
            convertBookmarks_63();
        }

        if (updateModules) {
            Logger.i(TAG, "Update built-in modules on external storage");
            updateBuiltInModules(context);
        }

        if (currVersionCode < 76) {
            ILibraryController libraryController = BibleQuoteApp.getInstance().getLibraryController();
            libraryController.reloadModules();
        }

        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            prefHelper.saveInt("versionCode", versionCode);
        } catch (NameNotFoundException e) {
            prefHelper.saveInt("versionCode", 39);
        }
    }

    private static void convertBookmarks_59(PreferenceHelper preferenceHelper) {
        Logger.d(TAG, "Convert bookmarks to DB version 1");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager newBM = new BookmarksManager(bookmarksRepo, new DbBookmarksTagsRepository(), new DbTagRepository());
        ArrayList<Bookmark> bookmarks = new BookmarksManager(
                new PrefBookmarksRepository(preferenceHelper),
                new DbBookmarksTagsRepository(),
                new DbTagRepository()).getAll();
        for (Bookmark curr : bookmarks) {
            newBM.add(curr.OSISLink, curr.humanLink);
        }
    }

    private static void convertBookmarks_63() {
        Logger.d(TAG, "Convert bookmarks to DB version 2");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager bmManager = new BookmarksManager(bookmarksRepo, new DbBookmarksTagsRepository(), new DbTagRepository());
        ArrayList<Bookmark> bookmarks = bmManager.getAll();
        for (Bookmark currBM : bookmarks) {
            if (currBM.name == null) {
                currBM.name = currBM.humanLink;
            }
            bmManager.add(currBM);
        }
    }

    private static void saveBuiltInModule(Context context, String fileName, int rawId) {
        File moduleDir = new File(DataConstants.getFsExternalDataPath());
        try (
                InputStream moduleStream = context.getResources().openRawResource(rawId);
                OutputStream newModule = new FileOutputStream(new File(moduleDir, fileName))
        ) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = moduleStream.read(buf)) > 0) {
                newModule.write(buf, 0, len);
            }
            moduleStream.close();
            newModule.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void saveTSK(Context context) {
        BufferedWriter tskBw = null;
        BufferedReader tskBr = null;
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
            }
            if (isReader == null) {
                return;
            }
            tskBr = new BufferedReader(isReader);

            File tskFile = new File(DataConstants.getFsAppDirName(), "tsk.xml");
            if (tskFile.exists() && !tskFile.delete()) {
                Logger.e(TAG, "Can't delete TSK-file");
                return;
            }
            tskBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tskFile), Charset.forName("UTF-8")));

            char[] buf = new char[1024];
            int len;
            while ((len = tskBr.read(buf)) > 0) {
                tskBw.write(buf, 0, len);
            }
            tskBw.flush();
            tskBw.close();
            tskBr.close();
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            if (tskBr != null) {
                try {
                    tskBr.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
            if (tskBw != null) {
                try {
                    tskBw.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }
    }

    private static void updateBuiltInModules(Context context) {
        saveBuiltInModule(context, "bible_rst.zip", R.raw.bible_rst);
        saveBuiltInModule(context, "bible_kjv.zip", R.raw.bible_kjv);
    }
}
