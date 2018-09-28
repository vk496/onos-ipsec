FROM maven:3.5.3-jdk-8-alpine as builder

ENV APP_GROUPID org.foo
ENV APP_ARTIFACTID foo-app

WORKDIR /root/$APP_ARTIFACTID

#Get maven cache
COPY src/$APP_ARTIFACTID/pom.xml .
RUN touch onos.properties; mvn -B dependency:go-offline clean verify

#The app
COPY src/$APP_ARTIFACTID/ .
RUN mvn -Dmaven.test.skip=true clean package

FROM onosproject/onos:1.13.1

# #Fix Yang Live Compiler
# RUN set -e; \
#       apk add --no-cache openjdk8; \
#         rm -Rf $JAVA_HOME/jre; \
#       mv /usr/lib/jvm/java-1.8-openjdk/ $JAVA_HOME/jre;

RUN set -e; \
      apk add --no-cache socat;

COPY misc/autodeploy.sh .
RUN chmod +x autodeploy.sh

ENV APP_GROUPID org.foo
ENV APP_ARTIFACTID foo-app

COPY --from=builder /root/$APP_ARTIFACTID/target/$APP_ARTIFACTID-1.0-SNAPSHOT.oar /deploy/

#Debug
EXPOSE 5005

ENTRYPOINT []
CMD ["./autodeploy.sh"]
