
# Rocket Food Delivery API

## Overview

The Rocket Food Delivery API is a comprehensive service designed for managing food delivery operations. This project implements a RESTful API for handling various aspects of food delivery, including restaurants, orders, products, and users.

## Directory Structure

`src/main/java/com/rocketFoodDelivery/rocketFood/`

- `controller/` - REST API controllers
- `dtos/` - Data Transfer Objects for API responses and requests
- `exception/` - Custom exception classes
- `models/` - Domain models representing entities in the application
- `repository/` - Repository interfaces for database interactions
- `security/` - Security configurations and JWT utility classes
- `service/` - Service layer components for business logic
- `util/` - Utility classes, such as the ResponseBuilder

`src/main/resources/`

- `application.properties` - Configuration for the application
- `templates/` - HTML templates for the frontend

`src/test/resources/`

- `application-test.properties` - Configuration for test environments

`src/test/java/com/rocketFoodDelivery/rocketFood/`

- `api/` - Tests for the API controllers

`target/` - Compiled classes and build artifacts

## Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/your-repo/rocket-food-delivery.git
   cd rocket-food-delivery
   ```

2. **Install dependencies:**

   Ensure you have Maven installed. Run:

   ```bash
   mvn install
   ```

3. **Set Up MySQL**

   **1. Install MySQL:**

   - Download and install MySQL from [MySQL's official website](https://dev.mysql.com/downloads/installer/).
   - Follow the installation instructions to complete the setup.

   **2. Create a Database:**

   Open a terminal or command prompt and log in to MySQL:

   ```bash
   mysql -u root -p
   ```

   Create a new database for your application:

   ```sql
   CREATE DATABASE rdelivery;
   ```

   **3. Configure MySQL User:**

   Create a user and grant permissions to the new database:

   ```sql
   CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON rdelivery.* TO 'your_username'@'localhost';
   FLUSH PRIVILEGES;
   ```

   Replace `your_username` and `your_password` with your desired username and password.

4. **Configure the Application:**

   Update the `application.properties` file in `src/main/resources/` with the MySQL connection details:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/rdelivery
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   hibernate.hbm2ddl.auto=update
   hibernate.show_sql=true

   logging.level.root=INFO
   logging.level.com.rocketFoodDelivery=DEBUG
   logging.level.org.hibernate.SQL=DEBUG

   spring.thymeleaf.prefix=classpath:/templates/
   spring.thymeleaf.suffix=.html
   spring.thymeleaf.mode=HTML5
   spring.thymeleaf.encoding=UTF-8
   spring.thymeleaf.cache=false

   app.jwt.secret="your_jwt_secret"
   ```

   Replace `your_username`, `your_password`, and `your_jwt_secret` with your credentials and secret key.

5. **Run the Application:**

   Start the Spring Boot application as usual. The application will connect to the MySQL database using the configured settings.

   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Authentication

- **`POST /api/auth/login`**

  **Request Body:**

  ```json
  {
    "email": "user@example.com",
    "password": "yourpassword"
  }
  ```

### Restaurants

- **`POST /api/restaurants`**

  **Request Body:**

  ```json
  {
    "user_id": 1,
    "name": "Test Restaurant",
    "phone": "15141234567",
    "email": "test.restaurant@example.com",
    "price_range": 2,
    "address": {
      "street_address": "123 Test St.",
      "city": "Test City",
      "postal_code": "12345"
    }
  }
  ```

- **`GET /api/restaurants?price_range=3&rating=4`**

- **`GET /api/restaurants`**

- **`GET /api/restaurants/{id}`**

- **`PUT /api/restaurants/{id}`**

  **Request Body:**

  ```json
  {
    "user_id": 2,
    "name": "Updated Restaurant",
    "price_range": 3
  }
  ```

- **`DELETE /api/restaurants/{id}`**

### Orders

- **`POST /api/order`**

  **Request Body:**

  ```json
  {
    "restaurant_id": 1,
    "customer_id": 2,
    "order_date": "2024-08-01T12:00:00Z",
    "items": [
      {
        "product_id": 1,
        "quantity": 2
      }
    ]
  }
  ```

- **`GET /api/order/{id}`**

- **`PUT /api/order/{id}`**

  **Request Body:**

  ```json
  {
    "status": "Completed"
  }
  ```

- **`DELETE /api/order/{id}`**

### Products

- **`POST /api/products`**

  **Request Body:**

  ```json
  {
    "restaurant_id": 1,
    "name": "Test Product",
    "description": "A delicious test product.",
    "price": 12.99
  }
  ```

- **`GET /api/products`**

- **`GET /api/products/{id}`**

- **`PUT /api/products/{id}`**

  **Request Body:**

  ```json
  {
    "name": "Updated Product",
    "price": 14.99
  }
  ```

- **`DELETE /api/products/{id}`**

### Users

- **`POST /api/users`**

  **Request Body:**

  ```json
  {
    "name": "Test User",
    "email": "test.user@example.com",
    "password": "yourpassword"
  }
  ```

- **`GET /api/users/{id}`**

- **`PUT /api/users/{id}`**

  **Request Body:**

  ```json
  {
    "name": "Updated User"
  }
  ```

- **`DELETE /api/users/{id}`**

## Running Tests

To run the tests, use the following command:

```bash
mvn test
```

## Configuration for Tests

Add an `application-test.properties` file in `src/test/resources/` with the following content:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rdelivery_test
spring.datasource.username=test_username
spring.datasource.password=test_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

hibernate.hbm2ddl.auto=create-drop
hibernate.show_sql=true

logging.level.root=INFO
logging.level.com.rocketFoodDelivery=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

Replace `test_username` and `test_password` with appropriate test database credentials.

## Acknowledgments

- Spring Boot for the framework.
- MySQL for the database.
- CodeBoxx Academy
