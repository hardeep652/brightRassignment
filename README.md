# BrightR Assignment – Java/Spring Boot + Elasticsearch

> **Note:** This project requires **Elasticsearch version 8.13**. Using any other version may cause compatibility issues between Spring Boot and the Elasticsearch client.

## Overview
This project demonstrates a Spring Boot application integrated with Elasticsearch. It indexes a set of sample “course” documents and exposes REST endpoints for:

- **Searching courses** with multiple filters, pagination, and sorting (Assignment A)
- **Autocomplete & fuzzy search** for course titles (Bonus, Assignment B)

The application uses **Docker Compose** to spin up a local Elasticsearch instance.

---

## Table of Contents
1. [Requirements](#requirements)  
2. [Project Setup](#project-setup)  
3. [Elasticsearch Setup](#elasticsearch-setup)  
4. [Sample Data](#sample-data)  
5. [Running the Application](#running-the-application)  
6. [API Endpoints](#api-endpoints)  
7. [Testing & Verification](#testing--verification)  
8. [Bonus Features](#bonus-features)  
9. [Project Structure](#project-structure)  

---

## Requirements
- Java 21
- Maven 3.8+
- Docker & Docker Compose
- cURL or Postman for API testing


---

## Project Setup
1. Clone the repository:
```bash
git clone https://github.com/hardeep652/brightRassignment.git
cd brightRassignment
 ```

## Elasticsearch Setup

1. **Start Elasticsearch via Docker Compose:**
```bash
docker-compose up -d

##Verify that Elasticsearch is running:

curl http://localhost:9200
```

## Sample Data
- File: `src/main/resources/sample-courses.json`
- Contains 50+ course objects with the following fields:
  - `id` (unique identifier)
  - `title` (short text)
  - `description` (longer text)
  - `category` (e.g., Math, Science, Art)
  - `type` (ONE_TIME, COURSE, CLUB)
  - `gradeRange` (e.g., "1st–3rd")
  - `minAge` and `maxAge`
  - `price` (decimal)
  - `nextSessionDate` (ISO-8601 date-time string)

**Usage:** Data is automatically bulk-indexed into Elasticsearch when the Spring Boot application starts.


## Running the Application

1. Build the project:
```bash
mvn clean install
```

2.Running the project:
```bash
mvn spring-boot:run
```

## Verify that courses index is populated 

```bash
curl http://localhost:9200/courses/_search
```


### 3. **API Endpoints**
List the endpoints, query parameters, and sample requests/responses. For example:

```markdown
## API Endpoints

### Search Courses
**Endpoint:** `GET /api/search`

**Query Parameters:**
- `q` – search keyword (title & description)
- `minAge`, `maxAge`
- `category`
- `type`
- `minPrice`, `maxPrice`
- `startDate` (ISO-8601)
- `sort` – `upcoming` (default), `priceAsc`, `priceDesc`
- `page`, `size` – pagination (default: 0, 10)
```
**Example Request:**
```bash
curl "http://localhost:8080/api/search?q=math&minAge=6&maxPrice=500&sort=priceAsc&page=0&size=5"
```

**Example Response**
```json
{
    "total": 3,
    "courses": [
        {
            "id": "course-012",
            "title": "Math Club: Olympiad Prep",
            "description": "Problem solving for math olympiads and contests.",
            "category": "Math",
            "type": "CLUB",
            "gradeRange": "8th–12th",
            "minAge": 13,
            "maxAge": 18,
            "price": 0.0,
            "nextSessionDate": "2025-09-14T17:30:00Z",
            "autocomplete": {
                "input": [
                    "Math Club: Olympiad Prep"
                ],
                "contexts": null,
                "weight": null
            }
        },
        {
            "id": "course-021",
            "title": "Math: Geometry Basics",
            "description": "Shapes, theorems and proof basics.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "7th–9th",
            "minAge": 12,
            "maxAge": 15,
            "price": 10.0,
            "nextSessionDate": "2025-09-27T09:00:00Z",
            "autocomplete": {
                "input": [
                    "Math: Geometry Basics"
                ],
                "contexts": null,
                "weight": null
            }
        },
        {
            "id": "course-009",
            "title": "Advanced Math Problem Solving",
            "description": "Challenging problems and techniques for high schoolers.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 20.0,
            "nextSessionDate": "2025-09-30T10:00:00Z",
            "autocomplete": {
                "input": [
                    "Advanced Math Problem Solving"
                ],
                "contexts": null,
                "weight": null
            }
        }
    ]
}
```

## Bonus Features – Assignment B (Autocomplete & Fuzzy Search)

### 1. Autocomplete Suggestions

This feature provides real-time suggestions for course titles as users type.

**Endpoint:** `GET /api/search/suggest?q={partialTitle}`

**Query Parameter:**
- `q` – partial or beginning of the course title

**Example Request:**
```bash
curl "http://localhost:8080/api/search/suggest?q=math"
```

**Example Response**
```json
[
    "Math Club: Olympiad Prep",
    "Math: Geometry Basics"
]
```

## Fuzzy Search

The fuzzy search feature allows users to search for course titles even if there are small typos or misspellings in the search query.

**Endpoint:** `GET /api/search`

**Query Parameter:**
- `q` – search keyword (title & description)
- Fuzziness is automatically applied for minor typos.

**Example Request with Typo:**
```bash
curl "http://localhost:8080/api/search?q=meth"
```

**Example Response**
```json
 {
    "total": 3,
    "courses": [
        {
            "id": "course-012",
            "title": "Math Club: Olympiad Prep",
            "description": "Problem solving for math olympiads and contests.",
            "category": "Math",
            "type": "CLUB",
            "gradeRange": "8th–12th",
            "minAge": 13,
            "maxAge": 18,
            "price": 0.0,
            "nextSessionDate": "2025-09-14T17:30:00Z",
            "autocomplete": {
                "input": [
                    "Math Club: Olympiad Prep"
                ],
                "contexts": null,
                "weight": null
            }
        },
        {
            "id": "course-021",
            "title": "Math: Geometry Basics",
            "description": "Shapes, theorems and proof basics.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "7th–9th",
            "minAge": 12,
            "maxAge": 15,
            "price": 10.0,
            "nextSessionDate": "2025-09-27T09:00:00Z",
            "autocomplete": {
                "input": [
                    "Math: Geometry Basics"
                ],
                "contexts": null,
                "weight": null
            }
        },
        {
            "id": "course-009",
            "title": "Advanced Math Problem Solving",
            "description": "Challenging problems and techniques for high schoolers.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 20.0,
            "nextSessionDate": "2025-09-30T10:00:00Z",
            "autocomplete": {
                "input": [
                    "Advanced Math Problem Solving"
                ],
                "contexts": null,
                "weight": null
            }
        }
    ]
}
```
