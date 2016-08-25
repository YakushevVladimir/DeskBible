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
 * File: StartSearch.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.async.command;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.async.AsyncCommand;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;

public class StartSearch implements AsyncCommand.ICommand {
    private static final String TAG = "StartSearch";
    private String query, fromBookID, toBookID;
    private Context context;

    public StartSearch(Context context, String query, String fromBookID, String toBookID) {
        this.context = context;
        this.query = query;
        this.fromBookID = fromBookID;
        this.toBookID = toBookID;
    }

    @Override
    public void execute() throws Exception {
        if (query.equals("") || fromBookID.equals("") || toBookID.equals("")) {
            return;
        }

        Librarian lib = ((BibleQuoteApp) ((FragmentActivity) context).getApplication()).getLibrarian();
        try {
            lib.search(query, fromBookID, toBookID);
        } catch (BookNotFoundException e) {
            ExceptionHelper.onBookNotFoundException(e, context, TAG);
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, context, TAG);
        }
    }
}
