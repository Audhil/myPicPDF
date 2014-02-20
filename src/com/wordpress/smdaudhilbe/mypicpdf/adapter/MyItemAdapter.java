package com.wordpress.smdaudhilbe.mypicpdf.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
	
		if(convertView == null){
			
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.my_list_view_item,null);
		}
		
		TextView dName = (TextView)convertView.findViewById(R.id.docuName);
		TextView dDateNTime = (TextView)convertView.findViewById(R.id.docuDateNTime);
		
		ImageView dIView = (ImageView)convertView.findViewById(R.id.docuPic);
		
		dName.setText(listOfItem.get(position).getItemName());
		dDateNTime.setText(listOfItem.get(position).getitemCreatedAt());
		
        BitmapFactory.Options options = new BitmapFactory.Options();
        
        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 16;
		
		dIView.setImageBitmap(BitmapFactory.decodeFile(Uri.parse(listOfItem.get(position).getItemPicPath()).getPath(),options));
		
		dIView.setRotation(90);
		
		return convertView;
	}
}