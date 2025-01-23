package com.W3yneRagsac.SnapShop.config.Security;

import com.W3yneRagsac.SnapShop.config.Security.Filters.GraphQLSecurityFilter;
import com.W3yneRagsac.SnapShop.service.classes.CustomOIDCUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    // Bean for the custom OIDC user service
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        return new CustomOIDCUserService(); // Return the custom OIDC user service instance
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for now (enable in production with proper configuration)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/graphql").permitAll() // Public access
                        .requestMatchers("/home", "/products").permitAll() // Public access
                        .requestMatchers("/checkout", "/cart").authenticated() // Authentication required
                        .anyRequest().permitAll()) // All other requests are allowed

                .formLogin(login -> login
                        .loginPage("/SnapShopLogin")
                        .defaultSuccessUrl("/home", true) // Redirect to home after successful login
                        .permitAll())

                //Logout page
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll())

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .oidcUserService(oAuth2UserService()))) // Use custom OIDC user service for OAuth2 login

                .addFilterBefore(new GraphQLSecurityFilter(), AuthenticationFilter.class); // Add custom security filter before authentication filter

        return http.build();
    }
}
