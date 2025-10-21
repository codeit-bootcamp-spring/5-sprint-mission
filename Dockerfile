# 1. 빌드 단계
FROM amazoncorretto:17 as builder

WORKDIR /app

# Gradle Wrapper 파일 복사 (Maven 사용 시 mvnw로 교체)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies --no-daemon || true

COPY src src
RUN ./gradlew clean bootJar -x test

# 2. 실행 단계
FROM amazoncorretto:17

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
