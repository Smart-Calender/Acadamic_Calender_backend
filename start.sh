#!/bin/bash

# Go to backend
cd backend

# Make sure Maven wrapper is executable
chmod +x mvnw

# Build the project
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/academicCalander-0.0.1-SNAPSHOT.jar