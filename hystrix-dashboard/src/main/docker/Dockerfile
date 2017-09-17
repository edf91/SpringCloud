FROM wangxd/java:8

VOLUME /tmp

ADD hystrix-dashboard-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30008
EXPOSE 30009

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]