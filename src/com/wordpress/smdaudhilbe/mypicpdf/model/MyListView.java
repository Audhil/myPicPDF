package com.wordpress.smdaudhilbe.mypicpdf.model;

public class MyListView {
	
	private String itemName,itemCreatedAt,itemPath;
	
	public MyListView() {
		
	}

	public MyListView(String itemName,String itemCreatedAt) {
		this.itemName = itemName;
		this.itemCreatedAt = itemCreatedAt;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getItemName() {
		return this.itemName;
	}
	
	public void setItemCreatedAt(String itemCreatedAt) {
		this.itemCreatedAt = itemCreatedAt;
	}
	
	public String getitemCreatedAt() {
		return this.itemCreatedAt;
	}

	public void setItemPicPath(String itemPath) {
		this.itemPath = itemPath;
	}
	
	public String getItemPicPath() {
		return this.itemPath;
	}
}