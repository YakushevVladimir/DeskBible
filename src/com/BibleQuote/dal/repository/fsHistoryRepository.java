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
 *
 *  @ File Name : fsHistoryRepository.java
 *  @ Date : 04.03.2012
 *  @ Author : Vladimir Yakushev
 *  
 */

package com.BibleQuote.dal.repository;

import android.util.Log;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.managers.History.IHistoryRepository;

import java.io.*;
import java.util.LinkedList;

public class fsHistoryRepository implements IHistoryRepository {
	private File dirPath;
	private final String historyFileName = "history.dat";
	private final String TAG = "fsHistoryRepository";

	public fsHistoryRepository(File file) {
		this.dirPath = file;
	}

	public void save(LinkedList<ItemList> list) {
		try {
			FileOutputStream fStr = new FileOutputStream(new File(dirPath, historyFileName));
			ObjectOutputStream out = new ObjectOutputStream(fStr);
			out.writeObject(list);
			out.close();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public LinkedList<ItemList> load() throws FileAccessException {
		try {
			FileInputStream fStr = new FileInputStream(new File(dirPath, historyFileName));
			ObjectInputStream out = new ObjectInputStream(fStr);
			LinkedList<ItemList> list = (LinkedList<ItemList>) out.readObject();
			out.close();
			return list;
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.toString());
			throw new FileAccessException(e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			throw new FileAccessException(e.getMessage());
		}
	}
}
