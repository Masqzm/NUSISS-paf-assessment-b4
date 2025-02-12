### DOCKERISATION - STAGE 1
#---------------------------
# Install JDK
FROM eclipse-temurin:21-jdk AS builder

LABEL maintainer="hazim"

# Set working dir
WORKDIR /compileDir

# Copy files & folders over
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn
COPY src src    

# Build jar app
RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true


### DOCKERISATION - STAGE 2
#---------------------------
FROM eclipse-temurin:21-jdk

# Set working dir
WORKDIR /app

# Copy over jar from first container (builder), rename to preferred app name
COPY --from=builder /compileDir/target/assessment-0.0.1-SNAPSHOT.jar assessment.jar

# Set environment variables
ENV SERVER_PORT=8080

ENV SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/bedandbreakfast"
ENV MYSQL_USER=""
ENV MYSQL_PASSWORD=""
ENV SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/bedandbreakfast"

# Expose app's port 
EXPOSE ${SERVER_PORT}

# Check if curl command is available (for healthcheck)
#RUN apt update && apt install -y curl

#HEALTHCHECK --interval=60s --timeout=10s --start-period=120s --retries=3 \
#        CMD curl -s -f http://localhost:${SERVER_PORT}/status || exit 1

# Run app
ENTRYPOINT java -jar assessment.jar