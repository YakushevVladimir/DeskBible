package com.BibleQuote.models;

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

	private Boolean isInvalid;
	
	public DbModule(long Id) {
		this.Id = Id;
	}

	@Override
	public String getDataSourceID() {
		return Id.toString();
	}
	
	@Override
	public Boolean getIsClosed() {
		return isInvalid;
	}
	
	@Override
	public void setIsClosed(Boolean value) {
		isInvalid = value;
	}

	@Override
	public String getModuleFileName() {
		return null;
	}

	@Override
	public String getID() {
		return ShortName;
	}
}
