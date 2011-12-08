package com.BibleQuote._new_.models;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class FsBook extends Book {
	
	private static final long serialVersionUID = -6570010365754882585L;

	/**
	 * Путь к файлу с книгой
	 */
	public String PathName;
	
	public String ModuleShortName;
	
	public FsBook(String name, String pathName, String shortNames,
			int chapterQty) {
		super(name, shortNames, chapterQty);
		this.PathName = pathName;
	}

}
