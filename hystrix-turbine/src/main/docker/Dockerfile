FROM wangxd/java:8

VOLUME /tmp

ADD hystrix-turbine-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30010
EXPOSE 30011

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]