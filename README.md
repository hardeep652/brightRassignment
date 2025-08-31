BRIGHTR ASSIGNMENT – JAVA/SPRING BOOT ELASTICSEARCH
OVERVIEW
This Spring Boot application indexes a set of sample courses into Elasticsearch and exposes a REST API to search courses with multiple filters, pagination, and sorting.

Assignment A Completed: Basic course search with filters, pagination, and sorting  
Assignment B (Bonus): Autocomplete & Fuzzy Search are pending

PREREQUISITES

Java 21+ installed  
Maven installed  
Docker & Docker Compose installed  
Postman or any REST client


PART 1: ELASTICSEARCH SETUP

Start Elasticsearch using Docker Compose:
sudo docker-compose up -d elasticsearch


Verify Elasticsearch is running:
curl http://localhost:9200

Expected Output:
{
  "name": "2178be8517e6",
  "cluster_name": "docker-cluster",
  "version": {
    "number": "7.17.9"
  },
  "tagline": "You Know, for Search"
}




PART 2: SAMPLE DATA

File: src/main/resources/sample-courses.json

This file contains 50+ course objects with the following fields:  

id  
title  
description  
category  
type  
gradeRange  
minAge  
maxAge  
price  
nextSessionDate

The data is automatically bulk-indexed into Elasticsearch on application startup.

PART 3: RUNNING THE APPLICATION

Build the application using Maven:
mvn clean install


Run the Spring Boot application:
mvn spring-boot:run



On startup, the ElasticsearchBootstrap component will:  

Create the courses index if it does not exist.  
Bulk-index all courses from sample-courses.json.  
Refresh the index for immediate availability.


PART 4: API USAGE
Search Endpoint
Endpoint: GET /api/search
Query Parameters



Parameter
Description
Example Value



q
Search keyword (full-text)
math


minAge
Minimum age filter
5


maxAge
Maximum age filter
10


category
Exact category filter
Math


type
Course type (ONE_TIME, COURSE, CLUB)
COURSE


minPrice
Minimum price filter
50


maxPrice
Maximum price filter
500


startDate
ISO date for next session
2025-01-01T00:00:00Z


sort
Sort order (upcoming, priceAsc, priceDesc)
upcoming


page
Page number (default: 0)
0


size
Page size (default: 10)
5


Example Request
curl "http://localhost:8080/api/search?q=math&minAge=5&maxAge=10&category=Math&type=COURSE&minPrice=50&maxPrice=500&startDate=2025-01-01T00:00:00Z&sort=upcoming&page=0&size=5"

Example Response
{
  "total": 3,
  "courses": [
    {
      "id": "101",
      "title": "Basic Math",
      "category": "Math",
      "price": 100.0,
      "nextSessionDate": "2025-06-10T15:00:00Z"
    }
  ]
}


PART 5: VERIFICATION

Check the Elasticsearch index:
curl http://localhost:9200/courses/_search?pretty


Use Postman or another REST client to test the /api/search endpoint with various combinations of filters, pagination, and sorting.



NOTES

Ensure Elasticsearch is running before starting the Spring Boot application.  
The application assumes the sample-courses.json file is present in the src/main/resources directory.  
Autocomplete and fuzzy search (Assignment B) are not yet implemented.
