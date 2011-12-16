package com.BibleQuote.utils;

import java.io.File;
import java.io.FileFilter;

import com.BibleQuote._new_.utils.DataConstants;

public class OnlyBQIni implements FileFilter {
	private String filter;

	public OnlyBQIni() {
		this.filter = DataConstants.DEFAULT_INI_FILE_NAME;
	}

	public OnlyBQIni(String filter) {
		this.filter = filter;
	}

	@Override
	public boolean accept(File myFile) {
		return myFile.getName().toLowerCase().equals(this.filter)
				|| myFile.isDirectory();
	}
}