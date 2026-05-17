# cs122b-4
> Demonstration: [https://youtu.be/jyqnEpHWISE](https://youtu.be/jyqnEpHWISE)

The third project extends the second project and implements reCAPTCHA, secure connections with HTTPS, and password encryption to enhance security, an employee dashboard to add new stars and movies to the database with stored procedures, an ETL pipeline for data ingestion from CSV files into the database, and full-text search for advanced search features. 

## Features

* Security
  * reCAPTCHA
  * HTTPS
  * Password encryption
* Employee Dashboard
  * Add stars and movies 
  * Created stored procedure to update movie-star and movie-genre relationships and handle duplicates when movies are inserted
* ETL Pipeline for Data Ingestion
  * ``LOAD DATA`` statement for batch loading  
  * Staging table to perform data deduplication and validation
  * Handled missing values, invalid values, duplicates, and relationship inconsistencies
* Full-Text Search
  * Created full-text search index for movie titles
  * Tokenized keywords to show search results of movie titles containing keywords

## JMeter Performance Tests Results

| Single Instance Version Cases                   | Graph Results                                                                                                                         | Average Query Time (ms) | Average Search Servlet Time (ns) | Average JDBC Time (ns) |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|-------------------------|----------------------------------|------------------------|
| Case 1: HTTP/10 threads (No connection pooling) | ![Graph results for HTTP/10 threads (no connection pooling) with single instance.](./images/single-instance-case-1-graph-results.png) | 6378                    | 6222752491.20969                 | 5136559926.728236      |
| Case 2: HTTP/1 thread                           | ![Graph results for HTTP/1 thread with single instance.](./images/single-instance-case-2-graph-results.png)                           | 933                     | 725666273.5919758                | 722999179.2517033      |
| Case 3: HTTP/10 threads                         | ![Graph results for HTTP/10 threads with single instance.](./images/single-instance-case-3-graph-results.png)                         | 6354                    | 6170243216.723694                | 6169547199.7906885     |
| Case 4: HTTPS/10 threads                        | ![Graph results for HTTPS/10 threads with single instance.](./images/single-instance-case-4-graph-results.png)                        | 6854                    | 6209355996.607116                | 6208921451.184709      |

| Scaled Version Cases                            | Graph Results                                                                                                                         | Average Query Time (ms) | Average Search Servlet Time (ns) | Average JDBC Time (ns) |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|-------------------------|----------------------------------|------------------------|
| Case 1: HTTP/10 threads (No connection pooling) | ![Graph results for HTTP/10 threads (no connection pooling) with scaled version.](./images/scaled-version-case-1-graph-results.png)   | 3579                    | 6321559308.036715                | 6321166982.816806      |
| Case 2: HTTP/1 thread                           | ![Graph results for HTTP/1 thread with scaled version.](./images/scaled-version-case-2-graph-results.png)                             | 792                     | 701944008.0666162                | 701437536.2668433      |
| Case 3: HTTP/10 threads                         | ![Graph results for HTTP/10 threads with scaled verison.](./images/scaled-version-case-3-graph-results.png)                           | 6391                    | 1953493497.8509464               | 1953021200.5449526     |