FROM wangxd/java:8

VOLUME /tmp

ADD u-consumer-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30003
EXPOSE 30004

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]