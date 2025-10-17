# 빌드 스테이지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일 먼저 복사
COPY gradle ./gradle
COPY gradlew ./gradlew

# Gradle 캐시를 위한 의존성 파일 복사
COPY build.gradle settings.gradle ./

# 권한 부여
#RUN chmod +x gradlew

# 의존성 다운로드
RUN ./gradlew dependencies

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew build -x test # 테스트 제외함 빌드 빠름!
#RUN #./gradlew build # 테스트 포함함! 빌드 느림!!

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보 환경 변수
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 80 포트 노출
EXPOSE 80

# jar 파일 실행
#ENTRYPOINT ["sh","-c","java $JVM_OPTS -jar /app/app.jar"]

# jar 파일 복사 (환경 변수 기반 이름 사용)
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 실행 명령어 (환경 변수 + prod 프로필)
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar --spring.profiles.active=prod"]