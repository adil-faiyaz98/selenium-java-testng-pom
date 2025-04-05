FROM eclipse-temurin:21-jdk

# Install dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    gnupg \
    xvfb \
    libgconf-2-4 \
    libxss1 \
    libnss3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgdk-pixbuf2.0-0 \
    libgtk-3-0 \
    libxslt1.1

# Install Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV CHROME_BIN=/usr/bin/google-chrome \
    FIREFOX_BIN=/usr/bin/firefox \
    DISPLAY=:99 \
    SCREEN_GEOMETRY="1920x1080x24" \
    CHROMEDRIVER_VERSION=114.0.5735.90 \
    GECKODRIVER_VERSION=0.33.0

# Install ChromeDriver
RUN wget -q "https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip" -O /tmp/chromedriver.zip \
    && unzip /tmp/chromedriver.zip -d /usr/bin/ \
    && rm /tmp/chromedriver.zip \
    && chmod +x /usr/bin/chromedriver

# Install GeckoDriver (Firefox)
RUN wget -q "https://github.com/mozilla/geckodriver/releases/download/v${GECKODRIVER_VERSION}/geckodriver-v${GECKODRIVER_VERSION}-linux64.tar.gz" -O /tmp/geckodriver.tar.gz \
    && tar -xzf /tmp/geckodriver.tar.gz -C /usr/bin/ \
    && rm /tmp/geckodriver.tar.gz \
    && chmod +x /usr/bin/geckodriver

# Create directories for test artifacts
RUN mkdir -p /app/screenshots /app/logs /app/target/allure-results

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src/ ./src/
COPY testng.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Set up Xvfb for headless browser testing
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Set volume for test results
VOLUME ["/app/target/surefire-reports", "/app/target/allure-results", "/app/screenshots", "/app/logs"]

# Set entrypoint
ENTRYPOINT ["docker-entrypoint.sh"]

# Default command
CMD ["mvn", "clean", "test"]