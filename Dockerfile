# 빌드 스테이지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일 먼저 복사
COPY gradle ./gradle
COPY gradlew ./gradlew

# Gradle 캐시를 위한 의존성 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드
RUN ./gradlew dependencies

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew build -x test # 테스트 제외함 빌드 빠름!

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# JVM 옵션을 환경 변수로 설정
ENV JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 80 포트 노출
EXPOSE 80

## jar 파일 실행
#ENTRYPOINT ["sh","-c","java $JVM_OPTS -jar /app/app.jar"]

# jar 파일 실행 (app.jar로 고정)
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar"]


## 실행 명령어
## 1. 기존 컨테이너 삭제
#docker rm -f discodeit-app
#
## 2. Dockerfile 재빌드 (수정된 버전)
#docker build -t discodeit:local .
#
## 3. 재실행 (DB 정보 수정 필요!)
#docker run -d --name discodeit-app -p 8081:80 -e SPRING_PROFILES_ACTIVE=prod -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/discodeit -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=1234 discodeit:local
#
## 4. 로그 확인
#docker logs -f discodeit-app