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
		db.delete(DataConstants.TAGS_TABLE, dbLibraryHelper.TAGS_NAME + "=\"" + tag + "\"", null);
		long id = addRow(db, tag);
		db.close();
		return id;
	}

	@Override
	public int update(Tag tag) {
		Log.w(TAG, String.format("Update tag %s", tag.name));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		ContentValues values = new ContentValues();
		values.put(dbLibraryHelper.TAGS_NAME, tag.name);
		return db.update(DataConstants.TAGS_TABLE, values, dbLibraryHelper.TAGS_KEY_ID + "=\"" + tag.id + "\"", null);
	}

	@Override
	public int delete(Tag tag) {
		Log.w(TAG, String.format("Delete tag %s", tag.name));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		bmTagRepo.deleteTag(db, tag);
		return db.delete(DataConstants.TAGS_TABLE, dbLibraryHelper.TAGS_KEY_ID + "=\"" + tag.id + "\"", null);
	}

	@Override
	public ArrayList<Tag> getAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		ArrayList<Tag> result = getAllRowsToArray(db);
		return result;
	}

	@Override
	public int deleteAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		return db.delete(DataConstants.TAGS_TABLE, null, null);
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
		return result;
	}

	private long addRow(SQLiteDatabase db, String tag) {
		ContentValues values = new ContentValues();
		values.put(dbLibraryHelper.TAGS_NAME, tag.trim());
		return db.insert(DataConstants.TAGS_TABLE, null, values);
	}

}
