package cz.vse.fis.keg.entityclassifier.rest;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import cz.vse.fis.keg.entityclassifier.core.THDController;
import cz.vse.fis.keg.entityclassifier.core.TextProcessor;
import cz.vse.fis.keg.entityclassifier.core.conf.Settings;
import cz.vse.fis.keg.entityclassifier.core.salience.EntitySaliencer;
import cz.vse.fis.keg.entityclassifier.core.vao.Entity;
import cz.vse.fis.keg.entityclassifier.exporter.JSONExporter;
import cz.vse.fis.keg.entityclassifier.exporter.XMLExporter;
import cz.vse.fis.keg.entityclassifier.rest.authorizator.Authorizator;
import cz.vse.fis.keg.entityclassifier.rest.authorizator.RateBucket;
import cz.vse.fis.keg.entityclassifier.rest.authorizator.User;
import cz.vse.keg.keygenerator.APIKeyGenerator;
import cz.vse.keg.thd.nif.NIFFormatter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Milan Dojčinovski 
 <dojcinovski.milan (at) gmail.com> 
 Twitter: @m1ci 
 www: http://dojchinovski.mk 
 */

@Path("/v2/")
public class TargetedHypernymAPIv2 {
    
    @Context
    Request request;        
    
    @GET
    @Path("/monitor")
    public String getHypernyms() {
        try {
            return "# Number of free threads: " + THDController.getInstance().getNumberOfFreeWorkers() + "</br>";
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPIv2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @OPTIONS
    @Path("/extraction")
    public Response getHypernymsAPIOptions(
            @QueryParam("apikey") String apikey
    ) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                .header("Access-Control-Max-Age", "*")
                .header("Access-Control-Allow-Headers", "*")
                .build();
    }
    
    @POST
    @Path("/classification")
    public Response getEntityTypesAPI(
            
            String body,
            @QueryParam("lang") String lang,
            @QueryParam("format") String format,
            @QueryParam("provenance") String provenance,
            @QueryParam("knowledge_base") String knowledge_base,            
            @QueryParam("prefix") String prefix,
            @QueryParam("priority_entity_linking") boolean priority_entity_linking,
            @QueryParam("apikey") String apikey,
            @QueryParam("types_filter") String types_filter,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Origin") String origin,
            @HeaderParam("Content-Type") String content_type
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
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "prefix: " + prefix);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "priority_entity_linking: " + priority_entity_linking);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "types filter: " + types_filter);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "api key: " + apikey);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "accept header: " + accept);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Content-Type header: " + content_type);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "body: " + body);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started prcessing request...");
        
        Settings.getInstance();
        
        Model model = null;
        String[] provs = {};
        
        cz.vse.fis.keg.entityclassifier.rest.vao.Error error = new cz.vse.fis.keg.entityclassifier.rest.vao.Error();
        
        String originStr = "*";
        if(origin != null) {
            originStr = origin;
        }
        
        if(format == null) {
            if(accept == null){
                format = "application/xml";
            }else {
                if(accept == null){
                    format = "application/xml";
                } else {
                    if(accept.equals("application/xml")){
                        format = accept;
                    } else if(accept.equals("application/json")) {
                        format = accept;
                    } else if(accept.equals("application/rdf+xml")) {
                        format = "rdf/xml";
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null,"RDF/XML");
                    } else if(accept.equals("application/ld+json")){
                        format = "jsonld";
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null,"JSON-LD");
                    } else if(accept.equals("application/x-turtle")){
                        format = "turtle";
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null, "TURTLE");
                    }else {
                        error.setMessage("Not supported format");
                        error.setCode(42);
                        return Response
                                .ok(error, "application/xml")
                                .status(406)
                                .header("Access-Control-Allow-Origin", originStr)
                                .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                                .header("Access-Control-Max-Age", "*")
                                .header("Access-Control-Allow-Headers", "*")
                                .build();
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
                model = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream( body.getBytes() );
                model.read(is, null, "RDF/XML");
            }else if(format.equals("application/x-turtle")){
                format = "turtle";
                model = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream( body.getBytes() );
                model.read(is, null, "TURTLE");
            }else {
                error.setMessage("Not supported format");
                error.setCode(41);
                return Response
                        .ok(error, "application/xml")
                        .status(406)
                        .header("Access-Control-Allow-Origin", originStr)
                        .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                        .header("Access-Control-Max-Age", "*")
                        .header("Access-Control-Allow-Headers", "*")
                        .build();
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
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .build();
        }
        
        if(body.equals("")) {
            error.setCode(45);
            error.setMessage("Empty body request");
            return Response
                    .ok(error,format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
                        return Response
                                .ok(error, format)
                                .status(400)
                                .header("Access-Control-Allow-Origin", originStr)
                                .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                                .header("Access-Control-Max-Age", "*")
                                .header("Access-Control-Allow-Headers", "*")
                                .build();
                    }
                }            
            }
        } else {
            String[] provs2 = {"thd","dbpedia","yago"};
            provs = provs2;
        }

        // Language checking
        if(lang != null){
            if(lang.equals("en")||lang.equals("de")||lang.equals("nl")){
                // ok
            } else {
                error.setCode(49);
                error.setMessage("Not supported language");
                return Response
                        .ok(error, format)
                        .status(400)
                        .header("Access-Control-Allow-Origin", originStr)
                        .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                        .header("Access-Control-Max-Age", "*")
                        .header("Access-Control-Allow-Headers", "*")
                        .build();
            }
        } else {
            // Default language is English.
            lang = "en";
        }
        
        RateBucket rate = Authorizator.getInstance().isAuthorized(apikey);
        if(!rate.getIsKeyValid()) {            
            error.setCode(32);
            error.setMessage("Could not authenticate you. Your key is not valid.");
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .build();
        } else if(!rate.isIsAuthorized()) {
            error.setCode(32);
            error.setMessage("Could not authenticate you. You reached the limit.");
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();                
        }        
        TextProcessor r = TextProcessor.getInstance();        
        try {
            
            List<Entity> results = r.classifyEntityAPI_MT(body, lang, knowledge_base, provs, types_filter);
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Number of extracted entities: " + results.size());
                        
            if(format.equals("application/xml")) {
                
                XMLExporter xmlExp = XMLExporter.getInstance();
                String s = xmlExp.toXMLOneEntity(results);
                return Response
                    .ok(s, "application/xml")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
                
            }else if(format.equals("application/json")) {
//                GenericEntity<List<Entity>> entities = new GenericEntity<List<Entity>>(results){};
                JSONExporter jsonExp = JSONExporter.getInstance();
                String s = jsonExp.toJSONOneEntity(results);
                
                return Response.ok(s, "application/json")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            } else if(format.equals("rdf/xml") ) {
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, body, prefix, "rdf/xml", model), "application/rdf+xml")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            } else if(format.equals("jsonld")) {
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, body, prefix, "jsonld", model), "application/ld+json")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            } else if(format.equals("turtle")) {
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, body, prefix, "turtle", model), "application/x-turtle")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            } else {
                return Response.ok("yes")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
            }
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
            error.setMessage("Internal error");
            error.setCode(51);
            return Response.ok(error, format).status(500)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
        }
    }
    
    @GET
    @Path("/consumers/{id}")
    public Response getUserLimits(@PathParam("id") String apikey) {
        
        Settings.getInstance();
        
        RateBucket rate = Authorizator.getInstance().getConsumersRateLimits(apikey);
        if(!rate.getIsKeyValid()) {
            return Response
                    .status(400)
                    .build();
        } else {
            return Response
                    .ok()
                    .status(200)
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
        }
    }
    
    @GET
    @Path("/consumers/")
    public Response getAllRegisteredUsers(@QueryParam("apikey") String apikey){
        
        Settings.getInstance();

        if(Authorizator.getInstance().isAdmin(apikey)) {
            ArrayList<User> users = Authorizator.getInstance().getAllUsers();
            String result = "";
            for(User u: users) {
                result += "Name: " + u.getName()+"</br>";
                result += "API key: " + u.getApikey()+"</br>";
                result += "Interval: " + u.getInterval()+"</br>";
                result += "Limit: " + u.getLimit()+"</br>";
                result += "---------------------------------- </br>";
            }
            return Response
                    .ok(result)
                    .build();
        } else {
            return Response
                    .status(401)
                    .build();
        }
    }
    
    /**
     * API for creating new users.
    */
    @PUT
    @Path("/consumers/")
    @Produces(MediaType.TEXT_PLAIN)
    public Response creteConsumer(@QueryParam("apikey") String apikey,
            @QueryParam("requester") String requester,
            @QueryParam("email") String email,
            @QueryParam("interval") String interval,
            @QueryParam("limit") String limit
            ) {
        
        Settings.getInstance();

        if(Authorizator.getInstance().isAdmin(apikey)){
            String newapikey = new APIKeyGenerator().generateClientApiKey(requester, email, interval, limit);
            return Response
                    .ok(newapikey)
                    .status(201)
                    .build();
        } else {
            return Response
                    .status(401)
                    .build();
        }
    }
    
    @POST
    @Path("/extraction")
    public Response performEntityExtraction(
            
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
            @QueryParam("spotting_method") String spotting_method,
            @QueryParam("linking_method") String linking_method,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Content-Type") String content_type,
            @HeaderParam("Origin") String origin
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
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "spotting method: " + spotting_method);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "linking method: " + linking_method);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "api key: " + apikey);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Accept header: " + accept);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Content-Type header: " + content_type);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "body: " + body);
        Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Started processing the request ...");
        
        Settings.getInstance();
        Model model = null;
        String[] provs = {};
        
        cz.vse.fis.keg.entityclassifier.rest.vao.Error error = new cz.vse.fis.keg.entityclassifier.rest.vao.Error();
        
        String originStr = "*";
        if(origin != null) {
            originStr = origin;
        }
        
        if(format == null) {
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
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null,"RDF/XML");
                    } else if(accept.equals("application/ld+json")){
                        format = "jsonld";
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null,"JSON-LD");
                    } else if(accept.equals("application/x-turtle")){
                        format = "turtle";
                        model = ModelFactory.createDefaultModel();
                        InputStream is = new ByteArrayInputStream( body.getBytes() );
                        model.read(is, null,"TURTLE");
                    } else {
                        error.setMessage("Not supported format");
                        error.setCode(42);
                        return Response
                                .ok(error, "application/xml")
                                .status(406)
                                .header("Access-Control-Allow-Origin", originStr)
                                .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                                .header("Access-Control-Max-Age", "*")
                                .header("Access-Control-Allow-Headers", "*")
                                .build();
                    }
                }
            }
        } else {
            if(format.equals("xml")){
                format = "application/xml";
            }else if(format.equals("json")){
                format = "application/json";
            } else if(format.equals("rdfxml")){
                format = "rdf/xml";
                model = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream( body.getBytes() );                
                model.read(is, null,"RDF/XML");
            }else {
                error.setMessage("Not supported format");
                error.setCode(41);
                return Response
                        .ok(error, "application/xml")
                        .status(406)
                        .header("Access-Control-Allow-Origin", originStr)
                        .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                        .header("Access-Control-Max-Age", "*")
                        .header("Access-Control-Allow-Headers", "*")
                        .build();
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
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .build();
        }
        
        if(body.equals("")) {
            error.setCode(45);
            error.setMessage("Empty body request");
            return Response
                    .ok(error,format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
        }

        if(linking_method == null){
            // ok, lucene search skip first as default linking method
            if(knowledge_base.equals("live")) {
                linking_method = "WikipediaSearch";            
            } else {
                linking_method = "LuceneSearchSkipDisPage";            
            }
        }else if(linking_method.equals("LuceneSearch")
                || linking_method.equals("LuceneSearchSkipDisPage")) {            
            if(knowledge_base.equals("live")) {
                error.setCode(44);
                error.setMessage("Not valid linking_method parameter. "
                        + " You can not at the same use 'LuceneSearch' and 'live' option. "
                        + " 'LuceneSearch can be used only with the 'local' or 'linkedHypernymsDataset' options.");
                return Response
                        .ok(error, format)
                        .status(400)
                        .header("Access-Control-Allow-Origin", originStr)
                        .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                        .header("Access-Control-Max-Age", "*")
                        .header("Access-Control-Allow-Headers", "*")
                        .build();            
            }
            // ok
        } else if(linking_method.equals("WikipediaSearch")){
            // ok
        } else if(linking_method.equals("SFISearch")){
            // ok
        } else if(linking_method.equals("AllVoting")){
            // ok
        } else if(linking_method.equals("SurfaceFormSimilarity")){
            // ok
        } else {
            error.setCode(44);
            error.setMessage("Not valid linking_method parameter. "
                    + linking_method + " is not a valid linking method. "
                    + "You can choose between 'LuceneSearch', 'LuceneSearchSkipDisPage', 'WikipediaSearch', 'SFISearch' or 'AllVoting'.");
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
        }
        
        // spotting_method parameter
        if(spotting_method == null){
            // ok, gramars as default spotting method
            spotting_method = "grammars";
        }else if(spotting_method.equals("grammars")) {
            // ok
        }else if(spotting_method.equals("CRF")){
            // ok
        } else {
            error.setCode(44);
            error.setMessage("Not valid spotting_method parameter. "
                    + spotting_method + " is not a valid linking method. "
                    + "You can choose between 'grammars' or 'CRF' .");
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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
                        return Response
                                .ok(error, format)
                                .status(400)
                                .header("Access-Control-Allow-Origin", originStr)
                                .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                                .header("Access-Control-Max-Age", "*")
                                .header("Access-Control-Allow-Headers", "*")
                                .build();
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
            return Response
                    .ok(error, format)
                    .status(400)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();        
        }
        
        // Language checking
        if(lang != null){
            if(lang.equals("en")||lang.equals("de")||lang.equals("nl")){
                // ok
            } else {
                error.setCode(49);
                error.setMessage("Not supported language");
                return Response
                        .ok(error, format)
                        .status(400)
                        .header("Access-Control-Allow-Origin", originStr)
                        .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                        .header("Access-Control-Max-Age", "*")
                        .header("Access-Control-Allow-Headers", "*")
                        .build();
            }
        } else {
            // Default language is English.
            lang = "en";
        }
        
        RateBucket rate = Authorizator.getInstance().isAuthorized(apikey);
        if(!rate.getIsKeyValid()) {
            error.setCode(32);
            error.setMessage("Could not authenticate you. Your API key is not valid.");
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .build();
        } else if(!rate.isIsAuthorized()) {
            error.setCode(32);
            error.setMessage("Could not authenticate you. You reached the limit.");
            return Response
                    .ok(error, "application/xml")
                    .status(401)
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();                
        }
        TextProcessor r = TextProcessor.getInstance();
        
        try {

            String tmpPar = "something_else";
            if(apikey.equals("4a6a80b4ad7248fa9ff62aedf6278cb9")) {
                tmpPar = "irapi";
                linking_method = "WikipediaSearch";
            }
            
            String text = "";
            if(format.equals("turtle")) {
                StmtIterator iter = model.listStatements(null,RDF.type, model.getResource("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context") );
                if(iter.hasNext()) {
                    Resource context = iter.nextStatement().getSubject();
                    NodeIterator nIter = model.listObjectsOfProperty(context, model.getProperty("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#isString"));
                    if(nIter.hasNext()) {
                        text = nIter.nextNode().asLiteral().getString();
                    }
                }                    
            } else {
                text = body;
            }
            List<Entity> results = r.processTextAPI_MT(text, lang, entity_type, knowledge_base, provs, priority_entity_linking, types_filter, spotting_method, linking_method, tmpPar);
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.INFO, "Number of extracted entities: " + results.size());
            
            EntitySaliencer.getInstance().computeSalience(results);
            
            if(format.equals("application/xml")) {
                XMLExporter xmlExp = XMLExporter.getInstance();
                String s = xmlExp.toXML(results);
                return Response
                    .ok(s, "application/xml")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
                
            }else if(format.equals("application/json")) {
//                GenericEntity<List<Entity>> entities = new GenericEntity<List<Entity>>(results){};
                JSONExporter jsonExp = JSONExporter.getInstance();
                String s = jsonExp.toJSON(results);
                
                return Response.ok(s, "application/json")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            } else if(format.equals("rdf/xml") ){
                
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, text, prefix, "rdf/xml", model), "application/rdf+xml")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();

            } else if(format.equals("jsonld")){
                
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, text, prefix, "jsonld", model), "application/ld+json")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
                
            } else if(format.equals("turtle")){
                
                return Response.ok(NIFFormatter.getInstance().formatAsNIF(results, text, prefix, "turtle", model), "application/x-turtle")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("X-Rate-Limit-Limit", rate.getIntervalLimit())
                    .header("X-Rate-Limit-Remaining", rate.getLeftRequests())
                    .header("X-Rate-Limit-Reset", rate.getTimeUntilReset())
                    .build();
            
            } else {
                return Response.ok("problem")
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
            }
        } catch (Exception ex) {
            Logger.getLogger(TargetedHypernymAPI.class.getName()).log(Level.SEVERE, null, ex);
            error.setMessage("Internal error");
            error.setCode(51);
            return Response.ok(error, format).status(500)
                    .header("Access-Control-Allow-Origin", originStr)
                    .header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                    .header("Access-Control-Max-Age", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .build();
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