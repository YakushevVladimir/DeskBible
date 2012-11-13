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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.BibleQuote.listeners.ChangeBooksEvent;
import com.BibleQuote.listeners.ChangeChaptersEvent;
import com.BibleQuote.listeners.ChangeModulesEvent;
import com.BibleQuote.listeners.IChangeBooksListener;
import com.BibleQuote.listeners.IChangeChaptersListener;
import com.BibleQuote.listeners.IChangeModulesListener;
import com.BibleQuote.listeners.ISearchListener;
import com.BibleQuote.listeners.SearchInLibraryEvent;


public class EventManager {
	
	///////////////// IChangeModulesListener
	private final List<IChangeModulesListener> myChangeModuleListeners = Collections.synchronizedList(new LinkedList<IChangeModulesListener>());
	public void addChangeModulesListener(IChangeModulesListener listener) {
		myChangeModuleListeners.add(listener);
	}

	public void removeChangeModulesListener(IChangeModulesListener listener) {
		myChangeModuleListeners.remove(listener);
	}
	
	public void fireChangeModulesEvent(ChangeModulesEvent event) {
		synchronized (myChangeModuleListeners) {
			for (IChangeModulesListener l : myChangeModuleListeners) {
				l.onChangeModules(event);
			}
		}
	}
	

	///////////////// IChangeBooksListener
	private final List<IChangeBooksListener> myChangeBooksListeners = Collections.synchronizedList(new LinkedList<IChangeBooksListener>());
	public void addChangeBooksListener(IChangeBooksListener listener) {
		myChangeBooksListeners.add(listener);
	}

	public void removeChangeBooksListener(IChangeBooksListener listener) {
		myChangeBooksListeners.remove(listener);
	}
		
	public void fireChangeBooksEvent(ChangeBooksEvent event) {
		synchronized (myChangeBooksListeners) {
			for (IChangeBooksListener l : myChangeBooksListeners) {
				l.onChangeBooks(event);
			}
		}
	}

	
	///////////////// IChangeChaptersListener
	private final List<IChangeChaptersListener> myChangeChaptersListeners = Collections.synchronizedList(new LinkedList<IChangeChaptersListener>());
	public void addChangeChaptersListener(IChangeChaptersListener listener) {
		myChangeChaptersListeners.add(listener);
	}

	public void removeChangeChaptersListener(IChangeChaptersListener listener) {
		myChangeChaptersListeners.remove(listener);
	}
		
	public void fireChangeChaptersEvent(ChangeChaptersEvent event) {
		synchronized (myChangeChaptersListeners) {
			for (IChangeChaptersListener l : myChangeChaptersListeners) {
				l.onChangeChapters(event);
			}
		}
	}
	

	/////////////// ISearchListener
	private final List<ISearchListener> mySearchListeners = Collections.synchronizedList(new LinkedList<ISearchListener>());
	public void addSearchListener(ISearchListener listener) {
		mySearchListeners.add(listener);
	}

	public void removeSearchListener(ISearchListener listener) {
		mySearchListeners.remove(listener);
	}
	
	public void fireSearchInLibraryEvent(SearchInLibraryEvent event) {
		synchronized (mySearchListeners) {
			for (ISearchListener l : mySearchListeners) {
				l.onSearchInLibrary(event);
			}
		}
	}
	
	
}
