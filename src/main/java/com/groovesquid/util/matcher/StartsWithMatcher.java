package com.groovesquid.util.matcher;

public class StartsWithMatcher implements SuggestMatcher {
	public boolean matches(String dataWord, String searchWord) {
		return dataWord.startsWith(searchWord);
	}
}