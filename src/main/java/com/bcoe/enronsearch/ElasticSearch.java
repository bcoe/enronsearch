package com.bcoe.enronsearch;

import java.io.IOException;

import org.elasticsearch.client.Client;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.elasticsearch.action.admin.indices.create.*;
import org.elasticsearch.action.admin.indices.delete.*;
import org.elasticsearch.action.admin.indices.flush.*;

import org.elasticsearch.action.admin.cluster.health.*;

import org.elasticsearch.common.settings.*;
import org.elasticsearch.common.settings.ImmutableSettings;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders.*;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.client.transport.*;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/*
Wrapper for ElasticSearch, performs
searching and indexing tasks.
*/
public class ElasticSearch {

  private static String indexName = "enron-messages";
  private static String mappingName = "message";
  private Client client;

  public ElasticSearch() {
    client = new TransportClient()
      .addTransportAddress(new InetSocketTransportAddress(
        System.getenv("ES_HOST"),
        Integer.parseInt( System.getenv("ES_PORT") )
      ));
  }

  public void index() {
    deleteIndex();
    createIndex();
    putMapping();
  }

  private void deleteIndex() {
    try {
      client.admin()
        .indices()
        .delete(new DeleteIndexRequest(indexName))
        .actionGet();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void createIndex() {
    Settings indexSettings = ImmutableSettings.settingsBuilder()
      .put("number_of_shards", 1)
      .put("number_of_replicas", 0)
      .put("analysis.analyzer.default.tokenizer", "uax_url_email")
      .build();

    CreateIndexRequestBuilder createIndexBuilder = client.admin()
      .indices()
      .prepareCreate(indexName);

    createIndexBuilder.setSettings(indexSettings);

    CreateIndexResponse createIndexResponse = createIndexBuilder
      .execute()
      .actionGet();

    waitForYellow();
  }

  private void waitForYellow() {
    ClusterHealthRequestBuilder healthRequest = client.admin()
      .cluster()
      .prepareHealth();
    
    healthRequest.setIndices(indexName);
    healthRequest.setWaitForYellowStatus();

    ClusterHealthResponse healthResponse = healthRequest.execute()
      .actionGet();
  }

  private void putMapping() {
    try {

      XContentBuilder mapping = XContentFactory.jsonBuilder()
         .startObject()
            .startObject("message")
              .startObject("properties")
                .startObject("to")
                  .field("type", "string")
                  .field("store", "yes")
                .endObject()
                .startObject("from")
                  .field("type", "string")
                  .field("store", "yes")
                .endObject()
                .startObject("subject")
                  .field("type", "string")
                  .field("store", "yes")
                .endObject()
                .startObject("body")
                  .field("type", "string")
                  .field("store", "yes")
                .endObject()
              .endObject()
            .endObject()
          .endObject();

      client.admin()
        .indices() 
        .preparePutMapping(indexName) 
        .setType(mappingName) 
        .setSource(mapping) 
        .execute()
        .actionGet(); 

      flush();
    } catch (IOException e) {
      System.out.println("Failed to put mapping: " + e);
    }
  }

  private void flush() {
    client.admin()
      .indices()
      .flush(new FlushRequest(indexName))
      .actionGet();
  }

  public void indexMessage(Message message) {
    try {
      XContentBuilder document = XContentFactory.jsonBuilder()
        .startObject()
          .field("to", message.getTo())
          .field("from", message.getFrom())
          .field("subject", message.getSubject())
          .field("body", message.getBody())
        .endObject();

      client.prepareIndex(indexName, mappingName, message.getId())
        .setSource(document)
        .execute()
        .actionGet();

    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public SearchResponse search(String query) {
    return client.prepareSearch(indexName)
        .setTypes(mappingName)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(
          QueryBuilders.queryString(query)
          .field("_all")
        )
        .setFrom(0).setSize(30)
        .addHighlightedField("to", 0, 0)
        .addHighlightedField("from", 0, 0)
        .execute()
        .actionGet();
  }

  public void cleanup() {
    client.close();
  }
}
