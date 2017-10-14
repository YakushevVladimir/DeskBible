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
 * File: SelectTextHandler.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.dialogs.BookmarksDialog;
import com.BibleQuote.presentation.widget.ReaderWebView;
import com.BibleQuote.utils.share.ShareBuilder;

import java.util.TreeSet;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 01.2016
 */
final class SelectTextHandler implements ActionMode.Callback {

    private static final String VIEW_REFERENCE = "org.scripturesoftware.intent.action.VIEW_REFERENCE";

    private ReaderActivity readerActivity;
    private ReaderWebView webView;

    SelectTextHandler(ReaderActivity readerActivity, ReaderWebView webView) {
        this.readerActivity = readerActivity;
        this.webView = webView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        readerActivity.getMenuInflater().inflate(R.menu.menu_action_text_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        TreeSet<Integer> selVerses = webView.getSelectedVerses();
        if (selVerses.isEmpty()) {
            return true;
        }

        Librarian myLibrarian = BibleQuoteApp.getInstance().getLibrarian();

        switch (item.getItemId()) {
            case R.id.action_bookmarks:
                myLibrarian.setCurrentVerseNumber(selVerses.first());
                DialogFragment bmDial = BookmarksDialog.newInstance(new Bookmark(myLibrarian.getCurrentOSISLink()));
                bmDial.show(readerActivity.getSupportFragmentManager(), "bookmark");
                break;

            case R.id.action_share:
                myLibrarian.shareText(readerActivity, selVerses, ShareBuilder.Destination.ActionSend);
                break;

            case R.id.action_copy:
                myLibrarian.shareText(readerActivity, selVerses, ShareBuilder.Destination.Clipboard);
                Toast.makeText(readerActivity, readerActivity.getString(R.string.added), Toast.LENGTH_LONG).show();
                break;

            case R.id.action_references:
                myLibrarian.setCurrentVerseNumber(selVerses.first());
                Intent intParallels = new Intent(VIEW_REFERENCE);
                intParallels.putExtra("linkOSIS", myLibrarian.getCurrentOSISLink().getPath());
                readerActivity.startActivityForResult(intParallels, ReaderActivity.ID_PARALLELS);
                break;

            default:
                return false;
        }

        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        webView.clearSelectedVerse();
    }
}
