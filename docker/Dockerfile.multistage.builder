## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:22.3-java17 AS build
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
COPY --chown=quarkus:quarkus app/pom.xml /code/app/pom.xml
COPY --chown=quarkus:quarkus gen/pom.xml /code/gen/pom.xml
COPY --chown=quarkus:quarkus common/pom.xml /code/common/pom.xml
COPY --chown=quarkus:quarkus api/pom.xml /code/api/pom.xml
COPY --chown=quarkus:quarkus svc/pom.xml /code/svc/pom.xml
COPY --chown=quarkus:quarkus dao/pom.xml /code/dao/pom.xml
USER quarkus
WORKDIR /code

# RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

COPY app/src /code/app/src
COPY gen/api.yaml /code/gen/api.yaml
COPY common/src /code/common/src
COPY api/src /code/api/src
COPY svc/src /code/svc/src
COPY dao/src /code/dao/src

COPY --chown=quarkus:quarkus docker/buildscript.sh /code/buildscript.sh
