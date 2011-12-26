package com.BibleQuote._new_.controllers;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.BibleQuote._new_.dal.CacheContext;
import com.BibleQuote._new_.dal.DbLibraryContext;
import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.utils.DataConstants;

public class LibraryController {

	public enum LibrarySource {
		FileSystem,
		LocalDb,	
		RemoteDb		
	}
	
	private IModuleController moduleCtrl;
	private IBookController bookCtrl;
	private IChapterController chapterCtrl;
	
	public static LibraryController create(LibrarySource librarySource, EventManager eventManager, Context context) {
		String libraryPath;
		File libraryDir;
		switch (librarySource) {
		
		case FileSystem:
			libraryPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) 
				? DataConstants.FS_EXTERNAL_DATA_PATH
				: DataConstants.FS_DATA_PATH;
			libraryDir = new File(libraryPath);
			CacheContext cacheContext = new CacheContext(context.getCacheDir(), DataConstants.LIBRARY_CACHE);
			CacheModuleController<FsModule> cache = new CacheModuleController<FsModule>(cacheContext);
			FsLibraryContext fsLibraryContext = new FsLibraryContext(libraryDir, context, cache);
			return new LibraryController( new FsLibraryUnitOfWork(fsLibraryContext, cacheContext) );

		case LocalDb:
			libraryPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) 
				? DataConstants.DB_EXTERNAL_DATA_PATH
				: DataConstants.DB_DATA_PATH;
			libraryDir = new File(libraryPath);
			DbLibraryContext dbLibraryContext = new DbLibraryContext(libraryDir, context);
			return new LibraryController(new DbLibraryUnitOfWork(dbLibraryContext), eventManager);

		default:
			break;
		}		
		
		return null;
	}
	
	
	public IModuleController getModuleCtrl() {
		return moduleCtrl;
	}

	public IBookController getBookCtrl() {
		return bookCtrl;
	}

	public IChapterController getChapterCtrl() {
		return chapterCtrl;
	}
	
	private LibraryController(FsLibraryUnitOfWork unit)
	{
		moduleCtrl = new FsModuleController(unit);
		bookCtrl = new FsBookController(unit);
		chapterCtrl = new FsChapterController(unit);		
	}

	private LibraryController(DbLibraryUnitOfWork unit, EventManager eventManager)
	{
		moduleCtrl = new DbModuleController(unit, eventManager);
		bookCtrl = new DbBookController(unit, eventManager);
		chapterCtrl = new DbChapterController(unit);		
	}
}
