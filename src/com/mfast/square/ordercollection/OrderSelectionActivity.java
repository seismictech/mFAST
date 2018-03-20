package com.mfast.square.ordercollection;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class OrderSelectionActivity extends Activity 
{
	private String formContent;
	private String sheetContent;
	private String OrderIndex;
	private String OrderId;
	private String selectedCustomerId;
	private String sessionIndex;
	private String dateString;
	private String grandTotal;
	
	private static final int SELECT_CUSTOMER=1;
	private static final int FORMAT_STRING=2;
	public static final String FORM_CONTENT="formContent";
	public static final String SHEET_CONTENT="sheetContent";
	
	private Spinner mSessionSpinner=null;
	private LinearLayout optionsBar;
	private Button select;
	private EditText mDate;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_selection);
		
		Intent intent = getIntent();
		String formatString = intent.getStringExtra(getResources().getString(R.string.FORMAT_STRING));
		OrderIndex = intent.getStringExtra(getResources().getString(R.string.OrderIndex));
		OrderId = intent.getStringExtra(getResources().getString(R.string.ORDER_ID));
		formContent = formatString.substring(0,formatString.indexOf('|'));
		sheetContent = formatString.substring(formatString.indexOf('|')+1);
		
		String[] parts = formContent.substring(2).split(",");
		selectedCustomerId = parts[0];
		sessionIndex = parts[1];
		dateString = parts[2];
		grandTotal = parts[3];
        
		mDate = (EditText)findViewById(R.id.delivery_date);
		mDate.setText(dateString);
		
		mSessionSpinner=(Spinner)findViewById(R.id.session_chooser);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(OrderSelectionActivity.this,
		        R.array.session_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSessionSpinner.setAdapter(spinnerAdapter);
		mSessionSpinner.setSelection((Integer.parseInt(sessionIndex)+1)%2);
		
		select=(Button)findViewById(R.id.select_customer);
		select.setOnClickListener(new OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent=new Intent(OrderSelectionActivity.this, CustomerSelectionActivity.class);
				startActivityForResult(intent,SELECT_CUSTOMER);
			}
		});
		select.setText(MySQLiteHelper.getInstance(OrderSelectionActivity.this).getCustomer(selectedCustomerId));
		select.requestFocus();
		optionsBar = (LinearLayout) findViewById(R.id.options_bar);
		optionsBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				OrderSelectionActivity.this.openOptionsMenu();	
			}
		});
		Button next=(Button) findViewById(R.id.next_button);
		next.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v)
			{
				if(mDate.getText().toString().equals("")||select.getText().toString().equals("Select Customer"))
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(OrderSelectionActivity.this);
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
					AlertDialog.Builder alert = new AlertDialog.Builder(OrderSelectionActivity.this);
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
					strBuff.append(","+ grandTotal);
					Intent intent=new Intent(OrderSelectionActivity.this,DirectedProductsActitivity.class);
					formContent = strBuff.toString();
					intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formContent+"|"+sheetContent);
					intent.putExtra(getResources().getString(R.string.ORDER_ID), OrderId);
					intent.putExtra(getResources().getString(R.string.OrderIndex), OrderIndex);
					startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
				}
			}
		});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_order_selection, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
	    switch (item.getItemId()) 
	    {
	        case R.id.menu_back:
	        	OrderSelectionActivity.this.finish();
	            return true;
	        case R.id.menu_next:
	        	if(!(mDate.getText().toString().equals("")||select.getText().toString().equals("Select Customer")))
				{
					StringBuffer strBuff = new StringBuffer();
					strBuff.append("1 "+ selectedCustomerId);
					strBuff.append(","+(mSessionSpinner.getSelectedItemPosition()+1)%2);
					strBuff.append(","+ mDate.getText().toString());
					strBuff.append(","+ grandTotal);
					Intent intent=new Intent(OrderSelectionActivity.this,DirectedProductsActitivity.class);
					formContent = strBuff.toString();
					intent.putExtra(getResources().getString(R.string.FORMAT_STRING), formContent+"|"+sheetContent);
					intent.putExtra(getResources().getString(R.string.ORDER_ID), OrderId);
					intent.putExtra(getResources().getString(R.string.OrderIndex), OrderIndex);
					startActivityForResult(intent,Integer.parseInt(getResources().getString(R.string.EXPAND)));
				}
				else
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(OrderSelectionActivity.this);
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void finishTheActivity()
	{
		Intent intent = null;
		if(OrderIndex.equals(getResources().getString(R.string.DraftOrder)))
			intent = new Intent(OrderSelectionActivity.this,DraftsActivity.class);
		else if(OrderIndex.equals(getResources().getString(R.string.SentBoxOrder)))
			intent = new Intent(OrderSelectionActivity.this,SentBoxActivity.class);
		OrderSelectionActivity.this.setResult(RESULT_OK,intent);
		OrderSelectionActivity.this.finish();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode == Activity.RESULT_OK && requestCode == SELECT_CUSTOMER)
		{
			String cName = data.getStringExtra(CustomerSelectionActivity.CUSTOMER_KEY);
			String[] parts = cName.split(",");
			this.selectedCustomerId = parts[1];
			select.setText(parts[0]);
		}
		else if(resultCode == Activity.RESULT_OK && requestCode == Integer.parseInt(getResources().getString(R.string.EXPAND)))
		{
			String dataString = data.getStringExtra(getResources().getString(R.string.FORMAT_STRING));
			if(dataString!=null)
			{
				sheetContent = dataString.substring(dataString.indexOf('|')+1);
				System.out.println(sheetContent);
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
				sheetContent = dataString.substring(dataString.indexOf('|')+1);
				System.out.println(sheetContent);
			}
			else
			{
								
			}		
		}
	}
}