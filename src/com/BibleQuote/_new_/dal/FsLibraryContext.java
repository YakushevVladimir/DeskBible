package com.BibleQuote._new_.dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.Log;

public class FsLibraryContext extends FsContext {
	private final String TAG = "FsLibraryContext";
	
	private File libraryDir = null;
	//private Context context;
	
	// libraryPath should be = Environment.getExternalStorageDirectory().toString() + "/BibleQuote/modules/"
	public FsLibraryContext(Context context, String libraryPath) {
		//this.context = context;
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			libraryDir = new File(libraryPath);
			libraryDir.mkdir();
			if (!libraryDir.exists()) {
				libraryDir = null;
			}		
		}		
	}

	public File getLibraryDir() {
		return libraryDir;
	}
	
	
	/**
	 * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
	 * @return Возвращает ArrayList со списком ini-файлов модулей
	 */
	public ArrayList<String> SearchModules(FileFilter filter) {

		Log.i(TAG, "SearchModules()");

		ArrayList<String> iniFiles = new ArrayList<String>();

		if (libraryDir == null) {
			return iniFiles;
		}

		try {
			// Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
			SearchByFilter(libraryDir, iniFiles, filter);
		} catch (Exception e) {
			Log.i(TAG, "Exception in SearchModules(): \r\n" + e.getLocalizedMessage());
			return iniFiles;
		}

		return iniFiles;
	}
	
	
	public void fillModule(Module module, BufferedReader bReader) throws CreateModuleErrorException {
		String str, HTMLFilter = "", key, value;
		ArrayList<String> fullNames = new ArrayList<String>();
		ArrayList<String> pathNames = new ArrayList<String>();
		ArrayList<String> shortNames = new ArrayList<String>();
		ArrayList<Integer> chapterQty = new ArrayList<Integer>();

		int pos;
		try {
			while ((str = bReader.readLine()) != null) {
				pos = str.indexOf("//");
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

				if (key.equals("biblename")) {
					module.Name = value;
				} else if (key.equals("bibleshortname")) {
					module.ShortName = value.replaceAll("\\.", "");
				} else if (key.equals("chaptersign")) {
					module.ChapterSign = value.toLowerCase();
				} else if (key.equals("chapterzero")) {
					module.ChapterZero = value.toLowerCase().contains("y") ? true : false;
				} else if (key.equals("versesign")) {
					module.VerseSign = value.toLowerCase();
				} else if (key.equals("htmlfilter")) {
					HTMLFilter = value;
				} else if (key.equals("bible")) {
					module.isBible = value.toLowerCase().contains("y") ? true : false;
				} else if (key.equals("strongnumbers")) {
					module.containsStrong = value.toLowerCase().contains("y") ? true
							: false;
				} else if (key.equals("pathname")) {
					pathNames.add(value);
				} else if (key.equals("fullname")) {
					fullNames.add(value);
				} else if (key.equals("shortname")) {
					shortNames.add(value.replaceAll("\\.", ""));
				} else if (key.equals("chapterqty")) {
					try {
						chapterQty.add(Integer.valueOf(value));
					} catch (NumberFormatException e) {
						chapterQty.add(0);
					}
				}
			}
			
		} catch (IOException e) {
			Log.e(TAG, e);
		}

		for (int i = 0; i < fullNames.size(); i++) {
			if (pathNames.size() < i || chapterQty.size() < i) {
				break;
			} else if (fullNames.get(i).equals("")
					|| pathNames.get(i).equals("") || chapterQty.get(i) == 0) {
				// Имя книги, путь к книге и кол-во глав должны быть обязательно
				// указаны
				continue;
			}
			FsBook book = new FsBook(fullNames.get(i), pathNames.get(i), (shortNames
							.size() > i ? shortNames.get(i) : ""), chapterQty
							.get(i));
			module.Books.put(book.OSIS_ID, book);
		}
		if (module.Books.size() == 0) {
			throw new CreateModuleErrorException();
		}

		String TagFilter[] = { "p", "b", "i", "em", "strong", "q", "big",
				"sub", "sup", "h1", "h2", "h3", "h4" };
		ArrayList<String> TagArray = new ArrayList<String>();
		for (String tag : TagFilter) {
			TagArray.add(tag);
		}

		if (!HTMLFilter.equals("")) {
			String[] words = HTMLFilter.replaceAll("\\W", " ").trim()
					.split("\\s+");
			for (String word : words) {
				if (word.equals("") || TagArray.contains(word)) {
					continue;
				}
				TagArray.add(word);
			}
		}

		String separator = "";
		for (String tag : TagArray) {
			module.HtmlFilter += separator + "(" + tag + ")|(/" + tag + ")" + "|("
					+ tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")";
			separator = "|";
		}
	}	
	
}
