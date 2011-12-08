package com.BibleQuote._new_.models;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class DbModule extends Module {
	
	private static final long serialVersionUID = -3650606026193395583L;
	
	/**
	 * Идентификатор модуля в БД
	 */
	public Long Id;	

	public DbModule(long Id) {
		this.Id = Id;
	}
}
