package com.mfast.square.ordercollection;

import java.util.ArrayList;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.FloatMath;

public class SMSSender 
{
	//private short SMS_PORT = 5901;
	private String phoneNumber;
	private SmsManager smsManager;
	static SMSSender sender=null;
	//= MySQLiteHelper.getInstance(OrderStartActivity.this).getServerPhoneNumber();
	public static SMSSender getInstance(Context context)
	{
		if(sender==null)
			sender = new SMSSender(context);
		return sender;
	}
	private SMSSender(Context context)
	{
		phoneNumber = MySQLiteHelper.getInstance(context).getServerPhoneNumber();
		smsManager = SmsManager.getDefault();
	}
	private ArrayList<String> splitMessage(String message)
	{
		ArrayList<String> parts = new ArrayList<String>();
		String formContent = message.substring(0,message.indexOf('|'));
		String sheetContent = message.substring(message.indexOf('|')+1);
		int numberOfParts = (int)FloatMath.ceil((sheetContent.length()*1.0f/130));
		int start=0;
		for(int i=0;i<numberOfParts;i++)
		{
			if((start+130)<sheetContent.length())
			{
				parts.add(formContent+"|"+sheetContent.substring(start,start+130));
				start += 130;
			}
			else
			{
				parts.add(formContent+"|"+sheetContent.substring(start));
			}
		}
		return parts;
	}
	public void send(String message)
	{
		if(message.length()>150)
		{
			//send SMS in Multiple Part
			ArrayList<String> parts = splitMessage(message);
			String messageText;
			for(int i=0;i<parts.size();i++)
			{
				messageText = parts.get(i)+"|"+(i+1)+","+parts.size();
				smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
			}
			//smsManager.sendMultipartTextMessage(destinationAddress, scAddress, parts, sentIntents, deliveryIntents)
		}
		else
			smsManager.sendTextMessage(phoneNumber, null, message+"|1,1", null, null);
			//smsManager.sendDataMessage(phoneNumber, null, SMS_PORT, message.getBytes(), null, null);
	}
}
