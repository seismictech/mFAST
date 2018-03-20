package com.mfast.square.ordercollection;

import java.util.List;

import org.apache.http.NameValuePair;

public interface DBHandler 
{
	void insert(String code,String value);
	void delete(String code);
	String select(String code);
	List<NameValuePair> selectAll();	
}
