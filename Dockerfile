# 빌드 시 주입할 프로젝트 메타(이미지 내부 파일명에도 사용)
ARG PROJECT_NAME=discodeit
ARG PROJECT_VERSION=1.2-M8

# Builder Stage
FROM amazoncorretto:17 AS builder
WORKDIR /app

# 그래들 캐시 최적화를 위해 먼저 래퍼/세팅만 복사
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle ./settings.gradle
COPY build.gradle ./build.gradle

RUN chmod +x gradlew

# 나머지 소스 복사
COPY . .

RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime Stage
FROM amazoncorretto:17
WORKDIR /app

# 런타임 환경변수
ENV SPRING_PROFILES_ACTIVE=prod \
    PROJECT_NAME=${PROJECT_NAME} \
    PROJECT_VERSION=${PROJECT_VERSION} \
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