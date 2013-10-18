EnronSearch
===========

Search the Eron email corpus: https://www.cs.cmu.edu/~enron/

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

Index the corpus:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --index
```

Run the search engine:

```bash
java -cp target/classes:target/dependency/*:./ com.bcoe.enronsearch.Cli --server
```
