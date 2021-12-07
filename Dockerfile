FROM openjdk:11 AS builder

COPY . .

RUN ["./gradlew", "assemble"]

FROM openjdk:11

COPY --from=builder build/libs/app.jar ./

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
