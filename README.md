# traffichistory

This repository is related to my Degree Programme in Business Information Systems (Bachelor of Business Administration) thesis work at the Oulu University of Applied Sciences. The repository contains a Quarkus framework based application which provides a simple JSON API about road traffic volume history at measurement points in Finland. The original datasource is the [TMS data](https://www.digitraffic.fi/en/road-traffic/lam/) ([LAM-tiedot](https://www.digitraffic.fi/tieliikenne/lam/) in Finnish) provided by Fintraffic / digitraffic.fi, license [CC 4.0 BY](https://creativecommons.org/licenses/by/4.0/).

## Background

Motivation for the app was to explore relatively new possibilities to implement a serverless API on some cloud provider using Java-based technologies. In the context of this work, the term **serverless** is defined as having no dedicated instance(s) and also allowing the service to scale to zero consuming no resources unless demanded. 

The selected content (traffic data history) / provided service of the API is not really ideal for serverless approach. More unpredictable or periodic workloads might be a better fit. But for this exploratory work, it will suffice. Serverless approach meant that low startup time would be essential to maintain good response times to requests even if the instance would start from scratch. This lead towards Ahead of Time compilation focused frameworks called Micronaut and Quarkus. Startup time is also heavily influenced by how the cloud provider handles things. Ultimately, Quarkus and Google Cloud was selected for the implementation.

## Structure

Project is a multi-module Maven project with a fairly generic API / service / DAO layer split of functionality (even though Maven won't enforce visibility between modules). In addition, there's the `common` module for DTO models and the `gen` module for API interface and model generation from OpenAPI specifications.

The repository also contains a separate [station-update-function](station-update-function/) folder which defines another Quarkus project providing a Google Cloud Function for updating station information. Ideally the function project would share code directly with the main project to guarantee, for example, the same data structure on the DAO level. The code sharing is currently done with a library module `datastore` which both the main project and the function project use. This creates some difficulty in handling the dependencies as the library project isn't published in any real repository.

Datastore

## Containerized

### JVM

* Package: `.\mvnw.cmd package`
* Build container: `docker build -f docker/Dockerfile.jvm -t traffichistory-jvm .` 

### Native image

Quarkus supports GraalVM Native Image which can be used to create native binaries. As the native binaries depend on the host OS, the repository contains also a multistage container build that will produce the final 64-bit Linux compatible containers.

Docker commands for multi-stage native build without a local GraalVM installation:

* Create the builder image: `docker build -f docker/Dockerfile.multistage.builder -t traffichistory-native-builder .`
* Run native build: `docker run --entrypoint /code/buildscript.sh -it --rm -v "C:/Users/mtuomiko/.m2:/home/quarkus/.m2" -v "$(pwd)/target:/code/app/target" traffichistory-native-builder`
  * Entrypoint replacement is for delaying the dependency fetching to runtime, so we can mount a local m2 folder. The mount is not required. The file access is somewhat slow at least on Windows, but we avoid downloading the dependencies everytime.
* Create runner image `docker build -f docker/Dockerfile.multistage.runner -t traffichistory-native .`
* Debug builder contents: `docker run --rm -it --entrypoint /bin/bash traffichistory-native-builder`
  * `-u root` can be helpful
* Run native image against local
  datastore: `docker run --rm -it --env-file ./docker/.env.local -p 8080:8080 traffichistory-native`

Multistage Dockerfiles have manually defined folder copy commands depending on project structure, so they must be updated if the basic project structure changes.

## Development

The project contains a Maven wrapper (`mvnw` / `mvnw.cmd`, depending on OS) which should be used to invoke any Maven commands.

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
* Launch something to serve CSV files on `http://localhost:3333/file/<filename>`. You could use for example [Mockoon](https://mockoon.com/).

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

| Environment variable                   | Description                                                                                                                                                                                                                        | Default                                              | Example                            |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------|------------------------------------|
| `TRAFFICHISTORY_TMS_CSV_FILES_URL`     | TMS API url for raw CSV files. Used to fetch traffic volumes.                                                                                                                                                                      | `https://tie.digitraffic.fi/api/tms/v1/history/raw/` |                                    |
| `TRAFFICHISTORY_GCLOUD_DATASTORE_AUTH` | Use Datastore with local (value `none`) or Application Default Credential approach (value `default`). `default` assumes app to be running in a Google Cloud environment where it has received necessary credentials automatically. | `default`                                            | `none`                             |
| `TRAFFICHISTORY_GCLOUD_DATASTORE_HOST` | Local datastore instance host. Will not be used when running using Application Default Credentials                                                                                                                                 | ''                                                   | `http://host.docker.internal:8000` |
| `TRAFFICHISTORY_GCLOUD_PROJECT_ID`     | Google Cloud project ID                                                                                                                                                                                                            | 'traffic-history-376700'                             | `some-id-420`                      |

## Deployment

### Manual deployment to Google Cloud Run using Container Registry

Instructions exclude any versioning. Requires gcloud CLI and Docker. This does not contain all the necessary steps to 
initialize the cloud environment such as project creation.

* Datastore composite indices must be initialized one time: `gcloud datastore indexes create ./dao/index.yaml`
* Build the runner image, see [Native image](#native-image)
* Tag it to GCP EU container registry: `docker tag traffichistory-native eu.gcr.io/<project_id>/traffichistory-native`
    * Use correct project id
* Push image: `docker push eu.gcr.io/<project_id>/traffichistory-native`
* Deploy with default settings: `gcloud run deploy <service_name> --image eu.gcr.io/<project_id>/traffichistory-native --region=europe-west3`
    * Give some service name

### Using JVM image

As above, except package the code before using the JVM-specific Dockerfile instead of the multistage builder.
* Package: `.\mvnw.cmd package`
* Build container: `docker build -f docker/Dockerfile.jvm -t traffichistory-jvm .` 
* Tag it to GCP EU container registry: `docker tag traffichistory-jvm eu.gcr.io/<project_id>/traffichistory-jvm`
  * Use correct project id
* Push image: `docker push eu.gcr.io/<project_id>/traffichistory-jvm`
* Deploy with default settings: `gcloud run deploy <service_name> --image eu.gcr.io/<project_id>/traffichistory-jvm`
  * Give some service name

### Deploy fat JAR manually to App Engine

* Package to fat JAR `.\mvnw.cmd package "-Dquarkus.package.type=uber-jar"`
* Deploy `gcloud app deploy --appyaml=app.yaml app/target/app-1.0.0-SNAPSHOT-runner.jar`
