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

        final ElasticSearch es = new ElasticSearch();
        
        if (System.getenv("PORT") != null) {
            setPort( Integer.parseInt( System.getenv("PORT") ));
        }

        staticFileLocation("/public");

        get(new Route("/search") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("application/json");
                return es.search( request.queryParams("q") ).toString();
            }
        });
    }
}