[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.johnjcool/keycloak-cas-services/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.johnjcool/keycloak-cas-services) [![Build Status](https://travis-ci.com/johnjcool/keycloak-cas-services.svg?branch=master)](https://travis-ci.com/johnjcool/keycloak-cas-services) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=io.github.johnjcool%3Akeycloak-cas-services&metric=alert_status)](https://sonarcloud.io/dashboard/index/io.github.johnjcool%3Akeycloak-cas-services) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.johnjcool%3Akeycloak-cas-services&metric=coverage)](https://sonarcloud.io/component_measures?id=io.github.johnjcool%3Akeycloak-cas-services&metric=coverage) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=io.github.johnjcool%3Akeycloak-cas-services&metric=bugs)](https://sonarcloud.io/component_measures?id=io.github.johnjcool%3Akeycloak-cas-services&metric=bugs) [![Code smells](https://sonarcloud.io/api/project_badges/measure?project=io.github.johnjcool%3Akeycloak-cas-services&metric=code_smells)](https://sonarcloud.io/component_measures?id=io.github.johnjcool%3Akeycloak-cas-services&metric=code_smells) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.johnjcool%3Akeycloak-cas-services&metric=security_rating)](https://sonarcloud.io/component_measures?id=io.github.johnjcool%3Akeycloak-cas-services&metric=security_rating)

Keycloak CAS Services
=====================

Using as maven dependency
-------------------------
You can use this module as dependency in your own modules.
```
<dependency>
	<groupId>io.github.johnjcool</groupId>
	<artifactId>keycloak-cas-services</artifactId>
	<version>4.8.3.Final</version>
</dependency>
```

Manual Deployment
-----------------
1. Download keycloak version from [https://www.keycloak.org/](https://www.keycloak.org/)
2. Download corresponding keycloak-cas-services version from [maven central](https://search.maven.org/search?q=g:io.github.johnjcool%20AND%20a:keycloak-cas-services&core=gav)
3. Copy keycloak-cas-services-<VERSION>.jar to ```<KEYCLOAK_INSTALLATION_DIR>/standalone/deployments```
4. Start keycloak with ```<KEYCLOAK_INSTALLATION_DIR>/bin/standalone.<sh|bat>```
5. Navigate to [http://localhost:8080](http://localhost:8080) and create an admin account

Using docker image
------------------
Start ready to use docker image:
```sh
docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=xxxxxx johnjcool/keycloak-cas
```

Central Authentication Service Configuration
--------------------------------------------
1. Navigate to [http://localhost:8080/auth/admin](http://localhost:8080/auth/admin)
2. Login with your admin credentials <img src="/docs/resources/26-09-2018 20-05-47.png">
3. Navigate to Themes **Important!!! You have to do this on master realm** <img src="/docs/resources/26-09-2018 20-05-59.png">
4. Switch Admin Console Theme to keycloak-extended
5. Signout
6. Login with your admin credentials
7. Navigate to Identity Providers <img src="/docs/resources/26-09-2018 20-06-21.png">
8. Add provider CAS
9. Configure CAS provider <img src="/docs/resources/26-09-2018 20-07-15.png">
10. Click Save
11. Navigate to Mappers to get Attributes from CAS <img src="/docs/resources/26-09-2018 20-07-44.png">
12. Configure Attribute Mapper for email, firstName, lastName <img src="/docs/resources/26-09-2018 20-08-04.png"> <img src="/docs/resources/26-09-2018 20-08-54.png">
13. Signout and Central Authentication Service should be available <img src="/docs/resources/26-09-2018 19-40-25.png">

Optional Central Authentication Service Configuration
--------------------------------------------
1. Login with your admin credentials <img src="/docs/resources/26-09-2018 20-05-47.png">
2. Navigate to Authetication 
3. Click on Actions/Config for Identity Provider Redirector <img src="/docs/resources/26-09-2018 20-09-49.png">
4. Set Alias and Default Identiy Provider to ```cas``` <img src="/docs/resources/26-09-2018 20-10-05.png">
5. Signout and now you are automatically redirected to CAS for Login


**Have fun!**
