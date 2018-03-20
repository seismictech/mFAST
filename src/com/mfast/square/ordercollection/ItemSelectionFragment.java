package com.mfast.square.ordercollection;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ItemSelectionFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ListView itemListview;
	private ItemAdapter adapter;
	private TextView mGradToatalTextView;
	private double grandTotal=0;
	private String[] editTextValues;
	private double[] itemRowValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		//MySQLiteHelper.getInstance(getActivity()).createDummyItems();
		
		getLoaderManager().initLoader(0, null, this);
		hasOptionsMenu();
		super.onCreate(savedInstanceState);
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout=inflater.inflate(R.layout.fragment_item_selection_layout, container, false);
		
		itemListview = (ListView) layout.findViewById(R.id.item_list);
		adapter = new ItemAdapter(null);
		itemListview.setAdapter(adapter);
		
		
		mGradToatalTextView=(TextView) layout.findViewById(R.id.grand_total);
		
		return layout;
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
			holder.columnId=id;
			holder.itemUnitPrice.setText("id "+id);
			holder.itemUnitvat.setText(itemUnitvat);
			
			final double itemUnitPriceVar=Double.parseDouble(itemUnitPrice);
			
			Log.d("Mkhan", "id : "+id+" edittext valu: "+editTextValues[holder.columnId]);
			
			if(editTextValues[holder.columnId].equalsIgnoreCase("0"))
			{
				holder.itemQuantity.setText("");
			}
			else {
				
				holder.itemQuantity.setText(editTextValues[holder.columnId]);
			}
			
			
			Log.d("Mkhan", " pos "+cursor.getPosition());
			
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
			
			//holder.itemQuantity.addTextChangedListener(new SearchTextWatcher(itemUnitPriceVar, holder.itemQuantity, holder.itemTotalPrice,cursor.getPosition()));
			
			holder.itemQuantity.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
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
					R.layout.item_selection_list_row_layout, null);
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
				Cursor cursor = null;
				try {
					
					cursor = MySQLiteHelper.getInstance(getContext())
								.getProducts();
					
					
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
		Log.d("Mkhan", "arr size " +arg1.getCount());
		adapter.changeCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

}
