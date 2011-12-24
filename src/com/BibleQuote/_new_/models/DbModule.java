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

	private Boolean isInvalid;
	
	public DbModule(long Id) {
		this.Id = Id;
	}

	@Override
	public Object getDataSourceID() {
		return Id;
	}
	
	@Override
	public Boolean getIsInvalidated() {
		return isInvalid;
	}
	
	@Override
	public void setIsInvalidated(Boolean value) {
		isInvalid = value;
	}

	@Override
	public String getModuleFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		return ShortName;
	}
}
