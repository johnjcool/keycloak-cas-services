package io.github.johnjcool.keycloak.broker.cas;

import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import io.github.johnjcool.keycloak.broker.cas.model.ServiceResponse;
import io.github.johnjcool.keycloak.broker.cas.model.Success;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.broker.provider.AbstractIdentityProvider;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.IdentityBrokerService;
import org.keycloak.services.resources.RealmsResource;

public class CasIdentityProvider extends AbstractIdentityProvider<CasIdentityProviderConfig> {

	protected static final Logger logger = Logger.getLogger(CasIdentityProvider.class);
	protected static final Logger LOGGER_DUMP_USER_PROFILE = Logger.getLogger("org.keycloak.social.user_profile_dump");

	protected static final String PROVIDER_PARAMETER_SERVICE = "service";
	protected static final String PROVIDER_PARAMETER_RENEW = "renew";
	protected static final String PROVIDER_PARAMETER_GATEWAY = "gateway";
	protected static final String PROVIDER_PARAMETER_TICKET = "ticket";
	protected static final String PROVIDER_PARAMETER_STATE = "state";

	public static final String USER_ATTRIBUTES = "UserAttributes";

	private final Client client;

	public CasIdentityProvider(final KeycloakSession session, final CasIdentityProviderConfig config) {
		super(session, config);
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbContextResolver.class, true);
		client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
	}

	@Override
	public Response performLogin(final AuthenticationRequest request) {
		try {
			URI authenticationUrl = createAuthenticationUrl(request).build();
			return Response.seeOther(authenticationUrl).build();
		} catch (Exception e) {
			throw new IdentityBrokerException("Could send authentication request to cas provider.", e);
		}
	}

	@Override
	public Response keycloakInitiatedBrowserLogout(final KeycloakSession session, final UserSessionModel userSession, final UriInfo uriInfo,
			final RealmModel realm) {
		URI logoutUrl = createLogoutUrl(userSession, realm, uriInfo).build();
		return Response.status(302).location(logoutUrl).build();
	}

	@Override
	public Response retrieveToken(final KeycloakSession session, final FederatedIdentityModel identity) {
		return Response.ok(identity.getToken()).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Object callback(final RealmModel realm, final org.keycloak.broker.provider.IdentityProvider.AuthenticationCallback callback, final EventBuilder event) {
		return new Endpoint(callback, realm, event);
	}

	protected BrokeredIdentityContext getFederatedIdentity(final String ticket, final UriInfo uriInfo, final String state) {
		Response response = null;
		try {
			WebTarget target = client.target(createValidateServiceUrl(ticket, uriInfo, state));
			response = target.request(MediaType.APPLICATION_XML_TYPE).get();
			if (response.getStatus() != 200) {
				throw new Exception("Failed : HTTP error code : " + response.getStatus());
			}

			response.bufferEntity();
			if (LOGGER_DUMP_USER_PROFILE.isDebugEnabled()) {
				LOGGER_DUMP_USER_PROFILE.debug("User Profile XML Data for provider " + getConfig().getAlias() + ": " + response.readEntity(String.class));
			}

			ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
			if (serviceResponse.getFailure() != null) {
				throw new Exception(serviceResponse.getFailure().getCode() + "(" + serviceResponse.getFailure().getDescription()
						+ ") for authentication by External IdP " + getConfig().getProviderId());
			}
			Success success = serviceResponse.getSuccess();
			BrokeredIdentityContext user = new BrokeredIdentityContext(success.getUser());
			user.setUsername(success.getUser());
			user.getContextData().put(USER_ATTRIBUTES, success.getAttributes());
			return user;
		} catch (Exception e) {
			throw new IdentityBrokerException("Could not fetch attributes from External IdP's userinfo endpoint.", e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	protected UriBuilder createAuthenticationUrl(final AuthenticationRequest request) {
		UriBuilder builder = UriBuilder.fromUri(getConfig().getCasServerLoginUrl()).queryParam(PROVIDER_PARAMETER_SERVICE,
				createServiceUrl(request.getRedirectUri(), request.getState().getEncoded()));
		if (getConfig().isRenew()) {
			builder.queryParam(PROVIDER_PARAMETER_RENEW, getConfig().isRenew());
		}
		if (getConfig().isGateway()) {
			builder.queryParam(PROVIDER_PARAMETER_GATEWAY, getConfig().isGateway());
		}
		return builder;
	}

	protected UriBuilder createValidateServiceUrl(final String ticket, final UriInfo uriInfo, final String state) {
		UriBuilder builder = UriBuilder.fromUri(getConfig().getCasServiceValidateUrl()).queryParam(PROVIDER_PARAMETER_TICKET, ticket)
				.queryParam(PROVIDER_PARAMETER_SERVICE, createServiceUrl(uriInfo.getAbsolutePath().toString(), state));
		if (getConfig().isRenew()) {
			builder.queryParam(PROVIDER_PARAMETER_RENEW, getConfig().isRenew());
		}
		return builder;
	}

	protected UriBuilder createLogoutUrl(final UserSessionModel userSession, final RealmModel realm, final UriInfo uriInfo) {
		String redirect = RealmsResource.brokerUrl(uriInfo).path(IdentityBrokerService.class, "getEndpoint").path(Endpoint.class, "logoutResponse")
				.queryParam("state", userSession.getId()).build(realm.getName(), getConfig().getAlias()).toString();
		UriBuilder builder = UriBuilder.fromUri(getConfig().getCasServerLogoutUrl()).queryParam(PROVIDER_PARAMETER_SERVICE, redirect);
		return builder;
	}

	protected String createServiceUrl(final String serviceUrlPrefix, final String state) {
		return String.format("%s?%s=%s", serviceUrlPrefix, PROVIDER_PARAMETER_STATE, state);
	}

	protected class Endpoint {
		protected AuthenticationCallback callback;
		protected RealmModel realm;
		protected EventBuilder event;

		@Context
		protected KeycloakSession session;

		@Context
		protected ClientConnection clientConnection;

		@Context
		protected HttpHeaders headers;

		@Context
		protected UriInfo uriInfo;

		public Endpoint(final AuthenticationCallback callback, final RealmModel realm, final EventBuilder event) {
			this.callback = callback;
			this.realm = realm;
			this.event = event;
		}

		@GET
		public Response authResponse(@QueryParam(PROVIDER_PARAMETER_TICKET) final String ticket, @QueryParam(PROVIDER_PARAMETER_STATE) final String state) {
			try {
				BrokeredIdentityContext federatedIdentity = getFederatedIdentity(ticket, uriInfo, state);
				federatedIdentity.setIdpConfig(getConfig());
				federatedIdentity.setIdp(CasIdentityProvider.this);
				federatedIdentity.setCode(state);
				return callback.authenticated(federatedIdentity);
			} catch (Exception e) {
				logger.error("Failed to call delegating authentication identity provider's callback method.", e);
			}
			event.event(EventType.LOGIN);
			event.error(Errors.IDENTITY_PROVIDER_LOGIN_FAILURE);
			return ErrorPage.error(session, null, Status.EXPECTATION_FAILED, Messages.IDENTITY_PROVIDER_UNEXPECTED_ERROR);
		}

		@GET
		@Path("logout_response")
		public Response logoutResponse(@Context final UriInfo uriInfo, @QueryParam("state") final String state) {
			UserSessionModel userSession = session.sessions().getUserSession(realm, state);
			if (userSession == null) {
				logger.error("no valid user session");
				EventBuilder event = new EventBuilder(realm, session, clientConnection);
				event.event(EventType.LOGOUT);
				event.error(Errors.USER_SESSION_NOT_FOUND);
				return ErrorPage.error(session, null, Response.Status.BAD_REQUEST, Messages.IDENTITY_PROVIDER_UNEXPECTED_ERROR);
			}
			if (userSession.getState() != UserSessionModel.State.LOGGING_OUT) {
				logger.error("usersession in different state");
				EventBuilder event = new EventBuilder(realm, session, clientConnection);
				event.event(EventType.LOGOUT);
				event.error(Errors.USER_SESSION_NOT_FOUND);
				return ErrorPage.error(session, null, Response.Status.BAD_REQUEST, Messages.SESSION_NOT_ACTIVE);
			}
			return AuthenticationManager.finishBrowserLogout(session, realm, userSession, uriInfo, clientConnection, headers);
		}
	}
}
