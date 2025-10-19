# Dockerfile for Spring Boot Multi-Stage Build

# ----------------------------------
# ---------- BUILD STAGE ----------
# ----------------------------------

FROM gradle:8.8-jdk17-alpine AS build
WORKDIR /src

# 1. gradlew 스크립트 복사
COPY gradlew .

# 2. 필수 Gradle Wrapper 디렉토리 복사
COPY gradle/wrapper/ gradle/wrapper/

# 3. 설정 파일 복사
COPY build.gradle settings.gradle ./

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (빌드 캐시 활용)
RUN ./gradlew --no-daemon help

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar --refresh-dependencies

# -------------------------------------
# ---------- RUNTIME STAGE ----------
# -------------------------------------

FROM amazoncorretto:17-alpine

# 작업 디렉터리
WORKDIR /app

# 프로젝트 정보 & JVM 옵션
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 서버 포트 80로 (요구사항)
ENV SERVER_PORT=80

# BinaryContentStorage 보존용 기본 경로(로컬 스토리지 사용 시)
ENV DISCODEIT_STORAGE_TYPE=local \
    DISCODEIT_STORAGE_LOCAL_ROOT_PATH=/data

# 빌드 산출물 복사
COPY --from=build /src/build/libs /app/libs

# 로그/데이터 디렉토리
VOLUME ["/app/.logs", "/data"]

# 컨테이너 노출 포트
EXPOSE 80

# 실행: 프로젝트 ENV로 JAR 이름 추론, prod 프로필 기본 적용
ENTRYPOINT ["/bin/sh","-lc", "\
  JAR=$(ls /app/libs/${PROJECT_NAME}-${PROJECT_VERSION}*.jar 2>/dev/null \
     || ls /app/libs/*-SNAPSHOT.jar 2>/dev/null \
     || ls /app/libs/*.jar | head -n1); \
  echo \"Launching: $JAR\"; \
  exec java $JVM_OPTS -jar \"$JAR\" \
    --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
    --server.port=${SERVER_PORT} \
"]