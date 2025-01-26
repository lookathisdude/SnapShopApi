package com.W3yneRagsac.SnapShop.config.Handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class AuthenticatedSuccess implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Authentication success logic
        Authentication newAuthentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
                updatedAuthorities.add(new SimpleGrantedAuthority("CUSTOMER"));
                return updatedAuthorities;
            }

            @Override
            public Object getCredentials() {
                return authentication.getCredentials();
            }

            @Override
            public Object getDetails() {
                return authentication.getDetails();
            }

            @Override
            public Object getPrincipal() {
                return authentication.getPrincipal();
            }

            @Override
            public boolean isAuthenticated() {
                return authentication.isAuthenticated();
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                authentication.setAuthenticated(isAuthenticated);
            }

            @Override
            public String getName() {
                return authentication.getName();
            }
        };

        // Set the updated authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        // Redirect to the home page or dashboard after successful login
        response.sendRedirect("/redirect/home");
    }
}
