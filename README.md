# 🚚 Logistics Platform

A containerized shipment tracking platform built using **Spring Boot, PostgreSQL, Redis, JavaScript, Docker, and Docker Compose**.

The platform allows users to create shipments, track shipments, update shipment status/location, and view complete shipment history.

---

## 🚀 Features

- 📦 Create a shipment
- 🔍 Track shipment using Tracking ID
- 🔄 Update shipment status and location
- 🕒 View complete shipment history
- ⚡ Redis caching for faster shipment retrieval
- 🗄️ PostgreSQL for persistent data storage
- 🌐 REST APIs using Spring Boot
- 🐳 Dockerized application
- 🔗 Docker Compose for multi-container deployment
- 💻 Web-based frontend

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java 17, Spring Boot |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL 17 |
| Cache | Redis 8 |
| Containerization | Docker |
| Orchestration | Docker Compose |
| Build Tool | Maven |

---

## 🏗️ System Architecture

```text
                    ┌─────────────────────┐
                    │      Frontend       │
                    │    HTML / CSS / JS  │
                    │      Port 80        │
                    └──────────┬──────────┘
                               │
                               │ HTTP REST API
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot      │
                    │      Backend        │
                    │      Port 8080      │
                    └─────────┬───────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
                    ▼                   ▼
             ┌─────────────┐     ┌─────────────┐
             │    Redis    │     │ PostgreSQL  │
             │    Cache    │     │  Database   │
             │   Port 6379 │     │  Port 5432  │
             └─────────────┘     └─────────────┘