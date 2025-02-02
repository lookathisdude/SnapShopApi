package com.W3yneRagsac.SnapShop.config.Security.Filters;

import com.W3yneRagsac.SnapShop.service.classes.JwtUtilService;
import com.W3yneRagsac.SnapShop.service.classes.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuthSecurityFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtilService jwtUtilService;

    @Autowired
    ApplicationContext context;

    // Logger to log user details and role assignment
    private static final Logger logger = LoggerFactory.getLogger(OAuthSecurityFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String oauthHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String email = null;

        // Check if the token exists and starts with "Bearer"
        if (oauthHeader != null && oauthHeader.startsWith("Bearer")) {
            jwtToken = oauthHeader.substring(7);  // Extract JWT token
            email = jwtUtilService.extractEmail(jwtToken);  // Extract username from the token
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(UserService.class).loadUserByEmail(email);

            // Validate the token
            if (jwtUtilService.validateToken(jwtToken, userDetails)) {
                // Create authentication token and set it in the context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Extract roles from the token (now a list of authorities)
                // Create the list of roles
                List<String> roles = jwtUtilService.extractRoles(jwtToken);

                //Convert the roles to authroites(Spring recognises authorities)
                List<GrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                logger.info("Authenticated user: '{}', Roles: '{}'", email, roles);


                // Create and set authentication with user and roles
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Set the authentication context with the token
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }
}
