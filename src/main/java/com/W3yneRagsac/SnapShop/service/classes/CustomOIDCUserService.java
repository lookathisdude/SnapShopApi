package com.W3yneRagsac.SnapShop.service.classes;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService; // Correct import

public class CustomOIDCUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService defaultOidcUserService = new OidcUserService(); // Initialize default OIDC user service

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default OIDC user service to load the user's information
        OidcUser oidcUser = delegateToDefaultOidcUserService(userRequest);

        // return the user
        return oidcUser;
    }

    private OidcUser delegateToDefaultOidcUserService(OidcUserRequest userRequest) {
        // Delegate to the default OidcUserService implementation provided by Spring Security
        return defaultOidcUserService.loadUser(userRequest);
    }
}