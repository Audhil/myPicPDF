package com.wordpress.smdaudhilbe.mypicpdf;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.wordpress.smdaudhilbe.mypicpdf.adapter.MyItemAdapter;
import com.wordpress.smdaudhilbe.mypicpdf.database.DatabaseConnectivity;
import com.wordpress.smdaudhilbe.mypicpdf.model.MyListView;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener {

    private ImageButton cameraBtn;
	private ListView recordList;
	private Uri fileUri;
	private boolean finishApp = false;
	private DatabaseConnectivity dbConnectivity;
	private List<MyListView> items;
	private ArrayList<MyListView> freshItems;
	private MyItemAdapter iAdapter;
	
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final String IMAGE_DIRECTORY_NAME = "myPicPDF";
	
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static String PATH = "";
    
    public static String PRESENT_PICTURE_PATH = "";
    static int i = 0;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dbConnectivity = new DatabaseConnectivity(getApplicationContext());
        
        initViewsWithListener();
        
        // 	check whether pdf reader is available or not
        if(!checkForPDFReader()){
        	
        	new AlertDialog.Builder(MainActivity.this)
        	.setTitle("myPicPDF")
        	.setMessage("Please install any PDF reader to proceed further ...")
        	.setCancelable(false)
        	.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
        	.show();
        }   
        //	PDF reader is available
        else{
        	
        	PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+IMAGE_DIRECTORY_NAME+"/images";
        	loadListView();
        	
        	Log.d("I am at MainActivity onCreate", "loadListView() executed ... "+i++);
        }
    }

	//	checking for PDFreader
	private boolean checkForPDFReader() {
		
		PackageManager packageManager = getPackageManager();
		
	    Intent testIntent = new Intent(Intent.ACTION_VIEW);
	    testIntent.setType(MIME_TYPE_PDF);
	    
	    if (packageManager.queryIntentActivities(testIntent,PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
	        return true;
	    
	    else
	        return false;
	}

	//	initViews
    private void initViewsWithListener() {
    	
		cameraBtn = (ImageButton)findViewById(R.id.cameraButton);
		recordList = (ListView)findViewById(R.id.recordList);
		
		cameraBtn.setOnClickListener(this);
		recordList.setOnItemClickListener(this);
		
		//	freshItems
		freshItems = new ArrayList<MyListView>();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	//	to launch camera activity
	@Override
	public void onClick(View view) {
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		fileUri = getOutputMediaFileUri();
		 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		 
		// start the image capture Intent
		startActivityForResult(intent,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	private Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	private File getOutputMediaFile() {

		// External sdcard location
	    File mediaStorageDir = new File(PATH);
	 
	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()) {
	    	
	        if (!mediaStorageDir.mkdirs()) {   	
	            Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "+ IMAGE_DIRECTORY_NAME + " directory");
	            return null;
	        }
	    }
	    
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
	    
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "IMG_" + timeStamp + ".jpg");
        
        PRESENT_PICTURE_PATH = mediaStorageDir.getPath()+ "/IMG_" + timeStamp + ".jpg";

		return mediaFile;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// if the result is capturing Image
	    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
	    
	    	if (resultCode == RESULT_OK) {
	            // successfully captured the image
	            // display it in image view
	            getNameOfItemAlertDialog();
	            
	        } else if (resultCode == RESULT_CANCELED) {
	            // user cancelled Image capture
	            Toast.makeText(getApplicationContext(),"User cancelled image capture", Toast.LENGTH_SHORT).show();
	            
	        } else {
	            // failed to capture image
	            Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	
	private void getNameOfItemAlertDialog() {
		
		final EditText eText = new EditText(MainActivity.this);
		eText.setHint("New Document");
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(20);
		eText.setFilters(fArray);
		
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(MainActivity.this)
											.setTitle("myPicPDF")
											.setMessage("Enter name for the document")
											.setView(eText)
											.setPositiveButton("Ok",null)
											.setNegativeButton("Cancel",null);
			
		final AlertDialog alert = aBuilder.create();
		alert.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				
				Button okButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
				
				okButton.setOnClickListener(new OnClickListener() {					

					@Override
					public void onClick(View v) {
						
						if(TextUtils.isEmpty(eText.getText()))
							Toast.makeText(MainActivity.this, "Insufficient name",Toast.LENGTH_LONG).show();
						
						//	to avoid spaces in between
						else if(eText.getText().toString().contains(" "))
							Toast.makeText(MainActivity.this, "Invalid name! Use single word as document name!",Toast.LENGTH_LONG).show(); 
						
						//	inserting name of document and creating sub table
						else if(!dbConnectivity.putinmyPicPDF(eText.getText().toString(),fileUri+""));

						else{
							loadListView();
							alert.cancel();							
						}
					}
				});
			
				Button cancelClicked = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
				
				cancelClicked.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						deletePicture(PRESENT_PICTURE_PATH);
						
						alert.cancel();
					}
				});
			}
		});
		
		//	to show keypad
		alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);		
		alert.show();
	}

	
	//	delete picture
	private void deletePicture(String pRESENT_PICTURE_PATH) {
		
		boolean val = new File(pRESENT_PICTURE_PATH).delete();
		
        Log.d("val", val+"");
	}
	
	//	loading listview
	private void loadListView() {
		
		items = dbConnectivity.getFrommyPicPDF();
		
		for(MyListView mItem : items) {
			freshItems.add(mItem);
		}
		
		iAdapter = new MyItemAdapter(MainActivity.this,0,freshItems); 
		
		recordList.setAdapter(iAdapter);
		
		//	if no items inside listview
		if(iAdapter.isEmpty()){
			View emptyView = (View)findViewById(R.id.emptyTView);
			recordList.setEmptyView(emptyView);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// save file url in bundle as it will be null on scren orientation
	    // changes
	    outState.putParcelable("file_uri", fileUri);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		// get the file url
	    fileUri = savedInstanceState.getParcelable("file_uri");
	}
	
	@Override
	public void onBackPressed() {

		if(finishApp){
			finish();
			
			Log.d("onBackPressed",""+finishApp);
		}
		
		else{
			Toast.makeText(getApplicationContext(),"Tap back again to exit",Toast.LENGTH_LONG).show();
			finishApp = true;
		}
		
		//	countdowntimer - 2 secs
		new CountDownTimer(2000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
			
			@Override
			public void onFinish() {
				finishApp = false;
				Log.d("countDownTimer",""+finishApp);
			}
		}.start();
	}
}