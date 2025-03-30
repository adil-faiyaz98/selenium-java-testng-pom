# OrangeHRM Automation Framework

This project contains automated tests for the OrangeHRM application using Selenium, TestNG, and WebDriverManager.

## Prerequisites

*   Java JDK (version 21 or later)
*   Maven (version 3.9.9 or later)
*   A compatible web browser (Chrome, Firefox, etc.)

## Setup

1.  Clone the repository.
2.  Navigate to the project directory.
3.  Run `mvn clean install` to download dependencies and build the project.

## Running Tests

*   To run all tests, use the command `mvn test`.
*   To run specific test suites or groups, modify the `testng.xml` file and use the command `mvn test -DsuiteXmlFile=testng.xml`.

## Project Structure

*   `config`: Contains configuration readers, property management, and constants.
*   `core`: Contains core framework components, such as WebDriver management.
*   `driver`: Contains browser driver setup and management.
*   `pages`: Contains Page Object Model classes representing the application's pages.
*   `tests`: Contains the TestNG test classes.
*   `utils`: Contains utility classes for common tasks like waiting, data handling, and screenshots.

## Reporting

(This section will be updated when reporting is integrated.)

## CI/CD

(This section will be updated if CI/CD is set up.)