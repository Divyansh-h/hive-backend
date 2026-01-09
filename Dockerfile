# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy module poms
COPY common/pom.xml common/
COPY infrastructure/pom.xml infrastructure/
COPY identity/pom.xml identity/
COPY content/pom.xml content/
COPY social/pom.xml social/
COPY interaction/pom.xml interaction/
COPY feed/pom.xml feed/
COPY notification/pom.xml notification/

# Resolve dependencies (go-offline) to leverage caching
RUN mvn dependency:go-offline -B

# Copy source code
COPY common/src common/src
COPY infrastructure/src infrastructure/src
COPY identity/src identity/src
COPY content/src content/src
COPY social/src social/src
COPY interaction/src interaction/src
COPY feed/src feed/src
COPY notification/src notification/src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S hive && adduser -S hive -G hive
USER hive

# Copy the built artifact from the build stage
# The main executable jar comes from the infrastructure module
COPY --from=build /app/infrastructure/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
