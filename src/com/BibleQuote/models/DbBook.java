package com.BibleQuote.models;

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
	 * Идентификатор модуля книги в БД
	 */
	public Long ModuleId;
	
	/**
	 * Путь к файлу с книгой в папке модуля
	 */
	public String PathName;
	

	public DbBook(Module module, String name, String pathName, String shortNames, int chapterQty, long id) {
		super(module, name, shortNames, chapterQty);
		this.Id = id;
		this.PathName = pathName;
	}


	@Override
	public Object getDataSourceID() {
		return Id;
	}


}
