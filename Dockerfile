FROM amazoncorretto:24-alpine-jdk
WORKDIR /app
EXPOSE 8002
ADD ./target/msvc-items-0.0.1-SNAPSHOT.jar msvc-item.jar
ENTRYPOINT ["java", "-jar", "msvc-item.jar"]