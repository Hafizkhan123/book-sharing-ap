# 📚 Book Sharing System

A full-stack web application built with Spring Boot, MySQL and Vanilla JavaScript.

## 🔗 Live Demo
👉 https://book-sharing-app-production.up.railway.app

## 🛠️ Tech Stack
- Java 17
- Spring Boot 3.2.3
- Spring Security (Session-based Auth)
- Spring Data JPA + Hibernate
- MySQL
- HTML + CSS + Vanilla JavaScript
- Docker
- Deployed on Railway

## ✅ Features
- User Registration & Login (BCrypt password hashing)
- Add Books
- Share Books between users
- Return Books
- Sharing History tracking
- Borrowed Books tracking
- Admin Dashboard
- Session-based Authentication
- Global Exception Handling
- Bean Validation

## 📡 API Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| POST | /users/register | Register user |
| POST | /auth/login | Login |
| POST | /books/add/{userId} | Add book |
| GET | /books | Get all books |
| POST | /share/{bookId}/{toUserId} | Share book |
| GET | /share/borrowed/{userId} | Get borrowed books |
| PUT | /share/return/{shareId} | Return book |
| GET | /share | Full history |
