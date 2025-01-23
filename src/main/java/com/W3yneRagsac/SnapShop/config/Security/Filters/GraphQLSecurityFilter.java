package com.W3yneRagsac.SnapShop.config.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class GraphQLSecurityFilter extends OncePerRequestFilter {

    private static AntPathRequestMatcher GRAPHQL_MATCHER = new AntPathRequestMatcher("/graphql");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // get the param of query
        if(GRAPHQL_MATCHER.matches(request)) {
            String query = request.getParameter("query");
            // if the query does not equal null, or it contains create user
            if(query != null && query.contains("mutation createUser")) {
                // allow the request
                filterChain.doFilter(request, response);
                return;
            }
        }
        // Then proceed with normal filters
        filterChain.doFilter(request, response);
    }
}

