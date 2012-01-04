package com.BibleQuote.utils;

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


public class FsUtils {
	private static final String TAG = "FsUtils";
	
	public static BufferedReader getTextFileReaderFromZipArchive(String archivePath, String textFileInArchive,
			String textFileEncoding) {
		try {
			InputStream moduleStream = new FileInputStream(new File(archivePath));
			ZipInputStream zStream = new ZipInputStream(moduleStream);
			ZipEntry entry;
			while ((entry = zStream.getNextEntry()) != null) {
				String entryName = entry.getName().toLowerCase();
				if (entryName.contains(File.separator)) {
					entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1);
				}
				String fileName = textFileInArchive.toLowerCase();
				if (entryName.equals(fileName)) {
					InputStreamReader iReader = new InputStreamReader(zStream, textFileEncoding);
					return new BufferedReader(iReader);
				};
			}
			android.util.Log.d(TAG, String.format("File %1$s in zip-arhive %2$s not found", textFileInArchive, archivePath));
			return null;
		} catch (IOException e) {
			Log.e(TAG, e);
			return null;
		}
	}
	
	
	public static BufferedReader getTextFileReader(String dir, String textfileName, String textFileEncoding) {
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
	
	
	public static void SearchByFilter(File currentFile, ArrayList<String> resultFiles, FileFilter filter)
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
			Log.e(TAG, e);
		}
		
	}
	
	
	public static String getFilePath(String fullFileName) {
		return fullFileName == null ? null : fullFileName.substring(0, fullFileName.lastIndexOf("/"));
	}
	
	public static String getFileName(String fullFileName) {
		return fullFileName == null ? null : fullFileName.substring(fullFileName.lastIndexOf("/") + 1);
	}
}
