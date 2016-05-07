#!/bin/bash
set -o errexit

PROJECT_VERSION=$(mvn -q \
    -Dexec.executable="echo" \
    -Dexec.args='${project.version}' \
    --non-recursive \
    org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

if [[ ${PROJECT_VERSION} != *-SNAPSHOT ]]
then
  curl -f -u$BINTRAY_USER:$BINTRAY_KEY -H "Content-Type: application/json" \
       -X POST https://bintray.com/api/v1/maven_central_sync/catalysts/catalysts-jars/swagger-jaxrs-maven-plugin/versions/${PROJECT_VERSION} \
       --data "{ \"username\": \"${SONATYPE_USER}\", \"password\": \"${SONATYPE_KEY}\", \"close\": \"1\" }"
  echo "Release synchronized to central"
else
  echo "Ignoring snapshot version"
fi
