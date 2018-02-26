FROM openjdk:8-jre-alpine
MAINTAINER Frank Endriss <fj.endriss@gmail.com>

EXPOSE 8087

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/Ticketstack.jar"]

ADD target/Ticketstack##*.war /usr/share/myservice/Ticketstack.jar
