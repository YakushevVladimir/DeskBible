package com.BibleQuote.managers.tags;

public class Tag {

	public static final String KEY_ID = "_id";
	public static final String NAME = "name";

	public long id;
	public String name;

	public Tag(int id, String name)	{
		this.id = id;
		this.name = name.trim().toLowerCase();
	}

	@Override
	public String toString() {
		return name;
	}
}
