package com.ittus.book_template.model;

import java.util.ArrayList;

public class Part {

	public String id;
	public String title;
	public ArrayList<Chapter> chapters;

	public Part() {
	}

	public Part(String id, String title) {
		this.id = id;
		this.title = title;
		chapters = new ArrayList<Chapter>();
	}
}
