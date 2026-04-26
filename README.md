# cs122b-3
> Demonstration: [https://youtu.be/F1dAByPfSxI](https://youtu.be/F1dAByPfSxI)

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
