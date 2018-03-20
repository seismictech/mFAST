package com.mfast.square.ordercollection;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductSelectionFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ListView itemListview;
	private ItemAdapter adapter;
	private TextView mGradToatalTextView;
	private double grandTotal=0;
	private String[] editTextValues;
	private double[] itemRowValues;
	private ScrollManager mScrollManager;
	private LinearLayout optionsBar;
	private MatrixCursor summeryCursor=null;
	
	private EditText search;
	private String mCurFilter;
	
	private Cursor tempCursor=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		
		getLoaderManager().initLoader(0, null, this);
		hasOptionsMenu();
		super.onCreate(savedInstanceState);
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout=inflater.inflate(R.layout.fragment_product_selection_layout, container, false);
		
		itemListview = (ListView) layout.findViewById(R.id.item_list);
		adapter = new ItemAdapter(null);
		itemListview.setAdapter(adapter);
		mScrollManager=new ScrollManager(adapter);
		itemListview.setOnScrollListener(mScrollManager);
		
		mGradToatalTextView=(TextView) layout.findViewById(R.id.grand_total);
		
		optionsBar=(LinearLayout) layout.findViewById(R.id.options_bar);
		
		String[] summeryCursorColumns=new String[]{"_id","item","quantity","totalprice","value","vat"};
		summeryCursor=new MatrixCursor(summeryCursorColumns);
		optionsBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "Options", Toast.LENGTH_SHORT).show();
				
				for(int i=0;i<editTextValues.length;i++)
				{
					if(!editTextValues[i].equalsIgnoreCase("0"))
					{
//						tempCursor.moveToPosition(i);
//						
//						String id=tempCursor.getString(tempCursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));
//						String itemName=tempCursor.getString(tempCursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_NAME));
//						String itemValue=tempCursor.getString(tempCursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_VALUE));
//						String itemVat=tempCursor.getString(tempCursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_VAT));
//						
//						String itemQunatity=editTextValues[i];
//						String itemTotalPrice=String.valueOf(itemRowValues[i]);
//						
//						summeryCursor.addRow(new Object[]{id,itemName,itemQunatity,itemTotalPrice,itemValue,itemVat});
						
						
					}
				}
				
				
				
				
				
			}
		});
		
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

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			
			double itemTotalPriceVar=0;
			int itemQuantityVar=0;
			
			
			final ViewHolder holder = (ViewHolder) view.getTag();
			final int id=cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.ProductIdColumn));
			final String itemName=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductValueColumn));
			final String itemUnitPrice=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitPrice));
			final String itemUnitvat=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitVat));
			
			holder.itemName.setText(itemName);
			holder.columnId=cursor.getPosition();
			holder.itemUnitPrice.setText(itemUnitPrice);
			holder.itemUnitvat.setText(itemUnitvat);
			
			final double itemUnitPriceVar=Double.parseDouble(itemUnitPrice);
			
			
			
			if(mScrollManager.getScrollState()==AbsListView.OnScrollListener.SCROLL_STATE_FLING)
			{
				holder.itemQuantity.setText("");
				holder.itemTotalPrice.setText("");
			}
			else {
				if(editTextValues[holder.columnId].equalsIgnoreCase("0"))
				{
					holder.itemQuantity.setText("");
				}
				else {
					
					holder.itemQuantity.setText(editTextValues[holder.columnId]);
				}
				
				int length=holder.itemQuantity.getText().toString().length();
				
				if(length>0)
				{
					itemQuantityVar=Integer.parseInt(holder.itemQuantity.getText().toString());
					itemTotalPriceVar=itemUnitPriceVar*itemQuantityVar;
					holder.itemTotalPrice.setText(""+itemTotalPriceVar);
					
				}
				else {
					
					holder.itemTotalPrice.setText("");
				}
			}
			
			
			
			
			
			
			
			//holder.itemQuantity.addTextChangedListener(new SearchTextWatcher(itemUnitPriceVar, holder.itemQuantity, holder.itemTotalPrice,cursor.getPosition()));
			
			holder.itemQuantity.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					
					if(mScrollManager.getScrollState()!=AbsListView.OnScrollListener.SCROLL_STATE_FLING)
					{
						int length=holder.itemQuantity.getText().toString().length();
						if(length>0)
						{
							
							editTextValues[holder.columnId]=holder.itemQuantity.getText().toString();
							holder.itemTotalPrice.setText(""+(Integer.parseInt(holder.itemQuantity.getText().toString())*itemUnitPriceVar));
							itemRowValues[holder.columnId]=Integer.parseInt(holder.itemQuantity.getText().toString())*itemUnitPriceVar;
							
							grandTotal=0;
							for(int i=0;i<itemRowValues.length;i++)
							{
								grandTotal+=itemRowValues[i];
							}
							mGradToatalTextView.setText(""+ grandTotal);
						}
						else if (length==0) {
							editTextValues[holder.columnId]="0";
							itemRowValues[holder.columnId]=0.0;
							
							grandTotal=0;
							for(int i=0;i<itemRowValues.length;i++)
							{
								grandTotal+=itemRowValues[i];
							}
							mGradToatalTextView.setText(""+ grandTotal);
							
						}
					}
			
					
						
					
						
						
					
					
					
					
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
			});
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View rowLayout=getActivity().getLayoutInflater().inflate(
					R.layout.product_selection_list_row_layout, null);
			ViewHolder holder = new ViewHolder();
			
			holder.itemName=(TextView) rowLayout.findViewById(R.id.item_name_textview);
			holder.itemTotalPrice=(TextView) rowLayout.findViewById(R.id.item_total_value);
			holder.itemUnitPrice=(TextView) rowLayout.findViewById(R.id.item_unit_value);
			holder.itemUnitvat=(TextView) rowLayout.findViewById(R.id.item_unit_vat);
			
			holder.itemQuantity=(EditText) rowLayout.findViewById(R.id.item_quantity_editext);
			
			rowLayout.setTag(holder);
			return rowLayout;
		}
	}
	

	private static class ViewHolder {
		
		TextView itemName;
		TextView itemTotalPrice;
		TextView itemUnitPrice;
		TextView itemUnitvat;
		
		int columnId;
		
		EditText itemQuantity;
		
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new SQLiteCursorLoader(getActivity()) {
			
			@Override
			public Cursor loadInBackground() {
				
				Cursor cursor=null;
				try {
					
					if (search != null)
						mCurFilter = search.getText().toString();
					if (mCurFilter != null && !mCurFilter.equals("")) {
						
						/**
						 * Will have to pass mCurFilter as an argument to create a new Cursor which contains
						 * the Filtered result
						 */
						cursor = MySQLiteHelper.getInstance(getContext())
								.getProducts();
						
					} else {
						cursor = MySQLiteHelper.getInstance(getContext())
								.getProducts();
					}
					
					tempCursor= cursor;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (cursor != null) {
					cursor.getCount();
					
				}

				return cursor;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		editTextValues=new String[arg1.getCount()+1];
		itemRowValues=new double[arg1.getCount()+1];
		for(int i=0;i<editTextValues.length;i++)
		{
			editTextValues[i]="0";
			itemRowValues[i]=0.0;
		}
		
		adapter.changeCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.items_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

}
