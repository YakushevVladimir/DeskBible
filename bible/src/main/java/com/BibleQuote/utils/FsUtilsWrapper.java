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
 * File: FsUtilsWrapper.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils;

import com.BibleQuote.domain.exceptions.DataAccessException;

import java.io.BufferedReader;
import java.io.InputStream;

public class FsUtilsWrapper {

    public byte[] getBytes(InputStream stream) {
        return FsUtils.getBytes(stream);
    }

    public InputStream getStreamFromZip(String path, String fileName) {
        return FsUtils.getStreamFromZip(path, fileName);
    }

    public InputStream getStream(String path, String fileName) {
        return FsUtils.getStream(path, fileName);
    }

    public BufferedReader getTextFileReader(String path, String dataSourceID, String encoding) throws DataAccessException {
        return FsUtils.getTextFileReader(path, dataSourceID, encoding);
    }

    public BufferedReader getTextFileReaderFromZipArchive(String path, String dataSourceID, String encoding) throws DataAccessException {
        return FsUtils.getTextFileReaderFromZipArchive(path, dataSourceID, encoding);
    }
}
