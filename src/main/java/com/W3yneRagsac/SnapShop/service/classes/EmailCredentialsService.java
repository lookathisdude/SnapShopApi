package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;
import com.W3yneRagsac.SnapShop.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailCredentialsService implements UserDetailsService {

    private final UserRepository userRepository;

    public EmailCredentialsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name())) // Ensure the role is prefixed with "ROLE_"
                .collect(Collectors.toList()); // Collect the stream into a List<GrantedAuthority>


        // Return the user as UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),  // Use email instead of username
                user.getPassword(),  // Ensure this is the encoded password
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                        .collect(Collectors.toList())
        );
    }
}
