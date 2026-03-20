# 📖 Okuulib - Backend API

> **Okuulib - Your Literary World.** > A robust Spring Boot backend powering a modern literary platform. It features seamless book uploading with automated PDF parsing, AI-driven reading assistants, real-time WebSocket chats, and advanced full-text search capabilities.

## ✨ Key Features

* **🔒 Authentication & Authorization**: Secure JWT-based authentication with Role-Based Access Control (Admin/User). Includes Google OAuth2 integration for quick sign-ins.
* **📄 Automated PDF Processing**: Intelligent PDF parsing that extracts text, chunks it into HTML format, and builds book chapters. **Privacy & Storage First**: PDF files are strictly parsed for text extraction and are immediately deleted from the server; they are *never* permanently stored.
* **🤖 AI Literary Assistant**: Real-time chat with an AI assistant using WebSockets, allowing users to ask context-aware questions about the books they are reading.
* **🔍 Full-Text Search (FTS)**: High-performance search using PostgreSQL's `tsvector` and `unaccent` extensions, enabling users to search across titles, descriptions, authors, and genres simultaneously.
* **☁️ Cloud File Storage**: Seamless Dropbox API integration for secure storage of user profile photos and book cover images. Includes automated and manual cleanup routines for temporary files.
* **🔖 Advanced Bookmarking**: Granular bookmarking system tracking exact user progress via chunk IDs and text offsets, complete with personal notes.

## 🛠️ Tech Stack

* **Core**: Java 21, Spring Boot 3.4.1
* **Database**: PostgreSQL 15, Spring Data JPA, Hibernate
* **Security**: Spring Security, JSON Web Tokens (JJWT), Google API Client
* **Integrations**: Dropbox Core SDK (Cloud Storage), Apache PDFBox (PDF Processing)
* **Real-time**: Spring WebSockets
* **Mapping & Boilerplate**: MapStruct, Lombok
* **API Documentation**: SpringDoc OpenAPI (Swagger UI)
* **Deployment**: Docker, Docker Compose

## 🚀 Getting Started

### Prerequisites
* Java 21 or higher
* Maven 3.9+ 
* PostgreSQL 15+
* Docker & Docker Compose (for containerized deployment)

### Environment Variables
Before running the application, configure the following environment variables (or update `src/main/resources/application.properties`):

| Variable | Description |
| :--- | :--- |
| `spring.datasource.url` | PostgreSQL connection URL (e.g., `jdbc:postgresql://localhost:5439/okuulib_db`) |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `umai.app.secret` | Base64 encoded secret key for JWT generation |
| `umai.app.isproduction` | Boolean flag (`true`/`false`) for secure cookie handling |
| `dropbox.access.token` | Dropbox API access token for cloud storage |
| `spring.security.oauth2.client.registration.google.client-id` | Google OAuth2 Client ID |
| `okuulib.ai.ask_endpoint` | Endpoint URL for the external AI service |

### Running Locally with Docker (Recommended)

The easiest way to get the application and database running is via Docker Compose. From the root directory of the project, run:

```bash
docker compose up --build
```
This will spin up both the Spring Boot application (exposed on port 8080) and the PostgreSQL database (exposed on port 5439).
Running Manually

Start your local PostgreSQL server and create a database named okuulib_db.

Clean and package the application:
Bash
```
./mvnw clean package -DskipTests
```
Run the application:
Bash
```
java -jar target/umai-0.0.1-SNAPSHOT.jar
```
📚 API Documentation

Once the application is running, you can interact with the API and view the full documentation via Swagger UI:

Swagger UI: http://localhost:8080/swagger-ui.html

API Docs (JSON): http://localhost:8080/v3/api-docs

🏗️ Architecture Overview

    controller/: REST API endpoints and WebSocket handlers.

    service/: Business logic, including PDF parsing (PdfTextServiceImpl), AI communication (AiServiceImpl), and Dropbox operations (DropboxServiceImpl).

    repo_service/: Wrapper services for Data Access logic, separating standard CRUD operations from complex business rules.

    security/: JWT filters, role seeding, and Google OAuth configurations.

    model/: Entities, DTOs, and Request/Response payload structures.

    config/: Setup for WebSockets, Dropbox client, AI RestClient, and Global Exception Handling.

🧹 Scheduled Tasks

    Dropbox Cleanup: A scheduled cron job runs daily at 3:00 AM to automatically purge temporary files (like unprocessed covers or old profile photos) from the Dropbox storage to maintain a clean environment.

Made with ❤️ by Manas
