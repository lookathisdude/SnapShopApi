package com.W3yneRagsac.SnapShop.config.Security;

import com.W3yneRagsac.SnapShop.config.Security.Filters.OAuthSecurityFilter;
import com.W3yneRagsac.SnapShop.service.classes.EmailCredentialsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize annotations
public class SpringSecurityConfig {

    private static final Dotenv dotenv = Dotenv.load();
    private final OAuthSecurityFilter oAuthSecurityFilter;
    private final EmailCredentialsService emailCredentialsService;

    @Lazy
    private final PasswordEncoder passwordEncoder;  // Inject PasswordEncoder here

    public SpringSecurityConfig(OAuthSecurityFilter oAuthSecurityFilter,
                                @Lazy EmailCredentialsService emailCredentialsService,
                                @Lazy PasswordEncoder passwordEncoder) {
        this.oAuthSecurityFilter = oAuthSecurityFilter;
        this.emailCredentialsService = emailCredentialsService;
        this.passwordEncoder = passwordEncoder;  // Use the injected PasswordEncoder
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(customizer -> customizer.disable())
                .addFilterBefore(oAuthSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/public/**", "/redirect/snapshoplogin", "/error", "/graphql", "/home").permitAll()
                        .requestMatchers("/graphql/mutations/login", "/graphql/mutations/createUser").permitAll()
                        .requestMatchers("/graphql/mutations/**").hasAnyRole("CUSTOMER", "VENDOR", "MODERATOR", "DELIVERY_PERSONNEL", "SUPER_ADMIN")
                        .requestMatchers("/role").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/redirect/snapshoplogin")
                        .permitAll()
                        .defaultSuccessUrl("/redirect/home", true)
                        .usernameParameter("email") //  use "email" as the username parameter
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/redirect/snapshoplogin")
                        .defaultSuccessUrl("/redirect/home", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/redirect/logout")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/redirect/snapshoplogin"))
                        .accessDeniedPage("/redirect/access-denied")
                )
                .anonymous(anonymous -> anonymous
                        .principal("guest")
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("GUEST")))
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(emailCredentialsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}