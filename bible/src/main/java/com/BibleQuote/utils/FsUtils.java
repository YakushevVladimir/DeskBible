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
 * File: FsUtils.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.logger.StaticLogger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FsUtils {

    private static final int BUFFER_SIZE = 1024 * 4;
    private static final String TAG = "FsUtils";

    private FsUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static InputStream getStreamFromZip(String path, String fileName) {
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.contains(File.separator)) {
                    entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1);
                }
                if (entryName.equalsIgnoreCase(fileName)) {
                    return zis;
                }
            }
        } catch (IOException e) {
            String message = String.format("File %1$s in zip-arhive %2$s not found", fileName, path);
            Log.e(TAG, message);
        }
        return null;
    }

    public static byte[] getBytes(InputStream stream) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
                BufferedInputStream in = new BufferedInputStream(stream)
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, length);
            }
            return out.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    public static InputStream getStream(String path, String fileName) {
        File streamFile = new File(path, fileName);
        try {
            return new FileInputStream(streamFile);
        } catch (IOException e) {
            String message = String.format("File %s/%s not found", path, fileName);
            Log.e(TAG, message);
        }
        return null;
    }

    public static BufferedReader getTextFileReaderFromZipArchive(String archivePath, String searchFileName,
            String encoding) throws DataAccessException {
        File zipFile = new File(archivePath);
        try {
            InputStream moduleStream = new FileInputStream(zipFile);
            ZipInputStream zStream = new ZipInputStream(moduleStream);
            ZipEntry entry;
            while ((entry = zStream.getNextEntry()) != null) {
                String entryName = entry.getName().toLowerCase();
                if (entryName.contains(File.separator)) {
                    entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1);
                }
                if (entryName.equalsIgnoreCase(searchFileName)) {
                    InputStreamReader iReader = new InputStreamReader(zStream, encoding);
                    return new BufferedReader(iReader);
                }
            }
            String message = String.format("File %1$s in zip-arhive %2$s not found", searchFileName, archivePath);
            Log.e(TAG, message);
            throw new DataAccessException(message);
        } catch (UTFDataFormatException e) {
            String message = String.format("Archive %1$s contains the file names not in the UTF format", zipFile.getName());
            Log.e(TAG, message);
            throw new DataAccessException(message);
        } catch (FileNotFoundException e) {
            String message = String.format("File %1$s in zip-arhive %2$s not found", searchFileName, archivePath);
            throw new DataAccessException(message);
        } catch (IOException e) {
            Log.e(TAG,
                    String.format("getTextFileReaderFromZipArchive(%1$s, %2$s, %3$s)",
                            archivePath, searchFileName, encoding), e);
            throw new DataAccessException(e);
        }
    }

    public static BufferedReader getTextFileReader(String dir, String fileName, String textFileEncoding) throws DataAccessException {
        BufferedReader bReader;
        try {
            File file = new File(dir, fileName);
            bReader = FsUtils.openFile(file, textFileEncoding);
            if (bReader == null) {
                throw new DataAccessException(String.format("File %1$s not exists", fileName));
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
        return bReader;
    }

    public static void searchByFilter(File currentFile, ArrayList<String> resultFiles, FileFilter filter) throws IOException {
        try {
            File[] files = currentFile.listFiles(filter);
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    StaticLogger.info(TAG, "Search in " + file.getAbsolutePath());
                    searchByFilter(file, resultFiles, filter);
                } else if (file.canRead()) {
                    StaticLogger.info(TAG, "Add file " + file.getAbsolutePath());
                    resultFiles.add(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            StaticLogger.error(TAG,
                    String.format("SearchByFilter(%1$s, %2$s)",
                            currentFile.getName(), filter.toString()), e);
        }

    }

    private static BufferedReader openFile(File file, String encoding) {
        Log.i(TAG, "FileUtilities.OpenFile(" + file + ", " + encoding + ")");

        if (!file.exists()) {
            return null;
        }
        BufferedReader bReader;
        try {
            InputStreamReader iReader;
            iReader = new InputStreamReader(new FileInputStream(file), encoding);
            bReader = new BufferedReader(iReader);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            return null;
        }

        return bReader;
    }

    public static String getAssetString(Context context, String fileName) {
        try (
                InputStream assetIS = context.getResources().getAssets().open(fileName);
                InputStreamReader localInputStreamReader = new InputStreamReader(assetIS, Charset.forName("UTF-8"));
                BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader)
        ) {
            StringBuilder sBuilder = new StringBuilder();
            for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine()) {
                sBuilder.append(str).append("\n");
            }
            return sBuilder.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Осуществляет поиск файла в списке директорий.
     *
     * @param name имя искомого файла
     * @param dirs список директорий, в которых осуществляется поиск
     *
     * @return найденный файл или {@code null}
     */
    @Nullable
    public static File findFile(String name, File... dirs) {
        for (File dir : dirs) {
            if (!dir.exists()) {
                continue;
            }

            File result = new File(dir, name);
            if (result.exists() && result.canWrite()) {
                return result;
            }
        }
        return null;
    }
}
