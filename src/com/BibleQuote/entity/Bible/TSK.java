package com.BibleQuote.entity.Bible;

import java.util.LinkedHashSet;

import com.BibleQuote.utils.BibleLinkParser;
import com.BibleQuote.utils.BibleReference.ITskRepository;

public class TSK {
	
	ITskRepository repository;
	
	public TSK(ITskRepository repository){
		this.repository = repository;
	}
	
	public LinkedHashSet<BibleReference> getLinks(BibleReference reference){
		String parallelsStr = getParallels(reference);
		return BibleLinkParser.parse(reference.getModuleID(), parallelsStr);
	}
	
	private String getParallels(BibleReference link) {
		String book = link.getBookID();
		String chapter = String.valueOf(link.getChapter());
		String verse = String.valueOf(link.getFromVerse());
		return repository.getReferences(book, chapter, verse);
	}
}
