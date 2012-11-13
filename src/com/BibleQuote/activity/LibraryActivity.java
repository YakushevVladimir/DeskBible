/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.activity;

import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.AsyncOpenModule;
import com.BibleQuote.managers.AsyncRefreshModules;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

public class LibraryActivity extends SherlockActivity implements OnTaskCompleteListener {
	private static final String TAG = "LibraryActivity";
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
	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;
	private String messageRefresh;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.books);
		ViewUtils.setActionBarBackground(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BibleQuoteApp app = (BibleQuoteApp) getApplication();
		myLibrarian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		
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

		BibleReference osisLink = myLibrarian.getCurrentOSISLink();
		if (myLibrarian.isOSISLinkValid(osisLink)) {
			moduleID = osisLink.getModuleID();
			bookID   = osisLink.getBookID();
			chapter  = String.valueOf(osisLink.getChapter());
			UpdateView(CHAPTER_VIEW);
		} else {
			UpdateView(MODULE_VIEW);
		}
		setButtonText();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infl = getSupportMenuInflater();
		infl.inflate(R.menu.menu_library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_bar_refresh:
				if (this.viewMode == MODULE_VIEW) {
					mAsyncManager.setupTask(
							new AsyncRefreshModules(messageRefresh, false, myLibrarian, this), this);
				}
                break;
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

	private AdapterView.OnItemClickListener modulesList_onClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			modules = myLibrarian.getModulesList();
			if (modules.size() <= position) {
				UpdateView(MODULE_VIEW);
				return;
			}
			modulePos  = position;
			moduleID   = modules.get(modulePos).get(ItemList.ID); 
			bookPos    = 0;
			chapterPos = 0;
			
			String message = getResources().getString(R.string.messageLoadBooks);
			BibleReference currentOSISLink = myLibrarian.getCurrentOSISLink();
			BibleReference OSISLink = new BibleReference(
					currentOSISLink.getModuleDatasource(), 
					null, 
					moduleID, 
					currentOSISLink.getBookID(), 
					currentOSISLink.getChapter(), 
					currentOSISLink.getFromVerse());
			AsyncOpenModule asyncOpenModuleTask = new AsyncOpenModule(message, false, myLibrarian, OSISLink);
			mAsyncManager.setupTask(asyncOpenModuleTask, LibraryActivity.this);
		}
	};

	private AdapterView.OnItemClickListener booksList_onClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			bookPos    = position;
			bookID     = books.get(bookPos).get("ID");
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
			
			ItemList itemModule = new ItemList(moduleID, myLibrarian.getModuleFullName());
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
				itemBook = new ItemList(bookID, myLibrarian.getBookFullName(moduleID, bookID));
				bookPos = books.indexOf(itemBook);
				if (bookPos >= 0) {
					booksList.setSelection(bookPos);
				}
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
		if (moduleID.equals("---"))
			return;
		UpdateView(MODULE_VIEW);
	}

	public void onBookClick(View v) {
		if (bookID.equals("---"))
			return;		
		UpdateView(BOOK_VIEW);
	}

	public void onChapterClick(View v) {
		if (chapter.equals("-"))
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
			if (task instanceof AsyncRefreshModules) {
				if (this.viewMode == MODULE_VIEW) {
					UpdateView(MODULE_VIEW);
				}
			} else if (task instanceof AsyncOpenModule) {
				onAsyncOpenModuleComplete((AsyncOpenModule) task);
			}
		}	
	}
	
	private void onAsyncOpenModuleComplete(AsyncOpenModule task) {
		if (task.isSuccess()) {
			moduleID = task.getModule().getID();
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
