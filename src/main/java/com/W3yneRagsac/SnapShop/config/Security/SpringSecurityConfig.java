package com.W3yneRagsac.SnapShop.config.Security;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize annotations
public class SpringSecurityConfig {

    private static final Dotenv dotenv = Dotenv.load();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection
                .csrf(customizer -> customizer.disable())
                // Authorize HTTP requests
                .authorizeHttpRequests(authz -> authz
                        // Allow unrestricted access to specific paths
                        .requestMatchers("/", "/public/**", "/redirect/snapshoplogin", "/error", "/graphql", "/home").permitAll()
                        // Allow only the login mutation to be accessible publicly
                        .requestMatchers("/graphql/mutations/login").permitAll()
                        // Require roles for other GraphQL mutations (update, delete, etc.)
                        .requestMatchers("/graphql/mutations/**").hasAnyRole("CUSTOMER", "VENDOR", "MODERATOR", "DELIVERY_PERSONNEL", "SUPER_ADMIN")
                        .requestMatchers("/role").permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated() // Allow role-check endpoint
                )
                // Configure form login
                .formLogin(form -> form
                        .loginPage("/redirect/snapshoplogin") // Custom login page
                        .permitAll() // Allow everyone to access login page
                        .defaultSuccessUrl("/redirect/home", true) // Redirect after successful login
                )
                // Configure OAuth2 login
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/redirect/snapshoplogin")
                        .defaultSuccessUrl("/redirect/home", true)
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/redirect/logout")
                        .permitAll()
                )
                // Handle exceptions
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/redirect/snapshoplogin")) // Redirect unauthenticated users
                        .accessDeniedPage("/redirect/access-denied") // Custom access-denied page
                )
                // Configure anonymous users
                .anonymous(anonymous -> anonymous
                        .principal("guest") // Principal name for unauthenticated users
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("GUEST"))) // Assign GUEST role
                );

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                ClientRegistration.withRegistrationId("google")
                        .clientId(dotenv.get("GOOGLE_OAUTH_CLIENT_ID"))
                        .clientSecret(dotenv.get("GOOGLE_OAUTH_CLIENT_SECRET"))
                        .scope("profile", "email")
                        .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                        .tokenUri("https://oauth2.googleapis.com/token")
                        .clientName("Google")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
