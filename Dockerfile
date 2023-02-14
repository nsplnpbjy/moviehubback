FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/moviehub-1.0.2.jar moviehubback.jar
EXPOSE 8090
ENTRYPOINT exec java $JAVA_OPTS -jar moviehubback.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar moviehubback.jar
