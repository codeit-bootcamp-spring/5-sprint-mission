#1. Amazon Corretto 17 이미지를 베이스로 사용
#아마존 17 JDK가 설치된 리눅스 환경을 내 앱으로 설치하겠다는 뜻
FROM amazoncorretto:17 as build

#2. 작업 디렉토리를 /app으로 설정
WORKDIR /app

#3. 프로젝트 로컬의 모든 파일 복사 (불필요한 파일은 .dockerignore로 제외)
COPY . .

#4. 테스트는 제외하고 grandle 빌드 수행
RUN ./gradlew clean build -x test

#5. 실제 실행 단계용 새 이미지(stage 분리 - 이미지 사이즈 줄임)
FROM amazoncorretto:17

#6. 작업 디렉토리 설정
WORKDIR /app

#7. 위에서 빌드한 JAR 복사
ARG PROJECT_NAME=discodeit
ARG PROJECT_VERSION=1.2-M8
COPY --from=build /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

#8. 환경 변수 설정 (discodeit,'1.2-M8')
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JVM_OPTS=""

#9. 포트 노출
EXPOSE 80

#10. 애플리케이션 실행 명령
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
