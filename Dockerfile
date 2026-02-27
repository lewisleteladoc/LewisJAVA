# Use a stable LTS version of Java
FROM eclipse-temurin:21-alpine

# Build-time arg from Jenkins
ARG SOME_API
ENV SOME_API=${SOME_API}

# Set the working directory
WORKDIR /usr/app

# 1. Copy Maven wrapper and pom.xml first to leverage Docker cache
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 2. Download dependencies
RUN ./mvnw dependency:go-offline -B

# 3. Copy the rest of the application code
COPY src src

# 4. Build the application
RUN ./mvnw clean package -DskipTests

# 5. Expose the port Spring Boot uses
EXPOSE 8081

# 6. Start the application
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]