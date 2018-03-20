package com.mfast.square.ordercollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
public class DirectedProductsActitivity extends FragmentActivity
//public class DirectedProductsActitivity extends FragmentActivity implements LoaderCallbacks<Cursor>
{
	private ListView itemListview;
	//private ItemAdapter adapter;
	private Button clearButton;
	private TextView mGradToatalTextView;
	private double grandTotal=0;
	private int numberOfSelectedProducts = 0;
	private ScrollManager mScrollManager;
	private LinearLayout optionsBar;
	private EditText search;
	private String mCurFilter;
	public static final String DATA_STRING="data";
	public HashMap<String,Integer> ProductQuantityMap = null;
	public HashMap<String,Double> ProductTotalPriceMap = null;
	public static final int SUMMARY=20; 
	public static final String QueryString="queryString";
	public static String FORM_CONTENT;
	public static String FORMAT_STRING;
	public static double DISCOUNT_RATE;
	public static String OrderId;
	public String OrderIndex;
	private FragmentActivity self;
	private Cursor cursor;
	private ProductList adapter;
	private List<Product> products;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//getSupportLoaderManager().initLoader(0, null, this);
		setContentView(R.layout.fragment_product_selection_layout);
		self = this;

		itemListview = (ListView) findViewById(R.id.item_list);
		//		adapter = new ItemAdapter(null);
		//		itemListview.setAdapter(adapter);
		//		mScrollManager=new ScrollManager(adapter);
		//		itemListview.setOnScrollListener(mScrollManager);
		mGradToatalTextView=(TextView) findViewById(R.id.grand_total);

		optionsBar=(LinearLayout) findViewById(R.id.options_bar);
		optionsBar.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				DirectedProductsActitivity.this.openOptionsMenu();	
			}
		});
		search = (EditText) findViewById(R.id.searchText);
		search.addTextChangedListener(new SearchTextWatcher());
		clearButton = (Button) findViewById(R.id.clear);
		clearButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{	
				search.setText("");
				mCurFilter = "";
				onQueryTextChange("");				
			}
		});

		Intent intent = getIntent();
		FORMAT_STRING = intent.getStringExtra(getResources().getString(R.string.FORMAT_STRING));
		OrderIndex = intent.getStringExtra(getResources().getString(R.string.OrderIndex));
		OrderId = intent.getStringExtra(getResources().getString(R.string.ORDER_ID));
		FORM_CONTENT = FORMAT_STRING.substring(0,FORMAT_STRING.indexOf('|'));
		grandTotal = Double.parseDouble(FORM_CONTENT.substring(FORM_CONTENT.lastIndexOf(',')+1));
		FORM_CONTENT = FORM_CONTENT.substring(0, FORM_CONTENT.lastIndexOf(','));
		String customerId = FORM_CONTENT.substring(2,FORM_CONTENT.indexOf(','));
		DISCOUNT_RATE = MySQLiteHelper.getInstance(this).getDiscountRate(customerId);
		ProductTotalPriceMap = new HashMap<String,Double>();
		ProductQuantityMap = new HashMap<String,Integer>();
		String[] parts = (FORMAT_STRING.substring(FORMAT_STRING.indexOf('|')+1)).split(";");
		String[] rowParts=null;
		int i;
		for(i=0;i<parts.length;i++)
		{	
			rowParts = parts[i].split(",");
			ProductQuantityMap.put(rowParts[0], Integer.parseInt(rowParts[1]));
			ProductTotalPriceMap.put(rowParts[0], Double.parseDouble(rowParts[2]));
		}
		cursor = MySQLiteHelper.getInstance(this).getProducts();
		products = new ArrayList<Product>(cursor.getCount());
		Product singleProduct;
		String productName;
		String productId;
		double unitVat,unitPrice;
		cursor.moveToFirst();
		do
		{
			productId = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductIdColumn));
			productName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductValueColumn));
			unitPrice = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitPrice)));
			unitVat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitVat)));
			singleProduct = new Product(productId,productName,unitPrice,unitVat);
			products.add(singleProduct);
		}
		while(cursor.moveToNext());
		adapter = new ProductList(this, products,ProductQuantityMap,ProductTotalPriceMap);
		itemListview.setAdapter(adapter);
		mScrollManager=new ScrollManager(adapter);
		itemListview.setOnScrollListener(mScrollManager);
		numberOfSelectedProducts = parts.length;
		mGradToatalTextView.setText(String.format("%.2f", grandTotal)+"("+numberOfSelectedProducts+")");
	}
	private class Product
	{
		String ProductName;
		String ProductId;
		double unitVat;
		double unitPrice;
		Product(String ProductId,String ProductName,double unitPrice,double unitVat)
		{
			this.ProductId = ProductId;
			this.ProductName = ProductName;
			this.unitPrice = unitPrice;
			this.unitVat = unitVat;
		}
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
		itemListview.setSelection(GetPosition(mCurFilter));
		//getSupportLoaderManager().restartLoader(0, null, this);
		return true;
	}
	private int GetPosition(String queryString)
	{
		if(queryString==null||queryString.equals(""))
			return 0;
		for(int i=0;i<products.size();i++)
		{
			if(products.get(i).ProductName.startsWith(queryString))
				return i;
		}
		return 0;
	}
	private class ProductList extends BaseAdapter
	{
		private List<Product> products;
		private Context context;
		private List<Integer> quantity;
		private List<Double> totalPrice;
		private ViewHolder holder;
		public ProductList(Context context,List<Product> products) 
		{
			this.context = context;
			this.products = products;
			this.quantity = new ArrayList<Integer>(products.size());
			this.totalPrice = new ArrayList<Double>(products.size());
			for(int i=0;i<products.size();i++)
			{
				quantity.add(0);
				totalPrice.add(0.0);
			}
		}
		public ProductList(Context context,List<Product> products,HashMap<String,Integer> productQuantityMap,
				HashMap<String,Double> productTotalPriceMap) 
		{
			this.context = context;
			this.products = products;
			this.quantity = new ArrayList<Integer>(products.size());
			this.totalPrice = new ArrayList<Double>(products.size());
			for(int i=0;i<products.size();i++)
			{
				if(productQuantityMap.containsKey(String.valueOf(this.products.get(i).ProductId)))
				{
					this.totalPrice.add(productTotalPriceMap.get(String.valueOf(this.products.get(i).ProductId)));
					this.quantity.add(productQuantityMap.get(String.valueOf(this.products.get(i).ProductId)));
				}
				else
				{
					this.totalPrice.add(0.0);
					this.quantity.add(0);
				}
			}
		}
		public void setQuantity(HashMap<String,Integer> productQuantityMap,HashMap<String,Double> productTotalPriceMap) 
		{	
			for(int i=0;i<this.products.size();i++)
			{
				if(productQuantityMap.containsKey(this.products.get(i).ProductId))
				{
					this.totalPrice.set(i, productTotalPriceMap.get(this.products.get(i).ProductId));
					this.quantity.set(i, productQuantityMap.get(this.products.get(i).ProductId));
				}
				else
				{
					this.totalPrice.set(i, 0.0);
					this.quantity.set(i, 0);
				}
			}
		}
		public String getSheetContent()
		{
			StringBuffer strBuff = new StringBuffer();
			for(int i=0;i<quantity.size();i++)
			{
				if(quantity.get(i)!=0)
				{
					strBuff.append(this.products.get(i).ProductId+",");
					strBuff.append(quantity.get(i)+",");
					strBuff.append(totalPrice.get(i)+";");
				}
			}
			return strBuff.toString();
		}
		public String getQueryString() 
		{
			StringBuffer strBuff = new StringBuffer();
			for(int i=0;i<quantity.size();i++)
			{
				if(quantity.get(i)!=0)
				{
					strBuff.append(this.products.get(i).ProductId+",");
					strBuff.append(quantity.get(i)+";");
				}
			}
			return strBuff.toString();
		}
		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return products.size();
		}

		@Override
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return products.get(arg0);
		}

		@Override
		public long getItemId(int position) 
		{
			// TODO Auto-generated method stub
			return position;
		}
		private class QuantityWatcher implements TextWatcher
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after)
			{
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count)
			{
			}
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final int row = position;
			final Product product = (Product)getItem(row);
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(convertView==null)
			{
				convertView = mInflater.inflate(R.layout.productlist_singlerow_layout, null);
				holder = new ViewHolder();
				holder.productName = (TextView)convertView.findViewById(R.id.item_name_textview);
				holder.productQuantity = (TextView)convertView.findViewById(R.id.item_quantity);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.productName.setLongClickable(true);
			holder.productName.setOnLongClickListener(new OnLongClickListener() 
			{	
				@Override
				public boolean onLongClick(View v) 
				{
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(DirectedProductsActitivity.this);
					alert.setTitle(product.ProductName);
					alert.setMessage("Unit Price: "+product.unitPrice+"\nUnit Vat: "+product.unitVat);
					alert.setCancelable(true);
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
					alert.show();
					return false;
				}
			});
			holder.productName.setOnClickListener(new OnClickListener() 
			{	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(DirectedProductsActitivity.this);
					alert.setTitle(product.ProductName);
					alert.setMessage("Unit Price: "+product.unitPrice+"\nUnit Vat: "+product.unitVat);
					alert.setCancelable(true);
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
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
			holder.productName.setText(product.ProductName);
			int quant = quantity.get(position);
			if(quant==0)
				holder.productQuantity.setText("");
			else
				holder.productQuantity.setText(String.valueOf(quant));
			//			holder.productQuantity.addTextChangedListener(new TextWatcher() 
			//			{	
			//				@Override
			//				public void onTextChanged(CharSequence s, int start, int before, int count)
			//				{
			//					if(mScrollManager.getScrollState()!=AbsListView.OnScrollListener.SCROLL_STATE_FLING)
			//					{
			//						System.out.println("Quantity is set for "+row+" "+s.toString());
			//						if(!s.toString().equals(""))
			//							quantity.set(row, Integer.parseInt(s.toString()));
			//						else
			//							quantity.set(row, Integer.valueOf(0));
			//					}
			//				}
			//				
			//				@Override
			//				public void beforeTextChanged(CharSequence s, int start, int count, int after)
			//				{	
			//				}
			//				
			//				@Override
			//				public void afterTextChanged(Editable s) 
			//				{
			//				}
			//			});
			holder.productQuantity.setLongClickable(true);
			holder.productQuantity.setOnLongClickListener(new OnLongClickListener() 
			{	
				@Override
				public boolean onLongClick(View v) 
				{
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.dialog);
					dialog.setTitle(product.ProductName);

					EditText prodQuantity = (EditText)dialog.findViewById(R.id.quantity);
					TextView unitVat = (TextView)dialog.findViewById(R.id.unitvat);
					TextView unitPrice = (TextView)dialog.findViewById(R.id.unitprice);
					TextView totalPrice = (TextView)dialog.findViewById(R.id.totalprice);
					unitVat.setText(String.valueOf(product.unitVat));
					unitPrice.setText(String.valueOf(product.unitPrice));
					totalPrice.setText("0.0");
					prodQuantity.requestFocus();
					dialog.show();

					//	                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					//	                imm.showSoftInput(prodQuantity, InputMethodManager.SHOW_FORCED);
					//	                
					//	                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
					//	                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
					return true;
				}
			});
			holder.productQuantity.setOnClickListener(new OnClickListener() 
			{	
				@Override
				public void onClick(View v) 
				{
					if(numberOfSelectedProducts<250)
					{
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.dialog);
						dialog.setTitle(product.ProductName);	                	                

						final EditText prodQuantity = (EditText)dialog.findViewById(R.id.quantity);
						final TextView unitVat = (TextView)dialog.findViewById(R.id.unitvat);
						final TextView unitPrice = (TextView)dialog.findViewById(R.id.unitprice);
						final TextView TP = (TextView)dialog.findViewById(R.id.totalprice);
						Button okButton = (Button)dialog.findViewById(R.id.ok_button);
						Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
						unitVat.setText(String.valueOf(product.unitVat));
						unitPrice.setText(String.valueOf(product.unitPrice));
						prodQuantity.addTextChangedListener(new TextWatcher() 
						{	
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) 
							{
								String quantity = s.toString();
								if(quantity.equals(""))
								{
									TP.setText("0.0");
								}
								else
								{
									double price = (Double.parseDouble(unitVat.getText().toString())
											+Double.parseDouble(unitPrice.getText().toString()))
											*Integer.parseInt(quantity);
									price -= (Double.parseDouble(unitPrice.getText().toString())*Integer.parseInt(quantity)*DISCOUNT_RATE)/100.0f;
									TP.setText(String.format("%.2f",price));								
								}
							}
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count,int after) 
							{	
							}
							@Override
							public void afterTextChanged(Editable s) 
							{	
							}
						});
						okButton.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View v)
							{
								int ProdQuant;
								if(prodQuantity.getText().toString().equals(""))
									ProdQuant = 0;
								else
									ProdQuant = Integer.parseInt(prodQuantity.getText().toString());
								if(quantity.get(row)!=ProdQuant)
								{
									quantity.set(row, ProdQuant);
									double prevPrice = totalPrice.get(row);
									double newPrice = Double.parseDouble(TP.getText().toString());
									totalPrice.set(row, newPrice);
									grandTotal = grandTotal - prevPrice + newPrice;
									if(prevPrice==0.0)
										numberOfSelectedProducts++;
									if(newPrice==0.0)
										numberOfSelectedProducts--;
									mGradToatalTextView.setText(""+ String.format("%.2f", grandTotal)+"("+numberOfSelectedProducts+")");
									notifyDataSetChanged();
								}
								dialog.dismiss();
							}
						});
						cancelButton.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								dialog.dismiss();
							}
						});
						int PQ = quantity.get(row);
						if(PQ==0)
						{
							prodQuantity.setText("");
							TP.setText("0.0");
						}
						else
						{
							TP.setText(String.valueOf(totalPrice.get(row)));
							prodQuantity.setText(String.valueOf(PQ));
						}
						prodQuantity.requestFocus();
						dialog.show();
					}
					else
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(DirectedProductsActitivity.this);
						alert.setTitle("Limit Exceeded");
						alert.setMessage("Your current order already contains 250 products. Please try another order");
						alert.setCancelable(false);
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
							}
						});
						alert.show();					
					}
				}
			});
			return convertView;
		}
		private class ViewHolder
		{
			TextView productName;
			TextView productQuantity;
		}
	}

	//	private class ItemAdapter extends CursorAdapter
	//	{	
	//		public ItemAdapter(Cursor c) 
	//		{
	//			super(DirectedProductsActitivity.this, c, false);
	//		}
	//
	//		@Override
	//		public void bindView(View view, Context context, final Cursor cursor) 
	//		{	
	//			double TotalPrice=0;
	//			int productQuantity=0;
	//			
	//			final ViewHolder holder = (ViewHolder) view.getTag();
	//			final String ProductId=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductIdColumn));
	//			final String ProductName=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductValueColumn));
	//			final String UnitPrice=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitPrice));
	//			final String Unitvat=cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ProductUnitVat));
	//			
	//			
	//			holder.ProductId = ProductId;			
	//			holder.ProductName.setText(ProductName);
	//			holder.UnitPrice.setText(UnitPrice);
	//			holder.Unitvat.setText(Unitvat);
	//			
	//			
	//			if(mScrollManager.getScrollState()==AbsListView.OnScrollListener.SCROLL_STATE_FLING)
	//			{
	//				holder.ProductQuantity.setText("");
	//				holder.TotalPrice.setText("");
	//			}
	//			else 
	//			{
	//				if(!ProductQuantityMap.containsKey(holder.ProductId)||(ProductQuantityMap.get(holder.ProductId).intValue()==0))
	//				{
	//					ProductQuantityMap.put(holder.ProductId, 0);
	//					holder.ProductQuantity.setText("");
	//				}
	//				else
	//				{
	//					holder.ProductQuantity.setText(ProductQuantityMap.get(holder.ProductId).toString());
	//				}
	//				///////////
	////				if(Quantity[holder.columnId].equalsIgnoreCase("0"))
	////				{
	////					if(ProductQuantityMap.containsKey(holder.ProductId)&&ProductQuantityMap.get(holder.ProductId)!=0)
	////					{
	////						Quantity[holder.columnId] = ProductQuantityMap.get(holder.ProductId).toString();
	////						holder.ProductQuantity.setText(ProductQuantityMap.get(holder.ProductId).toString());
	////					}
	////					else
	////						holder.ProductQuantity.setText("");
	////				}
	////				else
	////				{	
	////					holder.ProductQuantity.setText(ProductQuantityMap.get(holder.ProductId).toString());
	////					//holder.ProductQuantity.setText(Quantity[holder.columnId]);
	////				}
	//				/////////////
	//				
	//				/*
	//				int length=holder.ProductQuantity.getText().toString().length();				
	//				if(length>0)
	//				{
	//					productQuantity = Integer.parseInt(holder.ProductQuantity.getText().toString());
	//					TotalPrice = Double.parseDouble(UnitPrice)*productQuantity;
	//					holder.TotalPrice.setText(""+String.format("%.2f",TotalPrice));	
	//					ProductTotalPriceMap.put(holder.ProductId, TotalPrice);
	//				}
	//				else 
	//				{	
	//					ProductTotalPriceMap.put(holder.ProductId, 0.0);
	//					holder.TotalPrice.setText("");
	//				}*/
	//			}
	//			holder.ProductQuantity.addTextChangedListener(new TextWatcher() 
	//			{
	//				@Override
	//				public void onTextChanged(CharSequence s, int start, int before, int count) 
	//				{
	//					if(mScrollManager.getScrollState()!=AbsListView.OnScrollListener.SCROLL_STATE_FLING)
	//					{
	//						int length=holder.ProductQuantity.getText().toString().length();
	//						double totalPrice;
	//						if(length>0)
	//						{
	//							ProductQuantityMap.put(holder.ProductId, Integer.parseInt(holder.ProductQuantity.getText().toString()));
	//							//Quantity[holder.columnId] = holder.ProductQuantity.getText().toString();
	//							totalPrice = Integer.parseInt(holder.ProductQuantity.getText().toString())*Double.parseDouble(holder.UnitPrice.getText().toString());
	//							//TotalPriceValues[holder.columnId] = totalPrice;
	//							ProductTotalPriceMap.put(holder.ProductId, totalPrice);
	//							holder.TotalPrice.setText(""+totalPrice);
	//						}
	//						else if (length==0) 
	//						{
	//							ProductTotalPriceMap.put(holder.ProductId, 0.0);
	//							ProductQuantityMap.put(holder.ProductId, 0);
	////							Quantity[holder.columnId]="0";
	////							TotalPriceValues[holder.columnId]=0.0;
	//						}
	//						grandTotal=0.0;
	//						numberOfSelectedProducts=0;
	//						for(String product: ProductTotalPriceMap.keySet())
	//						{
	//							if(ProductTotalPriceMap.get(product).doubleValue()!=0.0)
	//							{
	//								numberOfSelectedProducts++;
	//								grandTotal += ProductTotalPriceMap.get(product).doubleValue();
	//							}
	//						}
	//						mGradToatalTextView.setText(""+ String.format("%.2f", grandTotal)+"("+numberOfSelectedProducts+")");
	//					}
	//				}
	//				
	//				@Override
	//				public void beforeTextChanged(CharSequence s, int start, int count,int after) 
	//				{
	//					// TODO Auto-generated method stub
	//				}
	//				@Override
	//				public void afterTextChanged(Editable s) 
	//				{
	//					// TODO Auto-generated method stub
	//				}
	//			});
	//		}
	//
	//		@Override
	//		public View newView(Context context, Cursor cursor, ViewGroup parent) 
	//		{
	//			View rowLayout=getLayoutInflater().inflate(R.layout.product_selection_list_row_layout, null);
	//			ViewHolder holder = new ViewHolder();
	//			
	//			holder.ProductName=(TextView) rowLayout.findViewById(R.id.item_name_textview);
	//			holder.TotalPrice=(TextView) rowLayout.findViewById(R.id.item_total_value);
	//			holder.UnitPrice=(TextView) rowLayout.findViewById(R.id.item_unit_value);
	//			holder.Unitvat=(TextView) rowLayout.findViewById(R.id.item_unit_vat);
	//			
	//			holder.ProductQuantity=(EditText) rowLayout.findViewById(R.id.item_quantity_editext);
	//			
	//			rowLayout.setTag(holder);
	//			return rowLayout;
	//		}
	//	}
	//	
	//	private static class ViewHolder 
	//	{	
	//		TextView ProductName;
	//		TextView TotalPrice;
	//		TextView UnitPrice;
	//		TextView Unitvat;
	//		String ProductId;
	//			
	//		EditText ProductQuantity;
	//	}
	//
	//	@Override
	//	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	//	{
	//		// TODO Auto-generated method stub
	//			return new SQLiteCursorLoader(this) 
	//			{
	//			
	//			@Override
	//			public Cursor loadInBackground() 
	//			{	
	//				Cursor cursor=null;
	//				try 
	//				{	
	//					if (search != null)
	//						mCurFilter = search.getText().toString();
	//					if (mCurFilter != null && !mCurFilter.equals("")) 
	//					{
	//						
	//						/**
	//						 * Will have to pass mCurFilter as an argument to create a new Cursor which contains
	//						 * the Filtered result
	//						 */
	//						cursor = MySQLiteHelper.getInstance(getContext()).getProductsFiltered(mCurFilter);
	//						
	//					} 
	//					else 
	//					{
	//						cursor = MySQLiteHelper.getInstance(getContext()).getProducts();
	//					}
	//				} 
	//				catch (SQLException e) 
	//				{
	//					e.printStackTrace();
	//				}
	//				
	//				if (cursor != null) 
	//				{
	//					cursor.getCount();	
	//				}
	//				return cursor;
	//			}
	//		};
	//	}
	//
	//	@Override
	//	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) 
	//	{
	//		int i=0;
	//		if(ProductQuantityMap==null)
	//		{
	//			ProductTotalPriceMap = new HashMap<String,Double>();
	//			ProductQuantityMap = new HashMap<String,Integer>();
	//			if(arg1.moveToFirst())
	//			{
	//				do
	//				{
	//					ProductTotalPriceMap.put(arg1.getString(arg1.getColumnIndex(MySQLiteHelper.ProductIdColumn)), 0.0);
	//					ProductQuantityMap.put(arg1.getString(arg1.getColumnIndex(MySQLiteHelper.ProductIdColumn)), 0);
	//					i++;
	//				}
	//				while(arg1.moveToNext());
	//			}
	//		}
	//		arg1.moveToFirst();
	//		adapter.changeCursor(arg1);
	//	}
	//	
	//	@Override
	//	public void onLoaderReset(Loader<Cursor> arg0) 
	//	{
	//		adapter.changeCursor(null);	
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.directed_item_fragment, menu);
		return true;
	}
	public String getSheetContent()
	{
		return this.adapter.getSheetContent();
	}
	public String getQueryString()
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(FORM_CONTENT+"|");
		//strBuff.append(FORM_CONTENT+",");
		//strBuff.append(String.format("%.2f", grandTotal)+"|");
		strBuff.append(this.adapter.getQueryString());
		return strBuff.toString();
	}
	public void finishTheActivity()
	{
		Intent intent=null;
		if(OrderIndex.equals(getResources().getString(R.string.DraftOrder)))
			intent = new Intent(DirectedProductsActitivity.this,DraftsActivity.class);
		else if(OrderIndex.equals(getResources().getString(R.string.SentBoxOrder)))
			intent = new Intent(DirectedProductsActitivity.this,SentBoxActivity.class);
		DirectedProductsActitivity.this.setResult(RESULT_OK,intent);
		DirectedProductsActitivity.this.finish();
	}
	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra(getResources().getString(R.string.FORMAT_STRING), FORM_CONTENT+","+String.format("%.2f", grandTotal)+"|"+getSheetContent());
		DirectedProductsActitivity.this.setResult(RESULT_OK, intent);
		DirectedProductsActitivity.this.finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.menu_next:
			if(grandTotal==0.0)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder((DirectedProductsActitivity.this));
				alert.setTitle("Warning");
				alert.setMessage("No Product Selected");
				alert.setCancelable(true);
				alert.setNegativeButton("OK",new DialogInterface.OnClickListener() 
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
			else
			{
				Intent intent = new Intent(this, SummeryActivity.class);
				intent.putExtra(getResources().getString(R.string.ORDER_ID), OrderId);
				intent.putExtra(getResources().getString(R.string.FORMAT_STRING), FORM_CONTENT+","+String.format("%.2f", grandTotal)+"|"+getSheetContent());
				intent.putExtra(getResources().getString(R.string.OrderIndex), this.OrderIndex);
				startActivityForResult(intent,SUMMARY);
			}
			return true;
		case R.id.menu_back:
			Intent intent = new Intent();
			intent.putExtra(getResources().getString(R.string.FORMAT_STRING), FORM_CONTENT+","+String.format("%.2f", grandTotal)+"|"+getSheetContent());
			DirectedProductsActitivity.this.setResult(RESULT_OK, intent);
			DirectedProductsActitivity.this.finish();
			return true;
			//	        case R.id.menu_back_to_order:
			//	        	intent = new Intent(this,OrderSelectionFragment.class);
			//	        	intent.putExtra(DATA_STRING, this.FORM_CONTENT);
			//	        	DirectedProductsActitivity.this.setResult(RESULT_OK,intent);
			//	        	DirectedProductsActitivity.this.finish();
			//	        	return true;
		case R.id.menu_save:
			if(grandTotal==0.0)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder((DirectedProductsActitivity.this));
				alert.setTitle("Warning");
				alert.setMessage("No Product Selected");
				alert.setCancelable(true);
				alert.setNegativeButton("OK",new DialogInterface.OnClickListener() 
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
			else
			{
				String QueryString = FORM_CONTENT+","+String.format("%.2f", grandTotal)+"|"+getSheetContent();
				MySQLiteHelper.getInstance(this).insertIntoDraftBox(QueryString);
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Success");
				alert.setMessage("Order Saved Successfully");
				alert.setCancelable(true);
				alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						dialog.cancel();
						finishTheActivity();
					}
				});
				alert.show();
			}
			return true;
		case R.id.menu_send:
			if(grandTotal!=0.0)
			{
				String QueryString = getQueryString();
				if(isValidQueryString(QueryString))
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("Order Sending");
					alert.setMessage("You are about to send an order of GrandTotal: "
							+String.format("%.2f", grandTotal)+"\nDo you want to proceed?");
					alert.setCancelable(true);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							TelephonyManager telMgr = (TelephonyManager)(DirectedProductsActitivity.this).getSystemService(Context.TELEPHONY_SERVICE);
							String simSerial = telMgr.getLine1Number();
							if(MySQLiteHelper.getInstance((DirectedProductsActitivity.this)).isSimSerialMatched(simSerial))
							{
								String QueryString = getQueryString();
								String savedString = FORM_CONTENT+","+String.format("%.2f", grandTotal)+"|"+getSheetContent();
								new DataSender(DirectedProductsActitivity.this,QueryString,savedString,OrderIndex,self,OrderId).execute();
							}
							else
							{
								AlertDialog.Builder alert = new AlertDialog.Builder((DirectedProductsActitivity.this));
								alert.setTitle("Failure");
								alert.setMessage("Your Current Sim is not authorized. Make sure you have put " +
										"your registered sim in Slot1");
								alert.setCancelable(true);
								alert.setNegativeButton("OK",new DialogInterface.OnClickListener() 
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
				}
				else
				{
					Time now = new Time();
					now.setToNow();
					Log.v(DataSender.class.getName(),"OrderIndex: "+OrderIndex+","+QueryString+","+now.monthDay+","+now.HOUR);
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("Wrong Format");
					alert.setMessage("An error has been occured. Please process the order again.");
					alert.setCancelable(true);
					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
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
			}
			else
			{
				AlertDialog.Builder alert = new AlertDialog.Builder((DirectedProductsActitivity.this));
				alert.setTitle("Warning");
				alert.setMessage("No Product Selected");
				alert.setCancelable(true);
				alert.setNegativeButton("OK",new DialogInterface.OnClickListener() 
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public static boolean isValidQueryString(String QueryString)
	{
		boolean valid = true;
		try
		{
			String formString = QueryString.substring(2,QueryString.indexOf('|'));
			String sheetString = QueryString.substring(QueryString.indexOf('|')+1);
			
			String[] products = sheetString.split(";");
			String[] parts;
			for(int i=0;i<products.length;i++)
			{
				parts = products[i].split(",");
				if(parts.length!=2)
					valid = false;
				if(parts[0].length()!=10)
					valid = false;
				if(parts[1].length()==0)
					valid = false;
			}
			valid = true;
		}
		catch(Exception e)
		{
			valid = false;
		}
		if(valid == false)
		{
			Time now = new Time();
			now.setToNow();
			//Log.v(DataSender.class.getName(),"OrderIndex: "+OrderIndex+","+QueryString+","+orderId+","+now.monthDay+","+now.HOUR);
		}
		return valid;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode == Activity.RESULT_OK && requestCode == SUMMARY)
		{
			String dataString = data.getStringExtra(getResources().getString(R.string.FORMAT_STRING));
			if(dataString!=null)
			{
				FORM_CONTENT = dataString.substring(0,dataString.indexOf('|'));
				grandTotal = Double.parseDouble(FORM_CONTENT.substring(FORM_CONTENT.lastIndexOf(',')+1));
				ProductTotalPriceMap = new HashMap<String,Double>();
				ProductQuantityMap = new HashMap<String,Integer>();
				if(grandTotal==0.0)
				{
					numberOfSelectedProducts = 0;
					ProductTotalPriceMap.clear();
					ProductQuantityMap.clear();
				}
				else
				{
					String[] parts = (dataString.substring(dataString.indexOf('|')+1)).split(";");
					String[] rowParts;
					int i;

					for(i=0;i<parts.length;i++)
					{
						rowParts = parts[i].split(",");
						System.out.println(rowParts[0]+" "+rowParts[1]+" "+rowParts[2]);
						ProductQuantityMap.put(rowParts[0], Integer.parseInt(rowParts[1]));
						ProductTotalPriceMap.put(rowParts[0], Double.parseDouble(rowParts[2]));
					}
					numberOfSelectedProducts = parts.length;
				}
				this.adapter.setQuantity(ProductQuantityMap,ProductTotalPriceMap);
				mGradToatalTextView.setText(String.format("%.2f", grandTotal)+"("+numberOfSelectedProducts+")");
				FORM_CONTENT = FORM_CONTENT.substring(0, FORM_CONTENT.lastIndexOf(','));
				this.adapter.notifyDataSetChanged();
			}
			else
			{
				finishTheActivity();
			}
		}
		else if(resultCode == Activity.RESULT_CANCELED && requestCode == 22)   //BackPressed
		{
			String dataString = data.getStringExtra(getResources().getString(R.string.FORMAT_STRING));
			if(dataString!=null)
			{
				FORM_CONTENT = dataString.substring(0,dataString.indexOf('|'));
				grandTotal = Double.parseDouble(FORM_CONTENT.substring(FORM_CONTENT.lastIndexOf(',')+1));
				ProductTotalPriceMap = new HashMap<String,Double>();
				ProductQuantityMap = new HashMap<String,Integer>();
				if(grandTotal==0.0)
				{
					numberOfSelectedProducts = 0;
					ProductTotalPriceMap.clear();
					ProductQuantityMap.clear();
				}
				else
				{
					String[] parts = (dataString.substring(dataString.indexOf('|')+1)).split(";");
					String[] rowParts;
					int i;

					for(i=0;i<parts.length;i++)
					{
						rowParts = parts[i].split(",");
						System.out.println(rowParts[0]+" "+rowParts[1]+" "+rowParts[2]);
						ProductQuantityMap.put(rowParts[0], Integer.parseInt(rowParts[1]));
						ProductTotalPriceMap.put(rowParts[0], Double.parseDouble(rowParts[2]));
					}
					numberOfSelectedProducts = parts.length;
				}
				this.adapter.setQuantity(ProductQuantityMap,ProductTotalPriceMap);
				mGradToatalTextView.setText(String.format("%.2f", grandTotal)+"("+numberOfSelectedProducts+")");
				FORM_CONTENT = FORM_CONTENT.substring(0, FORM_CONTENT.lastIndexOf(','));
				this.adapter.notifyDataSetChanged();
			}		
		}
	}
}