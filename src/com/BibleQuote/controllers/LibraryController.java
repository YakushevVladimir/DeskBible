package com.BibleQuote.controllers;

import android.content.Context;
import android.os.Environment;
import com.BibleQuote.dal.*;
import com.BibleQuote.managers.EventManager;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.utils.DataConstants;

import java.io.File;

public class LibraryController {

	private IModuleController moduleCtrl;

	public IModuleController getModuleCtrl() {
		return moduleCtrl;
	}

	private IBookController bookCtrl;

	public IBookController getBookCtrl() {
		return bookCtrl;
	}

	private IChapterController chapterCtrl;

	public IChapterController getChapterCtrl() {
		return chapterCtrl;
	}

	private ILibraryUnitOfWork unit;

	public ILibraryUnitOfWork getUnit() {
		return unit;
	}


	public static LibraryController create(Context context) {
		String libraryPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				? DataConstants.FS_EXTERNAL_DATA_PATH
				: DataConstants.FS_DATA_PATH;

		CacheContext cacheContext = new CacheContext(context.getCacheDir(), DataConstants.LIBRARY_CACHE);
		CacheModuleController<FsModule> cache = new CacheModuleController<FsModule>(cacheContext);

		LibraryContext libraryContext = new FsLibraryContext(new File(libraryPath), context, cache);
		return new LibraryController(new LibraryUnitOfWork((FsLibraryContext) libraryContext, cacheContext));
	}

	private LibraryController(LibraryUnitOfWork unit) {
		this.unit = unit;
		moduleCtrl = new FsModuleController(unit);
		bookCtrl = new FsBookController(unit);
		chapterCtrl = new FsChapterController(unit);
	}

	public EventManager getEventManager() {
		return unit.getEventManager();
	}
}
