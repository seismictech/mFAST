package com.mfast.square.ordercollection;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ItemsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState!=null)
		{
			return;
		}
		ItemSelectionFragment fragment=new ItemSelectionFragment();
		FragmentManager manager=getSupportFragmentManager();
		FragmentTransaction ft=manager.beginTransaction();
		
		ft.add(android.R.id.content, fragment);
		ft.commit();
	}



}
