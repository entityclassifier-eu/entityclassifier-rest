/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vse.fis.keg.entityclassifier.rest;

import com.hp.hpl.jena.rdf.model.Model;
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
    
    // NIF API
    @POST
    @Path("/extraction")
    public Response recognizeEntitiesInNIF(
            
            @QueryParam("input") String input, // depends on informat and intype
            @QueryParam("i") String i,
            @QueryParam("informat") String informat, // text or turtle, default is turtle
            @QueryParam("f") String f,
            @QueryParam("intype") String intype, // direct, url or file, default is direct 
            @QueryParam("t") String t,
            @QueryParam("outformat") String outformat, // turtle or text, default is turtle
            @QueryParam("o") String o,
            @QueryParam("urischeme") String urischeme, // RFC5147String or CStringInst, default is RFC5147String
            @QueryParam("u") String u,
            @QueryParam("prefix") String prefix, 
            @QueryParam("apikey") String apikey
            ) {
        return Response.ok().build();
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
    }
}