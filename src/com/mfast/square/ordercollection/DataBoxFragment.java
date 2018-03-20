package com.mfast.square.ordercollection;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
public class DataBoxFragment extends Fragment
{
	private ListView list;
	private DataBoxAdapter adapter;
	private String[] count;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	public void onDataSetChanged()
	{
		this.adapter.notifyDataSetChanged();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View layout = inflater.inflate(R.layout.databox, container, false);
		list = (ListView) layout.findViewById(R.id.dataBox_list);
		adapter = new DataBoxAdapter(inflater);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) 
			{
				// TODO Auto-generated method stub
				Intent intent=null;
				switch(position)
				{
				case 0:
					intent = new Intent(getActivity(), DraftsActivity.class);
					break;
				case 1:
					//intent = new Intent(getActivity(), InboxActivity.class);
					break;
				case 2:
					intent = new Intent(getActivity(), SentBoxActivity.class);
					break;
				}
				if(intent!=null)
					startActivityForResult(intent, Integer.parseInt(getResources().getString(R.string.NAVIGATE)));
			}
		});
		return layout;
	}
	private class DataBoxAdapter extends BaseAdapter
	{
		ViewHolder holder;
		LayoutInflater inflater;
		public DataBoxAdapter(LayoutInflater inflater)
		{
			this.inflater = inflater;
		}
		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return 3;
		}
		@Override
		public Object getItem(int position) 
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			// TODO Auto-generated method stub
			View view = convertView;
			if(convertView==null)
			{
				view = inflater.inflate(R.layout.databox_list_row, null);
				holder = new ViewHolder();
				holder.BoxName = (TextView)view.findViewById(R.id.tvCity);
				holder.TotalMessage = (TextView)view.findViewById(R.id.tvTemp);
				holder.UnreadMessage = (TextView)view.findViewById(R.id.tvCondition);
				holder.image = (ImageView)view.findViewById(R.id.list_image);
				view.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)view.getTag();
			}
			count = MySQLiteHelper.getInstance(getActivity()).getRowCount();
			switch(position)
			{
			case 0:
				holder.BoxName.setText("Drafts");
				holder.TotalMessage.setText(count[position]);
				holder.UnreadMessage.setText("0 Unread");
				String uri = "drawable/drafts";
				int imageResource = view.getContext().getApplicationContext().getResources().getIdentifier(uri, null, 
						view.getContext().getApplicationContext().getPackageName());
				Drawable image = view.getContext().getResources().getDrawable(imageResource);
				holder.image.setImageDrawable(image);
				break;
			case 1:
				holder.BoxName.setText("Inbox");
				holder.TotalMessage.setText(count[position]);
				holder.UnreadMessage.setText("0 Unread");
				uri = "drawable/inbox";
				imageResource = view.getContext().getApplicationContext().getResources().getIdentifier(uri, null, 
						view.getContext().getApplicationContext().getPackageName());
				image = view.getContext().getResources().getDrawable(imageResource);
				holder.image.setImageDrawable(image);
				break;
			case 2:
				holder.BoxName.setText("SentBox");
				holder.TotalMessage.setText(count[position]);
				holder.UnreadMessage.setText("0 Unread");
				uri = "drawable/sentbox";
				imageResource = view.getContext().getApplicationContext().getResources().getIdentifier(uri, null, 
						view.getContext().getApplicationContext().getPackageName());
				image = view.getContext().getResources().getDrawable(imageResource);
				holder.image.setImageDrawable(image);
				break;
			}
			return view;
		}
	}
	private static class ViewHolder 
	{	
		TextView BoxName;
		TextView TotalMessage;
		TextView UnreadMessage;
		
		ImageView image;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		System.out.println("DataSet Changed");
		super.onActivityResult(requestCode, resultCode, data);
		this.adapter.notifyDataSetChanged();
	}
}