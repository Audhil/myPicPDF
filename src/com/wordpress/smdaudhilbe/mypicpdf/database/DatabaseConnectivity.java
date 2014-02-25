package com.wordpress.smdaudhilbe.mypicpdf.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.wordpress.smdaudhilbe.mypicpdf.model.MyListView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseConnectivity extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "myPicPDF";
	
	@SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.wordpress.smdaudhilbe.mypicpdf/databases/";
	
	private Context context;

	private MySharedPreference mPreference;

	public DatabaseConnectivity(Context context) {
		
		super(context, DB_NAME,null,1);	
		this.context = context;
		
		mPreference = new MySharedPreference(context);
	}
	
	//	creating a empty database
	public void createDB() throws IOException{
		
		boolean dbExist = checkDB();
		
		//	do nothing
		if(dbExist);
		
		//	creating an empty database into the default system path / so that we can replace it with our own
		else{			
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
			
			while ((length = in.read(buffer)) > 0)
				out.write(buffer,0,length);
			
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
		
		db.execSQL("create table myPicPDF(_slNo integer auto increment," +
											"docuName text primary key," +
											"docuPicPath text not null," +
											"created_at date default (datetime('now','localtime')));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("drop table if exists myPicPDF;");		
		onCreate(db);
	}

	//	storing inside myPicPDF table
	public boolean putinmyPicPDF(String docuName,String pRESENT_PICTURE_PATH) {
		
		ContentValues cValues = new ContentValues();
		
		if(checkForDuplicates(docuName)){
			
			cValues.put("docuName",docuName);
			cValues.put("docuPicPath", pRESENT_PICTURE_PATH);
			
			SQLiteDatabase db = this.getWritableDatabase();
		
			db.insert("myPicPDF",null,cValues);
			
			//	create another table to hold pictures
			db.execSQL("create table "+docuName+"(picNo text not null);");
			
			db.close();
			
			//	storing inside shared Preference
			mPreference.storeDocuName(docuName);
			
			//	creating a folder in
			
			return true;
		}		
		else			
			return false;		
	}

	//	checking for duplicates
	private boolean checkForDuplicates(String docuName) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from myPicPDF where docuName='"+docuName+"';",null);
		
		if(cursor.moveToFirst()){
				
			Toast.makeText(context,"You already registered \""+docuName+"!\"", Toast.LENGTH_LONG).show();
				
			cursor.close();
			db.close();
				
			return false;
		}
		//	cursor is not empty - data is not available there so allow to get data from user 
		else{
				
			cursor.close();
			db.close();
				
			return true;
		}
	}
	
	//	getting data for myPicPDF table - for loadListView();
	public List<MyListView> getFrommyPicPDF() {
		
		List<MyListView> list = new ArrayList<MyListView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query("myPicPDF", new String[] {"docuName","docuPicPath","created_at"}, null,null,null,null,"created_at"+" DESC");
		
		if(cursor.moveToFirst()){
			
			do {
				
				MyListView mList = new MyListView();
				
				mList.setItemName(cursor.getString(0));
				mList.setItemPicPath(cursor.getString(1));
				mList.setItemCreatedAt(cursor.getString(2));
				
				list.add(mList);
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return list;
	}
	
	//	getting Item - for updateListView();
	public MyListView getItem(String docuName) {
		
		MyListView mListView = new MyListView();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from myPicPDF where docuName='"+docuName+"';",null);
		
		if(cursor.moveToFirst()){
			
			mListView.setItemName(cursor.getString(1));
			mListView.setItemPicPath(cursor.getString(2));
			mListView.setItemCreatedAt(cursor.getString(3));
			
			cursor.close();
			db.close();
		}		
		return mListView;
	}
	
	//	deleting item
	public void deleteFrommyPicPDF(String docuName) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		//	deleting from myPicPDF
		db.delete("myPicPDF","docuName=?",new String[]{docuName});
		
		//	deleting the respective table
		db.execSQL("drop table if exists "+docuName+";");		
		
		db.close();
	}
}