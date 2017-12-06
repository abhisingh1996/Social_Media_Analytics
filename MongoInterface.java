package com.zafin.twitter.twitterreadapi;
import java.util.List;
import java.util.Map;
	
	
	 interface MongoInterface {
	     String add(String tableName,String json);
	     String getAll(String tableName);
	     String getByKey(String tableName,String _id);
	     String getByCondition(String tableName,Map condition,String operator);
	     
	}

