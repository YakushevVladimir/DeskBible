package com.BibleQuote._new_.managers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.BibleQuote._new_.listeners.ChangeLibraryEvent;
import com.BibleQuote._new_.listeners.IChangeListener;
import com.BibleQuote._new_.listeners.ISearchListener;
import com.BibleQuote._new_.listeners.SearchInLibraryEvent;


public class EventManager {
	
	private final List<IChangeListener> myChangeListeners = Collections.synchronizedList(new LinkedList<IChangeListener>());
	private final List<ISearchListener> mySearchListeners = Collections.synchronizedList(new LinkedList<ISearchListener>());
	
	///////////////// IChangeListener
	public void addChangeListener(IChangeListener listener) {
		myChangeListeners.add(listener);
	}

	public void removeChangeListener(IChangeListener listener) {
		myChangeListeners.remove(listener);
	}
	
	public void fireChangeLibraryEvent(ChangeLibraryEvent event) {
		synchronized (myChangeListeners) {
			for (IChangeListener l : myChangeListeners) {
				l.onChangeLibrary(event);
			}
		}
	}
	
	/////////////// ISearchListener
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
