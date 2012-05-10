package com.BibleQuote.entity.Bible;

import java.util.ArrayList;

import com.BibleQuote.utils.BibleLinkParser;

public class TSK {
	
	public static ArrayList<BibleReference> getLinks(BibleReference reference){
		String parallelsStr = getParallels(reference);
		return BibleLinkParser.parse(reference.getModuleID(), parallelsStr);
	}
	
	private static String getParallels(BibleReference link) {
		return "Eze 40:3; Eze 40:5; Eze 47:4; Re 11:1; Re 21:15; Zec 1:16; Zec 1:18-25";
	}
}
