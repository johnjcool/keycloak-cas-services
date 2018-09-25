package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProvider;
import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class CasAttributeMapper extends AbstractIdentityProviderMapper {

	private static final String[] cp = new String[] { CasIdentityProviderFactory.PROVIDER_ID };

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

	private static final String ATTRIBUTE_NAME = "attribute.name";
	private static final String USER_ATTRIBUTE = "user.attribute";
	private static final String EMAIL = "email";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";

	static {
		ProviderConfigProperty property;
		property = new ProviderConfigProperty();
		property.setName(ATTRIBUTE_NAME);
		property.setLabel("Attribute Name");
		property.setHelpText("Name of attribute to search for in assertion.  You can leave this blank and specify a friendly name instead.");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		configProperties.add(property);
		property = new ProviderConfigProperty();
		property.setName(USER_ATTRIBUTE);
		property.setLabel("User Attribute Name");
		property.setHelpText("User attribute name to store CAS attribute.  Use email, lastName, and firstName to map to those predefined user properties.");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		configProperties.add(property);
	}

	public static final String PROVIDER_ID = "cas-user-attribute-idp-mapper";

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
		return cp;
	}

	@Override
	public String getDisplayCategory() {
		return "Attribute Importer";
	}

	@Override
	public String getDisplayType() {
		return "Attribute Importer";
	}

	@Override
	public void preprocessFederatedIdentity(final KeycloakSession session, final RealmModel realm, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE);
		if (attribute == null || attribute.isEmpty()) {
			return;
		}

		String attributeName = mapperModel.getConfig().get(ATTRIBUTE_NAME);

		List<String> attributeValuesInContext = findAttributeValuesInContext(attributeName, context);
		if (!attributeValuesInContext.isEmpty()) {
			if (attribute.equalsIgnoreCase(EMAIL)) {
				setIfNotEmpty(context::setEmail, attributeValuesInContext);
			} else if (attribute.equalsIgnoreCase(FIRST_NAME)) {
				setIfNotEmpty(context::setFirstName, attributeValuesInContext);
			} else if (attribute.equalsIgnoreCase(LAST_NAME)) {
				setIfNotEmpty(context::setLastName, attributeValuesInContext);
			} else {
				context.setUserAttribute(attribute, attributeValuesInContext);
			}
		}
	}

	private void setIfNotEmpty(final Consumer<String> consumer, final List<String> values) {
		if (values != null && !values.isEmpty()) {
			consumer.accept(values.get(0));
		}
	}

	@Override
	public void updateBrokeredUser(final KeycloakSession session, final RealmModel realm, final UserModel user, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE);
		if (attribute == null || attribute.isEmpty()) {
			return;
		}
		String attributeName = mapperModel.getConfig().get(ATTRIBUTE_NAME);
		List<String> attributeValuesInContext = findAttributeValuesInContext(attributeName, context);
		if (attribute.equalsIgnoreCase(EMAIL)) {
			setIfNotEmpty(user::setEmail, attributeValuesInContext);
		} else if (attribute.equalsIgnoreCase(FIRST_NAME)) {
			setIfNotEmpty(user::setFirstName, attributeValuesInContext);
		} else if (attribute.equalsIgnoreCase(LAST_NAME)) {
			setIfNotEmpty(user::setLastName, attributeValuesInContext);
		} else {
			List<String> currentAttributeValues = user.getAttributes().get(attribute);
			if (attributeValuesInContext.isEmpty()) {
				// attribute no longer sent by brokered idp, remove it
				user.removeAttribute(attribute);
			} else if (currentAttributeValues == null) {
				// new attribute sent by brokered idp, add it
				user.setAttribute(attribute, attributeValuesInContext);
			} else if (!CollectionUtil.collectionEquals(attributeValuesInContext, currentAttributeValues)) {
				// attribute sent by brokered idp has different values as before, update it
				user.setAttribute(attribute, attributeValuesInContext);
			}
			// attribute allready set
		}
	}

	private List<String> findAttributeValuesInContext(final String attributeName, final BrokeredIdentityContext user) {
		Object value = ((Map<String, Object>) user.getContextData().get(CasIdentityProvider.USER_ATTRIBUTES)).get(attributeName);
		if (value instanceof String) {
			return Collections.singletonList((String) value);
		} else if (value instanceof List) {
			return (List<String>) value;
		} else if (value == null) {
			return Collections.emptyList();
		} else {
			throw new UnsupportedOperationException("Type: " + value.getClass() + " not supported.");
		}
	}

	@Override
	public String getHelpText() {
		return "Import declared CAS attribute if it exists in assertion into the specified user property or attribute.";
	}

}
