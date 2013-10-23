package com.BibleQuote.managers.tags.repository;
import java.util.*;
import com.BibleQuote.managers.tags.*;

public interface ITagRepository {
	long add(String tag);
	int update(Tag tag);
	int delete(Tag tag);
	ArrayList<Tag> getAll();
	LinkedHashMap<Tag, String> getAllWithCount();
	int deleteAll();
	int deleteEmptyTags();
}
