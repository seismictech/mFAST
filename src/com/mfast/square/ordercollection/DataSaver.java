package com.mfast.square.ordercollection;

import android.content.Context;


public class DataSaver {
	String[] dataParts;
	DataSaver(String[] dataParts)
	{
		this.dataParts = dataParts;
	}
	void SaveData(Context context)
	{
		String startVersion = dataParts[0];
		if(dataParts.length>1)
		{
			String endVersion = dataParts[1];
			MySQLiteHelper.getInstance(context).updateDBVersion(endVersion);
	        String hasMore = dataParts[2];
	        String header,message;
			for(int i=3;i<dataParts.length;i++)
			{
				header = extractHeader(dataParts[i]);
				message = extractBody(dataParts[i]);
				HandleNewData(header,message,context);
				System.out.println(header+" "+message.length());
			}
		}
	}
	void upDateData(Context context)
	{
		String startVersion = dataParts[0];
		if(dataParts.length>1)
		{
			String endVersion = dataParts[1];
			MySQLiteHelper.getInstance(context).updateDBVersion(endVersion);
	        String hasMore = dataParts[2];
	        String header,message;
			for(int i=3;i<dataParts.length;i++)
			{
				header = extractHeader(dataParts[i]);
				message = extractBody(dataParts[i]);
				HandleUpdatedData(header,message,context);
				System.out.println(header+" "+message.length());
			}
		}
	}
	private void HandleUpdatedData(String header, String message,Context context) 
	{
		if(header.equals("_18"))
		{
			MySQLiteHelper.getInstance(context).updateIntoProductTable(message);
		}
		else if(header.equals("_19"))
		{
			MySQLiteHelper.getInstance(context).updateIntoCustomerTable(message);
		}
	}
	private void HandleNewData(String header, String message,Context context) 
	{
		if(header.equals("_18"))
		{
			MySQLiteHelper.getInstance(context).insertIntoProductTable(message);
		}
		else if(header.equals("_19"))
		{
			MySQLiteHelper.getInstance(context).insertIntoCustomerTable(message);
		}
//		DBConnector dbConn = DBConnector.getInstance(context);	
//		DBHandler dbhandler = GetHandler(header,dbConn);
//		
//		if(dbhandler!=null)
//		{
//			String[] rows = message.split("\\;");
//			String[] fields;
//			for(int i=0;i<rows.length;i++)
//			{
//				fields = rows[i].split("\\,");
//				if(fields[2].equals("0"))
//				{
//					dbhandler.delete(fields[0]);					
//				}
//				else
//				{
//					dbhandler.insert(fields[0],fields[1]);
//				}
//			}
//		}
	}
//	public static DBHandler GetHandler(String header, DBConnector dbConn) 
//	{
//		if(header.startsWith("_"))
//		{
//			switch(Integer.parseInt(header.substring(1)))
//			{
//				case 18:
//					return new CustomerHandler(dbConn);
//				case 19:
//					return new ProductHandler(dbConn);
//				default:
//					return null;
//			}
//		}
//		return null;
//	}
	public static String extractHeader(String message) 
	{
	    int index = message.indexOf(';');
	    String header = "0";

	    try {
	      if (index > 0) {
	        header = message.substring(0, index);
	      }
	    }
	    catch (NumberFormatException nfe) {
	      header = "0";
	    }
	    return header;
	}
	public static String extractBody(String message) 
	{
	    int headerEnd = message.indexOf(';');
	    if (headerEnd < 0) {
	      return message;
	    }
	    else {
	      return message.substring(headerEnd + 1, message.length());
	    }
	}
}