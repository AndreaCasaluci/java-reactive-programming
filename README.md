
# Satellite Management System

## Overview
This project is a Satellite Management System designed as an exercise to demonstrate the principles and practices of Reactive Programming in Java. It showcases how to efficiently handle asynchronous data streams with `Mono` and `Flux`, and how to leverage parallel programming to perform concurrent operations in a non-blocking way.


## Key Features
- **Reactive API Endpoints:** The system provides a set of RESTful API endpoints that are fully reactive, built on top of Spring WebFlux
- **Usage of Reactor (Mono/Flux):**
    - **Mono:** Used for operations that produce at most one item (e.g., fetching a single satellite);
    - **Flux:** Used for operations that produce zero to many items (e.g., fetching a list of satellites);
- **Parallel Processing:** The project demonstrates how to process multiple tasks concurrently, using Reactor's `parallel()` and `flatMap()` operators to maximize throughput while maintaining non-blocking I/O
- **Integration with External API:** The system fetches satellite data from an [external API](https://tle.ivanstanojevic.me/api/tle/), handling potential rate limits and failures gracefully using reactive patterns
## Technologies Used
- **Java 17:** The core language for developement
- **Spring Boot:** For building the RESTful Web Service
- **Spring WebFlux:** To enable reactive programming and create non-blocking REST APIs
- **Project Reactor:** The library that provides the `Mono` and `Flux` reactive types
- **PostgreSQL:** The relational database used to persist satellite data
- **Flyway:** Tool for Database Migrations
- **Testcontainers:** For integration testing with a real PostgreSQL database in a Docker container
- **JUnit 5:** For unit and integration testing
- **Mockito:** For mocking dependencies in tests
- **Lombok:** To reduce boilerplate code with annotations
## Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **Docker** (for running Testcontainers)

### Running the Application
- Clone the repository:
  ```bash
  git clone https://github.com/AndreaCasaluci/java-reactive-programming.git
  cd java-reactive-programming
  ```
- Build the project:
  ```bash
  mvn clean install
  ```
- Run the application:
  ```bash
  mvn spring-boot:run
  ```
The application will start on `http://localhost:8080`

### Running Tests
The project includes tests, which can be run using:
```bash
mvn test
```
Tests are configured to use ***Testcontainers***, which will automatically spin up a PostgreSQL container during the test run.

## Running with Docker Compose
This project includes a `docker-compose.yml` file to quickly set up and run the application along with a PostgreSQL database

### Steps to Run
- Build the Docker image for the application:
  ```bash
  docker-compose build
  ``` 
- Start the services (PostgreSQL and the application):
  ```bash
  docker-compose up
  ``` 
- Access the application at `http://localhost:8080`
- Stop the services:
  ```bash
  docker-compose down
  ``` 
## API Endpoints
The following are the key API endpoints provided by the application:
- **GET /v1/satellite/{guid}:** Retrieve satellite data by GUID
- **GET /v1/satellite/list:** Retrieve a paginated list of satellites, with optional filtering and sorting
- **POST /v1/satellite:** Create a new satellite record
- **PUT /v1/satellite/{guid}:** Update an existing satellite record
- **DELETE /v1/satellite/{guid}:** Delete a satellite by GUID
- **POST /v1/satellite/fetch:** Fetch and update satellite data from an external source
  The projects provides also an OpenAPI Swagger UI which contains all the API and their description, examples and more. Project Swagger can be reached at `http://localhost:8080/swagger-ui.html` while the app is running.
## Core Concepts Demonstrated

### Reactive Programming with Reactor
The project heavily utilizes Reactor's `Mono` and `Flux` types to handle asynchronous, non-blocking operations. By using reactive streams, the application can process a large number of concurrent requests with minimal resource usage, which is ideal for I/O-bound tasks like fetching data from databases or external APIs.
### Parallel Programming
In scenarios where data from an external source needs to be processed in chunks, the application demonstrates how to split these operations across multiple threads, using Reactor's `parallel()` and `runOn(Schedulers.boundedElastic())` operators. This allows the application to handle larger workloads efficiently while still adhering to the principles of reactive programming.
### Error Handling and Resilience
The application includes robust error handling to manage potential issues, such as external API rate limits or unavailble services. Techniques such as `retryWhen`, `onErrorResume`, and *Custom Exceptions* ensure that the system remains resilient and provides meaningful feedback when things go wrong.
