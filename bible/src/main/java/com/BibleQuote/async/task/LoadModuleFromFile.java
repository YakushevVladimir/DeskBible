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

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Task;

import java.io.File;

/**
 * @author ru_phoenix
 * @version 1.0
 */
public class LoadModuleFromFile extends Task {

    public enum StatusCode {Success, Unknown, FileNotExist, ReadFailed, FileNotSupported, MoveFailed}

    private final ILibraryController libraryController;
    private String path;
    private StatusCode statusCode = StatusCode.Success;

    public LoadModuleFromFile(String message, String path, ILibraryController libraryController) {
        super(message, false);
        this.path = path;
        this.libraryController = libraryController;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            File source = new File(path);
            if (!source.exists()) {
                statusCode = StatusCode.FileNotExist;
                return false;
            } else if (!source.canRead()) {
                statusCode = StatusCode.ReadFailed;
                return false;
            } else if (!source.getName().endsWith("zip")) {
                statusCode = StatusCode.FileNotSupported;
                return false;
            }

            File libraryDir = new File(DataConstants.getLibraryPath());
            File target = new File(libraryDir, source.getName());
            if (!source.renameTo(target)) {
                statusCode = StatusCode.MoveFailed;
                return false;
            }

            libraryController.loadModule(target.getAbsolutePath());
        } catch (Exception e) {
            statusCode = StatusCode.Unknown;
            return false;
        }

        return true;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
