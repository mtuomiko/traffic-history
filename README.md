# traffichistory

This repository is related to my Degree Programme in Business Information Systems (Bachelor of Business Administration) thesis work at the Oulu University of Applied Sciences. The repository contains a Quarkus framework based application which provides a simple JSON API about road traffic volume history at measurement points in Finland. The original datasource is the [TMS data](https://www.digitraffic.fi/en/road-traffic/lam/) ([LAM-tiedot](https://www.digitraffic.fi/tieliikenne/lam/) in Finnish) provided by Fintraffic / digitraffic.fi, license [CC 4.0 BY](https://creativecommons.org/licenses/by/4.0/).

## Background

Motivation for the app was to explore relatively new possibilities to implement a serverless API on some cloud provider using Java-based technologies. In the context of this work, the term **serverless** is defined as having no dedicated instance(s) and also allowing the service to scale to zero consuming no resources unless demanded. 

The selected content (traffic data history) / provided service of the API is not really ideal for serverless approach. More unpredictable or periodic workloads might be a better fit. But for this exploratory work, it will suffice. Serverless approach meant that low startup time would be essential to maintain good response times to requests even if the instance would start from scratch. This lead towards Ahead of Time compilation focused frameworks called Micronaut and Quarkus. Startup time is also heavily influenced by how the cloud provider handles things. Ultimately, Quarkus and Google Cloud was selected for the implementation. 

## Containerized

### Native image

Quarkus supports GraalVM Native Image which can be used to create native binaries. As the native binaries depend on the host OS, the repository contains also a multistage container build that will produce the final 64-bit Linux compatible containers.

Docker commands for multi-stage native build without local GraalVM installation:

* Build: `docker build -f docker/Dockerfile.multistage -t traffichistory-native .`
* Create only the builder image for debugging
  contents: `docker build -f docker/Dockerfile.multistage.builder -t traffichistory-native-builder .`
* Debug builder contents: `docker run --rm -it --entrypoint /bin/bash traffichistory-native-builder`
* Run native image against local
  datastore: `docker run --rm -it --env-file ./docker/.env.local -p 8080:8080 traffichistory-native`

`Dockerfile.multistage` has manually defined folder copy commands depending on project structure so update them as
project structure develops.

## Development

### Running the application in dev mode

You can run your application in dev mode (that maybe enables live coding) using:

```shell script
./mvnw compile quarkus:dev
```

### Debugging

Debugging with Quarkus Dev mode can be done using Remote JVM debugging. For IntelliJ, add a "Remote JVM Debug" run configuration.

If you need to debug initialization behavior (meaning that it's too late to start debugging after the app has already
started):

* run using suspend flag: `.\mvnw.cmd quarkus:dev -Dsuspend`
* connect JVM remote debugger to the default port 5005

### External APIs

In dev mode, app is set up to use localhost instead of Digitraffic APIs. 
* Launch something to serve CSV files on `http://localhost:3333/file/<filename>` and station API on `http://localhost:3333/station`. You could use for example [Mockoon](https://mockoon.com/).

### Datastore emulation

Google Datastore can be emulated locally with Cloud SDK. You can also run a docker instance using, for example:

`docker run -it --name dev-datastore -p 8000:8000 google/cloud-sdk gcloud --project=<insertProjectIdHere> beta emulators datastore start --host-port 0.0.0.0:8000`

### Static quality checks

Spotbugs doesn't appear to be binding to Maven `verify` phase despite execution declaration in root [pom.xml](pom.xml).
Run manually.

* For example: `./mvnw spotbugs:check`

## Environment variables

See configuration files at [app/src/main/resources](app/src/main/resources)

Following variables are not explicitly configured, they originate from the configuration files based
on [Quarkus configuration handling](https://quarkus.io/guides/config-reference)
and [Microprofile mapping rules](https://github.com/eclipse/microprofile-config/blob/master/spec/src/main/asciidoc/configsources.asciidoc#environment-variables-mapping-rules)
. They are only listed here for convenience and documentation.

| Environment variable                   | Description                                                                                                                                                                                                                        | Default                  | Example                            |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------|------------------------------------|
| `TRAFFICHISTORY_GCLOUD_DATASTORE_AUTH` | Use Datastore with local (value `none`) or Application Default Credential approach (value `default`). `default` assumes app to be running in a Google Cloud environment where it has received necessary credentials automatically. | `default`                | `none`                             |
| `TRAFFICHISTORY_GCLOUD_DATASTORE_HOST` | Local datastore instance host. Will not be used when running using Application Default Credentials                                                                                                                                 | ''                       | `http://host.docker.internal:8000` |
| `TRAFFICHISTORY_GCLOUD_PROJECT_ID`     | Google Cloud project ID                                                                                                                                                                                                            | 'traffic-history-376700' | `some-id-420`                      |

## Deployment

### Manual deployment to Google Cloud Run using Container Registry

Instructions exclude any versioning. Requires gcloud CLI and Docker. This does not contain all the necessary steps to 
initialize the cloud environment such as project creation.

* Datastore composite indices must be initialized one time: `gcloud datastore indexes create ./dao/index.yaml`
* Build native runner image: `docker build -f docker/Dockerfile.multistage -t traffichistory-native .`
* Tag it to GCP EU container registry: `docker tag traffichistory-native eu.gcr.io/<project_id>/traffichistory-native`
    * Use correct project id
* Push image: `docker push eu.gcr.io/<project_id>/traffichistory-native`
* Deploy with default settings: `gcloud run deploy <service_name> --image eu.gcr.io/<project_id>/traffichistory-native`
    * Give some service name

---

Below is original README content from Quarkus project generation, might not apply anymore on multimodule project
structure.

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/traffichistory-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
