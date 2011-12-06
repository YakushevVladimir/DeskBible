package com.BibleQuote._new_.dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.BibleQuote.utils.FileUtilities;
import com.BibleQuote.utils.Log;

public class FsContext {
	private final String TAG = "FsContext";
	
	public BufferedReader getTextFileReaderFromZipArchive(String archivePath, String textFileInArchive,
			String textFileEncoding) {
		try {
			InputStream moduleStream = new FileInputStream(new File(archivePath));
			ZipInputStream zStream = new ZipInputStream(moduleStream);
			ZipEntry entry;
			while ((entry = zStream.getNextEntry()) != null) {
				if (entry.getName().toLowerCase().contains(textFileInArchive.toLowerCase())) {
					InputStreamReader iReader = new InputStreamReader(zStream, textFileEncoding);
					return new BufferedReader(iReader);
				};
			}
			return null;
		} catch (IOException e) {
			Log.e(TAG, e);
			return null;
		}
	}
	
	
	public BufferedReader getTextFileReader(String dir, String textfileName, String textFileEncoding) {
		File file = new File(dir, textfileName);
		if (!file.exists()) {
			return null;
		}

		BufferedReader bReader = FileUtilities.OpenFile(file, textFileEncoding);
		if (bReader == null) {
			return null;
		}
		return bReader;
	}
	
	
	public void SearchByFilter(File currentFile, ArrayList<String> resultFiles, FileFilter filter)
			throws IOException {

		try {
			File[] files = currentFile.listFiles(filter);
			if (files == null) {
				return;
			}
			for (File file : files) {
				if (!file.canRead()) {
					continue;
				} else if (file.isDirectory()) {
					SearchByFilter(file, resultFiles, filter);
				} else {
					resultFiles.add(file.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
		}
		
	}
}
