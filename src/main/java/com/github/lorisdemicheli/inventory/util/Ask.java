package com.github.lorisdemicheli.inventory.util;

public abstract class Ask {

	private String question;
	public String yes = "YES";
	public String no = "NO";

	public Ask(String question) {
		this.question = question;
	}

	public abstract void onResult(boolean result);

	public String getQuestion() {
		return question;
	}

	public String getYes() {
		return yes;
	}

	public void setYes(String yes) {
		this.yes = yes;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	
}
