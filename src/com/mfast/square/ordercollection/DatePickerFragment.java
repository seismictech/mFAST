package com.mfast.square.ordercollection;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment
{
	OnDateSetListener onDateSet;
	public DatePickerFragment()
	{
		
	}
	public void setCallBack(OnDateSetListener onDateSet)
	{
		this.onDateSet = onDateSet;
	}
	private int month,day,year;
	
	@Override
	public void setArguments(Bundle args)
	{
		super.setArguments(args);
		year = args.getInt("year");
		month = args.getInt("month");
		day = args.getInt("day");
	}
	
	@Override
	public Dialog onCreateDialog(Bundle args)
	{
		return new DatePickerDialog(getActivity(),onDateSet,year,month,day);
	}
}
