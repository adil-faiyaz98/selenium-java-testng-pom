# OrangeHRM Automation Framework

This project contains automated tests for the OrangeHRM application using Selenium, TestNG, and WebDriverManager with a robust Page Object Model design pattern. The framework includes advanced features such as performance metrics, visual comparison tools, and AI-based testing capabilities.

## Features

- **Multi-browser support**: Chrome, Firefox, Edge, and Safari
- **Environment-specific configuration**: Staging, Production, etc.
- **Parallel test execution**: Run tests in parallel for faster execution
- **Data-driven testing**: Test with data from CSV and JSON files
- **Comprehensive reporting**: Allure and ExtentReports integration
- **Robust error handling**: Screenshot capture, browser logs, and page source on failure
- **CI/CD integration**: GitHub Actions workflow with matrix testing
- **Docker support**: Run tests in containers locally or in CI/CD
- **AWS integration**: Deploy and run tests in AWS ECS
- **Performance metrics**: Capture and analyze page load times and resource usage
- **Visual comparison**: Compare screenshots with baselines for visual regression testing
- **AI-based testing**: Integrate with AI testing services and use ML for validation
- **Test generation**: Automatically generate test cases based on application behavior

## Prerequisites

- Java JDK (version 21 or later)
- Maven (version 3.9.9 or later)
- A compatible web browser (Chrome, Firefox, Edge, or Safari)
- Docker and Docker Compose (optional, for containerized execution)

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/adil-faiyaz98/selenium-java-testng-pom.git
   cd selenium-java-testng-pom
   ```

2. Install dependencies:
   ```bash
   mvn clean install -DskipTests
   ```

## Running Tests

### Local Execution

- Run all tests with default configuration:
  ```bash
  mvn test
  ```

- Run tests with specific browser:
  ```bash
  mvn test -Dbrowser=firefox
  ```

- Run tests in specific environment:
  ```bash
  mvn test -Denvironment=production
  ```

- Run tests in headless mode:
  ```bash
  mvn test -Dheadless=true
  ```

- Run specific test groups:
  ```bash
  mvn test -Dgroups=smoke
  ```

### Docker Execution

- Start Selenium Grid and run tests:
  ```bash
  docker-compose up -d selenium-hub chrome firefox edge
  docker-compose up test-runner
  ```

- Run tests with specific configuration:
  ```bash
  docker-compose run -e BROWSER=firefox -e ENVIRONMENT=production test-runner
  ```

## Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── config       # Configuration management
│   │   │           ├── core         # Core framework components
│   │   │           ├── pages        # Page Object Model classes
│   │   │           └── utils        # Utility classes
│   │   └── resources
│   │       ├── config.properties    # Default configuration
│   │       └── environments         # Environment-specific configs
│   └── test
│       ├── java
│       │   └── com
│       │       └── example
│       │           ├── listeners    # TestNG listeners
│       │           └── tests        # Test classes
│       └── resources
│           └── testdata            # Test data files (CSV, JSON)
├── testng.xml                      # TestNG configuration
├── pom.xml                         # Maven configuration
├── Dockerfile                      # Docker configuration
├── docker-compose.yml              # Docker Compose configuration
└── aws-config.yml                 # AWS CloudFormation template
```

## Configuration

The framework supports multiple configuration sources with the following precedence:

1. System properties (command line arguments)
2. Environment-specific properties (src/main/resources/environments/[env].properties)
3. Default properties (src/main/resources/config.properties)

## Test Data

Test data can be provided in various formats:

- **CSV files**: For tabular data (src/test/resources/testdata/*.csv)
- **JSON files**: For structured data (src/test/resources/testdata/*.json)

## Performance Metrics

The framework captures and reports various performance metrics:

- **Page load times**: Measure how long pages take to load
- **Resource usage**: Track the number and size of resources loaded
- **Navigation timing**: Capture detailed browser timing information
- **Slow resources**: Identify resources that slow down page loading

To view performance reports, check the `target/reports/performance` directory after test execution.

## Visual Comparison

The framework includes visual comparison capabilities:

- **Baseline images**: Capture baseline screenshots for comparison
- **Visual diffs**: Generate visual difference images to highlight changes
- **Threshold-based comparison**: Configure acceptable difference thresholds
- **HTML reports**: View detailed visual comparison reports

To view visual comparison reports, check the `target/reports/visual` directory after test execution.

## AI-Based Testing

The framework includes several AI-based testing capabilities:

### AI Testing Service Integration

- **Applitools**: Integration with Applitools Visual AI for advanced visual testing
- **Testim**: Integration with Testim AI-powered testing platform

### ML-Based Validation

- **Form field validation**: Validate form inputs using machine learning
- **Content analysis**: Analyze page content using NLP techniques
- **Sentiment analysis**: Detect positive, negative, or neutral sentiment in text
- **Relevance analysis**: Measure content relevance to specific topics

### AI-Assisted Test Generation

- **Automatic test case generation**: Generate test cases based on page analysis
- **Form test generation**: Create tests for form fields and validation
- **Navigation test generation**: Generate tests for links and navigation
- **Table test generation**: Create tests for data tables and pagination

## Reporting

### Allure Reports

To generate and view Allure reports:

```bash
mvn allure:report
mvn allure:serve
```

### ExtentReports

ExtentReports are automatically generated in the `target/reports` directory after test execution.

## CI/CD

The project includes a GitHub Actions workflow that:

- Runs tests on multiple operating systems (Windows, Ubuntu)
- Tests with multiple browsers (Chrome, Firefox, Edge)
- Generates and publishes test reports
- Sends notifications with test results

## AWS Integration

The project includes an AWS CloudFormation template (`aws-config.yml`) that sets up:

- ECS cluster for test execution
- Task definition for running tests
- S3 bucket for storing test results

To deploy to AWS:

```bash
aws cloudformation deploy --template-file aws-config.yml --stack-name orangehrm-automation --capabilities CAPABILITY_IAM
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.