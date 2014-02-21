package com.wordpress.smdaudhilbe.mypicpdf.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.smdaudhilbe.mypicpdf.R;
import com.wordpress.smdaudhilbe.mypicpdf.model.MyListView;

public class MyItemAdapter extends ArrayAdapter<MyListView> {

	private Context context;
	private ArrayList<MyListView> listOfItem;

	public MyItemAdapter(Context context, int resource,ArrayList<MyListView> aList) {
		super(context, resource,aList);
		
		this.context = context;
		listOfItem = aList;
	}
	
	@SuppressLint("NewApi")
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
			new imageDownloaderTask(vHolder.iView).execute(mListView.getItemPicPath());
		
		return convertView;
	}
	
	private class imageDownloaderTask extends AsyncTask<String,Void,Bitmap>{

		WeakReference<ImageView> iViewRef;
		
		public imageDownloaderTask(ImageView iView) {			
			iViewRef = new WeakReference<ImageView>(iView); 
		}

		@Override
		protected Bitmap doInBackground(String... params) {			
			return getBitmapImage(params[0]);
		}
		
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Bitmap bitmap) {		
			super.onPostExecute(bitmap);
			
			if(isCancelled()){
				bitmap = null;
			}
			
			if(iViewRef != null){
				
				ImageView imageView = iViewRef.get();
				
				if(imageView != null){
					
					if (bitmap != null){ 						
						imageView.setImageBitmap(bitmap);
						imageView.setRotation(90);
					}
					
					else{
						imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.list_placeholder));
						imageView.setRotation(90);
					}
				}
			}
		}

		//	getting bitmap image
		private Bitmap getBitmapImage(String itemPicPath) {

			BitmapFactory.Options options = new BitmapFactory.Options();
	        
	        // downsizing image as it throws OutOfMemory Exception for larger
	        // images
	        options.inSampleSize = 16;
		
			return BitmapFactory.decodeFile(Uri.parse(itemPicPath).getPath(),options);
		}
	}
	
	//	ViewHolder
	static class ViewHolder{
		TextView dName;
		TextView dateNTime;
		ImageView iView;
	}
}