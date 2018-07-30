package io.github.johnjcool.keycloak.broker.cas.jaxb;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;

@Provider
@Produces(MediaType.WILDCARD)
public class ServiceResponseJaxbContextResolver extends XmlJAXBContextFinder {

}