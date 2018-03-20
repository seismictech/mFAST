package com.mfast.square.ordercollection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class DBConnector extends SQLiteOpenHelper
{
	private static final int databaseVersion = 1;
	private static final String databaseName = "mFASTDatabase.db";
	private static DBConnector dbConn=null;
	public static DBConnector getInstance(Context context)
	{
		if(dbConn==null)
		{
			dbConn = new DBConnector(context,databaseName,null,databaseVersion);
		}
		return dbConn;
	}
	
	
	private DBConnector(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		//onCreate(dbConn.getWritableDatabase());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.beginTransaction();
		try
		{
			db.execSQL(CustomerHandler.CreateTable());
			db.execSQL(ProductHandler.CreateTable());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
		
	}
}
