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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.presentation.ui.library;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static java.util.Collections.emptyList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.BibleQuote.R;
import com.BibleQuote.di.component.ActivityComponent;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.ui.base.BQActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.churchtools.deskbible.data.library.LibraryContext;

public class LibraryActivity extends BQActivity {

    private static final int BOOK_VIEW = 2, CHAPTER_VIEW = 3;
    private static final String TAG = LibraryActivity.class.getSimpleName();

    private final BooksAdapter booksAdapter = new BooksAdapter(book -> {
        onClickBookItem(book);
        return null;
    });

    private RecyclerView booksList;
    private Button btnBook;
    private Button btnChapter;
    private GridView chapterList;

    @Inject
    Librarian librarian;
    @Inject
    LibraryContext mLibraryContext;
    @Inject
    ILibraryController mILibraryController;

    private String moduleID = Librarian.EMPTY_OBJ;
    private String bookID = Librarian.EMPTY_OBJ;
    private String chapter = Librarian.EMPTY_OBJ;
    private String bookShortName = Librarian.EMPTY_OBJ;
    private List<Book> books = emptyList();
    private List<String> chapters = new ArrayList<>();

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, LibraryActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        booksList = findViewById(R.id.books);
        booksList.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
        booksList.setAdapter(booksAdapter);

        chapterList = findViewById(R.id.chapterChoose);
        chapterList.setOnItemClickListener((parent, view, position, id) -> onClickChapterItem(view));

        btnBook = findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> updateView(BOOK_VIEW));

        btnChapter = findViewById(R.id.btnChapter);
        btnChapter.setOnClickListener(v -> updateView(CHAPTER_VIEW));

        BibleReference osisLink = librarian.getCurrentOSISLink();
        if (!librarian.isOSISLinkValid(osisLink)) {
            finish();
            return;
        }

        moduleID = osisLink.getModuleID();
        bookID = osisLink.getBookID();
        chapter = String.valueOf(osisLink.getChapter());

        loadBooks();
        loadChapters();

        setButtonText();
        updateView(BOOK_VIEW);
    }

    @Override
    protected void inject(ActivityComponent component) {
        component.inject(this);
    }

    private void onClickChapterItem(View view) {
        TextView chapterView = view.findViewById(R.id.chapter);
        if (chapterView != null) {
            chapter = chapterView.getText().toString();
            sendSelectedLink();
        }
    }

    private void onClickBookItem(Book book) {
        bookID = book.getID();
        bookShortName = book.getShortName();

        loadChapters();
        if (chapters.size() == 1) {
            chapter = chapters.get(0);
            sendSelectedLink();
        } else {
            setupChapterAdapter();
            updateView(CHAPTER_VIEW);
            setButtonText();
        }

    }

    private void loadBooks() {
        try {
            books = librarian.getBooks(moduleID);
            setupBooksAdapter();
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, this, TAG);
            finish();

        }
    }

    private void loadChapters() {
        try {
            chapters = librarian.getChaptersList(moduleID, bookID);
            setupChapterAdapter();
        } catch (BookNotFoundException e) {
            ExceptionHelper.onBookNotFoundException(e, this, TAG);
            finish();
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, this, TAG);
            finish();
        }
    }

    private void setupBooksAdapter() {
        int selectedPosition = 0;
        for (int i = 0; i < books.size(); i++) {
            Book item = books.get(i);
            if (bookID.equals(item.getID())) {
                selectedPosition = i;
                break;
            }
        }

        bookShortName = books.get(selectedPosition).getShortName();
        booksAdapter.submitList(books);
        booksList.scrollToPosition(selectedPosition);
    }

    private void setupChapterAdapter() {
        chapterList.setAdapter(
                new ArrayAdapter<>(this, R.layout.chapter_item, R.id.chapter, chapters)
        );
    }

    private void sendSelectedLink() {
        Intent intent = new Intent()
                .putExtra("linkOSIS", String.format("%s.%s.%s", moduleID, bookID, chapter));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setButtonText() {
        btnBook.setText(bookShortName);
        btnChapter.setText(chapter);
    }

    private void updateView(int viewMode) {
        booksList.setVisibility(viewMode == BOOK_VIEW ? View.VISIBLE : View.GONE);
        chapterList.setVisibility(viewMode == CHAPTER_VIEW ? View.VISIBLE : View.GONE);
    }
}
