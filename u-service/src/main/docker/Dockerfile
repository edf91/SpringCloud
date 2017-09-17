FROM wangxd/java:8

VOLUME /tmp

ADD u-service-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30002
EXPOSE 30003

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]