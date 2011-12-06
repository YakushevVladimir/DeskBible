package com.BibleQuote._new_.models;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class DbBook extends Book {

	private static final long serialVersionUID = 2083315049799687476L;
	
	/**
	 * Идентификатор книги в БД
	 */
	public Long Id;

	/**
	 * Путь к файлу с книгой
	 */
	public String PathName;
	
	public DbBook(String name, String pathName, String shortNames, int chapterQty, long id) {
		super(name, shortNames, chapterQty);
		this.Id = id;
		this.PathName = pathName;
	}

}
