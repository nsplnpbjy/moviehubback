FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/moviehub-5.0.0.jar moviehubback.jar
COPY base64.js base64.js
EXPOSE 8090
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar moviehubback.jar
