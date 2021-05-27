package org.github.lorisdemicheli.inventory.util;

public abstract class Ask {

	private String question;

	public Ask(String question) {
		this.question = question;
	}

	public abstract void onResult(boolean result);

	public String getQuestion() {
		return question;
	}
}
