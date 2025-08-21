FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

CMD ["java", "-jar", "build/libs/discodeit-0.0.1-SNAPSHOT.jar"]