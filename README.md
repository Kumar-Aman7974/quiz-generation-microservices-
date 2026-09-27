# Quiz Generation Microservices 🎯

A scalable, cloud-ready **microservices-based Quiz Generation Platform** built with **Spring Boot** and **Spring Cloud**. The system allows users to create quizzes dynamically by fetching random questions from a question bank, submit answers, and receive instant scores.

This project demonstrates industry-standard microservices patterns including **service discovery**, **API gateway routing**, **inter-service communication via Feign**, and **distributed configuration**.

---

## 📌 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Microservices Breakdown](#-microservices-breakdown)
- [Service Communication Flow](#-service-communication-flow)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Project Structure](#-project-structure)
- [Key Features](#-key-features)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)

---

## 🏗 Architecture Overview

The application follows a **distributed microservices architecture** where each service is independently deployable and communicates over HTTP/REST. A **Eureka Service Registry** handles service discovery, and an **API Gateway** acts as the single entry point for all client requests.

```
                        ┌─────────────────────┐
                        │      Client         │
                        │  (Postman / Browser)│
                        └──────────┬──────────┘
                                   │
                                   ▼
                        ┌─────────────────────┐
                        │    API Gateway      │
                        │   (Port: 8765)      │
                        └──────────┬──────────┘
                                   │
                 ┌─────────────────┴─────────────────┐
                 ▼                                   ▼
       ┌──────────────────┐               ┌──────────────────┐
       │ Question Service │◄──────────────│   Quiz Service   │
       │   (Port: 8080)   │   Feign Call  │   (Port: 8090)   │
       └────────┬─────────┘               └────────┬─────────┘
                │                                  │
                ▼                                  ▼
         ┌────────────┐                    ┌────────────┐
         │  MySQL DB  │                    │  MySQL DB  │
         │ (question) │                    │   (quiz)   │
         └────────────┘                    └────────────┘
                ▲                                  ▲
                └──────────────┬───────────────────┘
                               │
                    ┌──────────────────────┐
                    │  Service Registry    │
                    │  (Eureka, Port:8761) │
                    └──────────────────────┘
```

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Microservices** | Spring Cloud (Eureka, Gateway, OpenFeign) |
| **Database** | MySQL 8 |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **API Testing** | Postman |
| **Version Control** | Git & GitHub |
| **IDE** | IntelliJ IDEA |

---

## 🧩 Microservices Breakdown

### 1️⃣ Service Registry (Eureka Server)
- **Port:** `8761`
- **Role:** Acts as a central directory where all microservices register themselves. Enables dynamic service discovery — services don't need hardcoded URLs to talk to each other.
- **URL:** `http://localhost:8761`

### 2️⃣ API Gateway
- **Port:** `8765`
- **Role:** Single entry point for all client requests. Routes incoming traffic to the appropriate microservice based on URL patterns using **Spring Cloud Gateway**.
- **Example Routes:**
  - `/question-service/**` → Question Service
  - `/quiz-service/**` → Quiz Service

### 3️⃣ Question Service
- **Port:** `8080`
- **Role:** Manages the question bank. Provides APIs to:
  - Fetch all questions
  - Filter questions by category
  - Generate a set of random question IDs for a quiz
  - Retrieve questions by IDs
  - Calculate a score based on submitted responses

### 4️⃣ Quiz Service
- **Port:** `8090`
- **Role:** Handles quiz creation and submission. Communicates with **Question Service** via **OpenFeign** to fetch questions and evaluate responses.
- Provides APIs to:
  - Create a new quiz based on a category and number of questions
  - Fetch a quiz by ID (returns questions without correct answers)
  - Submit answers and receive a score

---

## 🔄 Service Communication Flow

**Creating a Quiz:**
```
Client → API Gateway → Quiz Service → (Feign) → Question Service
                                    ← (Question IDs)
                                    → (Fetch Questions)
                                    ← (Question Wrappers)
Quiz Service → saves Quiz → returns Quiz ID to Client
```

**Submitting a Quiz:**
```
Client → API Gateway → Quiz Service → (Feign) → Question Service
                                    ← (Score calculated)
Quiz Service → returns Score to Client
```

---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed:
- **Java 17+** — [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8+** — [Download](https://dev.mysql.com/downloads/)
- **Git** — [Download](https://git-scm.com/)

### Step 1: Clone the Repository
```bash
git clone https://github.com/Kumar-Aman7974/quiz-generation-microservices-.git
cd quiz-generation-microservices
```

### Step 2: Set Up the Databases
Create two MySQL databases:
```sql
CREATE DATABASE question_db;
CREATE DATABASE quiz_db;
```

Then update the `application.properties` in each service:

**`question-service/src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/question_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

**`quiz-service/src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Start Services in Correct Order ⚠️

**Order matters!** Start services in this sequence:

```bash
# 1. Start Service Registry (must start first)
cd service-registry
./mvnw spring-boot:run

# 2. Start Question Service
cd ../question-service
./mvnw spring-boot:run

# 3. Start Quiz Service
cd ../quiz-service
./mvnw spring-boot:run

# 4. Start API Gateway
cd ../api-gateway
./mvnw spring-boot:run
```

### Step 4: Verify Everything is Running

| Service | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8765 |
| Question Service | http://localhost:8080 |
| Quiz Service | http://localhost:8090 |

All three services (question, quiz, gateway) should appear as **registered instances** in the Eureka dashboard.

---

## 📡 API Endpoints

### 🔹 Question Service (via Gateway: `/question-service`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/question/allQuestions` | Fetch all questions |
| `GET` | `/question/category/{category}` | Fetch questions by category |
| `POST` | `/question/add` | Add a new question |
| `GET` | `/question/generate?category={cat}&numQ={n}` | Generate random question IDs |
| `POST` | `/question/getQuestions` | Fetch questions by IDs |
| `POST` | `/question/getScore` | Calculate score for submitted answers |

### 🔹 Quiz Service (via Gateway: `/quiz-service`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/quiz/create?category={cat}&numQ={n}&title={title}` | Create a new quiz |
| `GET` | `/quiz/get/{id}` | Fetch a quiz by ID |
| `POST` | `/quiz/submit/{id}` | Submit answers and get score |

### 📝 Sample Request — Create a Quiz
```http
POST http://localhost:8765/quiz-service/create?category=Java&numQ=5&title=Java%20Basics
```

### 📝 Sample Request — Submit Answers
```http
POST http://localhost:8765/quiz-service/submit/1
Content-Type: application/json

[
  { "id": 1, "response": "Polymorphism" },
  { "id": 2, "response": "JVM" }
]
```

---

## 📁 Project Structure

```
quiz-generation-microservices/
│
├── service-registry/           # Eureka Server
│   └── src/main/java/com/aman/service_registry/
│       └── ServiceRegistryApplication.java
│
├── api-gateway/                # Spring Cloud Gateway
│   └── src/main/java/com/aman/api_gateway/
│       └── ApiGatewayApplication.java
│
├── question-service/           # Question Management
│   └── src/main/java/com/aman/question_service/
│       ├── controller/         # REST endpoints
│       ├── service/            # Business logic
│       ├── dao/                # JPA repositories
│       └── model/              # Entities & DTOs
│
├── quiz-service/               # Quiz Management
│   └── src/main/java/com/aman/quiz_service/
│       ├── controller/         # REST endpoints
│       ├── service/            # Business logic
│       ├── dao/                # JPA repositories
│       ├── feign/              # Feign client for Question Service
│       └── model/              # Entities & DTOs
│
└── README.md
```

---

## ✨ Key Features

- ✅ **Service Discovery** using Netflix Eureka — no hardcoded service URLs
- ✅ **API Gateway** as a single entry point with dynamic routing
- ✅ **Inter-service communication** via OpenFeign clients
- ✅ **Loose coupling** between services — each has its own database
- ✅ **RESTful APIs** following standard HTTP conventions
- ✅ **Layered architecture** — Controller → Service → DAO → Model
- ✅ **Clean separation** of DTOs and Entities
- ✅ **Proper Git workflow** with Conventional Commits

---

## 🔮 Future Enhancements

- [ ] Add JWT-based authentication & authorization
- [ ] Containerize all services using **Docker** and **docker-compose**
- [ ] Add **Spring Cloud Config Server** for centralized configuration
- [ ] Implement **Resilience4j** circuit breakers for fault tolerance
- [ ] Add **Prometheus + Grafana** for monitoring
- [ ] Introduce **Kafka** for asynchronous event-driven communication
- [ ] Write integration tests with **Testcontainers**
- [ ] Deploy to **AWS ECS** or **Kubernetes**

---

## 👨‍💻 Author

**Aman Kumar**
- GitHub: [@Kumar-Aman7974](https://github.com/Kumar-Aman7974)
- Repository: [quiz-generation-microservices](https://github.com/Kumar-Aman7974/quiz-generation-microservices-)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

⭐ If you found this project helpful or interesting, consider giving it a star!
