package com.zafin.twitter.twitterreadapi;

import java.net.UnknownHostException;

/**
 *
 * @author Partha
 */
final class DBmanager extends Mongodata {
    static DBmanager helperdb;
    
    private DBmanager(String schema, String DBURL, Integer portNumber) throws UnknownHostException {
        super(schema, DBURL, portNumber);
    }

    public static DBmanager getObj(String schema, String DBURL, Integer portNumber) throws UnknownHostException {
        if(helperdb==null){
           DBmanager db = new DBmanager(schema, DBURL, portNumber);
           helperdb=db; 
        }
        return helperdb;
    }

	/*@Override
	public String getByKey(String tableName, String _id) {
		// TODO Auto-generated method stub
		return null;
	}*/

	
}