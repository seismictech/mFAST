package com.mfast.square.ordercollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver 
{
	final SmsManager sms = SmsManager.getDefault();
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		final Bundle bundle = intent.getExtras();
		try
		{
			if (bundle != null)
			{
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
	            for (int i = 0; i < pdusObj.length; i++)
	            {
	            	SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
	            	String phoneNumber = currentMessage.getDisplayOriginatingAddress();
	                     
	            	String senderNum = phoneNumber;
	            	if(senderNum!=null&&senderNum.equals("+8801730320365"))
	            	{
		            	String message = currentMessage.getDisplayMessageBody();		            	
		            	Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
		 
		            	
		                String[] parts = message.split("\\;");
		                String header = parts[0];
		                if(header.equals("_41"))
		                {
		                	OrderStartActivity.onPostRegistrationSuccessful(parts[1].split("\\,"));
		                }
		                else if(header.equals("_20"))
		                {
		                	//Order through SMS
		                	String orderId = (parts[1].split("\\,"))[0];
		                	DataSender.OnSMSChannelResponse(orderId);
		                }
	            	}
	            }
			}		
		}
		catch (Exception e) 
		{
			Toast.makeText(context,"message: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("SmsReceiver", "Exception smsReceiver" +e);       
	    }
	}
}