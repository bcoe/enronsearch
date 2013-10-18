package com.bcoe.enronsearch;

import static spark.Spark.*;
import spark.*;

/*
HTTP API for performing searches on
Enron dataset.
*/
public class WebApp 
{
    public static void start() {

        //final ElasticSearch es = new ElasticSearch();
        
        staticFileLocation("/public");

        get(new Route("/search") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
              /*  response.type("application/json");
                return es.search( request.queryParams("q") ).toString();*/
            }
        });
    }
}