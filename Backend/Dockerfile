# Base image
FROM bellsoft/liberica-openjdk-alpine:17

# Set the working directory inside the container
WORKDIR /spring_back

# Copy all files to the /app directory in the container
COPY . /spring_back

# Install ffmpeg
RUN apk update && apk add --no-cache ffmpeg

# Run Gradle build
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test
RUN cp ./build/libs/welight-0.0.1-SNAPSHOT.jar /spring_back/spring.jar

# Expose Spring server port
EXPOSE 8081

# Command to run the Spring application
CMD ["java", "-jar", "/spring_back/spring.jar"]
