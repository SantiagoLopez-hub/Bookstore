# Bookstore

This Spring Boot API application was created using Spring Web, Lombok, Data JPA, and H2 as an RDBMS. It manages Authors, Books, and Publishers with full CRUD support.

## How to Run the App

1.  **Prerequisites:**
    *   Java 17 or higher.
    *   Gradle (optional, as the wrapper is included).

2.  **Build and Run:**
    Navigate to the `Bookstore` directory and run the following command:

    ```bash
    ./gradlew clean bootRun
    ```

    The application will start on `http://localhost:8080`.

## How to Run Tests

### Unit Tests
To run the unit tests (Service and Controller layers), execute:

```bash
./gradlew test
```

### Cucumber QA Tests
The Cucumber tests are integrated into the standard test suite. They verify end-to-end scenarios defined in `src/test/resources/features`. To run them, simply execute the standard test command:

```bash
./gradlew test
```

If you wish to run *only* the Cucumber tests (though Gradle usually runs all), you can filter by the test class:

```bash
./gradlew test --tests com.santiago.bookstore.cucumber.CucumberTest
```

## Resilience and Safety Features

The codebase has been refactored to adhere to high safety and quality standards:

1.  **Layered Architecture & DTOs:**
    *   **Separation of Concerns:** The application clearly separates Controllers (Routes), Services, and Repositories.
    *   **Data Transfer Objects (DTOs):** `BookRequest`, `AuthorRequest`, and `PublisherRequest` are used for incoming data. This prevents internal entities from being exposed directly and allows for strict control over input data.

2.  **Robust Input Validation:**
    *   **Jakarta Validation:** All input DTOs are annotated with `@NotNull`, `@NotBlank`, `@Min`, etc.
    *   **Controller Validation:** The `@Valid` annotation is used in controllers to trigger validation automatically. Invalid requests result in a 400 Bad Request with detailed error messages.

3.  **Comprehensive Exception Handling:**
    *   **Global Exception Handler:** A `GlobalExceptionHandler` captures specific exceptions (like `ResourceNotFoundException`) and unexpected runtime errors. This ensures clients always receive a consistent, formatted JSON error response (e.g., 404 Not Found, 500 Internal Server Error) instead of raw stack traces.

4.  **Transactional Integrity:**
    *   **`@Transactional`:** Service methods modifying data are annotated with `@Transactional`. This ensures that operations are atomic; if any part of a complex operation fails, the entire transaction is rolled back, preventing data inconsistency.

5.  **Safe Database Operations:**
    *   **JPA Repository:** The project uses `JpaRepository` for type-safe database interactions.
    *   **Cascading Deletes:** Deletion logic handles relationships (e.g., deleting an author removes their books) to maintain referential integrity.

## Interacting with the API

The API accepts and returns JSON.

### Endpoints

*   **/books**
*   **/authors**
*   **/publishers**

### Examples

**1. Create an Author**
*   **URL:** `POST http://localhost:8080/authors`
*   **Body (JSON):**
    ```json
    {
        "name": "J.K. Rowling"
    }
    ```

**2. Create a Book**
*   **URL:** `POST http://localhost:8080/books`
*   **Body (JSON):**
    ```json
    {
        "title": "Harry Potter and the Philosopher's Stone",
        "price": 20.00,
        "isbn": "9780747532743",
        "authorId": 1,
        "publisherId": 1
    }
    ```

**3. Get All Books**
*   **URL:** `GET http://localhost:8080/books`

**4. Update a Book**
*   **URL:** `PUT http://localhost:8080/books/1`
*   **Body (JSON):**
    ```json
    {
        "title": "Harry Potter (Updated)",
        "price": 25.00,
        "isbn": "9780747532743",
        "authorId": 1,
        "publisherId": 1
    }
    ```

**5. Delete a Book**
*   **URL:** `DELETE http://localhost:8080/books/1`
