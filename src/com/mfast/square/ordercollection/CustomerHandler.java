package com.mfast.square.ordercollection;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CustomerHandler implements DBHandler
{
	private static final String tableName = "Customer";
	private static final String IdColumn = "CustomerId";
	private static final String ValueColumn = "CustomerName";
	DBConnector dbConn=null;
	CustomerHandler(DBConnector dbConn)
	{
		this.dbConn = dbConn;
	}
	@Override
	public void insert(String code, String value) 
	{
		ContentValues values = new ContentValues();
		values.put(IdColumn, code);
		values.put(ValueColumn, value);
		
		SQLiteDatabase db = dbConn.getWritableDatabase();
		db.insert(tableName, null, values);
		db.close(); 
	}
	@Override
	public void delete(String code) 
	{
		SQLiteDatabase db = dbConn.getWritableDatabase();
		
		db.delete(tableName,IdColumn+" = ?",new String[] {code});
		db.close();
	}
	@Override
	public String select(String code) 
	{
		String retStr = null;
		String query = "Select * from "+tableName+" where "+IdColumn +" = "+code;
		SQLiteDatabase db = dbConn.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			cursor.moveToFirst();
			retStr = cursor.getString(1);
		}
		cursor.close();
		db.close();
		return retStr;
	}
	public static String CreateTable()
	{
		String createTable = "CREATE TABLE "+ tableName+ "( "+ IdColumn + " INTEGER PRIMARY KEY, "+ ValueColumn +" TEXT)";
		return createTable;
	}
	@Override
	public List<NameValuePair> selectAll() 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String query = "Select * from "+tableName;
        SQLiteDatabase db = dbConn.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				nameValuePairs.add(new BasicNameValuePair(cursor.getString(0), cursor.getString(1)));
			}
			while(cursor.moveToNext());
		}
        return nameValuePairs;
	}	
}