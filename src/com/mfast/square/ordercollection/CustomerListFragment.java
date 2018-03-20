package com.mfast.square.ordercollection;

import java.util.Locale;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerListFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ListView customerListview;
	private CustomerAdapter adapter;
	private EditText search;
	private String mCurFilter;
	private TextView totalCustomers;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		getLoaderManager().initLoader(0, null, this);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{	
		View layout=inflater.inflate(R.layout.activity_customer_selection, container, false);
		
		search = (EditText) layout.findViewById(R.id.searchText);
		search.addTextChangedListener(new SearchTextWatcher());
		customerListview = (ListView)layout.findViewById(R.id.customer_list);
		totalCustomers = (TextView)layout.findViewById(R.id.total_customer);
		totalCustomers.setText(String.valueOf(MySQLiteHelper.getInstance(getActivity()).getCustomers().getCount()));
		adapter = new CustomerAdapter(null);
		customerListview.setAdapter(adapter);
		return layout;
	}
	
	
	private class SearchTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			onQueryTextChange(search.getText().toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	}
	
	public boolean onQueryTextChange(String newText) {
		String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
		if (mCurFilter == null && newFilter == null) {
			return true;
		}
		if (mCurFilter != null && mCurFilter.equals(newFilter)) {
			return true;
		}
		mCurFilter = newFilter;
		getLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new SQLiteCursorLoader(getActivity()) {

			@Override
			public Cursor loadInBackground() {

				Cursor cursor = null;
				try {
					if (search != null)
						mCurFilter = search.getText().toString();
					if (mCurFilter != null && !mCurFilter.equals("")) {
						cursor = MySQLiteHelper.getInstance(getContext())
								.getCustomersFiltered(mCurFilter);
					} else {
						cursor = MySQLiteHelper.getInstance(getContext())
								.getCustomers();
					}
					
				} catch (SQLException e) {
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
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) 
	{
		// TODO Auto-generated method stub
		adapter.changeCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.changeCursor(null);
	}
	
	
	private static class ViewHolder {
		LinearLayout headerLayout;
		TextView header;
		TextView customerName;
		TextView customerAdress;
		TextView customerId;
		LinearLayout customerInfo;
	}
	
	private class CustomerAdapter extends CursorAdapter{
		
		public CustomerAdapter(Cursor c) {
			super(getActivity(), c, false);
			

		}

		
		public boolean isHeader(Cursor cursor) {
			int position = cursor.getPosition();
			String name = cursor.getString(cursor
					.getColumnIndex(MySQLiteHelper.CustomerValueColumn));
			String prevName = "";
			if (cursor.moveToPosition(position - 1)) {
				prevName = cursor
						.getString(cursor
								.getColumnIndex(MySQLiteHelper.CustomerValueColumn));
			}
			cursor.moveToPosition(position);

			if (prevName.equalsIgnoreCase("")
					|| (Character.isLetter(name.toUpperCase(Locale.ENGLISH)
							.charAt(0)) && !name
							.toUpperCase(Locale.ENGLISH)
							.substring(0, 1)
							.equalsIgnoreCase(
									prevName.toUpperCase(Locale.ENGLISH)
											.substring(0, 1)))) {
				return true;
			}
			return false;
		}
		
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) 
		{	
			ViewHolder holder = (ViewHolder) view.getTag();
			String cId=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CustomerIdColumn));
			final String cName=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CustomerValueColumn));
			String cAddress=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CustomerAddressColumn));
			if (isHeader(cursor)) {
				holder.headerLayout.setVisibility(View.VISIBLE);
				if (Character.isLetter(cName.charAt(0))) {
					holder.header.setText(cName.substring(0, 1).toUpperCase());
				} else {
					holder.header.setText("#");
				}
			} else {
				holder.headerLayout.setVisibility(View.GONE);
			}
			holder.customerName.setText(cName);
			holder.customerAdress.setText(cAddress);
			holder.customerId.setText(cId);
			
			holder.header.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			holder.customerInfo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) 
		{
			View rowLayout=getActivity().getLayoutInflater().inflate(
					R.layout.customer_selection_row, null);
			ViewHolder holder = new ViewHolder();
			
			holder.customerName=(TextView) rowLayout.findViewById(R.id.customer_name);
			holder.header=(TextView) rowLayout.findViewById(R.id.header);
			holder.headerLayout=(LinearLayout) rowLayout.findViewById(R.id.header_layout);
			holder.customerAdress=(TextView) rowLayout.findViewById(R.id.customer_address);
			holder.customerId=(TextView) rowLayout.findViewById(R.id.customer_id);
			holder.customerInfo=(LinearLayout) rowLayout.findViewById(R.id.customer_info);
			
			rowLayout.setTag(holder);
			return rowLayout;
		}
	}
}
