/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vse.fis.keg.entityclassifier.rest.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;

/**
 * @author Milan Dojchinovski
 * <milan (at) dojchinovski (dot) mk>
 * Twitter: @m1ci
 * www: http://dojchinovski.mk
 */
public class MongoDBClient {
        
    private static MongoClient   mongoClient = null;
    private static DB            db            = null;
            
    public static DB getDBInstance() throws UnknownHostException{
        if(db == null){
            init();
            db = mongoClient.getDB( "thddb" );
        }
        return db;
    }
    
    public static MongoClient getClient() throws UnknownHostException{
        if(db == null){
            init();
            db = mongoClient.getDB( "thddb" );
        }
        return mongoClient;
    }

    public static void init() throws UnknownHostException {
        mongoClient = new MongoClient( "localhost" , 27017 );
    }
}