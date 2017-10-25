FROM wangxd/java:8

VOLUME /tmp

ADD @project.artifactId@-@project.version@.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]