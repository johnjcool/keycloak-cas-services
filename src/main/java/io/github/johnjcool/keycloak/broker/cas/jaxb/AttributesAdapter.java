package io.github.johnjcool.keycloak.broker.cas.jaxb;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AttributesAdapter extends XmlAdapter<AttributesWrapper, Map<String, Object>> {

	@Override
	public AttributesWrapper marshal(final Map<String, Object> attributes) throws Exception {
		throw new UnsupportedOperationException("This adapter only supports from xml to map.");
	}

	@Override
	public Map<String, Object> unmarshal(final AttributesWrapper attributesWrapper) throws Exception {
		return attributesWrapper.toMap();
	}
}
