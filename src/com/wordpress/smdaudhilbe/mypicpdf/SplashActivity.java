package com.wordpress.smdaudhilbe.mypicpdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		 //Remove title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				// This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                
                startActivity(i);
 
                // close this activity
                finish();
			}
		}, 5000);
	}	
}