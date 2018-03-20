package com.mfast.square.ordercollection;

import java.util.HashMap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DraftsActivity extends FragmentActivity implements LoaderCallbacks<Cursor>
{
	private ListView itemListview;
	private ItemAdapter adapter;
	private ScrollManager mScrollManager;
	private LinearLayout optionsBar;
	private EditText search;
	private String mCurFilter;
	public static final String DATA_STRING="data";
	public static final String QueryString="queryString";
	public HashMap<String,Boolean> map;
	public static String[] orderFormatStrings;
	public static String[] orderId;
	
	
	public void refreshCursor()
	{
		getSupportLoaderManager().restartLoader(0, null, this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(0, null, this);
		setContentView(R.layout.drafts_layout);
		map = null;
		itemListview = (ListView) findViewById(R.id.dataBox_list);
//		itemListview.setOnItemLongClickListener(new OnItemLongClickListener()
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				// TODO Auto-generated method stub
//				final String formatString = orderFormatStrings[position];
//				final String orderID = orderId[position];
//				String str = formatString.substring(formatString.indexOf(' '),formatString.indexOf('|'));
//				final String[] array = str.split(",");
//				AlertDialog.Builder alert = new AlertDialog.Builder(DraftsActivity.this);
//				alert.setTitle("Order Sending from Drafts");
//				alert.setMessage("You are about to send an order of GrandTotal: "
//						+array[3]+"\nDo you want to proceed?");
//				alert.setCancelable(true);
//				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
//				{
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						dialog.cancel();
//						Intent intent = new Intent(DraftsActivity.this,DirectedProductsActitivity.class);
//						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
//						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
//						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.DraftOrder));
//						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
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
		itemListview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) 
			{
				final String formatString = orderFormatStrings[position];
				final String orderID = orderId[position];
				String str = formatString.substring(formatString.indexOf(' '),formatString.indexOf('|'));
				final String[] array = str.split(",");
				AlertDialog.Builder alert = new AlertDialog.Builder(DraftsActivity.this);
				alert.setTitle("Order Sending from Drafts");
				alert.setMessage("You are about to open an order of GrandTotal: "
						+array[3]+"\nDo you want to proceed?");
				alert.setCancelable(true);
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
//						Intent intent = new Intent(DraftsActivity.this,DirectedProductsActitivity.class);
//						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
//						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
//						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.DraftOrder));
//						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
						Intent intent = new Intent(DraftsActivity.this,OrderSelectionActivity.class);
						intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formatString);
						intent.putExtra(getResources().getString(R.string.ORDER_ID), orderID);
						intent.putExtra(getResources().getString(R.string.OrderIndex), getResources().getString(R.string.DraftOrder));
						startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
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
		adapter = new ItemAdapter(null);
		itemListview.setAdapter(adapter);
		mScrollManager=new ScrollManager(adapter);
		itemListview.setOnScrollListener(mScrollManager);
		optionsBar=(LinearLayout) findViewById(R.id.options_bar);
		optionsBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				DraftsActivity.this.openOptionsMenu();	
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
			super(DraftsActivity.this, c, false);
		}
		@Override
		public void bindView(View view, Context context, final Cursor cursor) 
		{	
			final ViewHolder holder = (ViewHolder) view.getTag();
			final String customerName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CustomerValueColumn));
			final String orderId = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn));
			final String date = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.dateColumn));
			final String FormatString = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.FormatStringColumn));
			String str = FormatString.substring(FormatString.indexOf(' '),FormatString.indexOf('|'));
			System.out.println("Format String: "+FormatString);
			final String[] array = str.split(",");
			if(array[1].equals("1"))
				holder.Session.setText(date+"(Evening)");
			else
				holder.Session.setText(date+"(Morning)");
			holder.CustomerId.setText(customerName);
			//holder.GrandTotal.setText("0.0");
			try
			{
				holder.GrandTotal.setText(array[3]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			holder.checkbox.setChecked(map.get(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn))));
			holder.checkbox.setOnClickListener(new View.OnClickListener() 
			{	
				@Override
				public void onClick(View v) 
				{
					if(((CheckBox)v).isChecked())
					{
						map.put(orderId, true);
					}
					else
					{
						map.put(orderId, false);
					}
				}
			});
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) 
		{
			View rowLayout = getLayoutInflater().inflate(R.layout.draft_list_row, null);
			ViewHolder holder = new ViewHolder();
			
			holder.CustomerId = (TextView)rowLayout.findViewById(R.id.tvCity);
			holder.GrandTotal = (TextView)rowLayout.findViewById(R.id.tvTemp);
			holder.Session = (TextView)rowLayout.findViewById(R.id.tvCondition);
			holder.checkbox = (CheckBox)rowLayout.findViewById(R.id.checkbox);
			//holder.image = (ImageView)rowLayout.findViewById(R.id.list_image);
			//holder.ProductQuantity=(EditText) rowLayout.findViewById(R.id.item_quantity_editext);
			rowLayout.setTag(holder);
			return rowLayout; 
		}
	}
	
	private static class ViewHolder 
	{	
		TextView CustomerId;
		TextView Session;
		TextView GrandTotal;
		
		CheckBox checkbox;
		//ImageView image;
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
						cursor = MySQLiteHelper.getInstance(getContext()).getDraftBox(mCurFilter);
						
					} 
					else 
					{
						cursor = MySQLiteHelper.getInstance(getContext()).getDraftBox();
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
		if((map==null)||(map.size()!=cursor.getCount()))
		{
			map = new HashMap<String,Boolean>();
			orderFormatStrings = new String[cursor.getCount()];
			orderId = new String[cursor.getCount()];
			int i=0;
			if(cursor.moveToFirst())
			{
				do
				{
					orderId[i] = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn));
					orderFormatStrings[i] = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.FormatStringColumn));
					map.put(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn)),false);
					i++;
				}
				while(cursor.moveToNext());
			}
		}
		else
		{
			if(cursor.moveToFirst())
			{
				do
				{
					map.put(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OrderIdColumn)),false);
				}
				while(cursor.moveToNext());
			}
		}
		adapter.changeCursor(cursor);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> cursor) 
	{
		adapter.changeCursor(null);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.draft, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	        case R.id.menu_back:
	        	DraftsActivity.this.finish();
	            return true;
	        case R.id.menu_select_all:
	        	System.out.println("Select All Menu Pressed");
	        	for(String key: map.keySet())
	            {
	            	map.put(key, true);
	            }
	        	adapter.notifyDataSetChanged();
	        	//getSupportLoaderManager().restartLoader(0, null, DraftsActivity.this);
	            return true;
	        case R.id.menu_delete_selected:
	        	int selected=0;
	        	StringBuffer strBuff = new StringBuffer();
	            for(String key: map.keySet())
	            {
	            	if(map.get(key))
	            	{
	            		selected++;
	            		strBuff.append("'"+key+"',");
	            	}
	            }
	            if(strBuff.length()!=0)
	            	strBuff.deleteCharAt(strBuff.length()-1);
	            final String ids = strBuff.toString();
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Order Deletion");
				if(selected==0)
				{
					alert.setMessage("You have not selected any order to be deleted");
					alert.setCancelable(true);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
				}
				else
				{
					alert.setMessage("You are about to delete " + selected
							+" Orders \nDo you want to proceed?");
					alert.setCancelable(true);
					System.out.println(ids);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							MySQLiteHelper.getInstance(DraftsActivity.this).deleteMultipleOrderFromDraftBox(ids);
							getSupportLoaderManager().restartLoader(0, null, DraftsActivity.this);
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
				}
				alert.show();
	            return true;
	        case R.id.menu_send_selected:
	        	selected=0;
	        	strBuff = new StringBuffer();
	            for(String key: map.keySet())
	            {
	            	if(map.get(key))
	            	{
	            		selected++;
	            		strBuff.append("'"+key+"',");
	            	}
	            }
	            if(strBuff.length()!=0)
	            	strBuff.deleteCharAt(strBuff.length()-1);
	            final String orderIds = strBuff.toString();
	        	alert = new AlertDialog.Builder(this);
				alert.setTitle("Mulitple Order Sending");
				if(selected==0)
				{
					alert.setMessage("You have not selected any order to be sent");
					alert.setCancelable(true);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
				}
				else
				{
					alert.setMessage("You are about to send " + selected
							+" Orders simultaneously\nDo you want to proceed?");
					alert.setCancelable(true);
					System.out.println(orderIds);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							final AlertDialog.Builder alert = new AlertDialog.Builder(DraftsActivity.this);
							alert.setTitle("Error");
							alert.setMessage("Not Implemented Yet");
							alert.setCancelable(true);
							alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
								}
							});	
							alert.show();
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
				}
				alert.show();
	            return true;
            default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		refreshCursor();
//		if(resultCode == Activity.RESULT_OK && requestCode == SUMMARY)
//		{
//			String dataString = data.getStringExtra(SummeryActivity.DATA_STRING);
//			String[] parts = dataString.split(";");
//			String[] rowParts;
//			for(int i=0;i<parts.length-1;i++)
//			{
//				rowParts = parts[i].split(",");
//				ProductQuantityMap.put(rowParts[0], Integer.parseInt(rowParts[1]));
//				ProductTotalPriceMap.put(rowParts[0], Double.parseDouble(rowParts[2]));
//			}
//			mGradToatalTextView.setText(parts[parts.length-1]);
//		}
	}
}