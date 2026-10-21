# Student Course Registration System

A full-stack web application for managing student registrations, courses, enrollments, and document version history.

## Features

- Student registration and login
- JWT-based authentication
- Course management and student enrollment
- MySQL database integration
- Document type management
- Document upload and version history
- Spring Batch enrollment report export
- React-based frontend

## Technologies Used

**Backend**
- Java
- Spring Boot
- Spring Data JPA
- Spring Security and JWT
- Spring Batch
- Maven

**Frontend**
- React
- Vite
- JavaScript
- Axios

**Database**
- MySQL

## Project Structure

```text
StudentCourseRegistrationSystem/
├── backend/
│   └── StudentRegistration/
│       ├── src/
│       └── pom.xml
├── frontend/
│   └── studentregistrationfrontend/
│       ├── src/
│       └── package.json
└── README.md
```

## Prerequisites

- Java version compatible with the backend configuration
- Maven
- Node.js and npm
- MySQL
- Git

## Backend Setup

1. Create a MySQL database named `student_registration_db`.
2. Configure the required environment variables:

   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `ADMIN_EMAIL`
   - `ADMIN_PASSWORD`

3. Open the backend project in STS or another Java IDE.
4. Run the Spring Boot application.

The backend uses port `8080` by default.

## Frontend Setup

1. Open a terminal in the frontend project folder.
2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

The Vite development server uses port `5173` by default.

## Document Management

The document module supports document types such as:

- `ID_PROOF`
- `COURSE_CERTIFICATE`
- `MARKSHEET`
- `OTHER`

It stores document metadata and version information in MySQL and stores uploaded file contents separately on disk during local development.

## Spring Batch

The backend includes a batch process for exporting enrollment records to a CSV report. Configure the required environment variables and follow the project's batch-run instructions.

## Security Notes

- Do not commit passwords, JWT secrets, email credentials, or `.env` files.
- Do not upload real student documents or personal information to the repository.
- Configure environment variables before starting the application.
- Review authorization and file-upload validation before production deployment.

## Project Status

This project is being developed as a learning project to practice Java, Spring Boot, React, MySQL, REST APIs, authentication, document management, and batch processing.