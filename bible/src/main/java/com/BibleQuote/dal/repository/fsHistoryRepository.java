/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: fsHistoryRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository;

import android.util.Log;

import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.entity.ItemList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class fsHistoryRepository implements IHistoryRepository {
	private File dirPath;
	private static final String historyFileName = "history.dat";
	private static final String TAG = "fsHistoryRepository";

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
	public LinkedList<ItemList> load() throws DataAccessException {
		try {
			FileInputStream fStr = new FileInputStream(new File(dirPath, historyFileName));
			ObjectInputStream out = new ObjectInputStream(fStr);
			LinkedList<ItemList> list = (LinkedList<ItemList>) out.readObject();
			out.close();
			return list;
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.toString());
			throw new DataAccessException(e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			throw new DataAccessException(e.getMessage());
		}
	}
}
