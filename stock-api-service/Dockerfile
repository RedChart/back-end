# Build stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN sed -i 's/\r$//' ./gradlew
RUN ./gradlew :stock-api-service:build -x test --no-daemon

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/stock-api-service/build/libs/*.jar app.jar
COPY --from=build /app/stock-api-service/src/main/resources/application.yml /app/

ENV MODE=${MODE:-prod}
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}

CMD java -jar -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE app.jar