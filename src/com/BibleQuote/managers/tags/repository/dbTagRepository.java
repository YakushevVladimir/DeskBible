package com.BibleQuote.managers.tags.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.BibleQuote.dal.dbLibraryHelper;
import com.BibleQuote.managers.bookmarks.repository.dbBookmarksTagsRepository;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.utils.DataConstants;

import java.util.ArrayList;

public class dbTagRepository implements ITagRepository {
	private final static String TAG = dbTagRepository.class.getSimpleName();
	private dbBookmarksTagsRepository bmTagRepo = new dbBookmarksTagsRepository();

	@Override
	public long add(String tag) {
		Log.w(TAG, String.format("Add tag %s", tag));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		long id = addRow(db, tag);
		dbLibraryHelper.closeDB();
		return id;
	}

	@Override
	public int update(Tag tag) {
		Log.w(TAG, String.format("Update tag %s", tag.name));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(dbLibraryHelper.TAGS_NAME, tag.name);
			result = db.update(DataConstants.TAGS_TABLE, values, dbLibraryHelper.TAGS_KEY_ID + "=\"" + tag.id + "\"", null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int delete(Tag tag) {
		Log.w(TAG, String.format("Delete tag %s", tag.name));
		int result = -1;
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			bmTagRepo.deleteTag(db, tag);
			result = db.delete(DataConstants.TAGS_TABLE, dbLibraryHelper.TAGS_KEY_ID + "=\"" + tag.id + "\"", null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public ArrayList<Tag> getAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		ArrayList<Tag> result = new ArrayList<Tag>();
		try {
			result = getAllRowsToArray(db);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int deleteAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			result = db.delete(DataConstants.TAGS_TABLE, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int deleteEmptyTags() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.execSQL(
				"DELETE FROM " + DataConstants.TAGS_TABLE
					+ " WHERE " + dbLibraryHelper.TAGS_KEY_ID
					+ " IN (SELECT " + dbLibraryHelper.TAGS_KEY_ID + " FROM " + DataConstants.TAGS_TABLE
						+ " WHERE NOT " + dbLibraryHelper.TAGS_KEY_ID
						+ " IN (SELECT " + dbLibraryHelper.BOOKMARKS_TAGS_TAG_ID + " FROM " + DataConstants.BOOKMARKS_TAGS_TABLE + "))");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return 0;
	}

	private ArrayList<Tag> getAllRowsToArray(SQLiteDatabase db) {
		ArrayList<Tag> result = new ArrayList<Tag>();
		Cursor allRows = db.query(true, DataConstants.TAGS_TABLE,
				null, null, null, null, null, dbLibraryHelper.TAGS_NAME, null);
		if (allRows.moveToFirst()) {
			do {
				result.add(new Tag(
						allRows.getInt(allRows.getColumnIndex(dbLibraryHelper.TAGS_KEY_ID)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.TAGS_NAME))
				)
				);
			} while (allRows.moveToNext());
		}
		allRows.close();
		return result;
	}

	private long addRow(SQLiteDatabase db, String tag) {
		long result;
		db.beginTransaction();
		try {
			Cursor cur = db.query(DataConstants.TAGS_TABLE, null, dbLibraryHelper.TAGS_NAME + " = \"" + tag.trim() + "\"", null, null, null, null);
			if (cur.moveToFirst()) {
				result = cur.getInt(0);
				cur.close();
			} else {
				ContentValues values = new ContentValues();
				values.put(dbLibraryHelper.TAGS_NAME, tag.trim());
				result = db.insert(DataConstants.TAGS_TABLE, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return result;
	}

}
