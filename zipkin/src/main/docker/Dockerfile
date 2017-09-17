FROM wangxd/java:8

VOLUME /tmp

ADD zipkin-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30006
EXPOSE 30007

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]