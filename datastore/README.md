## Datastore operations library

Library project used by both the main project and the function project. The goal is to increase code sharing between
said projects. Joining the projects and using a direct project dependency seemed difficult since the function project
requires a certain structure.

Provides a DatastoreOperations class which covers all Datastore use cases of both projects.

### As a dependency

This library is not published anywhere so it needs to be (locally) installed to provide access. No Maven wrapper is included so use a globally installed one or the one from root project directory.

Install using wrapper when in root directory:
`.\mvnw.cmd -f .\datastore\pom.xml clean install`

Install skipping tests:
`.\mvnw.cmd -f .\datastore\pom.xml clean install "-DskipTests"`

### Tests

Tests depend on a Datastore Emulator instance running on `http://host.docker.internal:8000` with project id `traffic-history-376700`.

Run one with, for example, Docker using ``docker run -it --name dev-datastore -p 8000:8000 google/cloud-sdk gcloud --project=<insertProjectIdHere> beta emulators datastore start --host-port 0.0.0.0:8000`
