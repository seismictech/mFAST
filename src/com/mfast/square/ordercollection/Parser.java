package com.mfast.square.ordercollection;

public class Parser 
{
	String data;
	private static String CODE_START_TAG_HTTP = "<code>";
	private static String CODE_END_TAG_HTTP = "</code>";
	private static String MSGS_START_TAG_HTTP = "<msg>";
	private static String MSGS_END_TAG_HTTP = "</msg>";
	Parser(String data)
	{
		this.data = data;
	}
	String[] Parse()
	{
		String msg = getResponseMsg(data);		
		String[] parts = msg.split("\\|");		
		return parts;
	}
	void Process(String code,String orderId)
	{
		String msg = getResponseMsg(data);		
		String[] parts = msg.split(";");
		code = parts[0];
		orderId = parts[1].split(",")[0];
	}
	public String getResponseCode(String _responseBody) 
	{
	    int iCodeStartTag = _responseBody.indexOf(CODE_START_TAG_HTTP);
	    int iCodeEndTag = _responseBody.indexOf(CODE_END_TAG_HTTP);

	    return _responseBody.substring( (iCodeStartTag +
	                                     CODE_START_TAG_HTTP.length()),
	                                   iCodeEndTag);
	}

	public String getResponseMsg(String _responseBody) 
	{
	    int iMsgStartTag = _responseBody.indexOf(MSGS_START_TAG_HTTP);
	    int iMsgEndTag = _responseBody.indexOf(MSGS_END_TAG_HTTP);

	    return _responseBody.substring( (iMsgStartTag +
	                                     MSGS_START_TAG_HTTP.length()),
	                                   iMsgEndTag);
	}
}
