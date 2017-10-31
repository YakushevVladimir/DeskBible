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
 * Created by Vladimir Yakushev at 10/2017
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
import com.BibleQuote.dal.repository.bookmarks.DbBookmarksTagsRepository;
import com.BibleQuote.dal.repository.bookmarks.DbTagRepository;
import com.BibleQuote.dal.repository.bookmarks.PrefBookmarksRepository;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.logger.StaticLogger;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

public class UpdateManager {

    private final Context context;
    private final PreferenceHelper prefHelper;

    @Inject
    UpdateManager(Context context, PreferenceHelper prefHelper) {
        this.context = context;
        this.prefHelper = prefHelper;
    }

    public void start() {

        StaticLogger.info(this, "Start update manager...");

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dirModules = new File(DataConstants.getFsExternalDataPath());
            if (!dirModules.exists() && !dirModules.mkdirs()) {
                StaticLogger.info(this, String.format("Fail create module directory %1$s", dirModules));
                return;
            }
        }

        int currVersionCode = prefHelper.getInt("versionCode");

        boolean updateModules = false;
        if (currVersionCode < 53 && Environment.MEDIA_MOUNTED.equals(state)) {
            updateModules = true;
        }

        if (currVersionCode < 39) {
            saveTSK(context);
        }

        if (currVersionCode < 59) {
            convertBookmarks_59(prefHelper);
        } else if (currVersionCode < 63) {
            convertBookmarks_63();
        }

        if (updateModules) {
            updateBuiltInModules(context);
        }

        if (currVersionCode < 76) {
            ILibraryController libraryController = BibleQuoteApp.getInstance().getLibraryController();
            libraryController.reloadModules();
        }

        if (currVersionCode < 84) {
            updatePreferences_84(context);
        }

        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            prefHelper.saveInt("versionCode", versionCode);
        } catch (NameNotFoundException e) {
            prefHelper.saveInt("versionCode", 39);
        }
        StaticLogger.info(this, "Update success");
    }

    private void updatePreferences_84(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            preference.edit()
                    .putInt("text_color", ColorUtils.toInt(preference.getString("TextColor", "#51150F")))
                    .putInt("sel_text_color", ColorUtils.toInt(preference.getString("TextColorSel", "#51150F")))
                    .putInt("background", ColorUtils.toInt(preference.getString("TextBG", "#faedc1")))
                    .putInt("sel_background", ColorUtils.toInt(preference.getString("TextBGSel", "#f9d979")))
                    .apply();
        } catch (Exception ex) {
            StaticLogger.error(this, "updatePreferences_84 failed", ex);
        }

    }

    private void convertBookmarks_59(PreferenceHelper preferenceHelper) {
        StaticLogger.info(this, "Convert bookmarks to DB version 1");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager newBM = new BookmarksManager(bookmarksRepo, new DbBookmarksTagsRepository(), new DbTagRepository());
        List<Bookmark> bookmarks = new BookmarksManager(
                new PrefBookmarksRepository(preferenceHelper),
                new DbBookmarksTagsRepository(),
                new DbTagRepository()).getAll();
        for (Bookmark curr : bookmarks) {
            newBM.add(curr.OSISLink, curr.humanLink);
        }
    }

    private void convertBookmarks_63() {
        StaticLogger.info(this, "Convert bookmarks to DB version 2");
        final IBookmarksRepository bookmarksRepo = BibleQuoteApp.getInstance().getBookmarksRepository();
        BookmarksManager bmManager = new BookmarksManager(bookmarksRepo, new DbBookmarksTagsRepository(), new DbTagRepository());
        List<Bookmark> bookmarks = bmManager.getAll();
        for (Bookmark currBM : bookmarks) {
            if (currBM.name == null) {
                currBM.name = currBM.humanLink;
            }
            bmManager.add(currBM);
        }
    }

    private void saveBuiltInModule(Context context, String fileName, int rawId) {
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

    private void saveTSK(Context context) {
        StaticLogger.info(this, "Save TSK file");
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
                StaticLogger.error(this, "Can't delete TSK-file");
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
            StaticLogger.error(this, e.getMessage());
        } finally {
            if (tskBr != null) {
                try {
                    tskBr.close();
                } catch (IOException e) {
                    StaticLogger.error(this, e.getMessage());
                }
            }
            if (tskBw != null) {
                try {
                    tskBw.close();
                } catch (IOException e) {
                    StaticLogger.error(this, e.getMessage());
                }
            }
        }
    }

    private void updateBuiltInModules(Context context) {
        StaticLogger.info(this, "Update built-in modules on external storage");
        saveBuiltInModule(context, "bible_rst.zip", R.raw.bible_rst);
        saveBuiltInModule(context, "bible_kjv.zip", R.raw.bible_kjv);
    }
}
