package io.github.johnjcool.keycloak.broker.cas;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

public class CasIdentityProviderFactory extends AbstractIdentityProviderFactory<CasIdentityProvider> {

	public static final String PROVIDER_ID = "cas";

	@Override
	public String getName() {
		return "CAS";
	}

	@Override
	public CasIdentityProvider create(final KeycloakSession session, final IdentityProviderModel model) {
		return new CasIdentityProvider(session, new CasIdentityProviderConfig(model));
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
}