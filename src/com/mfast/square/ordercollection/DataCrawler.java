package com.mfast.square.ordercollection;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DataCrawler extends AsyncTask<String, Void, Void>
{
	private static String URL;
	//= "http://202.59.136.1//square_mobilereport//DBVersion.aspx";
	//private static final String URL = "http://123.200.2.233//sanofi_mobilereport//DBVersion.aspx";
	//private static final String URL = "http://10.0.2.2//sanofi_mobilereport//DBVersion.aspx";
	private static Context context;
	private static String DbVersion;
	private static String action;
	private static ProgressDialog dialog;
	private static String queryString;
	private static String alertMessage;
	private static boolean isSuccessful;
	private static final int FIRST_PRIORITY = 0;
	private static final int SECOND_PRIORITY = 1;
	
	public DataCrawler(Context context,String action)//used for normal Sync
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		this.action = action;
		this.queryString = null;
		this.DbVersion = MySQLiteHelper.getInstance(context).getVersionNo();
		this.dialog = new ProgressDialog(context);
	}
	public DataCrawler(Context context,String action,String versionNo) //used for complete sync
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		this.action = action;
		this.queryString = null;
		this.DbVersion = versionNo;
		this.dialog = new ProgressDialog(context);
	}
	public DataCrawler(Context context,String action,String versionNo,String queryString) 
	//used for GetOrderStatus
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		this.action = action;
		this.DbVersion = versionNo;
		this.queryString = queryString;
		this.dialog = new ProgressDialog(context);
	}
	private static void ConstructURL(Context context)
	{
		String ipAddress = MySQLiteHelper.getInstance(context).getServerIP();
		URL = "http://"+ ipAddress +"//square_mobilereport//DBVersion.aspx";
	}
	private static void ConstructURL(Context context,int index)
	{
		String ipAddress = MySQLiteHelper.getInstance(context).getServerIP(index);
		URL = "http://"+ ipAddress +"//square_mobilereport//DBVersion.aspx";
	}
	@Override
	protected void onPreExecute()
	{
		if(this.action.equals("_2"))
		{
			this.dialog.setMessage("Syncing with mFAST server. Please wait...");
		}
		else if(this.action.equals("_7"))
		{
			this.dialog.setMessage("Retrieving Data from mFAST server. Please wait...");
		}
		this.dialog.setCanceledOnTouchOutside(false);
		this.dialog.show();
	}
	@Override
	protected Void doInBackground(String... arg0)
	{
		sendDataRequest();
		//connect(URL);
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void onPostExecute(Void v)
	{
		if(this.dialog.isShowing())
			this.dialog.dismiss();
		if(action.equals("_2"))
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			if(isSuccessful==true)
			{
				alert.setTitle("Success");				
			}
			else
			{
				alert.setTitle("Exception");
			}
			
			alert.setMessage(alertMessage);
			alert.setCancelable(false);
			alert.setNegativeButton("Close",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					dialog.cancel();
					System.exit(0);
				}
			});
			alert.show();
		}
		else if(action.equals("_7"))
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Success");
			alert.setMessage(alertMessage);
			alert.setCancelable(false);
			alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
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
	}
	public static void sendDataRequest()
	{
        ConstructURL(context);
		connect(URL);
		if(isSuccessful==false)
		{
			ConstructURL(context, SECOND_PRIORITY);
			connect(URL);
		}
	}
	public static void connect(String url)
	{
		isSuccessful = true;
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,30000);
		//HttpConnectionParams.setSoTimeout(params, 60000);
        HttpClient httpclient = new DefaultHttpClient(params);
        
        // Prepare a request object
        HttpPost httppost = new HttpPost(url);
        System.out.println("URL = "+URL+"DBVersion = " + DbVersion + " Action = "+action);
        //httpget.getParams().setIntParameter("Serial", 1012);
        //httpget.getParams().setIntParameter("DBVersion", 999);
        //httpget.getParams().setParameter("Action", "_2");	 
        List<NameValuePair> nameValuePairs=null;
        if(action.equals("_2"))
        {
        	if(DbVersion.equals("999"))
	        {
	        	MySQLiteHelper.getInstance(context).DeleteAllTables();
	        }
        	
        	nameValuePairs = new ArrayList<NameValuePair>(3);
        	//nameValuePairs.add(new BasicNameValuePair("Serial", "1761"));
        	nameValuePairs.add(new BasicNameValuePair("Serial", MySQLiteHelper.getInstance(context).getSerialNo()));
        	nameValuePairs.add(new BasicNameValuePair("DBVersion", DbVersion));
        	nameValuePairs.add(new BasicNameValuePair("Action", action));
        }
        else if(action.equals("_7")) // Order Status
        {
        	nameValuePairs = new ArrayList<NameValuePair>(4);
        	nameValuePairs.add(new BasicNameValuePair("Serial", MySQLiteHelper.getInstance(context).getSerialNo()));
        	nameValuePairs.add(new BasicNameValuePair("DBVersion", DbVersion));
        	nameValuePairs.add(new BasicNameValuePair("Action", action));
        	nameValuePairs.add(new BasicNameValuePair("QueryStr", encrypt(queryString)));
        	System.out.println(encrypt(queryString));
        }
        try
        {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
        catch (UnsupportedEncodingException e1) 
		{
//			
        	e1.printStackTrace();
		}
        HttpResponse response;
        try 
        {
            response = httpclient.execute(httppost);
            StatusLine statusline = response.getStatusLine();
            // Examine the response status
            
            if(statusline.getStatusCode()==HttpStatus.SC_OK)
            {
            	Log.i("Praeda",response.getStatusLine().toString());
	            HttpEntity entity = response.getEntity();
//	            	// If the response does not enclose an entity, there is no need
//	            	// to worry about connection release
	            if (entity != null) 
	            {
	                InputStream instream = entity.getContent();
	                String result= convertStreamToString(instream);
	                System.out.println(result);
	                if(action.equals("_2"))
	                {
	                	Parser parser = new Parser(result);
	                	String[] array = parser.Parse();
	                	System.out.println(array.length);
	                	
	                	DataSaver datasaver = new DataSaver(array);
	                	if(DbVersion.equals("999")) //Complete Sync
	    		        {
	                		datasaver.SaveData(context);
	    		        }
	                	else //Normal Sync
	                	{
	                		datasaver.upDateData(context);
	                	}
	                	System.out.println("Data Saved");
	                	alertMessage = "Your Device is now synchronized with mFAST server." +
	                			"We need to close the application";
	                }
	                else if(action.equals("_7"))
	                {
	                	Parser parser = new Parser(result);
	                	String responseMessage = parser.getResponseMsg(result);
	                	String responseCode = parser.getResponseCode(result);
	                	
	                	String[] array = responseMessage.split("\\|");
	                    String[] tmp = array[0].split("\\,");
	                    String msg = "";
	                    int count = Integer.parseInt(tmp[1]);
	                    if(count==0)
	                    {
	                    	msg = msg +"   No Order Found\n";
	                    }
	                    else
	                    {
	                    	String[] t = array[1].split(";");
	                    	for(int i=0;i<t.length;i++)
	                    	{
	                    		String[] tm = t[i].split(",");
	                    		if(tm[0].equals("1"))
	                    		{
	                    			msg = msg +"   "+tm[1]+" - "+"Evening\n";
	                    		}
	                    		else
	                    		{
	                    			msg = msg + "   "+tm[1]+" - "+"Morning\n";
	                    		}
	                    	}
	                    }
	                    alertMessage = msg;
	                }
	                instream.close();
            	}
            }
            else if(statusline.getStatusCode()==HttpStatus.SC_REQUEST_TIMEOUT)
            {
            	System.out.println("Request Time Out");
            	isSuccessful = false;
				alertMessage = "mFAST is not responding. Please try again later";
            }
        }
        catch (Exception e) 
        {
        	System.out.println(e.toString());
        	isSuccessful = false;
			alertMessage = e.toString();
        }
    }
	private static String encrypt(String queryString) 
    {
		StringBuffer strBuff = new StringBuffer();
    	for(int i=0;i<queryString.length();i++)
    	{
    		switch(queryString.charAt(i))
    		{
    		case ' ':
    			strBuff.append("%20");
    			break;
    		case ',':
    			strBuff.append("%2C");
    			break;
    		case ';':
    			strBuff.append("%3B");
    			break;
    		case '|':
    			strBuff.append("%7C");
    			break;
    		default:
    			strBuff.append(queryString.charAt(i));
    		}
    	}
    	return strBuff.toString();
    }
    private static String convertStreamToString(InputStream is) 
    {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try 
        {
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}