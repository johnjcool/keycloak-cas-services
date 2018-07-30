package io.github.johnjcool.keycloak.broker.cas;

import org.keycloak.models.IdentityProviderModel;

public class CasIdentityProviderConfig extends IdentityProviderModel {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_CAS_LOGIN_SUFFFIX = "login";
	private static final String DEFAULT_CAS_LOGOUT_SUFFFIX = "logout";
	private static final String DEFAULT_CAS_SERVICE_VALIDATE_SUFFFIX = "serviceValidate";

	public CasIdentityProviderConfig(final IdentityProviderModel model) {
		super(model);
	}

	public String getCasServerUrlPrefix() {
		return getConfig().get("casServerUrlPrefix");
	}

	public String getCasServerLoginUrl() {
		return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_LOGIN_SUFFFIX);
	}

	public String getCasServerLogoutUrl() {
		return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_LOGOUT_SUFFFIX);
	}

	public String getCasServiceValidateUrl() {
		return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_SERVICE_VALIDATE_SUFFFIX);
	}

	public boolean isGateway() {
		return Boolean.valueOf(getConfig().get("gateway"));
	}

	public boolean isRenew() {
		return Boolean.valueOf(getConfig().get("renew"));
	}

	public void setCasServerUrlPrefix(final String casServerUrlPrefix) {
		getConfig().put("casServerUrlPrefix", casServerUrlPrefix);
	}

	public void setGateway(final boolean gateway) {
		getConfig().put("gateway", String.valueOf(gateway));
	}

	public void setRenew(final boolean renew) {
		getConfig().put("renew", String.valueOf(renew));
	}
}
