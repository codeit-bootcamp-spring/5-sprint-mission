# Builder Stage
FROM amazoncorretto:17 AS builder
WORKDIR /app

# 그래들 캐시 최적화를 위해 먼저 래퍼/세팅만 복사
COPY gradlew ./
RUN chmod +x gradlew
COPY gradle ./gradle
COPY settings.gradle ./settings.gradle
COPY build.gradle ./build.gradle
RUN ./gradlew dependencies


# 나머지 소스 복사
COPY src ./src

RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime Stage
FROM amazoncorretto:17-alpine3.21
WORKDIR /app

RUN apk add --no-cache curl
RUN apk add --no-cache wget

# 런타임 환경변수
ENV SPRING_PROFILES_ACTIVE=prod \
    PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS="" \
    SERVER_PORT=80

# 빌더에서 나온 JAR 복사 후 ${PROJECT_NAME}-${PROJECT_VERSION}.jar 이름으로 정규화
COPY --from=builder /app/build/libs/*.jar /app/
# 이미지 빌드 단계에서 첫 번째 JAR를 표준 이름으로 바꿔둠
RUN set -eux; \
    JAR_PATH="$(ls /app/*.jar | head -n1)"; \
    mv "$JAR_PATH" "/app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

EXPOSE 80

ENTRYPOINT ["sh","-c","java $JVM_OPTS -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]