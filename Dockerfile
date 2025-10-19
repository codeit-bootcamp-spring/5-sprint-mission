# ===== Build Stage =====
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app

# gradlew 실행에 필요한 최소 도구
RUN apk add --no-cache bash unzip

# 프로젝트 복사 (.dockerignore 로 불필요한 파일 제외)
COPY . .

# Gradle Wrapper 권한 및 빌드 (필요시 -x test 제거 가능)
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# ===== Run Stage =====
FROM amazoncorretto:17-alpine
WORKDIR /app

#  - 실행할 jar 이름을 이 값으로 조합합니다.
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 컨테이너 내부에서 열어줄 포트
EXPOSE 80

# 빌드 산출물 복사 (디렉터리째 복사해서 jar 원래 이름을 유지)
COPY --from=builder /app/build/libs /app/build/libs

# 필요 시 Spring profile을 prod로 강제하고 80포트로 실행
ENV SPRING_PROFILES_ACTIVE=prod

# 환경변수로 정의한 이름을 이용해 jar 파일을 실행
ENTRYPOINT sh -c "exec java $JVM_OPTS \
  -jar /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar \
  --spring.profiles.active=${SPRING_PROFILES_ACTIVE} \
  --server.port=80"