## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:22.3-java17 AS build
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
COPY --chown=quarkus:quarkus app/pom.xml /code/app/pom.xml
COPY --chown=quarkus:quarkus api/pom.xml /code/api/pom.xml
COPY --chown=quarkus:quarkus dao/pom.xml /code/dao/pom.xml
USER quarkus
WORKDIR /code

RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

COPY app/src /code/app/src
COPY api/src /code/api/src
COPY dao/src /code/dao/src

RUN ./mvnw package -Pnative
