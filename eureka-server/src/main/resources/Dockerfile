FROM wangxd/java:8

VOLUME /tmp

ADD @project.build.finalName@.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 30000
EXPOSE 30001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]