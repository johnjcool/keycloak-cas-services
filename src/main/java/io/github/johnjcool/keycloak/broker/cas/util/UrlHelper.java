package io.github.johnjcool.keycloak.broker.cas.util;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProvider;
import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderConfig;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.services.resources.IdentityBrokerService;
import org.keycloak.services.resources.RealmsResource;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public final class UrlHelper {
	private static final String PROVIDER_PARAMETER_SERVICE = "service";
	private static final String PROVIDER_PARAMETER_RENEW = "renew";
	private static final String PROVIDER_PARAMETER_GATEWAY = "gateway";
	public static final String PROVIDER_PARAMETER_TICKET = "ticket";
	public static final String PROVIDER_PARAMETER_STATE = "state";

	private UrlHelper() {
		// util
	}

	public static UriBuilder createAuthenticationUrl(final CasIdentityProviderConfig config, final AuthenticationRequest request) {
		UriBuilder builder = UriBuilder.fromUri(config.getCasServerLoginUrl())
				.queryParam(PROVIDER_PARAMETER_SERVICE, createServiceUrl(request.getRedirectUri(), request.getState().getEncoded()));
		if (config.isRenew()) {
			builder.queryParam(PROVIDER_PARAMETER_RENEW, config.isRenew());
		}
		if (config.isGateway()) {
			builder.queryParam(PROVIDER_PARAMETER_GATEWAY, config.isGateway());
		}
		return builder;
	}

	public static UriBuilder createValidateServiceUrl(final CasIdentityProviderConfig config, final String ticket, final UriInfo uriInfo, final String state) {
		UriBuilder builder = UriBuilder.fromUri(config.getCasServiceValidateUrl()).queryParam(PROVIDER_PARAMETER_TICKET, ticket)
				.queryParam(PROVIDER_PARAMETER_SERVICE, createServiceUrl(uriInfo.getAbsolutePath().toString(), state));
		if (config.isRenew()) {
			builder.queryParam(PROVIDER_PARAMETER_RENEW, config.isRenew());
		}
		return builder;
	}

	public static UriBuilder createLogoutUrl(final CasIdentityProviderConfig config, final UserSessionModel userSession, final RealmModel realm,
			final UriInfo uriInfo) {
		final String redirect = RealmsResource.brokerUrl(uriInfo).path(IdentityBrokerService.class, "getEndpoint")
				.path(CasIdentityProvider.Endpoint.class, "logoutResponse").queryParam(PROVIDER_PARAMETER_STATE, userSession.getId())
				.build(realm.getName(), config.getAlias()).toString();
		return UriBuilder.fromUri(config.getCasServerLogoutUrl()).queryParam(PROVIDER_PARAMETER_SERVICE, redirect);
	}

	private static String createServiceUrl(final String serviceUrlPrefix, final String state) {
		return String.format("%s?%s=%s", serviceUrlPrefix, PROVIDER_PARAMETER_STATE, state);
	}
}
