# Imagen base de Java 21
FROM eclipse-temurin:21-jdk
COPY target/*.jar app.jar
EXPOSE 8080
# Usamos el ENTRYPOINT que deja pasar flags al JAR
ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/urandom -jar /app.jar ${0} ${@}"]
