package com.mfast.square.ordercollection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class QuantityInputDialog extends DialogFragment
{
	String title;
	public QuantityInputDialog(String title)
	{
		this.title = title;
	}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
        	@Override
			public void onClick(DialogInterface dialog, int id) 
        	{
               // FIRE ZE MISSILES!
        	}
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
        	@Override
			public void onClick(DialogInterface dialog, int id) 
        	{
        		dialog.dismiss();
        	}
        });
        return builder.create();
    }
}