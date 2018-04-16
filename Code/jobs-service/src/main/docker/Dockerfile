FROM openjdk:8u111-jdk-alpine
RUN apk add --no-cache  curl bash
CMD ["/bin/sh"]
EXPOSE 8080
VOLUME /tmp
ADD jobs-service-0.0.1-SNAPSHOT.jar jobs-service.jar
ADD /cacerts /etc/ssl/certs/java/cacerts
ADD /pruebabt.pfx pruebabt.pfx
RUN sh -c 'touch /jobs-service.jar'
ENTRYPOINT ["java","-Djavax.net.ssl.keyStore=/pruebabt.pfx","-Djavax.net.ssl.keyStorePassword=pruebabt","-Djavax.net.ssl.keyStoreType=PKCS12","-Xmx256M","-XX:NativeMemoryTracking=summary","-jar","/jobs-service.jar","--spring.profiles.active=test"]
