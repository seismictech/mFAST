package com.mfast.square.ordercollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
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
import android.text.format.Time;
import android.util.Log;

public class DataSender extends AsyncTask<Void, Void, Void>
{
	private static String URL;
	//private static final String URL = "http://10.0.2.2//sanofi_mobilereport//Default.aspx";
	//private static final String URL = "http://121.200.63.18//sanofi_mobilereport//Default.aspx";	
	//private static final String URL = "www.buet.ac.bd";	
//	private static final String CONN_ERROR = "-1";
//	private static final String SUCCESS = "1";
//	private static final String FAILURE = "0";
	
	private static final int FIRST_PRIORITY = 0;
	private static final int SECOND_PRIORITY = 1;
	
	private static final int FAILURE_RESPONSE = 0;
	private static final int SUCCESS_RESPONSE = 1;
	private static final int NUMBER_NOT_AVAILABLE_RESPONSE = 2;
	private static final int HTTP_CONNECTION_ERROR_RESPONSE = 3;
	private static final int ERROR_FROM_SERVER_RESPONSE = 4;
	
	private static final int CONNECTION_TIMEOUT = 15000;
	private static final int SOCKET_TIMEOUT = 10000;
	
	private static Context context;
	private static String QueryString;
	private static String savedString;
	private static String code;
	private static int responseCode;
	private static String orderId;
	private static ProgressDialog dialog;
	private static String _id;
	private DraftsActivity draftActivity;
	private static FragmentActivity activity;
	private static String OrderIndex;
	private static int MAX_SMS_LENGTH = 160;
	private static boolean isOrderThroughSMSChannel = false;
	public DataSender(Context context,String QueryString)
	{
		// TODO Auto-generated constructor stub
		DataSender.context = context;
		DataSender.QueryString = QueryString;
		dialog = new ProgressDialog(context);
	}
	public DataSender(Context context,String QueryString,String savedString,String orderIndex,FragmentActivity activity) 
	{
		DataSender.context = context;
		DataSender.QueryString = QueryString;
		dialog = new ProgressDialog(context);
		DataSender.OrderIndex = orderIndex;
		DataSender.activity = activity;
		DataSender.savedString = savedString;
	}
	public DataSender(Context context,String QueryString,String savedString,String orderIndex,FragmentActivity activity,String OrderId) 
	{
		DataSender.context = context;
		DataSender.QueryString = QueryString;
		dialog = new ProgressDialog(context);
		DataSender.savedString = savedString;
		DataSender.OrderIndex = orderIndex;
		DataSender.activity = activity;
		DataSender._id = OrderId;
	}
	private static void ConstructURL(Context context)
	{
		String ipAddress = MySQLiteHelper.getInstance(context).getServerIP();
		URL = "http://"+ ipAddress +"//square_mobilereport//Default.aspx";
	}
	private static void ConstructURL(Context context,int index)
	{
		String ipAddress = MySQLiteHelper.getInstance(context).getServerIP(index);
		URL = "http://"+ ipAddress +"//square_mobilereport//Default.aspx";
	}
	@Override
	protected Void doInBackground(Void... arg0)
	{
		//connect(QueryString);
		send(QueryString);
		return null;
	}
	private class WaitForSMSREply extends AsyncTask<Void, Void, Void> 
	{
		private Context context;
		private ProgressDialog dialog;
		private String savedString;
		private String QueryString;
		public WaitForSMSREply(Context context,ProgressDialog dialog,String QueryString,String savedString) 
		{
			// TODO Auto-generated constructor stub
			this.context = context;
			this.dialog = dialog;
			this.savedString = savedString;
			this.QueryString = QueryString;
		} 
	    @Override
	    protected void onPostExecute(Void params)
	    {
	    	if(dialog.isShowing())
			{
				dialog.dismiss();		
				if(QueryString.length()>150)
				{
					if(OrderIndex.equals("0")) //Normal Order
					{
						MySQLiteHelper.getInstance(context).insertIntoSentBox(this.savedString,context.getResources().getString(R.string.SMS),true);
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Success");
						alert.setMessage("Order Sent Successfully via SMS");
						alert.setCancelable(true);
						if(activity instanceof ProductsActivity)
						{
							final ProductsActivity pActivity = (ProductsActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									pActivity.finishTheActivity();
								}
							});
						}
						else if(activity instanceof SummeryActivity)
						{
							final SummeryActivity sActivity = (SummeryActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									sActivity.finishTheActivity();
								}
							});	
						}
						alert.show();
					}
					else if(OrderIndex.equals("1"))  //SentBox Order
					{
						//DeleteFromSentBox
						////// no need to delete from sent box
						//MySQLiteHelper.getInstance(context).deleteFromSentBox(_id);
						//insertIntoSentBox
						MySQLiteHelper.getInstance(context).insertIntoSentBox(this.savedString,context.getResources().getString(R.string.SMS),true);
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Success");
						alert.setMessage("Order Sent Successfully via SMS");
						alert.setCancelable(true);
						if(activity instanceof DirectedProductsActitivity)
						{
							final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									DpActivity.finishTheActivity();
								}
							});
						}
						else if(activity instanceof SummeryActivity)
						{
							final SummeryActivity sActivity = (SummeryActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									sActivity.finishTheActivity();
								}
							});	
						}
						alert.show();
					}
					else if(OrderIndex.equals("2")) //Draft Order
					{
						///deleteFromDraftBox
						//insertIntoSentBox
						MySQLiteHelper.getInstance(context).deleteFromDraftBox(_id);
						MySQLiteHelper.getInstance(context).insertIntoSentBox(this.savedString,context.getResources().getString(R.string.SMS),true);
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Success");
						alert.setMessage("Order Sent Successfully via SMS");
						alert.setCancelable(true);
						if(activity instanceof DirectedProductsActitivity)
						{
							final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									DpActivity.finishTheActivity();
								}
							});
						}
						else if(activity instanceof SummeryActivity)
						{
							final SummeryActivity sActivity = (SummeryActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									sActivity.finishTheActivity();
								}
							});	
						}
						alert.show();
					}
				}
				else
				{
					MySQLiteHelper.getInstance(context).insertIntoDraftBox(savedString);
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle("Failure");
					alert.setMessage("mFAST server is not responding. Order Saved in Draft");
					alert.setCancelable(true);
					if(activity instanceof DirectedProductsActitivity)
					{
						final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								DpActivity.finishTheActivity();
							}
						});
					}
					else if(activity instanceof SummeryActivity)
					{
						final SummeryActivity sActivity = (SummeryActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								sActivity.finishTheActivity();
							}
						});	
					}
					else if(activity instanceof ProductsActivity)
					{
						final ProductsActivity pActivity = (ProductsActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								pActivity.finishTheActivity();
							}
						});
					}
					alert.show();
				}
			}
	    }
		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			try 
			{
				Thread.sleep(60000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return null;
		}
	}
	@Override
	protected void onPreExecute()
	{
		dialog.setMessage("Sending your order to mFAST server.Please wait...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	@Override
	protected void onPostExecute(Void v)
	{
		if(isOrderThroughSMSChannel==false&&dialog.isShowing())
			dialog.dismiss();
		super.onPostExecute(v);
		if(responseCode == SUCCESS_RESPONSE)
		{
			if(!orderId.equals(""))
			{
				if(OrderIndex.equals("0")) //Normal Order
				{
					MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId, savedString,context.getResources().getString(R.string.GPRS),false);
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle("Success");
					alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
					alert.setCancelable(true);
					if(activity instanceof ProductsActivity)
					{
						final ProductsActivity pActivity = (ProductsActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								pActivity.finishTheActivity();
							}
						});
					}
					else if(activity instanceof SummeryActivity)
					{
						final SummeryActivity sActivity = (SummeryActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								sActivity.finishTheActivity();
							}
						});	
					}
					alert.show();
				}
				else if(OrderIndex.equals("1"))  //SentBox Order
				{
					//DeleteFromSentBox
					////// no need to delete from sent box
					//MySQLiteHelper.getInstance(context).deleteFromSentBox(_id);
					//insertIntoSentBox
					MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId,savedString,context.getResources().getString(R.string.GPRS),false);
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle("Success");
					alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
					alert.setCancelable(true);
					if(activity instanceof DirectedProductsActitivity)
					{
						final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								DpActivity.finishTheActivity();
							}
						});
					}
					else if(activity instanceof SummeryActivity)
					{
						final SummeryActivity sActivity = (SummeryActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								sActivity.finishTheActivity();
							}
						});	
					}
					alert.show();
				}
				else if(OrderIndex.equals("2")) //Draft Order
				{
					///deleteFromDraftBox
					//insertIntoSentBox
					MySQLiteHelper.getInstance(context).deleteFromDraftBox(_id);
					MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId, savedString,context.getResources().getString(R.string.GPRS),false);
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle("Success");
					alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
					alert.setCancelable(true);
					if(activity instanceof DirectedProductsActitivity)
					{
						final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)DataSender.activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								DpActivity.finishTheActivity();
							}
						});
					}
					else if(activity instanceof SummeryActivity)
					{
						final SummeryActivity sActivity = (SummeryActivity)activity;
						alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
								sActivity.finishTheActivity();
							}
						});	
					}
					alert.show();
				}
			}
			else
			{
				
			}
		}
		else if((responseCode == FAILURE_RESPONSE)||(responseCode == NUMBER_NOT_AVAILABLE_RESPONSE))
		{
			if(dialog.isShowing())
				dialog.dismiss();
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Server Failure");
			alert.setMessage("Order not sent and saved in draft. Please try again later");
			alert.setCancelable(false);
			alert.setNegativeButton("Ok",new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialogInterface, int which) 
				{
					dialogInterface.cancel();
					MySQLiteHelper.getInstance(context).insertIntoDraftBox(savedString);
					if(activity instanceof DirectedProductsActitivity)
					{
						final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
						DpActivity.finishTheActivity();
					}
					else if(activity instanceof SummeryActivity)
					{
						final SummeryActivity sActivity = (SummeryActivity)activity;
						sActivity.finishTheActivity();
					}
					else if(activity instanceof ProductsActivity)
					{
						final ProductsActivity pActivity = (ProductsActivity)activity;
						pActivity.finishTheActivity();
					}
				}
			});
			alert.show();
		}
		else if((responseCode == HTTP_CONNECTION_ERROR_RESPONSE)||(responseCode == ERROR_FROM_SERVER_RESPONSE))
		{
			if(isOrderThroughSMSChannel==true)
			{
				isOrderThroughSMSChannel = false;
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Failure");
				alert.setMessage("Falied to connect to Internet. Do you want to send the Order via SMS?");
				alert.setCancelable(false);
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int which) 
					{
						dialogInterface.cancel();
						dialog.setMessage("Sending Order through SMS");
						SMSSender.getInstance(context).send(QueryString);
						new WaitForSMSREply(context, dialog,QueryString,savedString).execute();
					}
				});
				alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int which) 
					{
						dialogInterface.cancel();
						dialog.dismiss();
						MySQLiteHelper.getInstance(context).insertIntoDraftBox(savedString);
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Failure");
						alert.setMessage("Order Saved in Draft");
						alert.setCancelable(false);
						if(activity instanceof DirectedProductsActitivity)
						{
							final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									DpActivity.finishTheActivity();
								}
							});
						}
						else if(activity instanceof SummeryActivity)
						{
							final SummeryActivity sActivity = (SummeryActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									sActivity.finishTheActivity();
								}
							});	
						}
						else if(activity instanceof ProductsActivity)
						{
							final ProductsActivity pActivity = (ProductsActivity)activity;
							alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.cancel();
									pActivity.finishTheActivity();
								}
							});
						}
						alert.show();
					}
				});
				alert.show();
			}
		}
	}
	public static void OnSMSChannelResponse(String orderId)
	{
		if(dialog!=null&dialog.isShowing())	
		{
			dialog.cancel();
		}
		if(orderId==null||orderId.equals(""))
		{
			
		}
		else
		{
			if(OrderIndex.equals("0")) //Normal Order
			{
				MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId, savedString,context.getResources().getString(R.string.SMS),false);
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Success");
				alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
				alert.setCancelable(true);
				if(activity instanceof ProductsActivity)
				{
					final ProductsActivity pActivity = (ProductsActivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							pActivity.finishTheActivity();
						}
					});
				}
				else if(activity instanceof SummeryActivity)
				{
					final SummeryActivity sActivity = (SummeryActivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							sActivity.finishTheActivity();
						}
					});	
				}
				alert.show();
			}
			else if(OrderIndex.equals("1"))  //SentBox Order
			{
				//DeleteFromSentBox
				////// no need to delete from sent box
				//MySQLiteHelper.getInstance(context).deleteFromSentBox(_id);
				//insertIntoSentBox
				MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId, savedString,context.getResources().getString(R.string.SMS),false);
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Success");
				alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
				alert.setCancelable(true);
				if(activity instanceof DirectedProductsActitivity)
				{
					final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							DpActivity.finishTheActivity();
						}
					});
				}
				else if(activity instanceof SummeryActivity)
				{
					final SummeryActivity sActivity = (SummeryActivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							sActivity.finishTheActivity();
						}
					});	
				}
				alert.show();
			}
			else if(OrderIndex.equals("2")) //Draft Order
			{
				///deleteFromDraftBox
				//insertIntoSentBox
				MySQLiteHelper.getInstance(context).deleteFromDraftBox(_id);
				MySQLiteHelper.getInstance(context).insertIntoSentBox(orderId, savedString,context.getResources().getString(R.string.SMS),false);
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Success");
				alert.setMessage("Order Sent Successfully\nOrder Id: "+orderId);
				alert.setCancelable(true);
				if(activity instanceof DirectedProductsActitivity)
				{
					final DirectedProductsActitivity DpActivity = (DirectedProductsActitivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							DpActivity.finishTheActivity();
						}
					});
				}
				else if(activity instanceof SummeryActivity)
				{
					final SummeryActivity sActivity = (SummeryActivity)activity;
					alert.setNegativeButton("Ok",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
							sActivity.finishTheActivity();
						}
					});	
				}
				alert.show();
			}
		}
	}
	public static boolean isValidQueryString(String QueryString)
	{
		boolean valid = true;
		try
		{
			String formString = QueryString.substring(2,QueryString.indexOf('|'));
			String sheetString = QueryString.substring(QueryString.indexOf('|')+1);
			
			String[] products = sheetString.split(";");
			String[] parts;
			for(int i=0;i<products.length;i++)
			{
				parts = products[i].split(",");
				if(parts.length!=2)
					valid = false;
				if(parts[0].length()!=10)
					valid = false;
				if(parts[1].length()==0)
					valid = false;
			}
			valid = true;
		}
		catch(Exception e)
		{
			valid = false;
		}
		if(valid == false)
		{
			Time now = new Time();
			now.setToNow();
			Log.v(DataSender.class.getName(),"OrderIndex: "+OrderIndex+","+QueryString+","+orderId+","+now.monthDay+","+now.HOUR);
		}
		return valid;
	}
	public static void send(String QueryString)
	{
		ConstructURL(context);
		Time now = new Time();
		now.setToNow();
		Log.v(DataSender.class.getName(),"OrderIndex: "+OrderIndex+","+QueryString+","+orderId+","+now.monthDay+","+now.HOUR);
		int responseId = -1;
		if(isValidQueryString(QueryString))
		{
			for(int i=0;i<2;i++)
			{
				//dialog.setMessage("Sending your order to mFAST server.Please wait...\nAttempts "+i+" of 3");
				responseId = sendUIRequest(QueryString,URL,context);
				if(responseId == SUCCESS_RESPONSE)
					break;
			}
			if(responseId != SUCCESS_RESPONSE)
			{
				ConstructURL(context, SECOND_PRIORITY);
				for(int i=0;i<2;i++)
				{
					//dialog.setMessage("Sending your order to mFAST server.Please wait...\nSecond Attempts "+i+" of 3");
					responseId = sendUIRequest(QueryString,URL,context);
					if(responseId == SUCCESS_RESPONSE)
					{
						MySQLiteHelper.getInstance(context).SwapServerIP();
						break;
					}
				}
			}
		}
		switch(responseId)
		{
		case FAILURE_RESPONSE:
		case NUMBER_NOT_AVAILABLE_RESPONSE:
		case ERROR_FROM_SERVER_RESPONSE:
		case HTTP_CONNECTION_ERROR_RESPONSE:
			isOrderThroughSMSChannel = true;
			responseCode = responseId;
			break;
		case SUCCESS_RESPONSE:
			responseCode = responseId;
			break;
		default:
			responseCode = -1;
			break;
		}
	}
	private static int sendUIRequest(String QueryString,String url,Context context)
	{
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,CONNECTION_TIMEOUT);
		//HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        HttpClient httpclient = new DefaultHttpClient(params);
        
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("Serial", MySQLiteHelper.getInstance(context).getSerialNo()));
        nameValuePairs.add(new BasicNameValuePair("QueryStr",encrypt(QueryString)));
        System.out.println(encrypt(QueryString));
        nameValuePairs.add(new BasicNameValuePair("Action", "_1"));
        try 
        {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
        catch (UnsupportedEncodingException e1) 
        {
			e1.printStackTrace();
		}
        String responseCode,msg;
        try 
        {
        	HttpResponse response = httpclient.execute(httppost);
            Log.i("Praeda",response.getStatusLine().toString());
            
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
            {            
	            HttpEntity entity = response.getEntity();
	            if (entity != null) 
	            {
	                InputStream instream = entity.getContent();
	                String result = null;
	                result = convertStreamToString(instream);
	            	instream.close();
	                System.out.println(result);
	                if(result!=null&&!result.equals(""))
	                {
		                Parser parser = new Parser(result);
		                responseCode = parser.getResponseCode(result);
	                	msg = parser.getResponseMsg(result);
	                	if(responseCode.equals("1"))
	                	{
	                		if(msg.equals(" "))
			                {
			                	return ERROR_FROM_SERVER_RESPONSE;
			                }
	                		else if(msg.equals(""))
	                		{
	                			return SUCCESS_RESPONSE;
	                		}
	                		else if(msg.equals("End Line Order is Saved"))
	                		{
	                			return SUCCESS_RESPONSE;
	                		}
			                else
			                {
			                	String[] array = msg.split(";");
			                	code = array[0];
			                	orderId = array[1].split(",")[0];
				                return SUCCESS_RESPONSE;
			                }
	                	}
	                	else
	                	{
	                		return HTTP_CONNECTION_ERROR_RESPONSE;
	                	}
	                }
	            }
            }
            else
            {
            	return HTTP_CONNECTION_ERROR_RESPONSE;
            }
        }
        catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            return HTTP_CONNECTION_ERROR_RESPONSE;
        }
        catch (ConnectTimeoutException e)
        {
            e.printStackTrace();
            return HTTP_CONNECTION_ERROR_RESPONSE;
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        	return HTTP_CONNECTION_ERROR_RESPONSE;
        }
        return -1;
	}
//	public static void connect(String QueryString)
//	{
//		isOrderThroughSMSChannel = false;
//		//MySQLiteHelper.getInstance(context).updateServerIP("127.0.0.1:80", "127.0.0.0:80");
//		HttpParams params = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(params,CONNECTION_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
//        HttpClient httpclient = new DefaultHttpClient(params);
//        
//        // Prepare a request object
//        
//        ConstructURL(context);
//        HttpPost httppost = new HttpPost(URL);
//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
//        nameValuePairs.add(new BasicNameValuePair("Serial", MySQLiteHelper.getInstance(context).getSerialNo()));
//        nameValuePairs.add(new BasicNameValuePair("QueryStr",encrypt(QueryString)));
//        System.out.println(encrypt(QueryString));
//        nameValuePairs.add(new BasicNameValuePair("Action", "_1"));
//        try 
//        {
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//		}
//        catch (UnsupportedEncodingException e1) 
//        {
//			e1.printStackTrace();
//		}
//        HttpResponse response;
//        for(int i=0;i<6;i++)
//        {
//	        try 
//	        {
//	        	response = httpclient.execute(httppost);
//	            // Examine the response status
//	            Log.i("Praeda",response.getStatusLine().toString());
//	
//	       
//	            // Get hold of the response entity
//	            HttpEntity entity = response.getEntity();
//	            // If the response does not enclose an entity, there is no need
//	            // to worry about connection release
//	
//	            if (entity != null) 
//	            {
//	                // A Simple JSON Response Read
//	                InputStream instream = entity.getContent();
//	                String result = null;
//	                result = convertStreamToString(instream);
//                	instream.close();
//	                System.out.println(result);
//	                if(result!=null&&!result.equals(""))
//	                {
//		                Parser parser = new Parser(result);
//		                responseCode = parser.getResponseCode(result);
//		                
//		                if(responseCode.equals(SUCCESS))
//		                {
//		                	String msg = parser.getResponseMsg(result);
//		                	String[] array = msg.split(";");
//		                	code = array[0];
//		                	orderId = array[1].split(",")[0];
//		                	if(i>=3)
//			                {
//			                	MySQLiteHelper.getInstance(context).SwapServerIP();
//			                }
//			                return;
//		                }
//		                else if(responseCode.equals(FAILURE))
//		                {
//		                	return;
//		                }
//	                }
//	            }
//	        }
//	        catch (SocketTimeoutException e)
//	        {
//	        	responseCode = FAILURE;
//	            e.printStackTrace();
//	            continue;
//	        }
//	        catch (ConnectTimeoutException e)
//	        {
//	        	responseCode = FAILURE;
//	            e.printStackTrace();
//	            if(i==3)
//	            {
//	            	//ConstructURL using second IP address
//	            	ConstructURL(context, SECOND_PRIORITY);
//	            	httppost = new HttpPost(URL);
//	                try 
//	                {
//	        			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	        		}
//	                catch (UnsupportedEncodingException e1) 
//	                {
//	        			e1.printStackTrace();
//	        		}
//	            }
//	            continue;
//	        }
//	        catch(IOException e)
//	        {
//	        	responseCode = CONN_ERROR;
//	        	e.printStackTrace();
//	        	break;
//	        }
//        }
//        ////send through SMS Channel
//        responseCode = CONN_ERROR;
//        isOrderThroughSMSChannel = true;
//        //SMSSender.getInstance(context).send(QueryString);
//	}
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
//	private static void SendSMS(String phoneNumber,String message)
//	{
//		short PORT = 5901;
//		SmsManager smsManager = SmsManager.getDefault();
//		if(message.length()>MAX_SMS_LENGTH)
//		{
//			ArrayList<String> messageList = smsManager.divideMessage(message);
//			smsManager.sendMultipartTextMessage(phoneNumber, null, messageList, null, null);
//		}
//		else
//		{
//			smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//		}
//	}
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
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}