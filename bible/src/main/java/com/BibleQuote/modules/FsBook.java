package com.BibleQuote.modules;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public class FsBook extends Book {

	private static final long serialVersionUID = -6570010365754882585L;

	/**
	 * Путь к файлу с книгой
	 */
	public final String FileName;

	public FsBook(FsModule module, String name, String fileName, String shortNames, int chapterQty) {
		super(module, name, shortNames, chapterQty);
		this.FileName = fileName;
	}

	@Override
	public String getDataSourceID() {
		return this.FileName;
	}

}
