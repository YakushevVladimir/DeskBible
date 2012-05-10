package com.BibleQuote.entity.Bible;

import android.util.Log;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.DbModule;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;

public class BibleReference {
	public final static String MOD_DATASOURCE_FS = "fs";
	public final static String MOD_DATASOURCE_DB = "db";

	private final String TAG = "LinkOSIS";
	private final String SEP_GROUPS = "\\;";
	private final String SEP_VALUES = "\\:";
	private final String SEP_SHORT = "\\.";
	
	private String OSISLinkPath = null;
	private String moduleDatasource = null;
	private String moduleDatasourceID = null;
	private String moduleID = null;
	
	private String bookID = "Gen";
	private String bookName = "Genesis";
	private int chapterNumber = 1;
	private int fromVerse = 1;
	private int toVerse = 1;
	
	public BibleReference(String OSISLinkPath) {
		if (OSISLinkPath != null) {
			String[] linkParam = OSISLinkPath.split(SEP_GROUPS); 
			if (linkParam.length >= 5) {
				// OSISLinkPath extended path format = ds:fs;id:sd-card/mnt/biblequote/modules/rst;m:RST;b:MARK;ch:1;v:1
				try {
					moduleDatasource = linkParam[0].split(SEP_VALUES)[1];
					moduleDatasourceID = linkParam[1].split(SEP_VALUES)[1];
					moduleID = linkParam[2].split(SEP_VALUES)[1];
					bookID = linkParam[3].split(SEP_VALUES)[1];
					chapterNumber = 1;
					fromVerse = 1;
					try {
						chapterNumber = Integer.parseInt(linkParam[4].split(SEP_VALUES)[1]);
						fromVerse = Integer.parseInt(linkParam[5].split(SEP_VALUES)[1]);
					} catch (NumberFormatException  e) {}
				} catch (Exception e) {
					Log.e(TAG, String.format("OSISLink(%1$s)", OSISLinkPath), e);
				}				
				
			} else {
				// OSISLinkPath short path format    
				linkParam = OSISLinkPath.split(SEP_SHORT);
				if (linkParam.length >= 2) {
					moduleID = linkParam[0];
					bookID = linkParam[1];
					chapterNumber = 1;
					fromVerse = 1;
					try {
						chapterNumber = (linkParam.length >= 3 ?  Integer.parseInt(linkParam[2]) : 1);
						fromVerse = (linkParam.length >= 4 ?  Integer.parseInt(linkParam[3]) : 1);
					} catch (NumberFormatException  e) {
					}
				}
			}
		}
	}

	public BibleReference(String moduleDatasource, String moduleDatasourceID, String moduleID, String bookID, Integer chapterNumber, Integer verseNumber)
	{
		this.moduleDatasource = moduleDatasource;
		this.moduleDatasourceID = moduleDatasourceID;
		this.moduleID = moduleID;
		this.bookID = bookID;
		this.chapterNumber = chapterNumber;
		this.fromVerse = verseNumber;
	}
	
	public BibleReference(Module module, Book book, Integer chapterNumber, Integer verseNumber)
	{
		if (module instanceof FsModule) {
			this.moduleDatasource = MOD_DATASOURCE_FS;
		} else if (module instanceof DbModule) {
			this.moduleDatasource = MOD_DATASOURCE_DB;
		} else  {
			this.moduleDatasource = null;
		}
		this.moduleDatasourceID = module == null ? null : (String)module.getDataSourceID();
		this.moduleID = module == null ? null : module.getID();
		this.bookID = book == null ? null : book.getID();
		this.bookName = book == null ? null : book.Name;
		this.chapterNumber = chapterNumber;
		this.fromVerse = verseNumber;
	}
	
	public BibleReference(String moduleID, String bookID, int chapter,
			int fromVerse, int toVerse) {
		this.moduleID = moduleID;
		this.bookID = bookID;
		this.chapterNumber = chapter;
		this.fromVerse = fromVerse;
		this.toVerse = toVerse;
	}

	public String getPath()
	{
		if (OSISLinkPath == null) {
			if (moduleID == null || bookID == null) {
				OSISLinkPath = null;
			} else {
				OSISLinkPath = String.format("%1$s.%2$s.%3$s.%4$s", moduleID, bookID, chapterNumber, fromVerse);
			}
		}
		return OSISLinkPath;
	}
	
	public String getExtendedPath()
	{
		if (OSISLinkPath == null) {
			if (moduleID == null || bookID == null) {
				OSISLinkPath = null;
			} else {
				OSISLinkPath = String.format("ds:%1$s;id:%2$s;m:%3$s;b:%4$s;ch:%5$s;v:%6$s", moduleDatasource, moduleDatasourceID, moduleID, bookID, chapterNumber, fromVerse);
			}
		}
		return OSISLinkPath;
	}
	
	public String getChapterPath()
	{
		if (OSISLinkPath == null) {
			if (moduleID == null || bookID == null) {
				OSISLinkPath = null;
			} else {
				OSISLinkPath = String.format("%1$s.%2$s.%3$s", moduleID, bookID, chapterNumber);
			}
		}
		return OSISLinkPath;
	}
	
	public String getModuleDatasource() {
		return moduleDatasource;
	}

	public String getModuleDatasourceID() {
		return moduleDatasourceID;
	}
	
	public String getModuleID() {
		return moduleID;
	}

	public String getBook() {
		return bookID;
	}

	public int getChapter() {
		return chapterNumber;
	}

	public int getFromVerse() {
		return fromVerse;
	}

	public int getToVerse() {
		return toVerse;
	}

	@Override
	public String toString() {
		return String.format("%1$s:%2$s %3$s:%4$s", moduleID, bookID, chapterNumber, fromVerse);
	}

	public String getBookFullName() {
		return bookName;
	}
}
