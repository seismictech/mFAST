package com.mfast.square.ordercollection;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OrderStartActivity extends FragmentActivity 
{
	private ViewPager viewPager;
	private TabPagerAdapter adapter;
	private static ProgressDialog dialog;
	private static Context context;
	private static String StringnewPin;
	private DataBoxFragment databoxFragment;
	private SyncFragment syncFragment;
	private ProductListFragment productListFragment;
	private CustomerListFragment customerListFragment;
	private OrderStatusFragment orderStatusFragment;
	private OrderSelectionFragment orderSelectionFragment;
	private int TabIndex;
	
	public static void onPostRegistrationSuccessful(String[] parts)
	{
		try
		{
			if(dialog!= null && dialog.isShowing())
				dialog.dismiss();
			MySQLiteHelper.getInstance(context).updatePin(StringnewPin);
			MySQLiteHelper.getInstance(context).updateSerialNo(parts[0]);
			MySQLiteHelper.getInstance(context).updateDBVersion(parts[1]);
			MySQLiteHelper.getInstance(context).insertIntoServerIP(parts[2], parts[3]);
		
			TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			
			String deviceID = telMgr.getDeviceId();
			String simSerialNumber = telMgr.getLine1Number(); 
			String simLineNumber = telMgr.getLine1Number();
			System.out.println(deviceID+" "+simSerialNumber+" "+simLineNumber);
			
			MySQLiteHelper.getInstance(context).insertSimSerial(simSerialNumber);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Regristration Successful");
			alert.setMessage("SMS Received. Your mFAST is now registered with number: "+simSerialNumber);
			alert.setCancelable(true);
			alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			});
			alert.show();
		}
		catch(Exception e)
		{
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	private class WaitForSMSREply extends AsyncTask<Void, Void, Void> 
	{
		private Context context;
		private ProgressDialog dialog;
		public WaitForSMSREply(Context context,ProgressDialog dialog) 
		{
			// TODO Auto-generated constructor stub
			this.context = context;
			this.dialog = dialog;
		} 
	    @Override
	    protected void onPostExecute(Void params)
	    {
	    	if(dialog.isShowing())
			{
				dialog.dismiss();						
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Reply Not Found");
				alert.setMessage("mFAST Server is not responding. Closing the Application");
				alert.setCancelable(true);
				alert.setNegativeButton("Close",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
						System.exit(0);
					}
				});
				alert.show();
			}
	    }

		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			try 
			{
				Thread.sleep(60000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return null;
		}
	 }
	private void sendRegistrationSMS(String message) 
	{
		//message = "_41;1421,999,202.59.136.1,202.59.136.4,0;";
		SMSSender.getInstance(OrderStartActivity.this).send(message);
		
		OrderStartActivity.dialog = new ProgressDialog(OrderStartActivity.this);
		OrderStartActivity.dialog.setMessage("Sending an SMS to mFAST SMS server. Please wait...");
		OrderStartActivity.dialog.setCanceledOnTouchOutside(false);
		OrderStartActivity.dialog.show();
		new WaitForSMSREply(OrderStartActivity.this, OrderStartActivity.dialog).execute();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Intent smsRecvIntent = new Intent("android.provider.Telephony.SMS_RECEIVED");
        Context contxt = OrderStartActivity.this;
        List<ResolveInfo> infos = contxt.getPackageManager().queryBroadcastReceivers(smsRecvIntent, 0);
        for (ResolveInfo info : infos) 
        {
            System.out.println("Receiver: "+info.activityInfo.name + ", priority=" + info.priority);
        }
        //TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
		if(MySQLiteHelper.getInstance(this).isProductRegistered()==false)
		{
			final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.registration);
            dialog.setTitle("Registration");	    
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new OnKeyListener() 
            {	
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
				{
					// TODO Auto-generated method stub
					if(keyCode == KeyEvent.KEYCODE_BACK)
					{	
						System.exit(0);
					}
					return false;
				}
			});
            
            final EditText employeeId = (EditText)dialog.findViewById(R.id.EmployeeId);
            final EditText newPin = (EditText)dialog.findViewById(R.id.newpin);
            final EditText confirmPin = (EditText)dialog.findViewById(R.id.confirm_pin);
            
            Button okButton = (Button)dialog.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new OnClickListener() 
            {
				@Override
				public void onClick(View v)
				{
					if(isRegistrationValid(employeeId.getText().toString(),newPin.getText().toString(),confirmPin.getText().toString()))
					{
						dialog.dismiss();
						StringnewPin = newPin.getText().toString();
						context = OrderStartActivity.this;
						sendRegistrationSMS("0 1,"+employeeId.getText().toString()+",1,"+newPin.getText().toString()+","+confirmPin.getText().toString());			
					}
				}
				private boolean isRegistrationValid(String empId, String newPin,String confirmPin) 
				{
					if(empId.length()<8)
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
						alert.setTitle("Warning");
						alert.setMessage("Employee Id must be of 8 characters");
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
						return false;
					}
					if(isPasswordValid(newPin, confirmPin))
						return true;
					else
						return false;
				}
				private boolean isPasswordValid(String newPin,String confirmPin)
				{
					if(!newPin.equals(confirmPin))
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
						alert.setTitle("Warning");
						alert.setMessage("New Pin and Confirm Pin doesn't match");
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
						return false;
					}
					if(newPin.length()!=4)
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
						alert.setTitle("Warning");
						alert.setMessage("Pin must be of 4 characters");
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
						return false;
					}
					boolean charFound,digitFound;
					charFound = digitFound = false;
					for(int i=0;i<newPin.length();i++)
					{
						if(newPin.charAt(i)>='0'&&newPin.charAt(i)<='9')
						{
							digitFound = true;
						}
						else if((newPin.charAt(i)>='a'&&newPin.charAt(i)<='z')||(newPin.charAt(i)>='A'&&newPin.charAt(i)<='Z'))
						{
							charFound = true;
						}
					}
					if(digitFound&&charFound)
						return true;
					else
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
						alert.setTitle("Warning");
						alert.setMessage("Pin must be alphanumeric");
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
						return false;
					}
				}
			});
            dialog.show();
		}
		else
		{
			final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.login);
            dialog.setTitle("Login");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new OnKeyListener() 
            {	
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
				{
					// TODO Auto-generated method stub
					if(keyCode == KeyEvent.KEYCODE_BACK)
					{	
						System.exit(0);
					}
					return false;
				}
			});
            
            final EditText pin = (EditText)dialog.findViewById(R.id.pin);            
            Button okButton = (Button)dialog.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new OnClickListener() 
            {	
				@Override
				public void onClick(View v) 
				{
					if(checkPin(pin.getText().toString()))
					{
						dialog.dismiss();
					}
					else
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
						alert.setTitle("Warning");
						alert.setMessage("Pin didn't match");
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
				private boolean checkPin(String password)
				{
					return MySQLiteHelper.getInstance(OrderStartActivity.this).checkPin(password);
				}
			});
            dialog.show();
		}
		setContentView(R.layout.order_selection_main);
		setUpView();
		setTab();
		viewPager.setOffscreenPageLimit(adapter.getCount());
	}

	private void setUpView() 
	{
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(orderSelectionFragment = new OrderSelectionFragment());
		fragments.add(orderStatusFragment = new OrderStatusFragment());
		fragments.add(customerListFragment = new CustomerListFragment());
		fragments.add(productListFragment = new ProductListFragment());
		fragments.add(databoxFragment = new DataBoxFragment());
		fragments.add(syncFragment = new SyncFragment());
		
		adapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);

		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		TabIndex = 0;
		findViewById(R.id.tab1_indicator).setVisibility(View.VISIBLE);
		findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
		findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
		findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
		findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
		findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
	}
	
	@Override
    public void onBackPressed()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(OrderStartActivity.this);
		alert.setTitle("Exit");
		alert.setMessage("Do you want to close the application?");
		alert.setCancelable(false);
		alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				System.exit(0);
			}
		});
		alert.setPositiveButton("Cancel",new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alert.show();
    }
	private void setTab() 
	{
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int position) 
			{
				
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				
			}
			@Override
			public void onPageSelected(int position) 
			{
				TabIndex = position;
				switch (position) 
				{
				case 0:
					findViewById(R.id.tab1_indicator).setVisibility(View.VISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
					break;
				case 1:
					findViewById(R.id.tab1_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.VISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
					break;
				case 2:
					findViewById(R.id.tab1_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.VISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
					break;
				case 3:
					findViewById(R.id.tab1_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.VISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
					break;
				case 4:
					findViewById(R.id.tab1_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.VISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.INVISIBLE);
					databoxFragment.onDataSetChanged();
					break;
				case 5:
					findViewById(R.id.tab1_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab2_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab3_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab4_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab5_indicator).setVisibility(View.INVISIBLE);
					findViewById(R.id.tab6_indicator).setVisibility(View.VISIBLE);
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	private class TabPagerAdapter extends FragmentPagerAdapter
	{
		
		private ArrayList<Fragment> fragments;
		
		public TabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments)
		{
			super(fm);
			this.fragments = fragments;
		}
		@Override
		public Fragment getItem(int arg0) 
		{
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
	public void onClick(View v) 
	{
		switch (v.getId())
		{
			case R.id.order:
				TabIndex = 0;
				viewPager.setCurrentItem(0);
				break;
			case R.id.orderstatus:
				TabIndex = 1;
				viewPager.setCurrentItem(1);
				break;
			case R.id.customer:
				TabIndex = 2;
				viewPager.setCurrentItem(2);
				break;
			case R.id.product:
				TabIndex = 3;
				viewPager.setCurrentItem(3);
				break;
			case R.id.dataBox:
				TabIndex = 4;
				viewPager.setCurrentItem(4);
				break;
			case R.id.sync:
				TabIndex = 5;
				viewPager.setCurrentItem(5);
				break;
			default:
				break;
		}
	}
}