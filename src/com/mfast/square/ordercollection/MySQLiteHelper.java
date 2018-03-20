package com.mfast.square.ordercollection;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;
public class MySQLiteHelper extends SQLiteOpenHelper 
{
	private static MySQLiteHelper instance;
	private static Context context;
	private static final String DATABASE_NAME = "mfastnd.db";
	private static final int DATABASE_VERSION = 2;

	public static final String DBVersiontableName = "DBVersion";
	public static final String DBVersionColumn = "VersionNo";
  
	public static final String ServerIPtableName = "ServerIP";
	public static final String IP1Column = "IP1";
	public static final String IP2Column = "IP2";
	
	public static final String UserPhonetableName = "usersim";
	public static final String simSerialColumn = "simserial";
	
	public static final String InfotableName = "Info";
	public static final String ServerNumbertableName = "number";
	public static final String PasswordColumn = "pswrd";
	public static final String NumberColumn = "num";
	public static final String SerialNoColumn = "Serial";
  
	public static final String ProducttableName = "Product";
	public static final String ProductIdColumn = "_id";
	public static final String ProductValueColumn = "ProductName";
	public static final String ProductPackSizeColumn = "ProductPackSize";
	public static final String ProductUnitVat = "UnitVat";
	public static final String ProductUnitPrice = "UnitPrice";
	  
	public static final String CustomertableName = "CustomerTable";
	public static final String CustomerIdColumn = "_id";
	public static final String CustomerValueColumn = "CustomerName";
	public static final String CustomerAddressColumn = "Address";
	public static final String CustomerRoadColumn = "Road";
	public static final String CustomerDiscountColumn = "Discount";
	  
	public static final String SentBox = "SentBox";
	public static final String InBox = "InBox";
	public static final String DraftsBox = "DraftsBox";
	public static final String OrderIdColumn = "_id";
	public static final String dateColumn = "date";
	public static final String FormatStringColumn = "FormatString";
	public static final String OrderChannelColumn = "channel";
	public static final String IsMultipleSMSColumn = "multiSMS";
	  
	  // Database creation sql statement
	private static final String createProductTable = "CREATE TABLE Product ( "+ ProductIdColumn + " INTEGER PRIMARY KEY, "+ ProductValueColumn +" TEXT, "
	  +ProductUnitPrice+" REAL, "+ProductUnitVat+" REAL, "+ProductPackSizeColumn+" TEXT)";
	  
	private static final String createCustomerTable = "CREATE TABLE "+ CustomertableName+ "( "+ CustomerIdColumn + " INTEGER PRIMARY KEY, "+ CustomerValueColumn +" TEXT, "
	  +CustomerAddressColumn+" TEXT, "+CustomerRoadColumn+" TEXT, "+CustomerDiscountColumn+" REAL)";
	  
	private static final String createDBVersion = "CREATE TABLE "+ DBVersiontableName+ "( "+ DBVersionColumn + " INTEGER )";
	private static final String createSentBox = "CREATE TABLE "+ SentBox+ "( "+ OrderIdColumn + " INTEGER PRIMARY KEY, "+dateColumn+" TEXT, " + OrderChannelColumn + " TEXT, " + IsMultipleSMSColumn + " TEXT, " + FormatStringColumn +" TEXT)";
	private static final String createDraftsBox = "CREATE TABLE "+ DraftsBox + "( "+ OrderIdColumn + " INTEGER PRIMARY KEY AUTOINCREMENT, "+dateColumn+" TEXT, "+ FormatStringColumn +" TEXT)";
	private static final String createInBox = "CREATE TABLE "+ InBox + "( "+ OrderIdColumn + " INTEGER PRIMARY KEY, "+dateColumn+" TEXT, "+ FormatStringColumn +" TEXT)";
	private static final String createServerIP = "CREATE TABLE "+ ServerIPtableName + "( "+ IP1Column+" TEXT, "+ IP2Column +" TEXT)";
	private static final String createInfo = "CREATE TABLE "+ InfotableName + "( "+ PasswordColumn +" TEXT, "+ SerialNoColumn +" TEXT)";
	private static final String createServerPhoneNumber = "CREATE TABLE "+ ServerNumbertableName + "( "+ NumberColumn +" TEXT)";
	private static final String createUserSimNumber = "CREATE TABLE "+ UserPhonetableName + "( "+ simSerialColumn +" TEXT)";
	public MySQLiteHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		database.execSQL(createDBVersion);
		database.execSQL(createCustomerTable);
		database.execSQL(createProductTable);
		database.execSQL(createSentBox);
		database.execSQL(createDraftsBox);
		database.execSQL(createInBox);
		database.execSQL(createServerIP);
		database.execSQL(createInfo);
		database.execSQL(createServerPhoneNumber);
		database.execSQL(createUserSimNumber);
		initializeInfo("", "", database);
		insertIntoDBVersion("999",database);
		insertIntoServerNumber("+8801730320365", database);
	}
	private void insertIntoServerIP(String ip1, String ip2, SQLiteDatabase db) 
	{
		try 
		{
			ContentValues values = new ContentValues();
			values.put(IP1Column, ip1);
			values.put(IP2Column, ip2);
			db.insert(ServerIPtableName, null, values);
			
			//Cursor cursor = db.rawQuery("select * from "+ServerIPtableName, null);
			//System.out.println("Row Count = "+cursor.getCount());
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	public void insertSimSerial(String simSerial) 
	{
		try 
		{
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(simSerialColumn, simSerial);
			db.insert(UserPhonetableName, null, values);
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	public String getSimSerial()
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String simSerial = null;
		try 
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select * from " + UserPhonetableName, null);
			if(cursor.moveToFirst())
			{
				System.out.println("Sim Serial = " + cursor.getString(0));
				simSerial = cursor.getString(0);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
		}	
		return simSerial;
	}
	public boolean isSimSerialMatched(String simSerial)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select * from " + UserPhonetableName + " where " 
					+ simSerialColumn + " = '" + simSerial + "'", null);
			System.out.println("Count = " + cursor.getCount());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
		}
		if(cursor.getCount()>0)
			return true;
		else
			return false;
	}
	public void insertIntoServerIP(String ip1, String ip2) 
	{
		try 
		{
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(IP1Column, ip1);
			values.put(IP2Column, ip2);
			db.insert(ServerIPtableName, null, values);
			
			//Cursor cursor = db.rawQuery("select * from "+ServerIPtableName, null);
			//System.out.println("Row Count = "+cursor.getCount());
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	public void updateServerIP(String ip1, String ip2) 
	{
		try 
		{
			SQLiteDatabase db = getWritableDatabase();
			db.delete(ServerIPtableName, null, null);
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
			
		}
		insertIntoServerIP(ip1, ip2);
	}
	public void SwapServerIP() 
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String serverIP1 = null,serverIP2 = null;
		try 
		{
			db = getWritableDatabase();
			cursor = db.rawQuery("select * from "+ServerIPtableName, null);
			if(cursor.moveToFirst())
			{
				serverIP1 = cursor.getString(0);
				serverIP2 = cursor.getString(1);
			}
			db.delete(ServerIPtableName, null, null);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
		try 
		{
			ContentValues values = new ContentValues();
			values.put(IP1Column, serverIP2);
			values.put(IP2Column, serverIP1);
			db.insert(ServerIPtableName, null, values);
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	private void insertIntoServerNumber(String number, SQLiteDatabase db) 
	{
		try 
		{
			ContentValues values = new ContentValues();
			values.put(NumberColumn, number);
			db.insert(ServerNumbertableName, null, values);
			Cursor cursor = db.rawQuery("select * from "+ServerNumbertableName, null);
			cursor.moveToFirst();
			System.out.println("Server Phone numner: "+cursor.getString(0));
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	public void initializeInfo(String password, String serialNo, SQLiteDatabase db) 
	{
		try 
		{
			ContentValues values = new ContentValues();
			values.put(PasswordColumn, password);
			values.put(SerialNoColumn, serialNo);
			db.insert(InfotableName, null, values);
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally 
		{
		}
	}
	public static MySQLiteHelper getInstance(Context contxt) 
	{
		if (instance == null) 
		{
			context = contxt;
			instance = new MySQLiteHelper(contxt);
		}
		return instance;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(MySQLiteHelper.class.getName(),"Upgrading database from version " + oldVersion + 
	    		" to " + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + ProducttableName);
	    db.execSQL("DROP TABLE IF EXISTS " + CustomertableName);
	    db.execSQL("DROP TABLE IF EXISTS " + SentBox);
	    db.execSQL("DROP TABLE IF EXISTS " + DraftsBox);
	    db.execSQL("DROP TABLE IF EXISTS " + InBox);
	    db.execSQL("DROP TABLE IF EXISTS " + DBVersiontableName);
	    db.execSQL("DROP TABLE IF EXISTS " + ServerIPtableName);
	    db.execSQL("DROP TABLE IF EXISTS " + ServerNumbertableName);
	    db.execSQL("DROP TABLE IF EXISTS " + InfotableName);
	    db.execSQL("DROP TABLE IF EXISTS " + UserPhonetableName);
	    System.out.println("Upgrading the database. Tables are deleted");
	    onCreate(db);
	}
	public void DeleteAllTables()
	{
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  db.execSQL("DROP TABLE IF EXISTS " + ProducttableName);
			  db.execSQL("DROP TABLE IF EXISTS " + CustomertableName);
			  //db.execSQL("DROP TABLE IF EXISTS " + SentBox);
			  //db.execSQL("DROP TABLE IF EXISTS " + DraftsBox);
			  //db.execSQL("DROP TABLE IF EXISTS " + InBox);
			  db.execSQL(createCustomerTable);
			  db.execSQL(createProductTable);
			  //db.execSQL(createSentBox);
			  //db.execSQL(createDraftsBox);
			  //db.execSQL(createInBox);
			  //db.execSQL("DROP TABLE IF EXISTS " + DBVersiontableName);
			  //db.execSQL("DROP TABLE IF EXISTS " + ServerIPtableName);
			  //onCreate(db);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void updateDBVersion(String versionNo)
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = new ContentValues();
			  values.put(DBVersionColumn, versionNo);
			  //Cursor cursor = db.rawQuery("select * from " + DBVersiontableName, null);
			  //System.out.println("Row Count of DbVersion = "+cursor.getCount());
			  db.update(DBVersiontableName, values, null, null);
			  //cursor = db.rawQuery("select * from "+DBVersiontableName, null);
			  //System.out.println("Row Count of DbVersion = "+cursor.getCount());
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void updatePin(String pin)
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = new ContentValues();
			  values.put(PasswordColumn, pin);
			  db.update(InfotableName, values, null, null);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void insertIntoDBVersion(String versionNo,SQLiteDatabase db)
	  {
		  try 
		  {
			  ContentValues values = new ContentValues();
			  values.put(DBVersionColumn, versionNo);
			  db.insert(DBVersiontableName, null, values);
			  Cursor cursor = db.rawQuery("select * from "+DBVersiontableName, null);
			  System.out.println("Row Count = "+cursor.getCount());
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void insertIntoSentBox(String orderId,String formatString,String orderChannel,boolean IsMultipleSMS) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = new ContentValues();
			  values.put(OrderIdColumn, orderId);
			  values.put(FormatStringColumn, formatString);
			  values.put(OrderChannelColumn, orderChannel);
			  if(IsMultipleSMS==true)
				  values.put(IsMultipleSMSColumn, 1);
			  else
				  values.put(IsMultipleSMSColumn, 0);
			  Time now = new Time();
			  now.setToNow();
			  values.put(dateColumn, now.monthDay+"-"+(now.month+1)+"-"+now.year);
			  db.insert(SentBox, null, values);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
			  
		  }
	  }
	  public void insertIntoSentBox(String formatString,String orderChannel,boolean IsMultipleSMS) 
	  {
		  if(orderChannel.equals(context.getResources().getString(R.string.SMS)))
		  {
			  SQLiteDatabase db = null;
			  try 
			  {
				  int maxOrderId = getMaxOrderIdFromSentBox();
				  db = getWritableDatabase();
				  ContentValues values = new ContentValues();
				  values.put(OrderIdColumn, String.valueOf(maxOrderId+1));
				  values.put(FormatStringColumn, formatString);
				  values.put(OrderChannelColumn, orderChannel);
				  Time now = new Time();
				  now.setToNow();
				  if(IsMultipleSMS==true)
					  values.put(IsMultipleSMSColumn, 1);
				  else
					  values.put(IsMultipleSMSColumn, 0);
				  values.put(dateColumn, now.monthDay+"-"+(now.month+1)+"-"+now.year);
				  db.insert(SentBox, null, values);
			  }
			  catch(Exception e)
			  {
				  e.printStackTrace();
			  }
			  finally 
			  {
				  
			  }
		  }
	  }
	  private int getMaxOrderIdFromSentBox()
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  int maxOrderId = -1;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.rawQuery("select max("+OrderIdColumn+") from "+SentBox, null);
			  if(cursor.moveToFirst())
			  {
				  maxOrderId = Integer.parseInt(cursor.getString(0));
			  }
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return maxOrderId;
	  }
	  public Cursor getSentBox(String mCurFilter) 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.query(SentBox, new String[] { OrderIdColumn, dateColumn,FormatStringColumn,OrderChannelColumn }, null,
					  null, null, null, OrderIdColumn + " asc");
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return cursor;
	  }
	  public String getServerIP()
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  String serverIP = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.rawQuery("select * from "+ServerIPtableName, null);
			  if(cursor.moveToFirst())
			  {
				  System.out.println("Server IP = " + cursor.getString(0));
				  serverIP = cursor.getString(0);
			  }
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
		  }
		  return serverIP;
	  }
	  public String getVersionNo()
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  String versionNo = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.rawQuery("select * from "+DBVersiontableName, null);
			  if(cursor.moveToFirst())
			  {
				  System.out.println("Row Count of DBVersion = "+cursor.getCount());
				  versionNo = String.valueOf(cursor.getInt(cursor.getColumnIndex(DBVersionColumn)));
			  }
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				  System.out.println("Rows returned "+cursor.getCount());
		  }
		  return versionNo;
	  }
	  public Cursor getSentBox() 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
//			  String query = "select " + OrderIdColumn + "," + dateColumn + "," + OrderChannelColumn + "," + FormatStringColumn + "," + CustomerValueColumn +
//					  " from "+ SentBox + "," + CustomertableName + " where " + FormatStringColumn + " like '1 " + CustomerIdColumn + ",%'" + 
//					  " order by " + OrderIdColumn + " desc";
			  String query = "select S." + OrderIdColumn + "," + dateColumn + "," + OrderChannelColumn + "," + FormatStringColumn + "," + CustomerValueColumn + "," + IsMultipleSMSColumn + 
					  " from "+ SentBox + " S," + CustomertableName +" C where substr("+FormatStringColumn+",3,6) = C." + CustomerIdColumn + " order by S." + OrderIdColumn + " desc";
			  cursor = db.rawQuery(query, null);
//			  cursor = db.query(SentBox, new String[] { OrderIdColumn, dateColumn,FormatStringColumn,OrderChannelColumn }, null,
//					  null, null, null, OrderIdColumn + " desc");
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return cursor;
	  }
	  public int getMaxOrderId() 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  String orderId = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.rawQuery("select max("+OrderIdColumn+") from "+OrderChannelColumn, null);
			  if(cursor.moveToFirst())
				  orderId = cursor.getString(0);
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return Integer.parseInt(orderId);
	  }
	  public void insertIntoDraftBox(String formatString) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = new ContentValues();
			  values.put(FormatStringColumn, formatString);
			  Time now = new Time();
			  now.setToNow();
			  values.put(dateColumn, now.monthDay+"-"+(now.month+1)+"-"+now.year);
			  db.insert(DraftsBox, null, values);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public Cursor getDraftBox(String mCurFilter) 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.query(DraftsBox, new String[] { OrderIdColumn, dateColumn,FormatStringColumn }, null,
					  null, null, null, OrderIdColumn + " asc");
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return cursor;
	  }
	  public void ClearDraftBox()
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  db.delete(DraftsBox, null, null);
			  //db.insert(DraftsBox, null, values);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public Cursor getDraftBox() 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
			  String query = "select D." + OrderIdColumn + "," + dateColumn + "," + FormatStringColumn + "," + CustomerValueColumn +
					  " from "+ DraftsBox + " D," + CustomertableName +" C where substr("+FormatStringColumn+",3,6) = C." + CustomerIdColumn + " order by D." + OrderIdColumn + " desc";
			  cursor = db.rawQuery(query, null);
//			  cursor = db.query(DraftsBox, new String[] { OrderIdColumn, dateColumn,FormatStringColumn }, null,
//					  null, null, null, OrderIdColumn + " asc");
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
				cursor.getCount();
		  }
		  return cursor;
	  }
	  public void insertIntoProductTable(String message) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  int inserted = 0;
			  int deleted = 0;
			  db = getWritableDatabase();
			  ContentValues values = null;
			  String[] rows = message.split("\\;");
			  String[] fields = new String[3];
			  for(int i=0;i<rows.length;i++)
			  {
				  //fields = rows[i].split("\\,");
				  fields[0] = rows[i].substring(0, rows[i].indexOf(','));
				  fields[1] = rows[i].substring(rows[i].indexOf(',')+1, rows[i].lastIndexOf(','));
				  fields[2] = rows[i].substring(rows[i].lastIndexOf(',')+1,rows[i].length());
				  if(fields[2].equals("1"))
				  {
					  deleted++;
					  db.delete(ProducttableName,ProductIdColumn+" = ?",new String[] {fields[0]});
					  //dbhandler.delete(fields[0]);					
				  }
				  else
				  {
					  inserted++;
					  values = new ContentValues();
					  values.put(ProductIdColumn, fields[0]);
					  String[] parts = fields[1].split("\\~");
					  values.put(ProductValueColumn, parts[0]);
					  values.put(ProductPackSizeColumn, parts[1]);
					  values.put(ProductUnitPrice, parts[2]);
					  values.put(ProductUnitVat, parts[3]);
					  db.insert(ProducttableName, null, values);
				  }
			  }
			  System.out.println("Product Inserted :"+ inserted+" Deleted :"+deleted);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void updateIntoProductTable(String message) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  int inserted = 0;
			  int deleted = 0;
			  db = getWritableDatabase();
			  ContentValues values = null;
			  String[] rows = message.split("\\;");
			  String[] fields = new String[3];
			  for(int i=0;i<rows.length;i++)
			  {
				  //fields = rows[i].split("\\,");
				  fields[0] = rows[i].substring(0, rows[i].indexOf(','));
				  fields[1] = rows[i].substring(rows[i].indexOf(',')+1, rows[i].lastIndexOf(','));
				  fields[2] = rows[i].substring(rows[i].lastIndexOf(',')+1,rows[i].length());
				  if(fields[2].equals("1"))
				  {
					  deleted++;
					  db.delete(ProducttableName,ProductIdColumn+" = ?",new String[] {fields[0]});
					  //dbhandler.delete(fields[0]);					
				  }
				  else
				  {
					  inserted++;
					  values = new ContentValues();
					  values.put(ProductIdColumn, fields[0]);
					  String[] parts = fields[1].split("\\~");
					  values.put(ProductValueColumn, parts[0]);
					  values.put(ProductPackSizeColumn, parts[1]);
					  values.put(ProductUnitPrice, parts[2]);
					  values.put(ProductUnitVat, parts[3]);
					  //db.delete(ProducttableName,ProductIdColumn+" = ?",new String[] {fields[0]});
					  if(db.update(ProducttableName, values, ProductIdColumn+" = ?", new String[]{fields[0]})==0)
						  db.insert(ProducttableName, null, values);
				  }
			  }
			  System.out.println("Product Inserted :"+ inserted+" Deleted :"+deleted);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public void insertIntoCustomerTable(String message) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  int deleted=0;
			  int inserted=0;
			  db = getWritableDatabase();
			  ContentValues values = null;
			  String[] rows = message.split("\\;");
			  String[] fields = new String[3];
			  for(int i=0;i<rows.length;i++)
			  {
				  //fields = rows[i].split("\\,");
				  fields[0] = rows[i].substring(0, rows[i].indexOf(','));
				  fields[1] = rows[i].substring(rows[i].indexOf(',')+1, rows[i].lastIndexOf(','));
				  fields[2] = rows[i].substring(rows[i].lastIndexOf(',')+1,rows[i].length());	  
				  if(fields[2].equals("1"))
				  {
					  deleted++;
					  db.delete(CustomertableName,CustomerIdColumn+" = ?",new String[] {fields[0]});
					  //dbhandler.delete(fields[0]);					
				  }
				  else
				  {
					  inserted++;
					  values = new ContentValues();
					  values.put(CustomerIdColumn, fields[0]);
					  String[] parts = fields[1].split("\\~");
					  values.put(CustomerValueColumn, parts[0]);
					  values.put(CustomerAddressColumn, parts[1]);
					  values.put(CustomerRoadColumn, parts[2]);
					  values.put(CustomerDiscountColumn, parts[3]);
					  db.insert(CustomertableName, null, values);
				  }
			  }
			  System.out.println("Customer Inserted :"+ inserted+" Deleted :"+deleted);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public double getDiscountRate(String customerId)
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor = db.rawQuery("select * from "+CustomertableName+" where "+CustomerIdColumn+"="+customerId, null);
		  }
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  if (cursor != null)
			  {
				  cursor.moveToFirst();
				  cursor.getCount();
			  }
		  }
		  return Double.parseDouble(cursor.getString(cursor.getColumnIndex(CustomerDiscountColumn)));
	  }
	  public void updateIntoCustomerTable(String message) 
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  int deleted=0;
			  int inserted=0;
			  db = getWritableDatabase();
			  ContentValues values = null;
			  String[] rows = message.split("\\;");
			  String[] fields = new String[3];
			  for(int i=0;i<rows.length;i++)
			  {
				  //fields = rows[i].split("\\,");
				  fields[0] = rows[i].substring(0, rows[i].indexOf(','));
				  fields[1] = rows[i].substring(rows[i].indexOf(',')+1, rows[i].lastIndexOf(','));
				  fields[2] = rows[i].substring(rows[i].lastIndexOf(',')+1,rows[i].length());	  
				  if(fields[2].equals("1"))
				  {
					  deleted++;
					  db.delete(CustomertableName,CustomerIdColumn+" = ?",new String[] {fields[0]});
					  //dbhandler.delete(fields[0]);					
				  }
				  else
				  {
					  inserted++;
					  values = new ContentValues();
					  values.put(CustomerIdColumn, fields[0]);
					  String[] parts = fields[1].split("\\~");
					  values.put(CustomerValueColumn, parts[0]);
					  values.put(CustomerAddressColumn, parts[1]);
					  values.put(CustomerRoadColumn, parts[2]);
					  values.put(CustomerDiscountColumn, parts[3]);
					  if(db.update(CustomertableName, values, CustomerIdColumn+" = ?", new String[]{fields[0]})==0)
						  db.insert(CustomertableName, null, values);
				  }
			  }
			  System.out.println("Customer Inserted :"+ inserted+" Deleted :"+deleted);
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  String[] getRowCount()
	  {
		  SQLiteDatabase db = null;
		  String[] rowCount = new String[3];
		  try
		  {
			  db = getWritableDatabase();
			  
			  Cursor cursor = null;
			  cursor = db.rawQuery("select *  from "+ DraftsBox, null);
			  rowCount[0] = String.valueOf(cursor.getCount());
			  cursor = db.rawQuery("select * from "+ InBox, null);
			  rowCount[1] = String.valueOf(cursor.getCount());
			  cursor = db.rawQuery("select * from "+ SentBox, null);
			  rowCount[2] = String.valueOf(cursor.getCount());
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return rowCount;
	  }
	  void deleteFromDraftBox(String _id)
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = null;
			  db.delete(DraftsBox,OrderIdColumn+" = ?",new String[] {_id});
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
			  db.close();
		  }
	  }
	  void deleteFromSentBox(String _id)
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  ContentValues values = null;
			  db.delete(SentBox,OrderIdColumn+" = ?",new String[] {_id});
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
			  
		  }
	  }
	  void deleteMultipleOrderFromDraftBox(String orderIds)
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  System.out.println(String.format("delete from "+DraftsBox+" where "+OrderIdColumn+" in (%s);", orderIds));
			  db.execSQL(String.format("delete from "+DraftsBox+" where "+OrderIdColumn+" in (%s);", orderIds));
			  db.close();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  void clearSentBox()
	  {
		  SQLiteDatabase db = null;
		  try 
		  {
			  db = getWritableDatabase();
			  db.delete(SentBox, null, null);
			  db.close();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
		  }
	  }
	  public Cursor getCustomers() {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getReadableDatabase();
//				cursor = db.query(TABLE_CUSTOMER, new String[] { COLUMN_ID,COLUMN_CUSTOMER,COLUMN_CUSTOMER_ADDRESS }, null,
//						null, null, null, COLUMN_CUSTOMER + " asc");
				//cursor = db.rawQuery("select * from Customer", null);
				cursor = db.query(CustomertableName, new String[] { CustomerIdColumn,CustomerValueColumn,CustomerAddressColumn }, null,
						null, null, null, CustomerValueColumn + " asc");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.getCount();
				
					
			}
			return cursor;
		}
	  
	  public Cursor getProducts() {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getReadableDatabase();
//				cursor = db.query(TABLE_ITEM, new String[] { COLUMN_ID,COLUMN_ITEM_NAME,COLUMN_ITEM_VALUE,COLUMN_ITEM_VAT }, null,
//						null, null, null, COLUMN_ITEM_NAME + " asc");
				cursor = db.query(ProducttableName, new String[] { ProductIdColumn,ProductValueColumn,ProductUnitPrice,ProductUnitVat }, null,
						null, null, null, ProductValueColumn + " asc");
//				cursor=db.rawQuery("Select * from item where _id in ('1','2')", null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.getCount();
				
					
			}
			return cursor;
		}
	  public Cursor getProducts(String QueryString) 
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try 
		  {
			  db = getReadableDatabase();
			  cursor=db.rawQuery("Select * from "+ProducttableName+" where _id in ("+QueryString+") order by "+ProductValueColumn+" asc", null);
		  }
		  catch (Exception e) 
		  {
			e.printStackTrace();
		  }
		  finally 
		  {
			if (cursor != null)
				cursor.getCount();	
		  }
		  return cursor;
	  }
	  public Cursor getProducts(String QueryString,String filterString)
	  {
		  SQLiteDatabase db = null;
		  Cursor cursor = null;
		  try
		  {
			  db = getReadableDatabase();
			  cursor=db.rawQuery("Select * from "+ProducttableName+" where _id in ("+QueryString+") and " 
			  + ProductValueColumn + " LIKE '"+filterString+"%' order by "+ProductValueColumn+" asc", null);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();			  
		  }
		  finally
		  {
			  if(cursor!=null)
				  cursor.getCount();
		  }
		  return cursor;
	  }
	  public Cursor getItems(String dataString) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getReadableDatabase();
//				cursor = db.query(TABLE_ITEM, new String[] { COLUMN_ID,COLUMN_ITEM_NAME,COLUMN_ITEM_VALUE,COLUMN_ITEM_VAT }, null,
//						null, null, null, COLUMN_ITEM_NAME + " asc");
				cursor = db.query(ProducttableName, new String[] { ProductIdColumn,ProductValueColumn,ProductUnitPrice,ProductUnitVat }, null,
						null, null, null, ProductValueColumn + " asc");
				
				//cursor=db.rawQuery("Select * from item where _id in ('1','2')", null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.getCount();	
			}
			return cursor;
		}
	  
	  public Cursor getCustomersFiltered(String searchedString) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getReadableDatabase();
				Log.d("Mkhan", "Searched "+ searchedString );
				cursor = db.query(CustomertableName, new String[] { CustomerIdColumn,CustomerValueColumn,CustomerAddressColumn }, CustomerValueColumn+" LIKE '"+searchedString+"%' ",
						null, null, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.getCount();
				
					
			}
			return cursor;
		}
	  public Cursor getProductsFiltered(String searchedString) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getReadableDatabase();
				Log.d("Mkhan", "Searched "+ searchedString );

//				cursor = db.query(TABLE_ITEM, new String[] { COLUMN_ID,COLUMN_ITEM_NAME,COLUMN_ITEM_VALUE,COLUMN_ITEM_VAT }, null,
//						null, null, null, COLUMN_ITEM_NAME + " asc");
				
				cursor = db.query(ProducttableName, new String[] { ProductIdColumn,ProductValueColumn,ProductUnitPrice,ProductUnitVat }, ProductValueColumn + " LIKE '"+searchedString+"%' ",
						null, null, null, ProductValueColumn + " asc");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.getCount();
			}
			return cursor;
		}

	public String getCustomer(String selectedCustomerId) 
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = getReadableDatabase();			
			cursor = db.rawQuery("select "+CustomerValueColumn+" from "+CustomertableName+" where "+CustomerIdColumn +" = '"+selectedCustomerId+"'", null);
			if(cursor.moveToFirst())
			{
				return cursor.getString(cursor.getColumnIndex(CustomerValueColumn));
			}
			else
				return null;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.getCount();
		}
		return null;
	}
	public boolean isProductRegistered()
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try 
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select count(*) from "+InfotableName +" where "+PasswordColumn+"=''", null);
			cursor.moveToFirst();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.getCount();
		}
		return Integer.parseInt(cursor.getString(0))==0;
	}
	public boolean checkPin(String password) 
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try 
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select count(*) from "+InfotableName +" where "+PasswordColumn+"='"+password+"'", null);
			cursor.moveToFirst();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.getCount();
		}
		return Integer.parseInt(cursor.getString(0))==1;
	}
	public String getServerPhoneNumber() 
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String number = null;
		try
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select * from "+ServerNumbertableName, null);
			cursor.moveToFirst();
			number = cursor.getString(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.getCount();
		}
		return number;
	}
	public String getSerialNo() 
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String Serial = null;
		try
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select * from "+InfotableName, null);
			cursor.moveToFirst();
			Serial = cursor.getString(cursor.getColumnIndex(SerialNoColumn));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.getCount();
		}
		return Serial;
	}
	public String getServerIP(int index)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String serverIP = null;
		try 
		{
			db = getReadableDatabase();
			cursor = db.rawQuery("select * from "+ServerIPtableName, null);
			if(cursor.moveToFirst())
			{
				//System.out.println("Server IP = " + cursor.getString(index));
				serverIP = cursor.getString(index);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
		}
		return serverIP;
	}
	public void updateSerialNo(String serialNo) 
	{
		SQLiteDatabase db = null;
		try 
		{
			db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(SerialNoColumn, serialNo);
			db.update(InfotableName, values, null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
		}
  	}
} 