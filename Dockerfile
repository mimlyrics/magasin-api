
FROM openjdk:21
WORKDIR /app
ADD target/magazina-0.0.1-SNAPSHOT.jar magazina.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "magazina.jar"]

