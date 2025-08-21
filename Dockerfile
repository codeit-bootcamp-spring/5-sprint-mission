FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

CMD ["java", "-jar", "build/libs/your-app-0.0.1-SNAPSHOT.jar"]