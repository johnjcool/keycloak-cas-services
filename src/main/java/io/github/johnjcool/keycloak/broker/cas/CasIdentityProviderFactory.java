package io.github.johnjcool.keycloak.broker.cas;

import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.Config;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

public class CasIdentityProviderFactory extends AbstractIdentityProviderFactory<CasIdentityProvider> {

	public static final String PROVIDER_ID = "cas";

	@Override
	public void init(final Config.Scope config) {
		super.init(config);
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
		ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbContextResolver.class, true);
	}

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