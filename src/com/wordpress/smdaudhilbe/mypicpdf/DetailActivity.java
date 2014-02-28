package com.wordpress.smdaudhilbe.mypicpdf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class DetailActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_screen);
		
		//	enabling Up / Back navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getIntent().getExtras().getString("ActionBarTitleName"));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}
}