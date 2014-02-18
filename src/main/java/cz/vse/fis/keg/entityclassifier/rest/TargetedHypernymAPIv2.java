/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vse.fis.keg.entityclassifier.rest;

import com.hp.hpl.jena.rdf.model.Model;
import cz.vse.fis.keg.entityclassifier.core.TextProcessor;
import cz.vse.fis.keg.entityclassifier.core.vao.Entity;
import cz.vse.fis.keg.entityclassifier.exporter.XMLExporter;
import cz.vse.fis.keg.entityclassifier.rest.authorizator.Authorizator;
//import cz.vse.fis.keg.thd.nif.NIFExporter;
//import cz.vse.keg.thd.authorizator.Authorizator;
//import cz.vse.keg.thd.lib.THDController;
//import cz.vse.keg.thd.restapi.vao.Entity;
//import cz.vse.keg.thd.lib.TextProcessor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
//import cz.vse.keg.thd.restapi.vao.Error;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.GenericEntity;
/**
 *
 * @author Milan Dojčinovski 
 * <dojcinovski.milan (at) gmail.com> 
 * Twitter: @m1ci 
 * www: http://dojchinovski.mk 
 */

// JavaScript API for the Web aplicaiton
@Path("/v2/")
public class TargetedHypernymAPIv2 {
    
    @Context
    Request request;        
    
    @GET
    @Path("/monitor")
    public String getHypernyms() {
        try {
            return "# number of free threads: " ;//+ THDController.getInstance().getNumberOfFreeWorkers() + "</br>";
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
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
            @QueryParam("types_filter") String types_filter,
            @HeaderParam("Accept") String accept
            ) {
        
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
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "types filter: " + types_filter);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "api key: " + apikey);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "accept header: " + accept);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "body: " + body);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started prcessing request...");
        
        String[] provs = {};
        
        cz.vse.fis.keg.entityclassifier.rest.vao.Error error = new cz.vse.fis.keg.entityclassifier.rest.vao.Error();
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
            error.setCode(31);
            return Response.ok(error, "application/xml").status(401).build();
        }

        if(!Authorizator.getInstance().isAuthorized(apikey)) {            
            error.setCode(32);
            error.setMessage("Could not authenticate you");
            return Response.ok(error, "application/xml").status(401).build();                
        }
        
        if(body.equals("")) {
            error.setCode(45);
            error.setMessage("Empty body request");
            return Response.ok(error,format).status(400).build();
        }
        
        if(types_filter == null){
            // ok
            types_filter = "all";
        }else if(types_filter.equals("dbo")){
            // ok
        }else if(types_filter.equals("dbinstance")){
            // ok
        }else if(types_filter.equals("all")) {
            // ok
        } else {
            error.setCode(43);
            error.setMessage("Not valid types_filter parameter");
            return Response.ok(error, format).status(400).build();
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
            List<Entity> results = r.processTextAPI_MT(body, lang, entity_type, knowledge_base, provs, priority_entity_linking, "all");
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
    // NIF API
//    @POST
//    @Path("/extraction")
//    public Response recognizeEntitiesInNIF(
//            
//            @QueryParam("input") String input, // depends on informat and intype
//            @QueryParam("i") String i,
//            @QueryParam("informat") String informat, // text or turtle, default is turtle
//            @QueryParam("f") String f,
//            @QueryParam("intype") String intype, // direct, url or file, default is direct 
//            @QueryParam("t") String t,
//            @QueryParam("outformat") String outformat, // turtle or text, default is turtle
//            @QueryParam("o") String o,
//            @QueryParam("urischeme") String urischeme, // RFC5147String or CStringInst, default is RFC5147String
//            @QueryParam("u") String u,
//            @QueryParam("prefix") String prefix, 
//            @QueryParam("apikey") String apikey
//            ) {
//        return Response.ok().build();
//        try {
//            input = URLDecoder.decode(input, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.INFO, "========= accepted NIF API request =========");
//        Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.INFO, "api key: " + apikey);
//        Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.INFO, "input: " + input);
//        Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.INFO, "Started prcessing request...");
//        
//        Error error = new Error();
//        
//        String pOutFormat = "turtle";
//        if(outformat == null) {
//            if(o == null) {
//                // outformat parameter is not present. Fallback to default.
//            } else {
//                // outformat parameter is present, check if the value is valid.
//                if(o.equals("turtle")) {
//                    pOutFormat = o;
//                    // OK.
//                } else if(o.equals("text")) {
//                    error.setCode(0);
//                    error.setMessage("Not supported out format.");
//                    return Response.ok(error, "application/xml").status(400).build();
//                } else {
//                    error.setCode(0);
//                    error.setMessage("Not supported out format.");
//                    return Response.ok(error, "application/xml").status(400).build();                
//                }
//            }
//        } else {
//            if(outformat.equals("turtle")) {
//                pOutFormat = outformat;
//                // OK.
//            } else if(outformat.equals("text")) {
//                error.setCode(0);
//                error.setMessage("Not supported out format.");
//                return Response.ok(error, "application/xml").status(400).build();
//            } else {
//                error.setCode(0);
//                error.setMessage("Not supported out format.");
//                return Response.ok(error, "application/xml").status(400).build();
//            }
//        }
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
//            Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.INFO, "Number of extracted entities: " + results.size());
//            Model model = NIFExporter.getInstance().toNIF(input, results, prefix);
//            StringWriter out = new StringWriter();
//            if(pOutFormat.equals("turtle")) {
//                model.write(out, "Turtle");
//                String resultsInTurtle = out.toString();
//                return Response.ok(resultsInTurtle, "text/turtle").build();
//            } else {
//                error.setMessage("Not supported serialization format.");
//                error.setCode(0);
//                return Response.ok(error, "application/xml").status(406).build();
//            }            
//        } catch (Exception ex) {
//            Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.SEVERE, null, ex);
//            error.setMessage("Internal error");
//            error.setCode(51);
//            return Response.ok(error, "Something went wrong on the server side.").status(500).build();
//        }
//    }
}