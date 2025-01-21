package com.W3yneRagsac.SnapShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for now (enable in production with proper configuration)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/graphql").permitAll() // Adjust based on public or private access
                        .requestMatchers("/home", "/products").permitAll()
                        .requestMatchers("/checkout", "/cart").authenticated()
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/SnapShopLogin")
                        .defaultSuccessUrl("/home")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll());

        return http.build();
    }
}
