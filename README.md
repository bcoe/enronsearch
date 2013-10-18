EnronSearch
===========

I wanted to play a bit with ElasticSearch's Java bindings, in the process
I made this fun little applicaton.

EnronSearch is an ElasticSearch index of the 500,000 emails in CMU's Enron corpus.

https://www.cs.cmu.edu/~enron/

EnronSearch:

* provides command-line tools for downloading and indexing the Enron emails.
* provides a small Spark-based web-app for interacting with the indexed corpus.
    * including some slick JavaScript for performing type-ahead searches.

Here it is in action:

http://enronsearch.herokuapp.com

Installing
----------

You will need to have an ElasticSearch server up and running to use EnronSearch.

Set the `ES_PORT` and `ES_HOST` environment variables, corresponding to this server.

Once you've done this:

* Install EnronSearch's dependent packages.

```bash
mvn package
```

* Download the Enron email corpus:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --download
```

* Index the corpus:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --index
```

* And run the search engine:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --server
```

EnronSearch will run on Heroku out of the gate, but you will need to host the ElasticSearch server elsewhere.

Have fun!

Copyright
=========

Copyright (c) 2013 Benjamin Coe. See LICENSE.txt for
further details.
