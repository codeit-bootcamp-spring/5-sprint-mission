# ---------- 빌드 스테이지 ----------
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper 복사
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

# 의존성 미리 다운받기 (캐시 최적화)
RUN ./gradlew dependencies

# 소스 복사 및 빌드 (테스트 포함)
COPY src ./src
RUN ./gradlew clean build # 테스트 포함 빌드

# ---------- 런타임 스테이지 ----------
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

# 빌드 결과 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8081

# JVM 옵션 (필요 시 외부에서 덮어쓰기 가능)
ENV JVM_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar"]