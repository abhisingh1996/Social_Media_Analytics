package com.zafin.twitter.twitterreadapi;
import com.zafin.twitter.dto.TweetData;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
class Mongodata implements MongoInterface {
	
	public static final Mongo mongoDTO = new Mongo();
	public static MongoClient mongoClient;
	public static DB db;
	 
	 
	 protected 	Mongodata(String schema, String DBURL, Integer portNumber) {
	        try {
	            if (schema != null && !schema.isEmpty()) {
	                mongoDTO.setDatabaseSchema(schema);
	            }
	            if (DBURL != null && !DBURL.isEmpty()) {
	                mongoDTO.setDatabaseUrl(DBURL);
	            }
	            if (portNumber != null) {
	                mongoDTO.setDatabasePort(portNumber);
	            }

	            createInstance(mongoDTO);
	        } catch (UnknownHostException un) {
	            un.printStackTrace();
	        }
	    }

	    private void createInstance(Mongo mongoDTO) throws UnknownHostException {
	        mongoClient = new MongoClient(mongoDTO.getDatabaseUrl(), mongoDTO.getDatabasePort());
	        db = mongoClient.getDB(mongoDTO.getDatabaseSchema());
	    }
	
	 public String add(String tableName, String json) {
	        if (tableName == null || tableName.equals("") || json == null || json.equals("")) {
	            return "501";
	        }
	        DBCollection table = db.getCollection(tableName);
	        DBObject dbObject = (DBObject) JSON.parse(json);
	        WriteResult wRes = table.insert(dbObject);
	        return ((ObjectId) dbObject.get("_id")).toString();
	    }
	 
	 public String getAll(String tableName) {
	        if (tableName == null || tableName.equals("")) {
	            return "501";
	        }
	        String row = "";
	        DBCollection table = db.getCollection(tableName);
       
	        DBCursor cursor = table.find();
	        String json = "[";
	        while (cursor.hasNext()) { 
	        	json += cursor.next()+",";
//	            System.out.println(cursor.next());
	         }
	        json = json.substring(0, json.length()-1)+"]";
	        cursor.close();
	        return json;

	    }
	 public String getByKey(String tableName, String _id)
	 {
		 if (tableName == null || tableName.equals("") || _id == null || _id.equals("")) {
	            return "501";
	        }
		 
	       
	        DBCursor cursor = null;
	        DBCollection table = db.getCollection(tableName);
	        BasicDBObject searchQuery = new BasicDBObject();
	        searchQuery.put("tweetLocation", _id);  //if i get more than one tweet then how can i store it
			cursor = table.find(searchQuery);
			 String json1=" ";
			 if(cursor.size()>0) {
				 json1 +=cursor.next();
			 }
			 cursor.close();
			 return json1;
			
	 }
	 public String getByCondition(String tableName, Map condition,String operator) {
	 	
	
		        //validating the inputs
		        if (tableName == null || tableName.isEmpty()
		                || condition == null || condition.isEmpty()) {
		            return null;
		        }
		        //verifying the logicalOperator given.
		        if (operator == null || operator.isEmpty()) {
		        	operator = "and";
		        }
		        //connect to specific table/collection.
		        //DBCollection dbCollection = mongoDB.getCollection(tableName);
		        DBCollection table = db.getCollection(tableName);

		        //validating the connection
		        if (db == null) {
		            return null;
		        }
		        //creating the condition List object.
		        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();

		        //convert the condition to list of DBObject.
		        Set keys = condition.keySet();
		        Iterator col_it = keys.iterator();
		        while (col_it.hasNext()) {
		            BasicDBObject query = new BasicDBObject();// for conditions
		            String key = (String) col_it.next();
		            query.put(key, condition.get(key));
		            conditionList.add(query);
		        }

		        //apply the logical operator for query conditions.
		        BasicDBObject finalQuery = new BasicDBObject();
		        if (operator.equalsIgnoreCase("and")) {
		            finalQuery.put("$and", conditionList);
		        } else if (operator.equalsIgnoreCase("or")) {
		            finalQuery.put("$or", conditionList);
		        } else if (operator.equalsIgnoreCase("nor")) {
		            finalQuery.put("$nor", conditionList);
		        } else {
		            finalQuery.put("$and", conditionList);
		        }

		        //get the data from DB.
		        DBCursor cursor = table.find(finalQuery);
		        //convert the DBCursor object to List of DBObjects.
		        String json3 = "[";
		        
		        while(cursor.hasNext()){
		        	json3 += cursor.next()+",";
		        }
		        json3 = json3.substring(0, json3.length()-1)+"]";
//		        String json3="[";
//				  if(cursor.size()>0) {
//					 json3 +=cursor.next();
//				 }
			      //  json3 = json3.substring(0, json3.length()-1);

				 cursor.close();
				 //return the results
				 return json3;
		        
		       
		 
		    }

	
	        
}





