#!/bin/bash

/code/mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
/code/mvnw package -Pnative
