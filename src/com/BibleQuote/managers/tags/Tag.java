package com.BibleQuote.managers.tags;

public class Tag {
	public long id;
	public String name;

	public Tag(int id, String name)	{
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
