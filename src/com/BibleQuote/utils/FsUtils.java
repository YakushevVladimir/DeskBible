/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.util.Log;
import com.BibleQuote.exceptions.FileAccessException;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FsUtils {
	private static final String TAG = "FsUtils";

	public static BufferedReader getTextFileReaderFromZipArchive(String archivePath, String textFileInArchive,
																 String textFileEncoding) throws FileAccessException {
		File zipFile = new File(archivePath);
		try {
			InputStream moduleStream = new FileInputStream(zipFile);
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
				}
				;
			}
			String message = String.format("File %1$s in zip-arhive %2$s not found", textFileInArchive, archivePath);
			Log.e(TAG, message);
			throw new FileAccessException(message);
		} catch (UTFDataFormatException e) {
			String message = String.format("Archive %1$s contains the file names not in the UTF format", zipFile.getName());
			Log.e(TAG, message);
			throw new FileAccessException(message);
		} catch (FileNotFoundException e) {
			String message = String.format("File %1$s in zip-arhive %2$s not found", textFileInArchive, archivePath);
			throw new FileAccessException(message);
		} catch (IOException e) {
			Log.e(TAG,
					String.format("getTextFileReaderFromZipArchive(%1$s, %2$s, %3$s)",
							archivePath, textFileInArchive, textFileEncoding), e);
			throw new FileAccessException(e);
		}
	}

	public static BufferedReader getTextFileReader(String dir, String textfileName, String textFileEncoding) throws FileAccessException {
		BufferedReader bReader = null;
		try {
			File file = new File(dir, textfileName);
			bReader = FsUtils.OpenFile(file, textFileEncoding);
			if (bReader == null) {
				throw new FileAccessException(String.format("File %1$s not exists", textfileName));
			}
		} catch (Exception e) {
			throw new FileAccessException(e);
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
			Log.e(TAG,
					String.format("SearchByFilter(%1$s, %2$s)",
							currentFile.getName(), filter.toString()), e);
		}

	}

	public static boolean loadContentFromURL(String fromURL, String toFile) {
		try {
			URL url = new URL("http://bible-desktop.com/xml" + fromURL);
			File file = new File(toFile);
	
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();
	
			/* Define InputStreams to read from the URLConnection */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
	
			/* Read bytes to the Buffer until there is nothing more to read(-1) */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
	
			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();

		} catch (IOException e) {
			Log.e(TAG, String.format("loadContentFromURL(%1$s, %2$s)", fromURL, toFile), e);
			return false;
		}

		return true;
	}

	public static BufferedReader OpenFile(File file, String encoding) {
		Log.i(TAG, "FileUtilities.OpenFile(" + file + ", " + encoding + ")");

		if (!file.exists()) {
			return null;
		}
		BufferedReader bReader = null;
		try {
			InputStreamReader iReader;
			iReader = new InputStreamReader(new FileInputStream(file), encoding);
			bReader = new BufferedReader(iReader);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			return null;
		}

		return bReader;
	}

	public static InputStream getAssetStream(Context context, String paramString)
			throws IOException {
		return context.getResources().getAssets().open(paramString);
	}

	public static String getAssetString(Context context, String paramString) {
		InputStream localInputStream;
		try {
			localInputStream = context.getResources().getAssets()
					.open(paramString);
			StringBuilder sBuilder = new StringBuilder();
			InputStreamReader localInputStreamReader = new InputStreamReader(
					localInputStream);
			BufferedReader localBufferedReader = new BufferedReader(
					localInputStreamReader);
			for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader
					.readLine())
				sBuilder.append(str).append("\n");
			return sBuilder.toString();
		} catch (IOException e) {
			return "";
		}
	}
}
