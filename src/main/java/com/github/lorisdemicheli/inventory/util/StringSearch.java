package com.github.lorisdemicheli.inventory.util;

public abstract class StringSearch {

	private String title;

	public StringSearch(String title) {
		this.title = title;
	}

	public abstract void onResult(String result);

	public String getTitle() {
		return title;
	}
}
