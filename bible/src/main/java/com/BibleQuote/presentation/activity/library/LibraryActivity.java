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
 * File: LibraryActivity.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.presentation.activity.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.task.AsyncOpenModule;
import com.BibleQuote.async.task.AsyncRefreshModules;
import com.BibleQuote.async.task.LoadModuleFromFile;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.activity.base.AsyncTaskActivity;
import com.BibleQuote.utils.FilenameUtils;
import com.BibleQuote.utils.NotifyDialog;
import com.BibleQuote.utils.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class LibraryActivity extends AsyncTaskActivity {

    private static final int ACTION_CODE_GET_FILE = 1;
    private static final int MODULE_VIEW = 1, BOOK_VIEW = 2, CHAPTER_VIEW = 3;
    private static final String TAG = LibraryActivity.class.getSimpleName();

    @BindView(R.id.books) ListView booksList;
    @BindView(R.id.btnBook) Button btnBook;
    @BindView(R.id.btnChapter) Button btnChapter;
    @BindView(R.id.btnModule) Button btnModule;
    @BindView(R.id.chapterChoose) GridView chapterList;
    @BindView(R.id.modules) ListView modulesList;

    @Inject Librarian librarian;

    private String bookID = Librarian.EMPTY_OBJ;
    private ArrayList<ItemList> books = new ArrayList<>();
    private String chapter = Librarian.EMPTY_OBJ;
    private List<String> chapters = new ArrayList<>();
    private String messageRefresh;
    private String moduleID = Librarian.EMPTY_OBJ;
    private int modulePos, bookPos, chapterPos;
    private ArrayList<ItemList> modules = new ArrayList<>();
    private int viewMode = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);

        getActivityComponent().inject(this);
        messageRefresh = getResources().getString(R.string.messageRefresh);

        BibleReference osisLink = librarian.getCurrentOSISLink();
        if (librarian.isOSISLinkValid(osisLink)) {
            moduleID = osisLink.getModuleID();
            bookID = osisLink.getBookID();
            chapter = String.valueOf(osisLink.getChapter());
            updateView(CHAPTER_VIEW);
        } else {
            updateView(MODULE_VIEW);
        }
        setButtonText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
                mAsyncManager.setupTask(new AsyncRefreshModules(messageRefresh, false), this);
                return true;
            case R.id.menu_library_add:
                choiceModuleFromFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateView(viewMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_CODE_GET_FILE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FilenameUtils.getPath(this, uri);
                    getModuleFromFile(path);
                }
                break;
            default:
                Log.e(TAG, "Unknown request code: " + requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task == null || task.isCancelled()) {
            return;
        }

        if (task instanceof AsyncOpenModule) {
            onOpenModuleComplete((AsyncOpenModule) task);
        } else if (task instanceof LoadModuleFromFile) {
            onLoadModuleComplete((LoadModuleFromFile) task);
        } else {
            updateView(MODULE_VIEW);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @OnItemClick(R.id.books)
    void onClickBookItem(int position) {
        bookPos = position;
        bookID = books.get(bookPos).get("ID");
        chapterPos = 0;

        updateView(CHAPTER_VIEW);
        setButtonText();

        if (chapters.size() == 1) {
            chapter = chapters.get(0);
            readChapter();
        }
    }

    @OnItemClick(R.id.chapterChoose)
    void onClickChapterItem(int position) {
        chapterPos = position;
        chapter = chapters.get(position);
        setButtonText();
        readChapter();
    }

    @OnItemClick(R.id.modules)
    void onClickModuleItem(int position2) {
        modules = librarian.getModulesList();
        if (modules.size() <= position2) {
            updateView(MODULE_VIEW);
            return;
        }
        modulePos = position2;
        moduleID = modules.get(modulePos).get(ItemList.ID);
        bookPos = 0;
        chapterPos = 0;

        String message = getResources().getString(R.string.messageLoadBooks);
        BibleReference currentOSISLink = librarian.getCurrentOSISLink();
        BibleReference osisLink1 = new BibleReference(
                currentOSISLink.getModuleDatasource(),
                null,
                moduleID,
                currentOSISLink.getBookID(),
                currentOSISLink.getChapter(),
                currentOSISLink.getFromVerse());

        mAsyncManager.setupTask(new AsyncOpenModule(message, false, osisLink1), this);
    }

    @OnClick({R.id.btnBook, R.id.btnChapter, R.id.btnModule})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBook:
                onClickBook();
                break;
            case R.id.btnChapter:
                onClickChapter();
                break;
            case R.id.btnModule:
                onClickModule();
                break;
        }
    }

    private void choiceModuleFromFile() {
        final Intent target = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("application/zip")
                .addCategory(Intent.CATEGORY_OPENABLE);
        if (target.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(target, ACTION_CODE_GET_FILE);
        } else {
            Toast.makeText(this, R.string.exception_add_module_from_file, Toast.LENGTH_LONG).show();
        }
    }

    private SimpleAdapter getBookAdapter() {
        books = new ArrayList<>();
        if (!librarian.getModulesList().isEmpty()) {
            try {
                books = librarian.getModuleBooksList(moduleID);
            } catch (OpenModuleException e) {
                ExceptionHelper.onOpenModuleException(e, this, TAG);
            } catch (BooksDefinitionException e) {
                ExceptionHelper.onBooksDefinitionException(e, this, TAG);
            } catch (BookDefinitionException e) {
                ExceptionHelper.onBookDefinitionException(e, this, TAG);
            }
        }
        return new SimpleAdapter(this, books,
                R.layout.item_list,
                new String[]{ItemList.ID, ItemList.Name}, new int[]{
                R.id.id, R.id.name});
    }

    private ArrayAdapter<String> getChapterAdapter() {
        try {
            chapters = librarian.getChaptersList(moduleID, bookID);
        } catch (BookNotFoundException e) {
            ExceptionHelper.onBookNotFoundException(e, this, TAG);
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, this, TAG);
        }
        return new ArrayAdapter<>(this, R.layout.chapter_item, R.id.chapter, chapters);
    }

    private SimpleAdapter getModuleAdapter() {
        modules = librarian.getModulesList();
        return new SimpleAdapter(this, modules,
                R.layout.item_list,
                new String[]{ItemList.ID, ItemList.Name}, new int[]{
                R.id.id, R.id.name});
    }

    private void getModuleFromFile(String path) {
        mAsyncManager.setupTask(new LoadModuleFromFile(getString(R.string.copy_module_from_file), path,
                BibleQuoteApp.getInstance().getLibraryController()), this);
    }

    private void onClickBook() {
        if (bookID.equals(Librarian.EMPTY_OBJ)) {
            return;
        }
        updateView(BOOK_VIEW);
    }

    private void onClickChapter() {
        if (chapter.equals(Librarian.EMPTY_OBJ)) {
            return;
        }
        updateView(CHAPTER_VIEW);
    }

    private void onClickModule() {
        if (moduleID.equals(Librarian.EMPTY_OBJ)) {
            return;
        }
        updateView(MODULE_VIEW);
    }

    private void onLoadModuleComplete(LoadModuleFromFile task) {
        LoadModuleFromFile.StatusCode statusCode = task.getStatusCode();
        String errorMessage;
        switch (statusCode) {
            case Success:
                updateView(MODULE_VIEW);
                return;
            case FileNotExist:
                errorMessage = getString(R.string.file_not_exist);
                break;
            case ReadFailed:
                errorMessage = getString(R.string.file_cant_read);
                break;
            case FileNotSupported:
                errorMessage = getString(R.string.file_not_supported);
                break;
            case MoveFailed:
                errorMessage = getString(R.string.file_not_moved);
                break;
            default:
                errorMessage = getString(R.string.err_load_module_unknown);
        }
        new NotifyDialog(errorMessage, this).show();
    }

    private void onOpenModuleComplete(AsyncOpenModule task) {
        Exception e = task.getException();
        if (e == null) {
            BaseModule module = task.getModule();
            moduleID = module.getID();
            Map<String, Book> books = module.getBooks();
            if (books != null && books.size() != 0 && !books.containsKey(bookID)) {
                Iterator<String> iterator = books.keySet().iterator();
                bookID = iterator.next();
            }
            setButtonText();
            updateView(BOOK_VIEW);
        } else {
            if (e instanceof OpenModuleException) {
                ExceptionHelper.onOpenModuleException((OpenModuleException) e, this, TAG);

            } else if (e instanceof BooksDefinitionException) {
                ExceptionHelper.onBooksDefinitionException((BooksDefinitionException) e, this, TAG);

            } else if (e instanceof BookDefinitionException) {
                ExceptionHelper.onBookDefinitionException((BookDefinitionException) e, this, TAG);
            }
            updateView(MODULE_VIEW);
        }
    }

    private void readChapter() {
        setResult(RESULT_OK, new Intent()
                .putExtra("linkOSIS", String.format("%s.%s.%s", moduleID, bookID, chapter)));
        finish();
    }

    private void setButtonText() {
        String bookShortName = Librarian.EMPTY_OBJ;
        if (!moduleID.equals(Librarian.EMPTY_OBJ) && !bookID.equals(Librarian.EMPTY_OBJ)) {
            try {
                bookShortName = librarian.getBookShortName(moduleID, bookID);
                List<String> chList = librarian.getChaptersList(moduleID, bookID);
                if (!chList.isEmpty()) {
                    chapter = chList.contains(chapter) ? chapter : chList.get(0);
                } else {
                    chapter = Librarian.EMPTY_OBJ;
                }
            } catch (OpenModuleException e) {
                ExceptionHelper.onOpenModuleException(e, this, TAG);
                moduleID = Librarian.EMPTY_OBJ;
                bookID = Librarian.EMPTY_OBJ;
                chapter = Librarian.EMPTY_OBJ;
            } catch (BookNotFoundException e) {
                ExceptionHelper.onBookNotFoundException(e, this, TAG);
                bookID = Librarian.EMPTY_OBJ;
                chapter = Librarian.EMPTY_OBJ;
            }
        }

        btnModule.setText(moduleID);
        btnBook.setText(bookShortName);
        btnChapter.setText(chapter);
    }

    private void updateView(int viewMode) {
        this.viewMode = viewMode;

        btnModule.setEnabled(viewMode != MODULE_VIEW);
        btnBook.setEnabled(viewMode != BOOK_VIEW);
        btnChapter.setEnabled(viewMode != CHAPTER_VIEW);

        modulesList.setVisibility(viewMode == MODULE_VIEW ? View.VISIBLE : View.GONE);
        booksList.setVisibility(viewMode == BOOK_VIEW ? View.VISIBLE : View.GONE);
        chapterList.setVisibility(viewMode == CHAPTER_VIEW ? View.VISIBLE : View.GONE);

        switch (viewMode) {
            case MODULE_VIEW:
                viewModeModule();
                break;
            case BOOK_VIEW:
                viewModeBook();
                break;
            case CHAPTER_VIEW:
                viewModeChapter();
                break;
            default:
                break;
        }
    }

    private void viewModeBook() {
        booksList.setAdapter(getBookAdapter());
        ItemList itemBook;
        try {
            itemBook = new ItemList(bookID, librarian.getBookFullName(moduleID, bookID));
            bookPos = books.indexOf(itemBook);
            if (bookPos >= 0) {
                booksList.setSelection(bookPos);
            }
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, this, TAG);
        }
    }

    private void viewModeChapter() {
        chapterList.setAdapter(getChapterAdapter());
        chapterPos = chapters.indexOf(chapter);
        if (chapterPos >= 0) {
            chapterList.setSelection(chapterPos);
        }
    }

    private void viewModeModule() {
        modulesList.setAdapter(getModuleAdapter());
        modulePos = modules.indexOf(new ItemList(moduleID, librarian.getModuleFullName()));
        if (modulePos >= 0) {
            modulesList.setSelection(modulePos);
        }
    }
}
