package com.mfast.square.ordercollection;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class OrderSelectionFragment extends Fragment
{
	private Spinner mSessionSpinner=null;
	private LinearLayout optionsBar;
	private static final int SELECT_CUSTOMER=1;
	private static final int FORMAT_STRING=2;
	public static final String FORM_CONTENT="formContent";
	public static final String SHEET_CONTENT="sheetContent";
	
	
	//private TextView mCustomerName;
	private Button select;
	//private EditText mRemarks;
	private EditText mDate;
	private String selectedCustomerId;
	private String formContent=null;
	private String sheetContent=null;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View layout=inflater.inflate(R.layout.fragment_order_selection, container, false);
		layout.setFocusableInTouchMode(true);
		mSessionSpinner=(Spinner)layout.findViewById(R.id.session_chooser);
		mDate = (EditText)layout.findViewById(R.id.delivery_date);
		Time now = new Time();
		now.setToNow();
		mDate.setText(String.valueOf(now.monthDay));
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.session_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSessionSpinner.setAdapter(spinnerAdapter);
		mSessionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
			{
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{	
				
			}
		});
		select=(Button) layout.findViewById(R.id.select_customer);
		select.setOnClickListener(new OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), CustomerSelectionActivity.class);
				startActivityForResult(intent,SELECT_CUSTOMER);
			}
		});
		select.requestFocus();
		optionsBar = (LinearLayout) layout.findViewById(R.id.options_bar);
		optionsBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				getActivity().openOptionsMenu();	
			}
		});
		Button next=(Button) layout.findViewById(R.id.next_button);
		next.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v)
			{
				if(mDate.getText().toString().equals("")||select.getText().toString().equals("Select Customer"))
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Warning");
					alert.setMessage("Please Fill Up all the Information Needed");
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
				else if(Integer.parseInt(mDate.getText().toString())>31)
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Warning");
					alert.setMessage("Delivery Date has to be between [1 - 31]");
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
				else
				{
					StringBuffer strBuff = new StringBuffer();
					strBuff.append("1 "+ selectedCustomerId);
					strBuff.append(","+(mSessionSpinner.getSelectedItemPosition()+1)%2);
					strBuff.append(","+ mDate.getText().toString());
					Intent intent=new Intent(getActivity(), ProductsActivity.class);
					formContent = strBuff.toString();
					intent.putExtra(FORM_CONTENT,strBuff.toString());
					intent.putExtra(SHEET_CONTENT,sheetContent);
					System.out.println("Sheet Content: " + sheetContent);
					startActivityForResult(intent, FORMAT_STRING);					
				}
			}
		});
		return layout;
	}
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) 
	{
	    inflater.inflate(R.menu.orderselection, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	    	case R.id.menu_next:
	    		if(!(mDate.getText().toString().equals("")||select.getText().toString().equals("Select Customer")))
				{
					StringBuffer strBuff = new StringBuffer();
					strBuff.append("1 "+ selectedCustomerId);
					strBuff.append(","+(mSessionSpinner.getSelectedItemPosition()+1)%2);
					strBuff.append(","+ mDate.getText().toString());
					Intent intent=new Intent(getActivity(), ProductsActivity.class);
					formContent = strBuff.toString();
					intent.putExtra(FORM_CONTENT,strBuff.toString());
					intent.putExtra(SHEET_CONTENT,sheetContent);
					System.out.println("Sheet Content: " + sheetContent);
					startActivityForResult(intent, FORMAT_STRING);
				}
				else
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Warning");
					alert.setMessage("Please Fill Up all the Information Needed");
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
	    		return true;
	        case R.id.menu_clear:
	        	this.selectedCustomerId = null;
				select.setText("Select Customer");
				mSessionSpinner.setSelection(0);
				Time now = new Time();
				now.setToNow();
				mDate.setText(String.valueOf(now.monthDay));
				sheetContent = null;
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode == Activity.RESULT_OK && requestCode == SELECT_CUSTOMER)
		{
			String cName = data.getStringExtra(CustomerSelectionActivity.CUSTOMER_KEY);
			String[] parts = cName.split(";");
			this.selectedCustomerId = parts[1];
			select.setText(parts[0]);
			//mCustomerName.setText(parts[0]);
			//mCustomerName.setVisibility(View.VISIBLE);
		}
		///////need to workout here
		else if(resultCode == Activity.RESULT_OK && requestCode == FORMAT_STRING)
		{
			String formContent = data.getStringExtra(ProductsActivity.FORM_DATA_STRING);
			if(formContent!=null)
			{
				sheetContent = data.getStringExtra(ProductsActivity.SHEET_DATA_STRING);
				//System.out.println(sheetContent);
				String[] parts = (formContent.substring(formContent.indexOf(' ')+1)).split(",");
				this.selectedCustomerId = parts[0];
				select.setText(MySQLiteHelper.getInstance(getActivity()).getCustomer(selectedCustomerId));
				mSessionSpinner.setSelection(Integer.parseInt(parts[1]));
				mDate.setText(parts[2]);
				//mRemarks.setText(parts[3]);
			}
			else
			{
				selectedCustomerId = null;
				select.setText("Select Customer");
				mSessionSpinner.setSelection(0);
				Time now = new Time();
				now.setToNow();
				mDate.setText(String.valueOf(now.monthDay));
				sheetContent = null;
				//mRemarks.setText("");
			}
		}
	}
}