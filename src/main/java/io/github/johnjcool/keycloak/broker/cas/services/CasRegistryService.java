package io.github.johnjcool.keycloak.broker.cas.services;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderConfig;
import org.keycloak.models.KeycloakSession;

public interface CasRegistryService {
    String getCasRegistry(String PF, KeycloakSession session, CasIdentityProviderConfig config);
}
