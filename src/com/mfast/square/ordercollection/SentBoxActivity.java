package com.mfast.square.ordercollection;

import java.util.HashMap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SentBoxActivity extends FragmentActivity implements LoaderCallbacks<Cursor>
{
	private ListView itemListview;
	private ItemAdapter adapter;
	private ScrollManager mScrollManager;
	private LinearLayout optionsBar;
	private EditText search;
	private String mCurFilter;
	public static final String DATA_STRING="data";
	public HashMap<String,Integer> ProductQuantityMap = null;
	public HashMap<String,Double> ProductTotalPriceMap = null;
	public static final int SUMMARY=20; 
	public static String FORM_CONTENT;
	public static String[] orderFormatStrings;
	public static String[] orderId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(0, null, this);
		setContentView(R.layout.sentbox_layout);
		
		itemListview = (ListView) findViewById(R.id.dataBox_list);
		adapter = new ItemAdapter(null);
		itemListview.setAdapter(adapter);
		itemListview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) 
			{
				final String formatString = orderFormatStrings[position];
				final String orderID = orderId[position];
				String str = formatString.substring(formatString.indexOf(' '),formatString.indexOf('|'));
				final String[] array = str.split(",");
				AlertDialog.Builder alert = new AlertDialog.Builder(SentBoxActivity.this);
				alert.setTitle("Order Sending from SentBox");
				alert.setMessage("You are about to open an order of GrandTotal: "
					+array[3]+"\nWhat do you want to do?");
				alert.setCancelable(true);
				alert.setPositiveButton("Open",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
//						Intent intent = new Intent(SentBoxActivity.this,DirectedProductsActitivity.class);
//						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
//						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
//						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.SentBoxOrder));
//						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
						Intent intent = new Intent(SentBoxActivity.this,OrderSelectionActivity.class);
						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.DraftOrder));
						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
					}
				});
				alert.setNeutralButton("Delete",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
						AlertDialog.Builder Deletealert = new AlertDialog.Builder(SentBoxActivity.this);
						Deletealert.setTitle("Order Deletion from SentBox");
						Deletealert.setMessage("You are about to delete this order\nDo you want to proceed?");
						Deletealert.setCancelable(true);
						Deletealert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								MySQLiteHelper.getInstance(SentBoxActivity.this).deleteFromSentBox(orderID);
								getSupportLoaderManager().restartLoader(0, null, SentBoxActivity.this);
							}
						});						
						Deletealert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
						Deletealert.show();
						///delete from SentBox
					}
				});
				alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				alert.show();	
			}
		});
//		itemListview.setOnItemLongClickListener(new OnItemLongClickListener()
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,long arg3)
//			{
//				final String formatString = orderFormatStrings[position];
//				final String orderID = orderId[position];
//				String str = formatString.substring(formatString.indexOf(' '),formatString.indexOf('|'));
//				final String[] array = str.split(",");
//				AlertDialog.Builder alert = new AlertDialog.Builder(SentBoxActivity.this);
//				alert.setTitle("Orders from SentBox");
//				alert.setMessage("You are about to open an order of GrandTotal: "
//					+array[3]+"\nWhat do you want to do?");
//				alert.setCancelable(true);
//				alert.setPositiveButton("Open",new DialogInterface.OnClickListener() 
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						dialog.cancel();
//						Intent intent = new Intent(SentBoxActivity.this,DirectedProductsActitivity.class);
//						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
//						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
//						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.SentBoxOrder));
//						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
//					}
//				});
//				alert.setNeutralButton("Delete",new DialogInterface.OnClickListener() 
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						dialog.cancel();
//						AlertDialog.Builder Deletealert = new AlertDialog.Builder(SentBoxActivity.this);
//						Deletealert.setTitle("Order Deletion from SentBox");
//						Deletealert.setMessage("You are about to delete this order\nDo you want to proceed?");
//						Deletealert.setCancelable(true);
//						Deletealert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
//						{
//							@Override
//							public void onClick(DialogInterface dialog, int which) 
//							{
//								dialog.cancel();
//								MySQLiteHelper.getInstance(SentBoxActivity.this).deleteFromSentBox(orderID);
//								getSupportLoaderManager().restartLoader(0, null, SentBoxActivity.this);
//							}
//						});						
//						Deletealert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
//						{
//							@Override
//							public void onClick(DialogInterface dialog, int which)
//							{
//								// TODO Auto-generated method stub
//								dialog.cancel();
//							}
//						});
//						Deletealert.show();
//						///delete from SentBox
//					}
//				});
//				alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						// TODO Auto-generated method stub
//						dialog.cancel();
//					}
//				});
//				alert.show();
//				return true;
//			}
//		});
		mScrollManager=new ScrollManager(adapter);
		itemListview.setOnScrollListener(mScrollManager);
		optionsBar=(LinearLayout) findViewById(R.id.options_bar);
		optionsBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				SentBoxActivity.this.openOptionsMenu();	
			}
		});	
	}
	private class SearchTextWatcher implements TextWatcher
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) 
		{
			onQueryTextChange(search.getText().toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) 
		{
			// TODO Auto-generated method stub
		}
		@Override
		public void afterTextChanged(Editable s) 
		{
			// TODO Auto-generated method stub
		}
	}
	public boolean onQueryTextChange(String newText)
	{
		String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
		if (mCurFilter == null && newFilter == null) 
		{
			return true;
		}
		if (mCurFilter != null && mCurFilter.equals(newFilter)) 
		{
			return true;
		}
		mCurFilter = newFilter;
		getSupportLoaderManager().restartLoader(0, null, this);
		return true;
	}
	private class ItemAdapter extends CursorAdapter
	{	
		public ItemAdapter(Cursor c) 
		{
			super(SentBoxActivity.this, c, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) 
		{	
			final ViewHolder holder = (ViewHolder) view.getTag();
			final String CustomerName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CustomerValueColumn));
			final String OrderId = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn));
			final String date = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.dateColumn));
			final String OrderChannel = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderChannelColumn));
			final String FormatString = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.FormatStringColumn));
			final String IsMultipleSMS = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.IsMultipleSMSColumn));
			String str = FormatString.substring(FormatString.indexOf(' '),FormatString.indexOf('|'));
			String[] array = str.split(",");
			if(array[1].equals("1"))
				holder.Session.setText(date+"(Evening)");
			else
				holder.Session.setText(date+"(Morning)");
			holder.CustomerName.setText(CustomerName);
			if(IsMultipleSMS.equals("0"))
				holder.OrderId.setText(OrderId);
			else
				holder.OrderId.setText("Multiple SMS");
			holder.GrandTotal.setText(array[3]);
			String uri=null;
			if(OrderChannel.equals(context.getResources().getString(R.string.SMS)))
				uri = "drawable/sms";
			else if(OrderChannel.equals(context.getResources().getString(R.string.GPRS)))
				uri = "drawable/gprs";
			int imageResource = view.getContext().getApplicationContext().getResources().getIdentifier(uri, null, 
					view.getContext().getApplicationContext().getPackageName());
			Drawable image = view.getContext().getResources().getDrawable(imageResource);
			holder.image.setImageDrawable(image);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) 
		{
			View rowLayout=getLayoutInflater().inflate(R.layout.sentbox_list_row, null);
			ViewHolder holder = new ViewHolder();
			
			holder.OrderId = (TextView)rowLayout.findViewById(R.id.tvCity);
			holder.GrandTotal = (TextView)rowLayout.findViewById(R.id.tvTemp);
			holder.Session = (TextView)rowLayout.findViewById(R.id.tvCustName);
			holder.image = (ImageView)rowLayout.findViewById(R.id.list_image);
			holder.CustomerName = (TextView)rowLayout.findViewById(R.id.tvCondition);
			//holder.ProductQuantity=(EditText) rowLayout.findViewById(R.id.item_quantity_editext);
			rowLayout.setTag(holder);
			return rowLayout;
		}
	}
	
	private static class ViewHolder 
	{	
		TextView OrderId;
		TextView Session;
		TextView GrandTotal;
		TextView CustomerName;
		
		ImageView image;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		// TODO Auto-generated method stub
			return new SQLiteCursorLoader(this) 
			{
			
			@Override
			public Cursor loadInBackground() 
			{	
				Cursor cursor=null;
				try 
				{	
					if (search != null)
						mCurFilter = search.getText().toString();
					if (mCurFilter != null && !mCurFilter.equals("")) 
					{
						
						/**
						 * Will have to pass mCurFilter as an argument to create a new Cursor which contains
						 * the Filtered result
						 */
						cursor = MySQLiteHelper.getInstance(getContext()).getSentBox(mCurFilter);
						
					} 
					else 
					{
						cursor = MySQLiteHelper.getInstance(getContext()).getSentBox();
					}
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				if (cursor != null) 
				{
					cursor.getCount();	
				}
				return cursor;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) 
	{
		cursor.moveToFirst();
		if(orderId==null||orderId.length!=cursor.getCount())
		{
			orderFormatStrings = new String[cursor.getCount()];
			orderId = new String[cursor.getCount()];
			int i=0;
			if(cursor.moveToFirst())
			{
				do
				{
					orderId[i] = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn));
					orderFormatStrings[i] = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.FormatStringColumn));
					i++;
				}
				while(cursor.moveToNext());
			}
		}
		adapter.changeCursor(cursor);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		adapter.changeCursor(null);	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sentbox, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
	    switch (item.getItemId()) 
	    {
	        case R.id.menu_back:
	        	SentBoxActivity.this.finish();
	            return true;
	        case R.id.menu_clear:
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Clear SentBox");
				alert.setMessage("You are about to clear the SentBox\nDo you want to proceed?");
				alert.setCancelable(true);
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						MySQLiteHelper.getInstance(SentBoxActivity.this).clearSentBox();
						getSupportLoaderManager().restartLoader(0, null, SentBoxActivity.this);
					}
				});
				alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});
				alert.show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void refreshCursor()
	{
		getSupportLoaderManager().restartLoader(0, null, this);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		refreshCursor();
	}
}