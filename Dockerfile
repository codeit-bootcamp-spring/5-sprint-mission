# 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew ./gradlew
COPY gradlew.bat ./gradlew.bat
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle settings.gradle ./
COPY src ./src

# 의존성 다운로드
RUN ./gradlew dependencies
# 빌드
RUN ./gradlew build -x test # 테스트 제외 빌드
#RUN ./gradlew build

# 런타임 스테이지
FROM amazoncorretto:17

EXPOSE 80

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8

WORKDIR /app

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

#ENV JVM_OPTS="-Xmx384m -Xms256m -XX:MaxMetaspaceSize=64m -XX:+UseSerialGC -Dserver.port=80"

# jar 파일 실행
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]

#docker build \
# docker build --tag dockerfile:local .

#docker run -d \
#  --name discodeit \
#  -p 8080:80 \
#  -e JVM_OPTS="-Dspring.profiles.active=prod" \
#  discodeit:local

#docker build -t discodeit/ecr-repo .
#docker build -t discodeit/ecr-repo:1.2-M8 .
#docker tag discodeit/ecr-repo:latest public.ecr.aws/y6l4y7s3/discodeit/ecr-repo:latest
#docker tag discodeit/ecr-repo:1.2-M8 public.ecr.aws/y6l4y7s3/discodeit/ecr-repo:1.2-M8
#docker push public.ecr.aws/y6l4y7s3/discodeit/ecr-repo:latest
#docker push public.ecr.aws/y6l4y7s3/discodeit/ecr-repo:1.2-M8

#docker build -t discodeit-repo:1.2-M8 .
#docker tag discodeit-repo:1.2-M8 257394472645.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit-repo:1.2-M8
#docker push 257394472645.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit-repo:1.2-M8

#docker buildx build \
#  --platform linux/amd64,linux/arm64 \
#  -t 257394472645.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit-repo:latest \
#  -t 257394472645.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit-repo:1.2-M8 \
#  --push \
#  .