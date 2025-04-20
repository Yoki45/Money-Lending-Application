# 💰 Money Lending Application

A comprehensive Spring Boot-based lending application that handles loan creation, approval, billing, repayment, overdue management, notifications, and support handling.

---

## 📦 Modules Overview

- **Loan Management**: Manages the full lifecycle of loans including creation, disbursement, repayment, overdue handling, and closing.
- **Product Module**: Handles loan product definitions, tenure configuration, and associated fees (service, daily, late).
- **Repayment Module**: Allows users to make repayments and view repayment history. Supports installment-based repayment.
- **Notification Module**: Sends event-based notifications via email or SMS.
- **FeedBack Module**: Allows users to raise feedback, issues, or support requests.
- **Scheduler Module**: Cron jobs for overdue checks, applying late fees, sending reminders.

---

## ⚙️ Architecture

- **Type**: Modular Monolith — all modules are developed in isolated packages but communicate through services.
- **Patterns Used**:
  - **Factory Pattern**: Used in Cron Jobs (e.g., date-based filtering) to dynamically determine and apply operations like reminders or overdue fee application.
  - **Builder Pattern**: Used extensively to construct DTOs and entities.
  - **DTO Pattern**: Used for transferring clean data between layers and controllers.
  - **Separation of Concerns**: Each module and service handles a specific domain responsibility.

---

## 🛠️ Prerequisites

- Java 17+
- Maven
- Docker (for Kafka)
- MySQL (Database)

---

## ▶️ How to Run

### 1. Start Kafka
```bash
docker run -d --name=kafka -p 9092:9092 apache/kafka
```

### 2. Add Configuration

#### Email (application-dev.properties and application-prod.properties)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### SMS - Africa's Talking (application-dev.properties and application-prod.properties)
```properties
#sms
africas.base.url=https://api.africastalking.com/version1/messaging
africas.apikey=atsk_f47b722bccbe56a69c4bf03e3474d91f039d2b82d6d97a11e3bf1bfc0009bc9bdc97f0e7
africas.username=yoki
```

### 3. Load SQL Scripts
Before running the app, execute SQL schema and seed files located under:
```
/src/main/resources/sql/
```

You’ll find a separate folder for each module (loan, product, notification, feedback, etc.) containing:
- `schema.sql`
- `data.sql`

These initialize the database structure and populate basic sample data.

### 4. Run Application
```bash
mvn spring-boot:run
```

---

## 🌐 API Documentation

Once the system is running, visit the Swagger UI:

[http://localhost:8099/api/v1/lms/swagger-ui/index.html#/](http://localhost:8099/api/v1/lms/swagger-ui/index.html#/)

---

## 🔗 Project Repository

[GitHub - Money Lending Application](https://github.com/Yoki45/Money-Lending-Application)

---