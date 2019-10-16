FROM java:8-jdk-alpine
COPY ./rocky.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "rocky.jar"]