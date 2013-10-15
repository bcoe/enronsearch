EnronSearch
===========

Performs type ahead search on the Enron email dataset.

mvn clean dependency:copy-dependencies package
java -cp target/enronsearch-1.0-SNAPSHOT.jar:target/dependency/* com.bcoe.enronsearch.App
