FROM gradle:7.0.2-jdk11 as build
WORKDIR /JoobyTest
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY src src
COPY conf conf
RUN gradle --no-daemon shadowJar

FROM openjdk:11-jdk-slim
WORKDIR /JoobyTest
COPY --from=build /JoobyTest/build/libs/JoobyTest-1.0.0-all.jar app.jar
COPY conf conf
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
