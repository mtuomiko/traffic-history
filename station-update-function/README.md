### Notes

Created using:
mvn io.quarkus.platform:quarkus-maven-plugin:2.15.3.Final:create "-DprojectGroupId=org.acme" "-DprojectArtifactId=google-cloud-functions" "-Dextensions=google-cloud-functions"

Copy invoker for running locally:
mvn dependency:copy "-Dartifact=com.google.cloud.functions.invoker:java-function-invoker:1.1.1" "-DoutputDirectory=."

Build:
./mvnw install

Run V1 function (define function also in properties):
java -jar java-function-invoker-1.1.1.jar --classpath target/station-update-function-1.0.0-SNAPSHOT-runner.jar --target io.quarkus.gcp.functions.QuarkusBackgroundFunction

Deploy:
gcloud functions deploy upsert-stations --region=europe-west3 --entry-point=io.quarkus.gcp.functions.QuarkusBackgroundFunction --runtime=java17 --source=target/deployment --trigger-topic=station-upsert-topic
