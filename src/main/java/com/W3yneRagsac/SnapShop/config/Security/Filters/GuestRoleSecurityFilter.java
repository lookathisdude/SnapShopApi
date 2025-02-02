package com.W3yneRagsac.SnapShop.config.Security.Filters;

import com.W3yneRagsac.SnapShop.model.Entity.RoleEntity;
import com.W3yneRagsac.SnapShop.model.enums.Roles;
import com.W3yneRagsac.SnapShop.repository.RoleRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class GuestRoleSecurityFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // let them bypass the createUser request in graphql
        if(request.getRequestURI().equals("/graphql/mutation/login") || request.getRequestURI().equals("/graphql/mutations/createUser")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if the user isn't authenticated, then automatically set their role to a guest
        if(authentication == null || !authentication.isAuthenticated()) {
            try {
                RoleEntity guestRole = roleRepository.findByRole(Roles.GUEST)
                        .orElseThrow(() -> new RuntimeException("Role GUEST not found"));

                // Add the GUEST role to the context
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        "guest", null, List.of(new SimpleGrantedAuthority(guestRole.getRole().name()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }
}
