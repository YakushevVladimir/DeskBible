package com.BibleQuote.managers;

import com.BibleQuote.listeners.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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
