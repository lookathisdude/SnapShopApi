package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.config.Handlers.JwtUtil;
import com.W3yneRagsac.SnapShop.repository.UserRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Data;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthResolver implements GraphQLMutationResolver {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResolver(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @MutationMapping
    public AuthPayload login(@Argument String username, @Argument String password) {
        // Attempt authentication
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Generate the JWT token if authentication is successful
            String token = jwtUtil.generateToken(username);

            return new AuthPayload(token, "Login successful");

        } catch (Exception e) {
            return new AuthPayload(null, "Invalid credentials");
        }
    }

    @Data
    public static class AuthPayload {
        private String token;
        private String message;

        public AuthPayload(String token, String message) {
            this.token = token;
            this.message = message;
        }
    }
}
