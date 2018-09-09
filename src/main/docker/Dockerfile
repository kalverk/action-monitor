FROM openjdk:8-jre-alpine

ENV JAVA_OPTS=""

CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war

EXPOSE 8080

ADD *.war /app.war
