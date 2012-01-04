package com.BibleQuote.utils;

import java.io.File;
import java.io.FileFilter;

public class OnlyBQZipIni implements FileFilter {
	private String filter;

	public OnlyBQZipIni() {
		this.filter = ".zip";
	}

	public OnlyBQZipIni(String filter) {
		this.filter = filter;
	}

	@Override
	public boolean accept(File myFile) {
		return myFile.getName().toLowerCase().endsWith(this.filter)
				|| myFile.isDirectory();
	}

	@Override
	public String toString() {
		return this.filter;
	}
}