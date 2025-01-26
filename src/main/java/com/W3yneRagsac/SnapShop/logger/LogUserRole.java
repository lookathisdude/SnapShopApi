package com.W3yneRagsac.SnapShop.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class LogUserRole {
    private static final Logger logger = LoggerFactory.getLogger(LogUserRole.class);

    public void logUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            logger.info("Authenticated user: " + authentication.getName());

            // Loop through granted authorities (roles)
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                logger.info("User role: " + authority.getAuthority());
            }
        } else {
            logger.info("No authenticated user found.");
        }
    }
}
