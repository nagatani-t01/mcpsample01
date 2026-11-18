# Build and run the Spring Boot API without a JDK on the host.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src
RUN chmod +x mvnw \
    && ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/spring-petclinic-rest-*.jar app.jar
EXPOSE 9966
ENTRYPOINT ["java", "-jar", "app.jar"]