package com.wordpress.smdaudhilbe.mypicpdf.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnectivity extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "myPicPDF";
	
	@SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.wordpress.smdaudhilbe.mypicpdf/databases/";
	
	private Context context;

	public DatabaseConnectivity(Context context) {
		
		super(context, DB_NAME,null,1);	
		this.context = context;
	}
	
	//	creating a empty database
	public void createDB() throws IOException{
		
		boolean dbExist = checkDB();
		
		if(dbExist)
			;//	do nothing
		
		else
		{
			//	creating an empty database into the default system path / so that we can replace it with our own
			this.getReadableDatabase();
			
			copyDB();
		}
	}

	private void copyDB() {
		
		//	opening the local db at asset/ in inputStream
		InputStream in = null;
		
		try {
			in = context.getAssets().open(DB_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		//	path to just created empty DB
		String outPath = DB_PATH+DB_NAME;
			
		//	open the empty db at output stream
		OutputStream out = null;
		try {
			out = new FileOutputStream(outPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
		//	buffer (byte rates) from input to output
		byte[] buffer = new byte[1024];
			
		int length;
			
		//	copying from local db to system empty db
		try {
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer,0,length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		//	closing
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private boolean checkDB() {
	
		SQLiteDatabase checkdb = null;
		
		try {
			String myPath = DB_PATH+DB_NAME;
			
			checkdb = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READONLY);
			
		} catch (SQLException e) {
			//	nothing to do
		}
		
		//	if database exists the close it
		if(checkdb != null)
			checkdb.close();
		
		return checkdb != null ? true : false;		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table myPicPDF(_slNo integer auto increment primary key,docuName text,created_at datetime default current_timestamp);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("drop table if exists myPicPDF;");
		
		onCreate(db);
	}
}