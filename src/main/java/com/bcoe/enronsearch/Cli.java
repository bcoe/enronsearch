package com.bcoe.enronsearch;

import org.apache.commons.cli.*;

/*
Provides a command line interface for
indexing documents and downloading the
Enron corpus.
*/
public class Cli 
{
    public static void main( String[] args ) {

      CommandLineParser parser = new BasicParser();

      Options options = new Options();

      options.addOption( "d", "download", false, "download enron dataset." );
      options.addOption( "i", "index", false, "reindex the enron dataset." );
      options.addOption( "h", "help", false, "display help information." );
      options.addOption( "s", "server", false, "run the web server." );

      try {
          
          CommandLine line = parser.parse( options, args );

          // Print available options.
          if ( line.hasOption("h") ) {

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "enronsearch", options );

          }

          // Download the enron email corpus.
          if( line.hasOption( "d" ) ) {
            
            Downloader downloader = new Downloader();
            System.out.println( "downloading enron mail dataset.");
            downloader.download();
            System.out.println( "download complete." );

          } else if ( line.hasOption( "i" ) ) {
            // Reindex the enron corpus.

            System.out.println( "index documents in enron dataset" );
            DatasetIterator datasetIterator = new DatasetIterator();
            ElasticSearch es = new ElasticSearch();

            es.index();
            
            int counter = 0;
            Message message = null;

            while ( (message = datasetIterator.nextMessage()) != null ) {
              es.indexMessage(message);
              counter += 1;
              System.out.println(counter + " messages indexed (" + message.getSubject() + ").");
            }

            es.cleanup();
          } else if ( line.hasOption( "s" ) ) {
            // Start the ES web app.

            WebApp.start();
          }
      } catch( ParseException exp ) {
          System.out.println( "Unexpected exception:" + exp.getMessage() );
      }
    }
}
