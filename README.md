# Library System Demo

This project is a Library  System Demo API built using **Java 17** and **Spring Boot**.

The API provides endpoints for managing books and borrowers, allowing users to register books, borrow and return them, and view available books with pagination.

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.3.4
- **PostgreSQL**: Database for storing book and borrower information
- **Docker**: For containerization of the PostgreSQL database

## Getting Started

### Prerequisites

- Java 17 installed on your machine and is set as the default Java versions in your terminal
- Maven 3.6.3 for building the project
- Docker installed on your machine

### Running the application

1. Start PostgreSQL with Docker

To run the PostgreSQL database using Docker, navigate to the project directory and run:
```bash
docker-compose up -d
```
This command will start a PostgreSQL container in detached mode.
`docker-compose.yml` file is configured to set up the database.


2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`.



### Making database changes

After adding new changeset in `resources/db.changelog/db.changelog-master.xml`, Liquibase will automatically apply any pending changesets on startup.

You can run Liquibase update manually by running the command below:
```bash
mvn liquibase:update
```

### Unit tests

Run the command below to run all unit tests
```
mvn test
```

### API Endpoints

Here are the available API endpoints:

#### 1. **Books**

- **Register a Book**
    - **Endpoint**: `POST /api/library/book`
    - **Request Body**: JSON object containing `isbn`, `title`, `author`
```json
 {
      "isbn": "1988575060",
      "title": "Hell Yeah Or No",
      "author": "Derek Sivers"
  }
```
- **Response**: Returns the registered book object.
```json
{
    "id": 1,
    "isbn": "1988575060",
    "title": "Hell Yeah Or No",
    "author": "Derek Sivers",
    "borrower": null
}
```

- **Get All Books (Paginated)**
    - **Endpoint**: `GET /api/library/books?page={page}&size={size}`
    - **Query Parameters**:
        - `page`: Page number (0-indexed)
        - `size`: Number of items per page
    - **Response**: Returns a paginated list of books.
```json
{
    "content": [
        {
            "id": 1,
            "isbn": "1988575060",
            "title": "Hell Yeah Or No",
            "author": "Derek Sivers",
            "borrower": null
        },
        {
            "id": 2,
            "isbn": "1988575061",
            "title": "Hell Yeah Or No",
            "author": "Derek Sivers",
            "borrower": null
        }
    ]
}
```

- **Borrow a Book**
    - **Endpoint**: `POST /api/library/borrow/{borrowerId}/{bookId}`
    - **Path Variables**:
        - `borrowerId`: ID of the borrower
        - `bookId`: ID of the book to borrow
    - **Response**: Returns the borrowed book object.
```json
{
    "id": 1,
    "isbn": "1988575060",
    "title": "Hell Yeah Or No",
    "author": "Derek Sivers",
    "borrower": {
        "id": 1,
        "name": "Oliver Bennett",
        "email": "oliver.bennett@maildemo.com"
    }
}
```

- **Return a Book**
    - **Endpoint**: `POST /api/library/return/{bookId}`
    - **Path Variable**:
        - `bookId`: ID of the book to return
    - **Response**: Returns the updated book object indicating it has been returned.
```json
{
    "id": 152,
    "isbn": "1988575060",
    "title": "Hell Yeah Or No",
    "author": "Derek Sivers",
    "borrower": null
}
```

#### 2. **Borrowers**

- **Register a Borrower**
    - **Endpoint**: `POST /api/library/borrower
    - **Request Body**: JSON object containing `name`, `email`
```json
  {
      "name": "Oliver Bennett",
      "email": "oliver.bennett@maildemo.com"
  }
```
- **Response**: Returns the registered borrower object.
```json
{
    "id": 1,
    "name": "Oliver Bennett",
    "email": "oliver.bennett@maildemo.com"
}
```

- **Get All Borrowers (Paginated)**
    - **Endpoint**: `GET /api/library/borrowers?page={page}&size={size}`
    - **Query Parameters**:
        - `page`: Page number (0-indexed)
        - `size`: Number of items per page
    - **Response**: Returns a paginated list of books.
```json
{
  "content": [
    {
      "id": 1,
      "name": "Oliver Bennett",
      "email": "oliver.bennett@maildemo.com"
    },
    {
      "id": 2,
      "name": "Oliver Smith",
      "email": "oliver.smith@maildemo.com"
    }
  ]
}
```

### Swagger UI

You can access Swagger UI to view and test all API endpoints by navigating to:

```
http://localhost:8080/swagger-ui.html
```


### Assumptions
- Borrower's email should be unique
- A borrower can borrow multiple books
