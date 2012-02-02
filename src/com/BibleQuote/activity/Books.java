/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.activity;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.AsyncLoadModules;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.AsyncOpenModule;
import com.BibleQuote.managers.AsyncRefreshModules;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

public class Books extends GDActivity implements OnTaskCompleteListener {
	private static final String TAG = "Books";
	private final int MODULE_VIEW = 1, BOOK_VIEW = 2, CHAPTER_VIEW = 3;
	private int viewMode = 1;

	private ListView modulesList, booksList;
	private GridView chapterList;
	private Button btnModule, btnBook, btnChapter;
	
	private ArrayList<ItemList> modules = new ArrayList<ItemList>();
	private ArrayList<ItemList> books = new ArrayList<ItemList>();
	private ArrayList<String> chapters = new ArrayList<String>();
	
	private int modulePos = 0, bookPos = 0, chapterPos = 0;
	private String moduleID = "---", bookID = "---", chapter = "-";
	private String moduleDatasourceID, bookDatasourceID;
	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;
	private String messageLoadModules;
	private String messageRefresh;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.books);
		initActionBar();

		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibrarian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		
		messageLoadModules = getResources().getString(R.string.messageLoadModules);
		messageRefresh = getResources().getString(R.string.messageRefresh);
		
		btnModule  = (Button) findViewById(R.id.btnModule);
		btnBook    = (Button) findViewById(R.id.btnBook);
		btnChapter = (Button) findViewById(R.id.btnChapter);

		modulesList = (ListView) findViewById(R.id.modules);
		modulesList.setOnItemClickListener(modulesList_onClick);

		booksList = (ListView) findViewById(R.id.books);
		booksList.setOnItemClickListener(booksList_onClick);

		chapterList = (GridView) findViewById(R.id.chapterChoose);
		chapterList.setOnItemClickListener(chapterList_onClick);

		OSISLink OSISLink = myLibrarian.getCurrentOSISLink();
		if (OSISLink.getPath() != null) {
			moduleDatasourceID = OSISLink.getModuleDatasourceID();
			moduleID = OSISLink.getModuleID();
			bookID   = OSISLink.getBookID();
			chapter  = OSISLink.getChapterNumber().toString();
			
			modulesList.setAdapter(getModuleAdapter());
			booksList.setAdapter(getBookAdapter());
			chapterList.setAdapter(getChapterAdapter());
			
			UpdateView(CHAPTER_VIEW);
		} else {
			UpdateView(MODULE_VIEW);
		}
		setButtonText();
		
		if (!mAsyncManager.isWorking()) {
			// to continue open closed modules in background 
			if (myLibrarian.getClosedModule() != null) {
//				mAsyncManager.setupTask(
//						new AsyncLoadModules(messageLoadModules, true, myLibrarian, false), this);
			}
		}		
	}
	
	private void initActionBar() {
		ActionBar bar = getActionBar();
		ActionBarItem itemCont = bar.newActionBarItem(NormalActionBarItem.class);
		itemCont.setDrawable(R.drawable.ic_action_bar_refresh);
		addActionBarItem(itemCont, R.id.action_bar_refresh);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		Log.i(TAG, "onHandleActionBarItemClick(" + item + ", " + position + ")");
		switch (item.getItemId()) {
		case R.id.action_bar_refresh:
			if (this.viewMode == MODULE_VIEW) {
				mAsyncManager.setupTask(
						new AsyncRefreshModules(messageRefresh, false, myLibrarian, this), this);
//						new AsyncLoadModules(messageLoad, false, myLibrarian, true), this);
			}
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}
	
	private AdapterView.OnItemClickListener modulesList_onClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			modules = myLibrarian.getModulesList();
			modulePos  = position;
			moduleID   = modules.get(modulePos).get(ItemList.ID); 
			moduleDatasourceID = modules.get(modulePos).get(ItemList.DatasourceID); 
			bookPos    = 0;
			chapterPos = 0;
			
			String message = getResources().getString(R.string.messageLoadBooks);
			OSISLink currentOSISLink = myLibrarian.getCurrentOSISLink();
			OSISLink OSISLink = new OSISLink(
					currentOSISLink.getModuleDatasource(), 
					moduleDatasourceID, 
					moduleID, 
					currentOSISLink.getBookID(), 
					currentOSISLink.getChapterNumber(), 
					currentOSISLink.getVerseNumber());
			AsyncOpenModule asyncOpenModuleTask = new AsyncOpenModule(message, false, myLibrarian, OSISLink);
			mAsyncManager.setupTask(asyncOpenModuleTask, Books.this);
		}
	};

	private AdapterView.OnItemClickListener booksList_onClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			bookPos    = position;
			bookID     = books.get(bookPos).get("ID");
			bookDatasourceID   = books.get(bookPos).get("Path"); 
			chapterPos = 0;

			UpdateView(CHAPTER_VIEW);
			setButtonText();

			if (chapters.size() == 1) {
				chapter = chapters.get(0);
				readChapter();
			}
		}
	};
	
	private AdapterView.OnItemClickListener chapterList_onClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			chapterPos = position;
			chapter    = chapters.get(position);

			setButtonText();
			readChapter();
		}
	};

	private void readChapter() {
		Intent intent = new Intent();
		intent.putExtra("linkOSIS", moduleID + "." + bookID + "." + chapter);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void setButtonText(){

		String bookShortName = "---";
		try {
			bookShortName = myLibrarian.getBookShortName(moduleID, bookID);
		} catch (BookNotFoundException e) {
			ExceptionHelper.onBookNotFoundException(e, this, TAG);
		} catch (OpenModuleException e) {
			ExceptionHelper.onOpenModuleException(e, this, TAG);
		}
		
		btnModule.setText(moduleID);
		btnBook.setText(bookShortName);
		btnChapter.setText(chapter);
	}

	private void UpdateView(int viewMode) {

		this.viewMode = viewMode;

		switch (viewMode) {
		case MODULE_VIEW:
			btnModule.setEnabled(false);
			btnBook.setEnabled(true);
			btnChapter.setEnabled(true);

			modulesList.setVisibility(View.VISIBLE);
			booksList.setVisibility(View.GONE);
			chapterList.setVisibility(View.GONE);
			
			modulesList.setAdapter(getModuleAdapter());
			
			ItemList itemModule = new ItemList(moduleID, myLibrarian.getModuleFullName(moduleID), moduleDatasourceID);
			modulePos = modules.indexOf(itemModule);
			if (modulePos >= 0) {
				modulesList.setSelection(modulePos);
			}
			break;

		case BOOK_VIEW:
			btnModule.setEnabled(true);
			btnBook.setEnabled(false);
			btnChapter.setEnabled(true);

			modulesList.setVisibility(View.GONE);
			booksList.setVisibility(View.VISIBLE);
			chapterList.setVisibility(View.GONE);

			booksList.setAdapter(getBookAdapter());

			ItemList itemBook;
			try {
				itemBook = new ItemList(bookID, myLibrarian.getBookFullName(moduleID, bookID), bookDatasourceID);
				bookPos = books.indexOf(itemBook);
				if (bookPos >= 0) {
					booksList.setSelection(bookPos);
				}
			} catch (BookNotFoundException e) {
				ExceptionHelper.onBookNotFoundException(e, this, TAG);
			} catch (OpenModuleException e) {
				ExceptionHelper.onOpenModuleException(e, this, TAG);
			}
			break;

		case CHAPTER_VIEW:
			btnModule.setEnabled(true);
			btnBook.setEnabled(true);
			btnChapter.setEnabled(false);

			modulesList.setVisibility(View.GONE);
			booksList.setVisibility(View.GONE);
			chapterList.setVisibility(View.VISIBLE);

			chapterList.setAdapter(getChapterAdapter());

			chapterPos = chapters.indexOf(chapter);
			if (chapterPos >= 0) {
				chapterList.setSelection(chapterPos);
			}

			break;

		default:
			break;
		}
	}

	private SimpleAdapter getModuleAdapter() {
		modules = myLibrarian.getModulesList();
		return new SimpleAdapter(this, modules,
				R.layout.item_list,
				new String[] { ItemList.ID, ItemList.Name }, new int[] {
						R.id.id, R.id.name });
	}

	private SimpleAdapter getBookAdapter() {
		books = new ArrayList<ItemList>();
		if (myLibrarian.getModulesList().size() > 0) {
			try {
				books = myLibrarian.getModuleBooksList(moduleID);
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
				new String[] { ItemList.ID, ItemList.Name }, new int[] {
						R.id.id, R.id.name });
	}

	private ArrayAdapter<String> getChapterAdapter() {
		chapters = new ArrayList<String>();
		try {
			chapters = myLibrarian.getChaptersList(moduleID, bookID);
		} catch (BookNotFoundException e) {
			ExceptionHelper.onBookNotFoundException(e, this, TAG);
		} catch (OpenModuleException e) {
			ExceptionHelper.onOpenModuleException(e, this, TAG);
		}
		return new ArrayAdapter<String>(this, R.layout.chapter_item,
				R.id.chapter, chapters);
	}

	public void onModuleClick(View v) {
		if (moduleID == "---")
			return;
		UpdateView(MODULE_VIEW);
	}

	public void onBookClick(View v) {
		if (bookID == "---")
			return;		
		UpdateView(BOOK_VIEW);
	}

	public void onChapterClick(View v) {
		if (chapter == "-")
			return;		
		UpdateView(CHAPTER_VIEW);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		UpdateView(viewMode);
	}

	public void onTaskComplete(Task task) {
		Log.i(TAG, "onTaskComplete()");
		if (task != null && !task.isCancelled()) {
			if (task instanceof AsyncLoadModules) {
				onAsyncLoadModulesComplete((AsyncLoadModules) task); 
				
			} else if (task instanceof AsyncOpenModule) {
				onAsyncOpenModuleComplete((AsyncOpenModule) task);
			}
		}	
	}
	
	private void onAsyncLoadModulesComplete(AsyncLoadModules task) {
		if (task.isSuccess()) {
			if (this.viewMode == MODULE_VIEW) {
				UpdateView(MODULE_VIEW);
			}
			// to continue open closed modules in background 
			if (task.getNextClosedModule() != null) {
				mAsyncManager.setupTask(
					new AsyncLoadModules(messageLoadModules, true, myLibrarian, false), Books.this);
			}
			
		} else {
			Exception e = task.getException();
			if (e instanceof OpenModuleException) {
				ExceptionHelper.onOpenModuleException((OpenModuleException) e, this, TAG);
			}  
		}		
	}
	
	private void onAsyncOpenModuleComplete(AsyncOpenModule task) {
		if (task.isSuccess()) {
			moduleID = task.getModule().getID();
			bookID = "---";
			chapter = "-";
			setButtonText();
			UpdateView(BOOK_VIEW);
			
		} else {
			Exception e = task.getException();
			if (e instanceof OpenModuleException) {
				ExceptionHelper.onOpenModuleException((OpenModuleException) e, this, TAG);
				
			} else if (e instanceof BooksDefinitionException) {
				ExceptionHelper.onBooksDefinitionException((BooksDefinitionException) e, this, TAG);
				
			} else if (e instanceof BookDefinitionException) {
				ExceptionHelper.onBookDefinitionException((BookDefinitionException) e, this, TAG);
			}
			UpdateView(MODULE_VIEW);
		}		
	}
	

	
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return mAsyncManager.retainTask();
    }
}
