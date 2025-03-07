# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose the application port (change if needed)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
