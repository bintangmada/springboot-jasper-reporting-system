# Spring Boot Jasper Reporting System

A modern, robust reporting system built with **Spring Boot 4.0.5** and **JasperReports 7.0.6**. This application demonstrates how to manage employee data and generate professional-grade reports in both **PDF** and **Excel (XLSX)** formats.

## 🚀 Features

- **Employee Management**: View a complete list of employees with details such as Department, Position, and Salary.
- **Dynamic Report Generation**: Reports are built programmatically using JasperReports' API, allowing for high flexibility and customization without relying solely on static `.jrxml` files.
- **Multi-Format Export**:
  - 📄 **PDF**: High-quality, print-ready documents with professional styling.
  - 📊 **Excel**: Data-rich spreadsheets (XLSX) optimized for analysis.
- **Initial Data Seeding**: Automatically seeds the database with sample employee records on first startup.
- **Dockerized Environment**: Fully containerized using Docker and Docker Compose for easy deployment.
- **Stylish UI**: Clean and responsive web interface built with Thymeleaf and custom CSS.

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 4.0.5, Spring Data JPA, Lombok
- **Reporting**: JasperReports 7.0.6, Apache POI (for Excel export)
- **Database**: MySQL 8.0
- **Frontend**: Thymeleaf, HTML5, CSS3
- **DevOps**: Docker, Docker Compose, Maven

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Java 17 JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or higher
- [Maven 3.6+](https://maven.apache.org/)
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/) (optional, for containerized run)
- [MySQL](https://www.mysql.com/) (if running locally without Docker)

## ⚙️ Setup & Installation

### Option 1: Using Docker (Recommended)

The easiest way to get the system running is using Docker Compose.

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/springboot-jasper-reporting-system.git
    cd springboot-jasper-reporting-system
    ```

2.  **Run with Docker Compose**:
    ```bash
    docker-compose up --build
    ```
    This will start both the MySQL database and the Spring Boot application. The app will be accessible at `http://localhost:8080`.

### Option 2: Local Development

If you prefer to run it locally:

1.  **Configure Database**:
    - Create a MySQL database named `jasper_reporting_db`.
    - Set your database credentials as environment variables or update `src/main/resources/application.properties`.
    ```properties
    DB_USERNAME=your_username
    DB_PASSWORD=your_password
    ```

2.  **Build the project**:
    ```bash
    ./mvnw clean install
    ```

3.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```

## 📖 Usage

1.  **Home Page**: Navigate to `http://localhost:8080/`. You will be redirected to the Employee List.
2.  **Viewing Data**: The seeder will automatically populate the table with 10 sample employees.
3.  **Generating Reports**:
    - Click on the **'Reports'** tab in the navigation bar.
    - Choose either **'Export as PDF'** or **'Export as Excel'**.
    - The system will process the data and prompt you to download the generated file.

## 📂 Project Structure

- `src/main/java`: Backend logic (Controllers, Entities, Services, Seeders).
- `src/main/resources/templates`: Thymeleaf templates for the UI.
- `src/main/resources/static`: Static assets (CSS/Images).
- `src/main/resources/reports`: Contains any static Jasper template files.
- `docker-compose.yml`: Docker configuration for the app and database.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---
Built with ❤️ by [Bintang Mada](https://github.com/bintangmada)
