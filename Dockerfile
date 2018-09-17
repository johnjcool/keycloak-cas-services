FROM jboss/keycloak:4.1.0.Final
MAINTAINER John J Cool <john.j.cool@gmail.com>

ADD target/keycloak-cas-services-$KEYCLOAK_CAS_SERVICES_VERSION.jar /opt/jboss/keycloak/standalone/deployments/keycloak-cas-services-$KEYCLOAK_CAS_SERVICES_VERSION.jar