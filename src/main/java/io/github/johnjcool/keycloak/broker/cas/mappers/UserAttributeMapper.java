package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class UserAttributeMapper extends AbstractAttributeMapper {

	private static final String[] cp = new String[] { CasIdentityProviderFactory.PROVIDER_ID };

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

	private static final String ATTRIBUTE = "attribute";
	private static final String USER_ATTRIBUTE = "user.attribute";
	private static final String EMAIL = "email";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";

	static {
		ProviderConfigProperty property;
		property = new ProviderConfigProperty();
		property.setName(ATTRIBUTE);
		property.setLabel("Attribute");
		property.setHelpText("Name of attribute to search for in assertion.");
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

		Object value = getAttributeValue(mapperModel, context);
		List<String> values = toList(value);

		if (EMAIL.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(context::setEmail, values);
		} else if (FIRST_NAME.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(context::setFirstName, values);
		} else if (LAST_NAME.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(context::setLastName, values);
		} else {
			List<String> valuesToString = values.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
			context.setUserAttribute(attribute, valuesToString);
		}
	}

	private void setIfNotEmpty(final Consumer<String> consumer, final List<String> values) {
		if (values != null && !values.isEmpty()) {
			consumer.accept(values.get(0));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> toList(final Object value) {
		List<Object> values = (value instanceof List) ? (List) value : Collections.singletonList(value);
		return values.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
	}

	@Override
	public void updateBrokeredUser(final KeycloakSession session, final RealmModel realm, final UserModel user, final IdentityProviderMapperModel mapperModel,
			final BrokeredIdentityContext context) {
		String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE);
		if (attribute == null || attribute.isEmpty()) {
			return;
		}
		Object value = getAttributeValue(mapperModel, context);
		List<String> values = toList(value);
		if (EMAIL.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(user::setEmail, values);
		} else if (FIRST_NAME.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(user::setFirstName, values);
		} else if (LAST_NAME.equalsIgnoreCase(attribute)) {
			setIfNotEmpty(user::setLastName, values);
		} else {
			List<String> current = user.getAttribute(attribute);
			if (!CollectionUtil.collectionEquals(values, current)) {
				user.setAttribute(attribute, values);
			} else if (values.isEmpty()) {
				user.removeAttribute(attribute);
			}
		}
	}

	@Override
	public String getHelpText() {
		return "Import declared CAS attribute if it exists in assertion into the specified user property or attribute.";
	}

}
