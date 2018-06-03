FROM maven:3.5.3-jdk-8-alpine as builder

ENV APP_GROUPID org.foo
ENV APP_ARTIFACTID foo-app

WORKDIR /root/$APP_ARTIFACTID

#Get maven cache
COPY src/$APP_ARTIFACTID/pom.xml .
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip -Dasciidoctor.skip -Djacoco.skip -Dmaven.gitcommitid.skip -Dspring-boot.repackage.skip -Dmaven.exec.skip=true -Dmaven.install.skip -Dmaven.resources.skip

#The app
COPY src/$APP_ARTIFACTID/ .
RUN mvn clean install

FROM onosproject/onos:1.11.1

COPY misc/autodeploy.sh .
RUN chmod +x autodeploy.sh

ENV APP_GROUPID org.foo
ENV APP_ARTIFACTID foo-app

COPY --from=builder /root/$APP_ARTIFACTID/target/$APP_ARTIFACTID-1.0-SNAPSHOT.oar /deploy/

ENTRYPOINT []
CMD ["./autodeploy.sh"]
