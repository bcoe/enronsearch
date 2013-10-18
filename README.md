EnronSearch
===========

I wanted to play a bit with ElasticSearch's Java bindings, in the process
I made this fun little applicaton.

EnronSearch:

* provides command-line-tools for downloading and indexing CMU's Enron Email Corpus.
* includes a small Spark-based web-app for hosting the search engine on Heroku.
* provides a small JavaScript library for performing type-ahead search on the corpus.

Installing
----------

Install the dependent packages:

```bash
mvn package
```

Download the email corpus:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --download
```

Set the `ES_PORT` and `ES_HOST` environment variables.

Index the corpus:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --index
```

Run the search engine:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --server
```

Have fun!

Copyright
=========

Copyright (c) 2013 Benjamin Coe. See LICENSE.txt for
further details.
