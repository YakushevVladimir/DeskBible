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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.Task;

import java.io.File;

/**
 * @author ru_phoenix
 * @version 1.0
 */
public class LoadModuleFromFile extends Task {

    @Nullable
    private final File mLibraryDir;
    @NonNull
    private final ILibraryController mLibraryController;
    @NonNull
    private final String mPath;
    private StatusCode mStatusCode = StatusCode.Success;

    public LoadModuleFromFile(@NonNull Context context, String message, @NonNull String path, @NonNull ILibraryController libraryController) {
        super(message, false);
        mPath = path;
        mLibraryController = libraryController;
        mLibraryDir = FsUtils.getLibraryDir(context);
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        StaticLogger.info(this, "Load module from " + mPath);

        if (mLibraryDir == null) {
            mStatusCode = StatusCode.LibraryNotFound;
            StaticLogger.error(this, "Library directory not found");
            return false;
        }

        try {
            File source = new File(mPath);
            if (!source.exists()) {
                StaticLogger.error(this, "Module file not found");
                mStatusCode = StatusCode.FileNotExist;
                return false;
            } else if (!source.canRead()) {
                StaticLogger.error(this, "File not readable");
                mStatusCode = StatusCode.ReadFailed;
                return false;
            } else if (!source.getName().endsWith("zip")) {
                StaticLogger.error(this, "Unsupported module type");
                mStatusCode = StatusCode.FileNotSupported;
                return false;
            }

            File target = new File(mLibraryDir, source.getName());
            if (!source.renameTo(target)) {
                StaticLogger.error(this, "Unable to move file to module directory");
                mStatusCode = StatusCode.MoveFailed;
                return false;
            }

            mLibraryController.loadModule(target.getAbsolutePath());
        } catch (Exception e) {
            StaticLogger.error(this, e.getMessage(), e);
            mStatusCode = StatusCode.Unknown;
            return false;
        }

        return true;
    }

    public StatusCode getStatusCode() {
        return mStatusCode;
    }

    public enum StatusCode {Success, Unknown, FileNotExist, ReadFailed, FileNotSupported, MoveFailed, LibraryNotFound}
}
