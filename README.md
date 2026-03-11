# 📖 Library Management System

A production-ready **REST API** backend for managing a library, built with **Spring Boot 3.4.3**, **Spring Data JPA**, and **MySQL**.

 Features
- Book Management — Add, Update, Delete, Search by title or author
- Student Management — Register, Update, Delete students
- Book Issue System — Issue books, return books, auto fine calculation for late returns
- Global Exception Handling with structured JSON error responses
- Bean Validation on all API inputs

##  Tech Stack
- Java 24
- Spring Boot 3.4.3
- Spring Data JPA + Hibernate
- MySQL 8
- Maven

##  API Base URL
`http://localhost:8080/api`

## ⚙️ Setup
1. Create MySQL database: `CREATE DATABASE library_db;`
2. Update `application.properties` with your MySQL credentials
3. Run: `mvn clean spring-boot:run`
