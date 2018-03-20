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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ProductListFragment extends Fragment implements LoaderCallbacks<Cursor>{

	private ListView itemListview;
	private ItemAdapter adapter;
	private TextView totalProducts;
	private EditText search;
	private String mCurFilter;
	private LinearLayout optionsBar;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		getLoaderManager().initLoader(0, null, this);	
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View layout=inflater.inflate(R.layout.activity_product_selection, container, false);
		
		itemListview = (ListView) layout.findViewById(R.id.product_list);
		adapter = new ItemAdapter(null);
		itemListview.setAdapter(adapter);
		totalProducts = (TextView) layout.findViewById(R.id.total_product);
		totalProducts.setText(String.valueOf(MySQLiteHelper.getInstance(getActivity()).getProducts().getCount()));
		search = (EditText) layout.findViewById(R.id.searchText);
		search.addTextChangedListener(new SearchTextWatcher());
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
	
	
	private class ItemAdapter extends CursorAdapter{
		
		public ItemAdapter(Cursor c) {
			super(getActivity(), c, false);
			

		}
		public boolean isHeader(Cursor cursor) {
			int position = cursor.getPosition();
			String name = cursor.getString(cursor
					.getColumnIndex(MySQLiteHelper.ProductValueColumn));
			String prevName = "";
			if (cursor.moveToPosition(position - 1)) {
				prevName = cursor
						.getString(cursor
								.getColumnIndex(MySQLiteHelper.ProductValueColumn));
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
		public void bindView(View view, Context context, final Cursor cursor) 
		{
			final ViewHolder holder = (ViewHolder) view.getTag();
			
			final String itemName=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductValueColumn));
			final String itemUnitPrice=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitPrice));
			final String itemUnitvat=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitVat));
			if(isHeader(cursor))
			{
				holder.headerLayout.setVisibility(View.VISIBLE);
				if (Character.isLetter(itemName.charAt(0))) {
					holder.header.setText(itemName.substring(0, 1).toUpperCase());
				} else {
					holder.header.setText("#");
				}
			}
			else
			{
				holder.headerLayout.setVisibility(View.GONE);
			}
			holder.itemName.setText(itemName);
			
			holder.itemUnitPrice.setText(itemUnitPrice);
			holder.itemUnitvat.setText(itemUnitvat);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View rowLayout=getActivity().getLayoutInflater().inflate(
					R.layout.product_list_row_layout, null);
			ViewHolder holder = new ViewHolder();
			holder.header=(TextView) rowLayout.findViewById(R.id.header);
			holder.headerLayout=(LinearLayout) rowLayout.findViewById(R.id.header_layout);
			holder.itemName=(TextView) rowLayout.findViewById(R.id.product_name);
			holder.itemUnitPrice=(TextView) rowLayout.findViewById(R.id.unit_price);
			holder.itemUnitvat=(TextView) rowLayout.findViewById(R.id.unit_vat);
			holder.productInfo=(LinearLayout) rowLayout.findViewById(R.id.product_info);
			rowLayout.setTag(holder);
			return rowLayout;
		}
	}
	private static class ViewHolder 
	{
		LinearLayout headerLayout;
		TextView header;
		LinearLayout productInfo;
		TextView itemName;
		TextView itemUnitPrice;
		TextView itemUnitvat;	
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
						
						/**
						 * Will have to pass mCurFilter as an argument to create a new Cursor which contains
						 * the Filtered result
						 */
						cursor = MySQLiteHelper.getInstance(getContext()).getProductsFiltered(mCurFilter);
					} 
					else 
					{
						cursor = MySQLiteHelper.getInstance(getContext()).getProducts();
					}	
				}catch (SQLException e) {
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
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		
		adapter.changeCursor(arg1);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);	
	}
}