package org.jjc.keycloak.broker.cas.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "serviceResponse", namespace = "http://www.yale.edu/tp/cas")
public class ServiceResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "authenticationFailure")
	private Failure failure;

	@XmlElement(name = "authenticationSuccess")
	private Success success;

	public Failure getFailure() {
		return failure;
	}

	public void setFailure(final Failure failure) {
		this.failure = failure;
	}

	public Success getSuccess() {
		return success;
	}

	public void setSuccess(final Success success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return String.format("ServiceResponse [failure=%s, success=%s]", failure, success);
	}
}
