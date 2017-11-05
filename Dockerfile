#FROM dockerfile/java:oracle-java8
FROM openjdk:latest
MAINTAINER Frank Endriss <fj.endriss@gmail.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/Ticketstack.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
#ADD target/lib           /usr/share/myservice/lib
# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/myservice/Ticketstack.jar
