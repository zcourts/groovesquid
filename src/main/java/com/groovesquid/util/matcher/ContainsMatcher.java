package com.groovesquid.util.matcher;

public class ContainsMatcher implements SuggestMatcher {
	public boolean matches(String dataWord, String searchWord) {
		return dataWord.contains(searchWord);
	}
}