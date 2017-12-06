package com.zafin.twitter.twitterreadapi;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import twitter4j.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class Test {

	//private static final Object  = null;

	public static void main(String arg[]) throws UnknownHostException{

		Mongodata mongoData = new Mongodata("TEST","127.0.0.1",27017);
		//get all tweet

//		String json = mongoData.getAll("sma");
 //		System.out.println("Get all tweet:");

//		System.out.println(json);
//		//get all tweet 
	
	/*	String json1=mongoData.getByKey("sma","INDIA");
 		System.out.println("Get By key:");
 		System.out.println(json1);
	*/
     //   get tweet by condition
	/*	Map params = new HashMap();
		params.put("favoriteCount", 0);

		params.put("Retweetcount",0);
        String json3=mongoData.getByCondition("abc", params,"and");
        
		System.out.println(json3);
*/
	}
}
