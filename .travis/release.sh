#!/usr/bin/env bash

set -e
echo "Ensuring that pom <version> matches $TRAVIS_TAG"
mvn org.codehaus.mojo:versions-maven-plugin:2.5:set --settings .travis/settings.xml -DnewVersion=$TRAVIS_TAG -B -U

echo "Uploading to oss repo"
mvn clean deploy --settings .travis/settings.xml -DskipTests=true -B -U -Prelease