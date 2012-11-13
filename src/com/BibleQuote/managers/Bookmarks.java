/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.managers;

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class Bookmarks {

    private static final Byte BOOKMARK_DELIMITER = (byte) 0xFE;
    private static final Byte BOOKMARK_PATH_DELIMITER = (byte) 0xFF;

    public static void add(Module module, Book book,
                           Integer chapter, Integer verse) {
        String fav = PreferenceHelper.restoreStateString("Favorits");
        final BibleReference ref = new BibleReference(module, book, chapter, verse);
        final String currLinkPath =  ref.getChapterPath() + "." + verse;
        final String currLink = module.ShortName + ": " + book.Name + " " + chapter + ":" + verse;
        fav = currLink + BOOKMARK_PATH_DELIMITER + currLinkPath + BOOKMARK_DELIMITER + fav;
        PreferenceHelper.saveStateString("Favorits", fav);
    }

    public static void add(Librarian lib, Integer verse) {
        lib.setCurrentVerseNumber(verse);
        Module module = lib.getCurrModule();
        Book book = lib.getCurrBook();
        Integer chapter = lib.getCurrChapterNumber();
        add(module, book, chapter, verse);
    }

    public static void delete(String bookmark) {
        String fav = PreferenceHelper.restoreStateString("Favorits");
        fav = fav.replaceAll(bookmark + "(.)+?" + BOOKMARK_DELIMITER, "");
        PreferenceHelper.saveStateString("Favorits", fav);
    }

    public static ArrayList<String> getAll() {
        ArrayList<String> favorits = new ArrayList<String>();
        String fav = PreferenceHelper.restoreStateString("Favorits");
        if (!fav.equals("")) {
            favorits.addAll(Arrays.asList(fav.split(BOOKMARK_DELIMITER.toString())));
        }

        ArrayList<String> ret = new ArrayList<String>();
        for (String favItem : favorits) {
            ret.add(favItem.split(BOOKMARK_PATH_DELIMITER.toString())[0]);
        }
        return ret;
    }

    public static String get(String humanLink) {
        ArrayList<String> favorits = new ArrayList<String>();
        String fav = PreferenceHelper.restoreStateString("Favorits");
        if (!fav.equals("")) {
            favorits.addAll(Arrays.asList(fav.split(BOOKMARK_DELIMITER.toString())));
            for (String favItem : favorits) {
                if (favItem.contains(humanLink)) {
                    return favItem.split(BOOKMARK_PATH_DELIMITER.toString())[1];
                }
            }
        }

        return "";
    }

    public static void sort() {
        String fav = PreferenceHelper.restoreStateString("Favorits");
        if (!fav.equals("")) {
            TreeSet<String> favorits = new TreeSet<String>();
            favorits.addAll(Arrays.asList(fav.split(BOOKMARK_DELIMITER.toString())));
            StringBuilder newFav = new StringBuilder();
            for (String favItem : favorits) {
                newFav.append(favItem + BOOKMARK_DELIMITER);
            }
            PreferenceHelper.saveStateString("Favorits", newFav.toString());
        }
    }

    public static void deleteAll() {
        PreferenceHelper.saveStateString("Favorits", "");
    }
}