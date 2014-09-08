package com.groovesquid.util.matcher;

public class EndsWithMatcher implements SuggestMatcher {
	public boolean matches(String dataWord, String searchWord) {
		return dataWord.endsWith(searchWord);
	}
}