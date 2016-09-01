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
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Xml.Encoding;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.dal.repository.bookmarks.PrefBookmarksRepository;
import com.BibleQuote.dal.repository.bookmarks.dbBookmarksTagsRepository;
import com.BibleQuote.dal.repository.bookmarks.dbTagRepository;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.bookmarks.BookmarksManager;

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
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class UpdateManager {

    private final static String TAG = "UpdateManager";

    private UpdateManager() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static void start(Context context) {

        Logger.i(TAG, "Start update manager...");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dirModules = new File(DataConstants.FS_EXTERNAL_DATA_PATH);
            if (!dirModules.exists() && !dirModules.mkdirs()) {
                Logger.i(TAG, String.format("Fail create module directory %1$s", dirModules));
                return;
            }
        }

        int currVersionCode = settings.getInt("versionCode", 0);

        boolean updateModules = false;
        if (currVersionCode < 53 && Environment.MEDIA_MOUNTED.equals(state)) {
            updateModules = true;
        }

        if (currVersionCode < 39) {
            Logger.i(TAG, "Update to version 0.05.02");
            saveTSK(context);
        }

        if (currVersionCode < 59) {
            convertBookmarks_59();
        } else if (currVersionCode < 63) {
            convertBookmarks_63();
        }

        if (updateModules) {
            Logger.i(TAG, "Update built-in modules on external storage");
            updateBuiltInModules(context);
        }

        if (currVersionCode < 76) {
            ILibraryController libraryController = BibleQuoteApp.getInstance().getLibraryController();
            libraryController.loadModules();
        }

        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            settings.edit().putInt("versionCode", versionCode).apply();
        } catch (NameNotFoundException e) {
            settings.edit().putInt("versionCode", 39).apply();
        }
    }

    private static void convertBookmarks_63() {
        Logger.d(TAG, "Convert bookmarks to DB version 2");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager bmManager = new BookmarksManager(bookmarksRepo, new dbBookmarksTagsRepository(), new dbTagRepository());
        ArrayList<Bookmark> bookmarks = bmManager.getAll();
        for (Bookmark currBM : bookmarks) {
            if (currBM.name == null) currBM.name = currBM.humanLink;
            bmManager.add(currBM);
        }
    }

    private static void convertBookmarks_59() {
        Logger.d(TAG, "Convert bookmarks to DB version 1");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager newBM = new BookmarksManager(bookmarksRepo, new dbBookmarksTagsRepository(), new dbTagRepository());
        ArrayList<Bookmark> bookmarks = new BookmarksManager(new PrefBookmarksRepository(), new dbBookmarksTagsRepository(), new dbTagRepository()).getAll();
        for (Bookmark curr : bookmarks) {
            newBM.add(curr.OSISLink, curr.humanLink);
        }
    }

    private static void updateBuiltInModules(Context context) {
        try {
            saveBuiltInModule(context, "bible_rst.zip", R.raw.bible_rst);
            saveBuiltInModule(context, "bible_kjv.zip", R.raw.bible_kjv);
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
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
            }
            if (isReader == null) {
                return;
            }
            BufferedReader tskBr = new BufferedReader(isReader);

            File tskFile = new File(DataConstants.FS_APP_DIR_NAME, "tsk.xml");
            if (tskFile.exists() && !tskFile.delete()) {
                Logger.e(TAG, "Can't delete TSK-file");
                return;
            }
            BufferedWriter tskBw = new BufferedWriter(new FileWriter(tskFile));

            char[] buf = new char[1024];
            int len;
            while ((len = tskBr.read(buf)) > 0) {
                tskBw.write(buf, 0, len);
            }
            tskBw.flush();
            tskBw.close();
            tskBr.close();
        } catch (FileNotFoundException e) {
            Logger.e(TAG, e.getMessage());
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        }
    }
}
