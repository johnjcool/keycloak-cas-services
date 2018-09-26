package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.IdentityProviderMapperModel;

public abstract class AbstractAttributeMapper extends AbstractIdentityProviderMapper {

	public static final String ATTRIBUTE = "attribute";
	public static final String ATTRIBUTE_VALUE = "attribute.value";

	public static Object getAttributeValue(final IdentityProviderMapperModel mapperModel, final BrokeredIdentityContext user) {
		String attributeName = mapperModel.getConfig().get(ATTRIBUTE);
		@SuppressWarnings("unchecked")
		Map<String, Object> userAttributes = (Map<String, Object>) user.getContextData().get(CasIdentityProvider.USER_ATTRIBUTES);
		if (userAttributes.containsKey(attributeName)) {
			return getAttributeValue(userAttributes.get(attributeName));
		}
		return null;
	}

	public static Object getAttributeValue(final Object userAttribute) {
		if (userAttribute instanceof String && ((String) userAttribute).startsWith("[") && ((String) userAttribute).endsWith("]")) {
			String userArrayAttribute = (String) userAttribute;
			String[] userAttributes = userArrayAttribute.substring(1, userArrayAttribute.length() - 1).split(", ");
			return new ArrayList<>(Arrays.asList(userAttributes));
		}
		return userAttribute;
	}

	protected boolean hasAttributeValue(final IdentityProviderMapperModel mapperModel, final BrokeredIdentityContext context) {
		Object value = getAttributeValue(mapperModel, context);
		String desiredValue = mapperModel.getConfig().get(ATTRIBUTE_VALUE);
		return valueEquals(desiredValue, value);
	}

	public boolean valueEquals(final String desiredValue, final Object value) {
		if (value instanceof String) {
			if (desiredValue.equals(value)) {
				return true;
			}
		} else if (value instanceof Double) {
			try {
				if (Double.valueOf(desiredValue).equals(value)) {
					return true;
				}
			} catch (Exception e) {

			}
		} else if (value instanceof Integer) {
			try {
				if (Integer.valueOf(desiredValue).equals(value)) {
					return true;
				}
			} catch (Exception e) {

			}
		} else if (value instanceof Boolean) {
			try {
				if (Boolean.valueOf(desiredValue).equals(value)) {
					return true;
				}
			} catch (Exception e) {

			}
		} else if (value instanceof List) {
			List<?> list = (List<?>) value;
			for (Object val : list) {
				if (valueEquals(desiredValue, val)) {
					return true;
				}
			}
		}
		return false;
	}
}
