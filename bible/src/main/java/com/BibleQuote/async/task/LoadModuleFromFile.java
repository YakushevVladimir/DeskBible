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
 * File: LoadModuleFromFile.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.async.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.FileUtils;

import androidx.annotation.NonNull;

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.utils.FilenameUtils;
import com.BibleQuote.utils.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import ru.churchtools.deskbible.data.library.LibraryContext;
import ru.churchtools.deskbible.domain.logger.StaticLogger;

/**
 * @author ru_phoenix
 * @version 1.0
 */
public class LoadModuleFromFile extends Task {

    @NonNull
    private final LibraryContext mLibraryContext;
    @NonNull
    private final ILibraryController mLibraryController;
    @NonNull
    private final WeakReference<Context> mContext;
    @NonNull
    private final Uri mUri;
    private StatusCode mStatusCode = StatusCode.Success;

    public LoadModuleFromFile(@NonNull Context context,
                              @NonNull String message,
                              @NonNull Uri uri,
                              @NonNull ILibraryController libraryController,
                              @NonNull LibraryContext libraryContext) {
        super(message, false);
        mContext = new WeakReference<>(context.getApplicationContext());
        mUri = uri;
        mLibraryController = libraryController;
        mLibraryContext = libraryContext;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        Context context = mContext.get();
        if (context == null) {
            return false;
        }

        StaticLogger.info(this, "Load module from " + mUri);

        File modulesDir = mLibraryContext.modulesDir();
        if (!modulesDir.exists() && !modulesDir.mkdirs()) {
            mStatusCode = StatusCode.LibraryNotFound;
            StaticLogger.error(this, "Library directory not found");
            return false;
        }

        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(mUri);
        if (!"application/zip".equals(type)) {
            mStatusCode = StatusCode.FileNotSupported;
            return false;
        }

        String fileName = FilenameUtils.getFileName(context, mUri);
        if (fileName == null) {
            mStatusCode = StatusCode.FileNotExist;
            return false;
        }

        try(InputStream stream = contentResolver.openInputStream(mUri)) {
            File target = new File(modulesDir, fileName);
            FileUtils.copy(stream, new FileOutputStream(target));
            mLibraryController.loadModule(target);
        } catch (Exception e) {
            StaticLogger.error(this, e.getMessage(), e);
            mStatusCode = StatusCode.MoveFailed;
            return false;
        }

        return true;
    }

    public StatusCode getStatusCode() {
        return mStatusCode;
    }

    public enum StatusCode {Success, FileNotExist, FileNotSupported, MoveFailed, LibraryNotFound}
}
