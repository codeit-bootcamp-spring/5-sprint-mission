# ---------- build stage ----------
FROM amazoncorretto:17-alpine AS builder

# (베이스 이미지) Amazon Corretto 17 ✔
# (작업 디렉토리) 컨테이너 안에서 작업할 폴더 위치를 /app 으로 고정 ✔
WORKDIR /app

# (빌드에 필요한 기본 도구) Gradle Wrapper가 zip을 풀 때 필요해요 (unzip)
RUN apk add --no-cache unzip

# (프로젝트 메타정보) 실행할 JAR 이름을 유추하는 데 사용 ✔
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8

# (캐시 최적화) 의존성 변동이 적은 파일을 먼저 복사
#   - Gradle Wrapper 스크립트/메타, 설정 파일들 먼저 넣고
#   - 의존성(라이브러리) 캐시를 최대한 재사용
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle* settings.gradle.kts* ./
COPY build.gradle* build.gradle.kts* ./

# (권한) gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# (의존성만 먼저 내려받기) 소스 복사 전에 의존성 캐시 생성
RUN ./gradlew --no-daemon dependencies || true

# (나머지 모든 소스) 불필요 파일은 .dockerignore로 제외 ✔
COPY . .

# (빌드) Gradle Wrapper 사용하여 애플리케이션 빌드 ✔
# 테스트는 컨테이너 빌드 속도를 위해 제외(-x test), 필요시 제거
RUN ./gradlew --no-daemon clean build -x test

# ---------- runtime stage ----------
FROM amazoncorretto:17-alpine

# (작업 디렉토리) ✔
WORKDIR /app

# (포트 노출) 컨테이너 메타데이터상 80 포트 사용 알림 ✔
EXPOSE 80

# (환경변수) 프로젝트 정보 + JVM 옵션 기본값 "" ✔
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# (JAR 배치) 빌드 산출물을 런타임 이미지로 복사
#   표준 Gradle 산출물 경로: build/libs/<name>-<version>.jar
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/app.jar

# (실행 명령) 환경변수로 정의한 JVM_OPTS, PROJECT_* 활용 ✔
CMD ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar"]
