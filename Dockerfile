# 빌드 스테이지
FROM amazoncorretto:17 AS builder
WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
RUN chmod +x ./gradlew

COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21
WORKDIR /app

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar

# 80 포트
EXPOSE 80

# 동적 jar 파일명 사용
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]