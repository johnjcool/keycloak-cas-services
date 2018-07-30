package org.jjc.keycloak.broker.cas.model;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jjc.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import org.jjc.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServiceResponseTest {

	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;

	@SuppressWarnings("deprecation")
	private TJWSEmbeddedJaxrsServer s;

	@Test
	public void testReadSuccess() {
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbContextResolver.class, true);
		Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
		WebTarget target = client.target(String.format("http://%s:%d%s", "127.0.0.1", 9999, "/success"));
		Response response = target.request().get();
		Assert.assertEquals(200, response.getStatus());
		response.bufferEntity();

		ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
		Success success = serviceResponse.getSuccess();

		Assert.assertEquals("test", success.getUser());
		Assert.assertTrue(success.getAttributes() != null);
	}

	@Path("")
	static public class TestResource {

		@GET
		@Path("success")
		@Consumes("*/*")
		@Produces("text/html; charset=UTF-8")
		public String success() throws Exception {
			return FileUtils.readFileToString(new File("src/test/resources/test.xml"), "UTF-8");
		}
	}

	@SuppressWarnings("deprecation")
	@Before
	public void before() throws Exception {
		s = new TJWSEmbeddedJaxrsServer();
		s.setPort(9999);
		s.setBindAddress("127.0.0.1");
		s.setRootResourcePath("/");
		s.start();
		s.getDeployment().getDispatcher().getRegistry().addSingletonResource(new TestResource());
	}

	@SuppressWarnings("deprecation")
	@After
	public void after() throws Exception {
		s.stop();
	}

}
