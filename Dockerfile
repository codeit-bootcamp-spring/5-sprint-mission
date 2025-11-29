FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY config ./config

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

FROM amazoncorretto:17-alpine3.21

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=2.2-M11 \
    JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
