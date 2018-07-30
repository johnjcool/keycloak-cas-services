package io.github.johnjcool.keycloak.broker.cas.model;

import io.github.johnjcool.keycloak.broker.cas.jaxb.AttributesAdapter;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Success implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "user")
	private String user;

	@XmlElement(name = "attributes")
	@XmlJavaTypeAdapter(AttributesAdapter.class)
	private Map<String, Object> attributes;

	public String getUser() {
		return user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(final Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return String.format("Success [user=%s, attributes=%s]", user, attributes);
	}
}