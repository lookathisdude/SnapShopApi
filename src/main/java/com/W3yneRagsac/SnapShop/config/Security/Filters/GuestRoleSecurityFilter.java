package com.W3yneRagsac.SnapShop.config.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class GuestRoleSecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if the user isn't authenticated, then automatically set their role to a guest
        if(authentication == null || !authentication.isAuthenticated()) {
            // Assign their role to a guest
            authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    "GUEST",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
