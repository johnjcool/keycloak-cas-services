package io.github.johnjcool.keycloak.broker.cas;

import org.keycloak.models.IdentityProviderModel;

public class CasIdentityProviderConfig extends IdentityProviderModel {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_CAS_LOGOUT_SUFFFIX = "logout";
    private static final String DEFAULT_CAS_SERVICE_VALIDATE_SUFFFIX = "serviceValidate";
    private static final String DEFAULT_CAS_3_PROTOCOL_PREFIX = "p3";

    public CasIdentityProviderConfig(final IdentityProviderModel model) {
        super(model);
    }

    public CasIdentityProviderConfig() {
        super();
    }

    // casRegistryUrl
    public void setCasRegistryUrl(final String casRegistryUrl) {
        getConfig().put("casRegistryUrl", casRegistryUrl);
    }

    public String getCasRegistryUrl() {
        return getConfig().get("casRegistryUrl");
    }

    // casServerUrlPrefix
    public void setCasServerUrlPrefix(final String casServerUrlPrefix) {
        getConfig().put("casServerUrlPrefix", casServerUrlPrefix);
    }

    public String getCasServerUrlPrefix() {
        return getConfig().get("casServerUrlPrefix");
    }

    public void setCasServerProtocol3(final boolean casServerProtocol3) {
        getConfig().put("casServerProtocol3", String.valueOf(casServerProtocol3));
    }

    public boolean isCasServerProtocol3() {
        return Boolean.valueOf(getConfig().get("casServerProtocol3"));
    }

    public void setGateway(final boolean gateway) {
        getConfig().put("gateway", String.valueOf(gateway));
    }

    public boolean isGateway() {
        return Boolean.valueOf(getConfig().get("gateway"));
    }

    public void setRenew(final boolean renew) {
        getConfig().put("renew", String.valueOf(renew));
    }

    public boolean isRenew() {
        return Boolean.valueOf(getConfig().get("renew"));
    }

    public String getCasServerLogoutUrl() {
        return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_LOGOUT_SUFFFIX);
    }

    public String getCasServiceValidateUrl() {
        return isCasServerProtocol3() ?
                String.format("%s/%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_3_PROTOCOL_PREFIX, DEFAULT_CAS_SERVICE_VALIDATE_SUFFFIX)
                : String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_SERVICE_VALIDATE_SUFFFIX);
    }
}
