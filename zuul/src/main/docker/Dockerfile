FROM wangxd/java:8

VOLUME /tmp

ADD zuul-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30012
EXPOSE 30013

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]