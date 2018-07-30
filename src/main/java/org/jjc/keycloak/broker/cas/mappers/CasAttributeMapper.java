package org.jjc.keycloak.broker.cas.mappers;

import java.util.List;

import org.jjc.keycloak.broker.cas.CasIdentityProviderFactory;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class CasAttributeMapper extends AbstractIdentityProviderMapper {

	private static final String[] cp = new String[] { CasIdentityProviderFactory.PROVIDER_ID };

	@Override
	public String[] getCompatibleProviders() {
		return cp;
	}

	@Override
	public String getId() {
		return "cas-attribute-mapper";
	}

	@Override
	public String getDisplayCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHelpText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBrokeredUser(final KeycloakSession session, final RealmModel realm, final UserModel user, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		// TODO Auto-generated method stub
	}
}