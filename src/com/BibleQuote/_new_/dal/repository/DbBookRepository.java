package com.BibleQuote._new_.dal.repository;

import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote._new_.dal.DbLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.DbBook;

public class DbBookRepository implements IBookRepository<Long> {

	private SQLiteDatabase db;
	
    public DbBookRepository(DbLibraryContext context)
    {
        db = context.getDB();
    }
    
	@Override
	public Collection<Book> getBooks(Long moduleId) {
		Cursor cursor = db.rawQuery("SELECT * FROM Book WHERE `moduleId` = " + moduleId, null);
		final ArrayList<Book> bookList = new ArrayList<Book>();
		while (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			final Book book = new DbBook("", "", "", 0, id);   
			bookList.add(book);
		}
		cursor.close();
		return bookList;
	}
	

	@Override
	public Book getBookById(Long bookId) {
		DbBook book = null;
		final Cursor cursor = db.rawQuery("SELECT * FROM Book WHERE book_id = ? ", new String[] {"" + bookId});
		if (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			book = new DbBook("", "", "", 0, id);   
		}
		cursor.close();
		return book;			
	}
	
	
	@Override
	public void insertBook(Book book) {
	}

	
	@Override
	public void deleteBook(Long bookId) {
	}

	
	@Override
	public void updateBook(Book book) {
	}


}
