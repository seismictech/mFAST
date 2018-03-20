package com.mfast.square.ordercollection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SyncFragment extends Fragment 
{
	private Button syncButton = null;
	private TextView dbVersionText = null;
	private String dbversion = null;
	private Spinner mSyncSpinner = null;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout=inflater.inflate(R.layout.sync_layout, container, false);
		dbVersionText = (TextView) layout.findViewById(R.id.dbVersion_text);
		dbVersionText.setText("Current DBVersion : "+MySQLiteHelper.getInstance(getActivity()).getVersionNo());
		
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.sync_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSyncSpinner=(Spinner)layout.findViewById(R.id.sync_chooser);
		mSyncSpinner.setAdapter(spinnerAdapter);
		syncButton = (Button) layout.findViewById(R.id.syncButton);
		syncButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				//MySQLiteHelper.getInstance(getActivity()).updateDBVersion("2117");
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert.setTitle("Sync Apps");
				alert.setMessage("Sync your app with mFAST? This might charge you.");
				alert.setCancelable(true);
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						System.out.println(mSyncSpinner.getSelectedItemPosition());
						TelephonyManager telMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
						String simSerial = telMgr.getLine1Number();
						if(MySQLiteHelper.getInstance(getActivity()).isSimSerialMatched(simSerial))
						{
							switch(mSyncSpinner.getSelectedItemPosition())
							{
								case 0:
									new DataCrawler(getActivity(),"_2").execute();
									break;
								case 1:
									new DataCrawler(getActivity(),"_2","999").execute();
									break;
							}
						}
						else
						{
							AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
						/*
						if(mSyncSpinner.getSelectedItemPosition()==0)  //Normal Sync
							new DataCrawler(getActivity(),"_2").execute();
						else if(mSyncSpinner.getSelectedItemPosition()==1) //Complete Sync
							new DataCrawler(getActivity(),"_2","999").execute();*/
						//dbVersionText.setText("Current DBVersion : "+MySQLiteHelper.getInstance(getActivity()).getVersionNo());
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
		return layout;
	}
}