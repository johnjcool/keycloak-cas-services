package io.github.johnjcool.keycloak.broker.cas.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Failure implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private Code code;

	@XmlValue
	private String description;

	public Code getCode() {
		return code;
	}

	public void setCode(final Code code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return String.format("Failure [code=%s, description=%s]", code, description);
	}
}