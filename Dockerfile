# Base Image
FROM amazoncorretto:17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사 (불필요한 건 .dockerignore로 제외됨)
COPY . .

# Gradle Wrapper로 빌드
RUN ./gradlew clean build -x test


FROM amazoncorretto:17-alpine
WORKDIR /app
# 빌드 산출물을 환경변수 이름대로 복사
COPY --from=build /app/build/libs/discodeit-1.2-M8.jar /app/discodeit-1.2-M8.jar

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 포트 노출
EXPOSE 80

# 실행 명령어
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]