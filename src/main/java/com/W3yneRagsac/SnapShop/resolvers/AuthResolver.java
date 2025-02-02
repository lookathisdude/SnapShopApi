package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.service.classes.JwtUtilService;
import com.W3yneRagsac.SnapShop.model.Entity.AuthPayloadEntity;
import com.W3yneRagsac.SnapShop.service.classes.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class AuthResolver {

    private final JwtUtilService jwtUtilService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @MutationMapping
    public AuthPayloadEntity login(@Argument String email, @Argument String password) {
        try {
            // Authenticate user and generate token with roles
            return userService.login(email, password);
        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed for email: " + email);  // Debug line
            return new AuthPayloadEntity(null, "Invalid credentials", null);
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());  // Debug line
            return new AuthPayloadEntity(null, "Error occurred", null);
        }
    }
}


