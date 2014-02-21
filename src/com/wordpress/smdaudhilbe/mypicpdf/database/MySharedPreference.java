package com.wordpress.smdaudhilbe.mypicpdf.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPreference {

	SharedPreferences sPreferences;
	private Editor editor;
	
	public static final String MY_SPREFERENCE = "mySharedPreference";
	
	public MySharedPreference(Context context) {
		
		sPreferences = context.getSharedPreferences(MY_SPREFERENCE, 0);
		editor = sPreferences.edit();
		
		editor.commit();
	}
	
	//	Storing and getting docuname
	public void storeDocuName(String docuName) {
		editor.putString("presentDocuName", docuName);
		editor.commit();
	}
	
	public String getDocuName() {
		return sPreferences.getString("presentDocuName","No_Docu");
	}
}