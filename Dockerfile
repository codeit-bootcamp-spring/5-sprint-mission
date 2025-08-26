FROM openjdk:17

# JAR 파일의 경로를 ARG로 정의
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너 내 app.jar로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작될 때 실행될 명령어 정의
ENTRYPOINT ["java", "-jar", "app.jar"]