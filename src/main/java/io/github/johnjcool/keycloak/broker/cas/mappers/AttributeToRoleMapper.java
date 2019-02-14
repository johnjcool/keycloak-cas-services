package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderFactory;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.ConfigConstants;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;

public class AttributeToRoleMapper extends AbstractAttributeMapper {

	protected static final String[] COMPATIBLE_PROVIDERS = { CasIdentityProviderFactory.PROVIDER_ID };

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

	static {
		ProviderConfigProperty property;
		ProviderConfigProperty property1;
		property1 = new ProviderConfigProperty();
		property1.setName(ATTRIBUTE);
		property1.setLabel("Attribute Name");
		property1.setHelpText("Name of attribute to search for in assertion.");
		property1.setType(ProviderConfigProperty.STRING_TYPE);
		configProperties.add(property1);
		property1 = new ProviderConfigProperty();
		property1.setName(ATTRIBUTE_VALUE);
		property1.setLabel("Attribute Value");
		property1.setHelpText("Value the attribute must have.  If the attribute is an array, then the value must be contained in the array.");
		property1.setType(ProviderConfigProperty.STRING_TYPE);
		configProperties.add(property1);
		property = new ProviderConfigProperty();
		property.setName(ConfigConstants.ROLE);
		property.setLabel("Role");
		property.setHelpText("Role to grant to user if attribute is present.  Click 'Select Role' button to browse roles, or just type it in the textbox.  To reference an application role the syntax is appname.approle, i.e. myapp.myrole");
		property.setType(ProviderConfigProperty.ROLE_TYPE);
		configProperties.add(property);
	}

	public static final String PROVIDER_ID = "cas-role-idp-mapper";

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return configProperties;
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String[] getCompatibleProviders() {
		return COMPATIBLE_PROVIDERS;
	}

	@Override
	public String getDisplayCategory() {
		return "Role Importer";
	}

	@Override
	public String getDisplayType() {
		return "Attribute to Role";
	}

	@Override
	public void importNewUser(final KeycloakSession session, final RealmModel realm, final UserModel user, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		String roleName = mapperModel.getConfig().get(ConfigConstants.ROLE);
		if (hasAttributeValue(mapperModel, context)) {
			RoleModel role = KeycloakModelUtils.getRoleFromString(realm, roleName);
			if (role == null) {
				throw new IdentityBrokerException("Unable to find role: " + roleName);
			}
			user.grantRole(role);
		}
	}

	@Override
	public void updateBrokeredUser(final KeycloakSession session, final RealmModel realm, final UserModel user, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		String roleName = mapperModel.getConfig().get(ConfigConstants.ROLE);
		if (!hasAttributeValue(mapperModel, context)) {
			RoleModel role = KeycloakModelUtils.getRoleFromString(realm, roleName);
			if (role == null) {
				throw new IdentityBrokerException("Unable to find role: " + roleName);
			}
			user.deleteRoleMapping(role);
		}

	}

	@Override
	public String getHelpText() {
		return "If a attribute exists, grant the user the specified realm or application role.";
	}

}
