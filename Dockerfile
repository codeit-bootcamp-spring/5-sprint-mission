# ---------- Build stage ----------
FROM amazoncorretto:17-alpine AS build
WORKDIR /app

# Gradle wrapper & metadata 먼저 복사 (캐시 최적화)
COPY gradlew settings.gradle* build.gradle* gradle/ ./
RUN chmod +x gradlew

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew clean bootJar -x test

# ---------- Runtime stage ----------
FROM amazoncorretto:17-alpine
WORKDIR /app

# 요구된 환경변수 (기본값 포함)
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 앱 JAR 복사 (폴더째 복사 후 ENV로 경로 지정해 실행)
COPY --from=build /app/build/libs /app/libs

# 80 포트 노출 (컨테이너 내부 포트)
EXPOSE 80

# prod 프로필 + 80 포트로 실행, ENV의 프로젝트 이름/버전으로 JAR 경로 구성
CMD ["sh", "-c", "java $JVM_OPTS -jar \"/app/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar\" --spring.profiles.active=prod --server.port=80"]
