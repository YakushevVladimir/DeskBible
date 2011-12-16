package com.BibleQuote._new_.controllers;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.managers.EventManager;
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
			return new LibraryController(new FsLibraryUnitOfWork(libraryDir), eventManager);

		case LocalDb:
			libraryPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) 
				? DataConstants.FS_EXTERNAL_DATA_PATH
				: DataConstants.FS_DATA_PATH;
			libraryDir = new File(libraryPath);
			return new LibraryController(new DbLibraryUnitOfWork(libraryDir, context), eventManager);

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
	
	private LibraryController(FsLibraryUnitOfWork unit, EventManager eventManager)
	{
		moduleCtrl = new FsModuleController(unit, eventManager);
		bookCtrl = new FsBookController(unit, eventManager);
		chapterCtrl = new FsChapterController(unit);		
	}

	private LibraryController(DbLibraryUnitOfWork unit, EventManager eventManager)
	{
		moduleCtrl = new DbModuleController(unit, eventManager);
		bookCtrl = new DbBookController(unit, eventManager);
		chapterCtrl = new DbChapterController(unit);		
	}
}
