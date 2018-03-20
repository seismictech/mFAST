package com.mfast.square.ordercollection;


import java.util.Calendar;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class OrderStatusFragment extends Fragment
{
	//private Spinner mSessionSpinner=null;
	private static final int SELECT_CUSTOMER=1;
	public static final String FORM_CONTENT="formContent";
	private Button setDate;
	private Button select;
	private static String selectedCustomerId;
	private String formContent=null;
	private int selectedyear;
	private int selectedmonth;
	private int selectedday;
	private DatePickerFragment date;
	private int Month,Day,Year;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		Month=Day=Year=-1;
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View layout=inflater.inflate(R.layout.fragment_order_status, container, false);
		layout.setFocusableInTouchMode(true);
//		mSessionSpinner=(Spinner)layout.findViewById(R.id.session_chooser);
//		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
//		        R.array.session_array, android.R.layout.simple_spinner_item);
//		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		
//		mSessionSpinner.setAdapter(spinnerAdapter);
		select =(Button) layout.findViewById(R.id.select_customer);
		select.setOnClickListener(new OnClickListener() 
		{	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), CustomerSelectionActivity.class);
				startActivityForResult(intent, SELECT_CUSTOMER);
			}
		});
		setDate = (Button)layout.findViewById(R.id.datePicker);
		setDate.setOnClickListener(new OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				showDatePicker();
			}
		});
		Button next =(Button) layout.findViewById(R.id.get_button);
		next.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v) 
			{
				if(!select.getText().toString().equals("Select Customer")
						&&!setDate.getText().toString().equals("Select Date"))
				{
					StringBuffer strBuff = new StringBuffer();
					strBuff.append("2 "+ Month + "/" + Day + "/" + Year);
					strBuff.append(","+selectedCustomerId);
					strBuff.append(",|;");
					//strBuff.append(","+mSessionSpinner.getSelectedItemPosition()+"|;");
					System.out.println(strBuff.toString());
					new DataCrawler(getActivity(),"_7","999",strBuff.toString()).execute();
//					Intent intent=new Intent(getActivity(), ProductsActivity.class);
//					System.out.println("Form Content "+strBuff.toString());
//					formContent = strBuff.toString();
//					intent.putExtra(FORM_CONTENT, strBuff.toString());
//					startActivity(intent);
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
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
					alert.show();
				}
			}
		});
		return layout;
	}
	protected void showDatePicker() 
	{
		// TODO Auto-generated method stub
		date = new DatePickerFragment();
		Calendar cal = Calendar.getInstance();
		Bundle args = new Bundle();
		if(Month==-1)
		{
			args.putInt("year",cal.get(Calendar.YEAR));
			args.putInt("month",cal.get(Calendar.MONTH));
			args.putInt("day",cal.get(Calendar.DAY_OF_MONTH));
		}
		else
		{
			args.putInt("year",Year);
			args.putInt("month",Month);
			args.putInt("day",Day);
		}
		date.setArguments(args);
		date.setCallBack(onDateSet);
		date.show(getFragmentManager(), "Date Picker");
	}
	OnDateSetListener onDateSet = new OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
		{
			Day = dayOfMonth;
			Month = monthOfYear;
			Year = year;
			setDate.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);			
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode == Activity.RESULT_OK && requestCode == SELECT_CUSTOMER)
		{
			String cName = data.getStringExtra(CustomerSelectionActivity.CUSTOMER_KEY);
			String[] parts = cName.split(";");
			OrderStatusFragment.selectedCustomerId = parts[1];
			select.setText(parts[0]);
			//mCustomerName.setText(parts[0]);
			//mCustomerName.setVisibility(View.VISIBLE);
		}
	}
}