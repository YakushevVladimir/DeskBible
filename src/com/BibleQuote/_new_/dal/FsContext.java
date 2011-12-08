package com.BibleQuote._new_.dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	public String getModuleEncoding(BufferedReader bReader) {
		String encoding = "cp1251";

		HashMap<String, String> charsets = getCharsets();
		String str = "", key, value;
		try {
			while ((str = bReader.readLine()) != null) {
				int pos = str.indexOf("//");
				if (pos >= 0)
					str = str.substring(0, pos);

				int delimiterPos = str.indexOf("=");
				if (delimiterPos == -1) {
					continue;
				}

				key = str.substring(0, delimiterPos).trim().toLowerCase();
				delimiterPos++;
				value = delimiterPos >= str.length() ? "" : str.substring(
						delimiterPos, str.length()).trim();
				if (key.equals("desiredfontcharset")) {
					return charsets.containsKey(value) ? charsets.get(value)
							: encoding;
				} else if (key.equals("defaultencoding")) {
					return value;
				}
			}
			bReader.close();
		} catch (IOException e) {
			return encoding;
		}

		return encoding;
	}

	private HashMap<String, String> getCharsets() {
		HashMap<String, String> charsets = new HashMap<String, String>();
		charsets.put("0", "ISO-8859-1"); // ANSI charset
		charsets.put("1", "US-ASCII"); // DEFAULT charset
		charsets.put("77", "MacRoman"); // Mac Roman
		charsets.put("78", "Shift_JIS"); // Mac Shift Jis
		charsets.put("79", "ms949"); // Mac Hangul
		charsets.put("80", "GB2312"); // Mac GB2312
		charsets.put("81", "Big5"); // Mac Big5
		charsets.put("82", "johab"); // Mac Johab (old)
		charsets.put("83", "MacHebrew"); // Mac Hebrew
		charsets.put("84", "MacArabic"); // Mac Arabic
		charsets.put("85", "MacGreek"); // Mac Greek
		charsets.put("86", "MacTurkish"); // Mac Turkish
		charsets.put("87", "MacThai"); // Mac Thai
		charsets.put("88", "cp1250"); // Mac East Europe
		charsets.put("89", "cp1251"); // Mac Russian
		charsets.put("128", "MS932"); // Shift JIS
		charsets.put("129", "ms949"); // Hangul
		charsets.put("130", "ms1361"); // Johab
		charsets.put("134", "ms936"); // GB2312
		charsets.put("136", "ms950"); // Big5
		charsets.put("161", "cp1253"); // Greek
		charsets.put("162", "cp1254"); // Turkish
		charsets.put("163", "cp1258"); // Vietnamese
		charsets.put("177", "cp1255"); // Hebrew
		charsets.put("178", "cp1256"); // Arabic
		charsets.put("186", "cp1257"); // Baltic
		charsets.put("201", "cp1252"); // Cyrillic charset
		charsets.put("204", "cp1251"); // Russian
		charsets.put("222", "ms874"); // Thai
		charsets.put("238", "cp1250"); // Eastern European
		charsets.put("254", "cp437"); // PC 437
		charsets.put("255", "cp850"); // OEM
		
		return charsets;
	}
}
