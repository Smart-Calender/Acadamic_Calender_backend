#!/bin/bash

# Install Java
apt-get update
apt-get install -y openjdk-17-jdk

# Go to backend
cd backend

# Make sure Maven wrapper is executable
chmod +x mvnw

# Build project (skip tests for deployment)
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/academicCalander-0.0.1-SNAPSHOT.jar