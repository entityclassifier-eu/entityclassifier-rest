package cz.vse.fis.keg.entityclassifier.rest.authorizator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import cz.vse.fis.keg.entityclassifier.rest.mongo.MongoDBClient;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milan Dojchinovski <milan@dojchinovski.,mk>
 */


public class Authorizator {
    
    private static Authorizator authorizator = null;
    private BasicDBObject queryObj;
    private DBCursor cursor = null;
    
    public static Authorizator getInstance() {
        if(authorizator == null){
            authorizator = new Authorizator();
        }
        return authorizator;
    }
    
    public boolean isAuthorized(String apikey){
        try {
            BasicDBObject queryObj = new BasicDBObject();
            queryObj.append("apikey", apikey);
            
            DBCursor cursor = MongoDBClient.getClient().getDB("thddb3").getCollection("users").find(queryObj);
            
            if(cursor.hasNext()){
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Authorizator.class.getName()).log(Level.SEVERE, null, ex);
            return false;        
        }
    }
}
