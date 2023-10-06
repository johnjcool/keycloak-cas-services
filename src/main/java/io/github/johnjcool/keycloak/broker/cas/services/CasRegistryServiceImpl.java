package io.github.johnjcool.keycloak.broker.cas.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderConfig;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;

public class CasRegistryServiceImpl implements CasRegistryService {
    protected static final Logger logger = Logger.getLogger(CasRegistryServiceImpl.class);

    // FIXME configure back office
    public static final String URL_KEY = "baseUrl";
    @Override
    public String getCasRegistry(String PF,
                                 KeycloakSession session,
                                 final CasIdentityProviderConfig config) {
        logger.info("CasRegistryServiceImpl - getCasRegistry with PF " + PF);

        String apiUrl = config.getCasRegistryUrl();
        logger.info("apiUrl : "+ apiUrl);

        String casServerUrl = null;
        try {
            JsonNode j = SimpleHttp
                    .doGet(apiUrl, session)
                    .param("pf", PF)
                    .asJson();

            if (j == null) {
                throw new Exception("renew access token error");
            }

            logger.info(j);
            casServerUrl = j.path(URL_KEY).textValue();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace(System.out);
//            throw new RuntimeException(e);
        }

        logger.info("1/ casUrl  : "+ casServerUrl);
        return casServerUrl;
    }
}
