package com.wordpress.smdaudhilbe.mypicpdf.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.smdaudhilbe.mypicpdf.R;
import com.wordpress.smdaudhilbe.mypicpdf.model.MyListView;

public class MyItemAdapter extends ArrayAdapter<MyListView> {

	private static Context context;
	private ArrayList<MyListView> listOfItem;

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	public MyItemAdapter(Context context, int resource,ArrayList<MyListView> aList) {
		super(context, resource,aList);
		
		this.context = context;
		listOfItem = aList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		ViewHolder vHolder;
	
		if(convertView == null){
			
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.my_list_view_item,null);
			
			vHolder = new ViewHolder();
			
			vHolder.dName = (TextView)convertView.findViewById(R.id.docuName);
			vHolder.dateNTime = (TextView)convertView.findViewById(R.id.docuDateNTime);
			
			vHolder.iView = (ImageView)convertView.findViewById(R.id.docuPic);
			
			convertView.setTag(vHolder);
		}
		
		else
			vHolder = (ViewHolder)convertView.getTag();
		
		
		//	My Listview
		MyListView mListView = (MyListView)listOfItem.get(position);
		
		vHolder.dName.setText(mListView.getItemName());
		vHolder.dateNTime.setText(mListView.getitemCreatedAt());
		
		if(vHolder.iView != null)
			Image.loadToView(mListView.getItemPicPath(),vHolder.iView);
		
		return convertView;
	}
	
	private static class Image{
		
		private static LruCache<String, Bitmap> mMemoryCache = null;
	    private static int cacheSize = 1024 * 1024 * 10;
	
	    private static class imageDownloaderTask extends AsyncTask<String,Void,Bitmap>{

	    	private ImageView mTarget;

	    	public imageDownloaderTask(ImageView target) {
	    		this.mTarget = target;
	    	}

	    	@Override
	    	protected void onPreExecute() {
	    		mTarget.setTag(this);
	    	}

	    	@SuppressLint("NewApi")
	    	@Override
	    	protected Bitmap doInBackground(String...urls) {
        	
	    		String url = urls[0];

	    		Bitmap result = null;

	    		if (url != null) {
	    			result = load(url);

	    			if (result != null) 
	    				mMemoryCache.put(url,result);	    			
	    		}
	    		return result;
	    	}

	    	@Override
	    	protected void onPostExecute(Bitmap result) {
        	
	    		if(mTarget.getTag() == this) {
	    			mTarget.setTag(null);
                
	    			if(result != null)
	    				mTarget.setImageBitmap(result);
                
	    		} else if (mTarget.getTag() != null) {
            	
	    			((imageDownloaderTask) mTarget.getTag()).cancel(true);
	    			mTarget.setTag(null);
	    		}
	    	}
	    }

	    public static Bitmap load(String urlString) {
    	
	    	BitmapFactory.Options options = new BitmapFactory.Options();
        
	    	// downsizing image as it throws OutOfMemory Exception for larger
	    	// images
	    	options.inSampleSize = 16;
	
	    	Bitmap bitmap =  BitmapFactory.decodeFile(Uri.parse(urlString).getPath(),options);
        
	    	if (bitmap == null)
	    		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.list_placeholder);

	    	return bitmap;
	    }

	    @SuppressLint("NewApi")
		public static void loadToView(String url,ImageView view) {
    	
	    	if (url == null || url.length() == 0) 
	    		return;
        
	    	if (mMemoryCache == null) {
        	
	    		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	    			
	    			@Override
	    			protected int sizeOf(String key, Bitmap bitmap) {
	    				return (bitmap.getRowBytes() * bitmap.getHeight());
	    			}
	    		};
	    	}

	    	Bitmap bitmap = getBitmapFromMemCache(url);
	    	
	    	if (bitmap == null) {
	    		
	    		final imageDownloaderTask task = (imageDownloaderTask) new imageDownloaderTask(view);
	    		
	    		view.setTag(task);
	    		view.setRotation(90);
	    		
	    		task.execute(url);	    		
	    	} else {
            	view.setImageBitmap(bitmap);
            	view.setRotation(90);
	    	}
	    }
	    
//		getting Bitmap from memory cache
		@SuppressLint("NewApi")
		public static Bitmap getBitmapFromMemCache(String url) {
			return (Bitmap)mMemoryCache.get(url); 
	    }
	}
	
	//	ViewHolder
	class ViewHolder{
		
		TextView dName;
		TextView dateNTime;
		ImageView iView;
	}
}