# Banking System Microservices

## Overview

This project implements two co-dependent microservices for managing bank customers and their accounts. The solution is built using **Java 21** and **Spring Boot 3.4.2**, leveraging **Spring Cloud 2024.0.0** to enhance service communication and maintainability. The system follows a microservices architecture with synchronous and asynchronous communication mechanisms, proper logging, validation, exception handling, and documentation.

## Design Decisions

### 1. **Service Communication**

- **Synchronous Communication:** Used **Feign Client** to enable REST-based communication between the **account-service** and **customer-service**.
- **Asynchronous Communication:** Integrated **RabbitMQ** for event-driven messaging. RabbitMQ was chosen for its lightweight nature and simplicity compared to Kafka or AWS SQS.

### 2. **Database Choice**

- **Development Database:** **MySQL** was selected as the primary relational database.
- **Testing Database:** **H2** was used for unit testing, ensuring an isolated and efficient test environment.

### 3. **Security**

- Initially, **Keycloak** was used for securing the application. However, due to time constraints, all related configurations were commented out to focus on completing the core business logic.

### 4. **Spring Profiles**

- Implemented **development** and **testing** profiles to manage different configurations seamlessly.

### 5. **Testing & Code Coverage**

- **Unit and Integration Tests:** Used **JUnit 5** and **Mockito** for writing unit and integration tests.
- **Code Coverage:** Generated a test report using **JaCoCo**, achieving **79%** coverage for both services. JaCoCo reports can be found in 
  Jacoco-Reports directory (in the root of project directory) 


### 6. **Logging**

- Used **@Log4j2** for structured and efficient logging across the microservices.

### 7. **Validation & Exception Handling**

- Enforced **business logic constraints** such as:
    - Customer ID must be **7 digits**.
    - Account number must be **10 digits**, starting with the customer ID.
    - A customer can have a maximum of **10 accounts**.
    - Only one account can be a **salary account**, while others can be **savings or investment accounts**.

### 8. **API Documentation**

- Used **JavaDocs** and **Swagger (OpenAPI)** to document endpoints, enabling easy API exploration.

## Shortcomings & Assumptions

### **Shortcomings:**

- **Keycloak integration was not completed** due to time constraints.
- **Limited scalability testing** was performed.

### **Assumptions:**

- Customers and accounts are managed within a **single banking organization**.
- The **account ID is generated dynamically** based on the customer ID and account type.

## Technologies Used

- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Cloud 2024.0.0**
- **Spring Data JPA**
- **Netflix Eureka (Discovery Server)**
- **Feign Client**
- **RabbitMQ**
- **Docker & Docker Compose**
- **JUnit & Mockito**
- **JaCoCo**
- **Log4j2**
- **Swagger (OpenAPI 3)**
- **MySQL & H2**

## Setup Instructions

### 1. **Clone the Project**

```sh
git clone https://github.com/AlSharaiyra/Banking-System-Microservices.git
```

### 2. **Start Required Services using Docker**

Ensure Docker is installed and running. Navigate to the `docker-compose.yml` directory (int the root of project directory) and execute:

```sh
sudo docker-compose up -d
```

### 3. **RabbitMQ Setup**

- Access RabbitMQ Web UI at: **[http://localhost:15672](http://localhost:15672)**
- Login using **guest/guest**
- Create a queue: **account.closed.queue**

### 4. **Build ```common```**

Common/shared classes are stored in ```common``` project.
To be able to use them you need to build the project, run: 

```sh
mvn clean install
```

### 4. **Start Services**

Start those services IN ORDER:
- discovery-server
- api-gateway
- account-service
- customer-service

### 5. **API Gateway & Service Endpoints**

- API Gateway: **[http://localhost:8765](http://localhost:8765)**
- Swagger Documentation:
    - **Customer Service:** [http://localhost:8765/api/v1/customer/service/swagger-ui/index.html](http://localhost:8765/api/v1/customer/service/swagger-ui/index.html)
    - **Account Service:** [http://localhost:8765/api/v1/account/service/swagger-ui/index.html](http://localhost:8765/api/v1/account/service/swagger-ui/index.html)

## Conclusion

This project successfully demonstrates a **microservices-based banking system** with proper **synchronous and asynchronous communication**, **event-driven architecture**, **validation**, **exception handling**, **logging**, and **API documentation**. While security via Keycloak was not completed, the architecture is scalable and can accommodate further enhancements.

