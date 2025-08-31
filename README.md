# BrightR Assignment – Java/Spring Boot Elasticsearch

## Overview
This project demonstrates a **Spring Boot application integrated with Elasticsearch** to manage and search courses. The system supports:

- Full-text search
- Filtering
- Pagination & sorting
- Bulk indexing from sample data

The backend exposes RESTful APIs for searching and retrieving course information.

## Prerequisites
- Java 21+ installed
- Maven installed
- Docker & Docker Compose installed
- Postman or any REST client

## Technologies Used
- Java 21
- Spring Boot
- Elasticsearch 7.17.9
- Maven
- Docker
- JSON (for sample data)






## PART 2: SAMPLE DATA

The application uses a sample dataset of courses to demonstrate Elasticsearch indexing and search functionality.

### **File Location**
### **Description**
- Contains **50+ course objects**  
- Each course has the following fields:

| Field           | Type    | Description |
|-----------------|---------|-------------|
| `id`            | String  | Unique identifier for the course |
| `title`         | String  | Name of the course |
| `description`   | String  | Detailed description of the course |
| `category`      | String  | Category of the course (e.g., Math, Science, Art) |
| `type`          | String  | ONE_TIME / COURSE / CLUB |
| `gradeRange`    | String  | Grade levels suitable for the course (e.g., 1-5) |
| `minAge`        | Integer | Minimum age for the course |
| `maxAge`        | Integer | Maximum age for the course |
| `price`         | Double  | Price of the course |
| `nextSessionDate` | String (ISO Date) | Date and time of the next session |

### **Usage**
- The data is **automatically bulk-indexed** into Elasticsearch on application startup.  
- No manual indexing is required.  
- Example of a single course object:

json
{
  "id": "101",
  "title": "Basic Math",
  "description": "Introduction to basic math concepts",
  "category": "Math",
  "type": "COURSE",
  "gradeRange": "1-5",
  "minAge": 5,
  "maxAge": 10,
  "price": 100.0,
  "nextSessionDate": "2025-06-10T15:00:00Z"
}

## PART 3: RUNNING THE APPLICATION

This section explains how to build and run the Spring Boot application, and how it interacts with Elasticsearch on startup.```

### **Build the Application**
Use Maven to clean, compile, and package the project:

mvn clean install
mvn spring-boot:run


## PART 4: API USAGE

This section explains the main REST API endpoint for searching courses, including query parameters, sorting, filtering, and pagination.

### **Search Endpoint**

**GET** `/api/search`

### **Query Parameters**

| Parameter      | Type    | Description |
|----------------|---------|-------------|
| `q`            | String  | Search keyword (full-text search) |
| `minAge`       | Integer | Minimum age filter |
| `maxAge`       | Integer | Maximum age filter |
| `category`     | String  | Exact category filter (e.g., Math, Science) |
| `type`         | String  | Course type: ONE_TIME / COURSE / CLUB |
| `minPrice`     | Double  | Minimum price filter |
| `maxPrice`     | Double  | Maximum price filter |
| `startDate`    | String  | ISO date for filtering courses by next session date |
| `sort`         | String  | Sorting options: upcoming / priceAsc / priceDesc |
| `page`         | Integer | Page number for pagination (default: 0) |
| `size`         | Integer | Page size for pagination (default: 10) |

### **Example Request**


- Search for courses with the keyword "math" only (no filters, default pagination):
GET /api/search?q=math
### **Example Response 4**

```json
{
  "total": 3,
  "courses": [
    {
      "id": "101",
      "title": "Basic Math",
      "category": "Math",
      "price": 100.0,
      "nextSessionDate": "2025-06-10T15:00:00Z"
    },
    {
      "id": "102",
      "title": "Advanced Math",
      "category": "Math",
      "price": 150.0,
      "nextSessionDate": "2025-06-15T15:00:00Z"
    },
    {
      "id": "103",
      "title": "Math for Kids",
      "category": "Math",
      "price": 90.0,
      "nextSessionDate": "2025-06-20T15:00:00Z"
    }
  ]
}
```
## PART 5: VERIFICATION

This section explains how to verify that the courses have been indexed correctly in Elasticsearch and that the search API is working as expected.

### **Check Elasticsearch Index**

- You can query the `courses` index directly in Elasticsearch to verify data:
GET http://localhost:9200/courses/_search?pretty



- This will return all indexed courses in a readable JSON format.

### **Verify via API**

1. Use **Postman** or any REST client to call the `/api/search` endpoint.  
2. Test different combinations of:
   - Search keyword (`q`)  
   - Filters (`minAge`, `maxAge`, `category`, `type`, `minPrice`, `maxPrice`, `startDate`)  
   - Sorting (`sort`)  
   - Pagination (`page` and `size`)  

3. Ensure that:
   - The **total number of hits** matches your expectations.  
   - The **courses returned** match the filters applied.  
   - Pagination and sorting work correctly.

### **Notes**

- Make sure Elasticsearch is running before performing verification.  
- Any changes in `sample-courses.json` require reindexing (restart the Spring Boot application).  
- Use simple keyword searches first to verify basic indexing, then test advanced filters and sorting.

## PART A: ASSIGNMENT COMPLETED
