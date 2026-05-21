# Okuulib Backend

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](#)
[![License](https://img.shields.io/badge/license-MIT-blue)](#)

## Project Overview

Okuulib is a backend service for an AI-assisted library platform dedicated to reading and interacting with books in the Kyrgyz language. The system provides a robust API for managing digital libraries, author profiles, bookmarks, and incorporates an interactive AI assistant via WebSockets to help users deeply engage with the text.

## Tech Stack

* **Language:** Java
* **Framework:** Spring Boot (WebMVC, Data JPA, Security, WebSocket)
* **Security:** JWT (JSON Web Tokens) and Google OAuth integration
* **Media Storage:** Cloudinary
* **Document Processing:** PDF parsing and custom text chunking
* **Infrastructure:** Docker & Docker Compose
* **Build Tool:** Maven

## Core Features

* **Advanced Library Management:** Complete CRUD operations for works, authors, genres, and chapters.
* **AI-Assisted Reading:** WebSocket-based chat sessions allowing users to interact dynamically with an AI assistant contextualized to the specific reading material.
* **Document Processing:** Automated PDF handling, text extraction, and chunking pipeline for AI vectorization and search.
* **User Engagement:** Robust tracking system for user bookmarks and reading progress.
* **Secure Authentication:** Role-based access control (RBAC) with JWT authentication and external provider support (Google Auth).
* **Media Management:** Direct integration with Cloudinary for seamless image and asset uploads.

## Installation Steps

### Prerequisites
* Java JDK (17 or higher recommended)
* Maven
* Docker and Docker Compose (for infrastructure)

### Option 1: Full Docker Deployment
To run the database and backend services using Docker Compose:

1. Clone the repository:
   ```bash
   git clone [https://github.com/your-org/okuulib_backend.git](https://github.com/your-org/okuulib_backend.git)
   cd okuulib_backend
    ```
    Start the services:
    ```Bash

    docker-compose up -d --build
    ```
    The API will be accessible at ```http://localhost:8080```

Option 2: Local Development

To run the Spring Boot application locally while utilizing Docker for the database:

Start the dependent infrastructure (e.g., PostgreSQL):
    ```Bash

    docker-compose up -d db
    ```
Build the project using the Maven wrapper:
    ```Bash

    ./mvnw clean install -DskipTests
    ```
Run the application:
    ```Bash

    ./mvnw spring-boot:run
    ```
Usage Examples

Below are standard interactions with the REST API.
1. Authenticate User

Retrieve a JWT token to access protected endpoints.
```Bash

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securepassword"
  }'
```
Expected Response:
```JSON

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5c...",
  "type": "Bearer"
}
```
2. Fetch Library Works

Retrieve a paginated list of available books or works.
```Bash

curl -X GET "http://localhost:8080/api/works?page=0&size=10" \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
```
3. Initialize AI Chat Session

Open a session for the AI-assisted reading feature.
```Bash

curl -X POST http://localhost:8080/api/chat-sessions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
  -d '{
    "workId": 123
  }'
```
Contributing

-Fork the repository.

-Create your feature branch: ```git checkout -b feature/amazing-feature```

-Commit your changes: ```git commit -m 'Add amazing feature'```

-Push to the branch: ```git push origin feature/amazing-feature```

-Submit a pull request.

Please ensure all tests pass (./mvnw test) and your code adheres to the project's formatting guidelines before submitting.
License

This project is licensed under the MIT License - see the LICENSE file for details.
Made with ❤️ by Manas
