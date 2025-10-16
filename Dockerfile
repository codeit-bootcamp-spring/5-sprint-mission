# 빌드 스테이지: Amazon Corretto 17 기반
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper, 설정 파일 복사 및 권한 부여
COPY gradlew ./gradlew
RUN chmod +x gradlew
COPY gradle ./gradle
COPY settings.gradle ./settings.gradle
COPY build.gradle ./build.gradle

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# 런타임 스테이지: Amazon Corretto 17 기반
FROM amazoncorretto:17

WORKDIR /app

# curl 설치 (YUM 패키지 매니저 기반 이미지이므로 yum 사용)
RUN yum update -y && yum install -y curl && yum clean all

ENV SPRING_PROFILES_ACTIVE=prod \
    PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS="" \
    SERVER_PORT=80

COPY --from=builder /app/build/libs/*.jar /app/

# jar 파일명을 환경변수에 맞춘 파일명으로 정규화
RUN set -eux; \
    JAR_PATH="$(ls /app/*.jar | head -n1)"; \
    TARGET_PATH="/app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"; \
    if [ "$JAR_PATH" != "$TARGET_PATH" ]; then mv "$JAR_PATH" "$TARGET_PATH"; fi

EXPOSE 80

# 애플리케이션 실행: JVM 옵션, 서버 포트, 프로필 환경변수 활용
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]