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
 * File: ExceptionHelper.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.exceptions;

import android.content.Context;
import android.util.Log;

import com.BibleQuote.R;
import com.BibleQuote.utils.NotifyDialog;

public final class ExceptionHelper {

    private ExceptionHelper() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static void onException(Exception ex, Context context, String tag) {
        String message = ex.getMessage();
        if (message == null) {
            return;
        }
        Log.e(tag, message);
        new NotifyDialog(message, context).show();
    }

    public static void onOpenModuleException(OpenModuleException ex, Context context, String tag) {
        String moduleId = ex.getModuleId();
        if (moduleId == null) moduleId = "";
        String moduleDatasourceId = ex.getModuleDatasourceId();
        if (moduleDatasourceId == null) moduleDatasourceId = "";


        if (moduleId.equals("") && moduleDatasourceId.equals("")) {
            return;
        }

        String message;
        if (!moduleId.equals("") && !moduleDatasourceId.equals("")) {
            message = String.format(
                    context.getResources().getString(R.string.exception_open_module_short),
                    moduleId);
        } else {
            message = String.format(
                    context.getResources().getString(R.string.exception_open_module_short),
                    !moduleId.equals("") ? moduleId : moduleDatasourceId);
        }
        Log.e(tag, message);
        new NotifyDialog(message, context).show();
    }

    public static void onBooksDefinitionException(BooksDefinitionException ex, Context context, String tag) {
        String message = String.format(
                context.getResources().getString(R.string.exception_books_definition),
                ex.getModuleDatasourceID(), ex.getBooksCount(),
                ex.getPathNameCount(), ex.getFullNameCount(), ex.getShortNameCount(), ex.getChapterQtyCount());
        Log.e(tag, message);
        new NotifyDialog(message, context).show();
    }

    public static void onBookDefinitionException(BookDefinitionException ex, Context context, String tag) {
        String message = String.format(
                context.getResources().getString(R.string.exception_book_definition),
                ex.getBookNumber(), ex.getModuleDatasourceID());
        Log.e(tag, message);
        new NotifyDialog(message, context).show();
    }

    public static void onBookNotFoundException(BookNotFoundException ex, Context context, String tag) {
        String message = String.format(
                context.getResources().getString(R.string.exception_book_not_found),
                ex.getBookID(), ex.getModuleID());
        Log.e(tag, message);
        new NotifyDialog(message, context).show();
    }
}
