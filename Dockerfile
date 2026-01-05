FROM eclipse-temurin:17-jre

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xms128m", "-Xmx512m", "-jar", "app.jar"]