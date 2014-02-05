/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vse.fis.keg.entityclassifier.rest;

//import cz.vse.fis.keg.thd.nif.NIFExporter;
//import cz.vse.keg.thd.authorizator.Authorizator;
//import cz.vse.keg.thd.lib.THDController;
//import cz.vse.keg.thd.restapi.vao.Entity;
//import cz.vse.keg.thd.lib.TextProcessor;
//import cz.vse.keg.thd.nif.NIFFormatter;
//import cz.vse.keg.thd.restapi.vao.Hypernym;
import cz.vse.fis.keg.entityclassifier.core.THDController;
import cz.vse.fis.keg.entityclassifier.core.TextProcessor;
import cz.vse.fis.keg.entityclassifier.core.vao.Entity;
import cz.vse.fis.keg.entityclassifier.core.vao.Hypernym;
import cz.vse.fis.keg.entityclassifier.exporter.XMLExporter;
import cz.vse.fis.keg.entityclassifier.rest.authorizator.Authorizator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import cz.vse.fis.keg.entityclassifier.rest.vao.Error;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.ws.rs.GET;
/**
 *
 * @author Milan Dojčinovski 
 * <dojcinovski.milan (at) gmail.com> 
 * Twitter: @m1ci 
 * www: http://dojchinovski.mk 
 */

// JavaScript API for the Web aplicaiton
@Path("/v1/")
public class TargetedHypernymAPI {
    
    @Context
    Request request;        
    
    // e.g., http://localhost:8080/thd/api/v1/hypernyms?input=Amy%20Millan&offset=0&max_results=10&similarity=0.9&lang=en&range=all
    @POST
    @Produces({"application/xml", "application/json" })
    @Path("/hypernyms")
    public Response getHypernyms(
            String body,
            @QueryParam("lang") String lang,
            @QueryParam("entity_type") String entity_type,
            @QueryParam("knowledge_base") String knowledge_base,
            @QueryParam("priority_entity_linking") boolean priorityEntityLinking,
            @QueryParam("provenance") String provenance
            ) {

        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "========= accepted Web APP request =========");
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "lang: " + lang);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "entity_type: " + entity_type);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "knowledge_base: " + knowledge_base);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "priority_entity_linking: " + priorityEntityLinking);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "provenance: " + provenance);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "body: " + body);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started prcessing request...");
        
        TextProcessor r = TextProcessor.getInstance();
        String[] provs = provenance.split(",");
        List<Hypernym> results = new ArrayList<Hypernym>();
        results = r.processText_MT(body, lang, entity_type, knowledge_base, provs, priorityEntityLinking);            
        GenericEntity<List<Hypernym>> entity = new GenericEntity<List<Hypernym>>(results){};
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO,"Number of results: " + results.size());
        return Response.ok(entity).build();
    }

    // http://localhost:8080/thd/api/v1/extraction
    // http://localhost:8080/thd/api/v1/extraction?apikey=123456789&lang=en&format=xml&provenance=thd&knowledge_base=cached_results&entity_type=ne
    
    // PURE REST API    
    @POST
    @Path("/extraction")
    public Response getHypernymsAPI(
            
            String body,
            @QueryParam("lang") String lang,
            @QueryParam("format") String format,
            @QueryParam("provenance") String provenance,
            @QueryParam("knowledge_base") String knowledge_base,            
            @QueryParam("entity_type") String entity_type,
            @QueryParam("prefix") String prefix,
            @QueryParam("priority_entity_linking") boolean priority_entity_linking,
            @QueryParam("apikey") String apikey,
            @HeaderParam("Accept") String accept
            ) {
        
//        return Response.ok("hi").build();        
//        
        try {
            body = URLDecoder.decode(body, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "========= accepted Web API request =========");
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "lang: " + lang);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "format: " + format);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "provenance: " + provenance);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "knowledge_base: " + knowledge_base);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "entity_type: " + entity_type);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "prefix: " + prefix);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "priority_entity_linking: " + priority_entity_linking);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "api key: " + apikey);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "accept header: " + accept);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "body: " + body);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started prcessing request...");
        
        String[] provs = {};
        
        Error error = new Error();
        if(format == null){
            if(accept == null){
                format = "application/xml";
            }else {
                if(accept == null){
                    format = "application/xml";
                }else{
                    if(accept.equals("application/xml")){
                        format = accept;
                    } else if(accept.equals("application/json")){
                        format = accept;
                    } else if(accept.equals("application/rdf+xml")){
                        format = "rdf/xml";
                    }else {
                        error.setMessage("Not supported format");
                        error.setCode(42);
                        return Response.ok(error, "application/xml").status(406).build();
                    }
                }
            }
        } else {
            if(format.equals("xml")){
                format = "application/xml";
            }else if(format.equals("json")){
                format = "application/json";
            }else if(format.equals("rdfxml")){
                format = "rdf/xml";
            }else {
                error.setMessage("Not supported format");
                error.setCode(41);
                return Response.ok(error, "application/xml").status(406).build();
            }
        }
                
        if(prefix == null) {
            prefix = "http://ner.vse.cz/thd/ns/";
            
        } else if(prefix.equals("")){
            prefix = "http://ner.vse.cz/thd/ns/";
        }
        
        // API key checking
        if(apikey == null){
            error.setMessage("Could not authenticate you");
            error.setCode(43);
            return Response.ok(error, "application/xml").status(401).build();
        }

        if(!Authorizator.getInstance().isAuthorized(apikey)) {            
            error.setCode(44);
            error.setMessage("Could not authenticate you");
            return Response.ok(error, "application/xml").status(401).build();                
        }
        
        if(body.equals("")) {
            error.setCode(45);
            error.setMessage("Empty body request");
            return Response.ok(error,format).status(400).build();
        }
        
        if(knowledge_base == null){
            // ok
            knowledge_base = "linkedHypernymsDataset";
        }else if(knowledge_base.equals("linkedHypernymsDataset")){
            // ok
        }else if(knowledge_base.equals("local")){
            // ok
        }else if(knowledge_base.equals("live")) {
            // ok
        } else {
            error.setCode(46);
            error.setMessage("Not valid knowledge base parameter");
            return Response.ok(error, format).status(400).build();
        }
                
        // Provenance checking
        if(provenance != null) {
            provs = provenance.split(",");            
            if(provs.length > 0){
                for(String prov : provs){
                    if(prov.equals("thd") || prov.equals("dbpedia") || prov.equals("yago")){
                    }else{
                        error.setCode(47);
                        error.setMessage("Not valid provenance parameter");
                        return Response.ok(error, format).status(400).build();                    
                    }
                }            
            }
        } else {
            String[] provs2 = {"thd","dbpedia","yago"};
            provs = provs2;
        }
        
        // Entity type checking
        if(entity_type == null) {
            // ok 
            entity_type="all";
        } else if(entity_type.equals("ne")){
            // ok 
        } else if(entity_type.equals("ce")){
            // ok 
        } else if(entity_type.equals("all")){
            // ok 
        } else {
            error.setCode(48);
            error.setMessage("Not correctly set entity_type parameter");
            return Response.ok(error, format).status(400).build();        
        }
        
        // Language checking
        if(lang != null){
            if(lang.equals("en")||lang.equals("de")||lang.equals("nl")){
                // ok
            } else {
                error.setCode(49);
                error.setMessage("Not supported language");
                return Response.ok(error, format).status(400).build();
            }
        } else {
            // default language is English
            lang = "en";
        }
        
        TextProcessor r = TextProcessor.getInstance();        
        try {
            List<Entity> results = r.processTextAPI_MT(body, lang, entity_type, knowledge_base, provs, priority_entity_linking);
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO,"Number of extracted entities: " + results.size());
            
            if(format.equals("application/xml")){
                
                XMLExporter xmlExp = XMLExporter.getInstance();
                String s = xmlExp.toXML(results);
                return Response.ok(s, "application/xml").build();
                
            }else if(format.equals("application/json")){
                GenericEntity<List<Entity>> entities = new GenericEntity<List<Entity>>(results){};
                return Response.ok(entities, "application/json").build();            
                
            }else{
                return Response.ok("yes").build();  
            }
//            if(format.equals("rdf/xml")){
//                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, prefix), "application/rdf+xml").build();
//            }else{
//                return Response.ok(entities, format).build();            
//            }
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
            error.setMessage("Internal error");
            error.setCode(51);
            return Response.ok(error, format).status(500).build();
        }
    }
//    
    @GET
    @Path("/monitor")
    public String getHypernyms() {
        try {
            return "# number of free threads: " + THDController.getInstance().getNumberOfFreeWorkers() + "</br>";
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    
//    // NIF API
//    @POST
//    @Path("/extraction/nif")
//    public Response recognizeEntitiesInNIF(
//            
//            String input, // depends on informat and intype
//            String i,
//            String informat, // text or turtle, default is turtle
//            String f,
//            String intype, // direct, url or file, default is direct 
//            String t,
//            String outformat, // turtle or text, default is turtle
//            String o,
//            String urischeme, // RFC5147String or CStringInst, default is RFC5147String
//            String u,
//            String prefix, 
//            @QueryParam("apikey") String apikey
//            ) {
//        try {
//            input = URLDecoder.decode(input, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "========= accepted NIF API request =========");
//        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "api key: " + apikey);
//        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "input: " + input);
//        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started prcessing request...");
//        
//        Error error = new Error();
//        
//        // API key checking
//        if(apikey == null){
//            error.setMessage("Could not authenticate you");
//            error.setCode(43);
//            return Response.ok(error, "application/xml").status(401).build();
//        }
//
//        if(!Authorizator.getInstance().isAuthorized(apikey)) {            
//            error.setCode(44);
//            error.setMessage("Could not authenticate you");
//            return Response.ok(error, "application/xml").status(401).build();
//        }
//        String pIntype = "direct"; // default value for the intype parameter
//        if(intype == null) {
//            if(t == null) {
//                // intype parameter is not present. Fallback to default.
//            } else {
//                // intype parameter is present, check if the value is valid.
//                if(t.equals("direct")) {
//                    // OK.
//                } else if(t.equals("url") || t.equals("file")) {
//                    error.setCode(1);
//                    error.setMessage("No intype parameter specified.");
//                    return Response.ok(error, "application/xml").status(400).build();
//                }
//            }
//        } else {
//            if(t.equals("direct")) {
//                    // OK.
//                } else if(t.equals("url") || t.equals("file")) {
//                    // n
//                    error.setCode(1);
//                    error.setMessage("No intype parameter specified.");
//                    return Response.ok(error, "application/xml").status(400).build();
//                } else {
//                
//                }
//        }
//        
//        String knowledge_base = "linkedHypernymsDataset";
//        String[] provs = {};   
//        String[] provs2 = {"thd","dbpedia"};
//        provs = provs2;
//        
//        // Entity type checking
//        String entity_type="ne";
//        
//        
//        TextProcessor r = TextProcessor.getInstance();        
//        try {
//            List<Entity> results = r.processTextAPI_MT(input, "en", entity_type, knowledge_base, provs, false);
////            GenericEntity<List<Entity>> entities = new GenericEntity<List<Entity>>(results){};
//            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Number of extracted entities: " + results.size());
////            if()
////            String resInTurtle = NIFExporter.getInstance().toNIF(input, results, "turtle", prefix);
////            return Response.ok(resInTurtle, "text/turtle").build();
//        } catch (Exception ex) {
//            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
//            error.setMessage("Internal error");
//            error.setCode(51);
//            return Response.ok(error, "Something went wrong on the server side.").status(500).build();
//        }
//        return null;
//    }
}