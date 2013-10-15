package com.bcoe.enronsearch;

import static spark.Spark.*;
import spark.*;

/*
HTTP endpoint for enron search engine.
*/
public class App 
{
    public static void main( String[] args ) {

        staticFileLocation("/public");

        get(new Route("/search") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });

    }
}