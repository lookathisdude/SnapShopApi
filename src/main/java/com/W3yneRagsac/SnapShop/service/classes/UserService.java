package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.DTO.Specifications.UserSpecifications;
import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.Entity.AuthPayloadEntity;
import com.W3yneRagsac.SnapShop.model.Entity.RoleEntity;
import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;
import com.W3yneRagsac.SnapShop.model.enums.Roles;
import com.W3yneRagsac.SnapShop.repository.RoleRepository;
import com.W3yneRagsac.SnapShop.repository.UserRepository;
import com.W3yneRagsac.SnapShop.service.interfaces.IUserService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtUtilService jwtUtilService;

    @Autowired
    AuthenticationManager authenticationManager;

    // Logging of the roles
    @Transactional
    private void logAuthenticatedUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            logger.info("Authenticated user: " + authentication.getName());

            // Loop through granted authorities (roles)
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                logger.info("User role: " + authority.getAuthority());
            }
        } else {
            logger.info("No authenticated user found.");
        }
    }


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;  // Accept the PasswordEncoder interface
        this.userRepository = userRepository;
    }

    // Helper function to ensure a valid timezone or default to UTC
    private String validateAndGetTimezone(String userTimeZone) {
        if (userTimeZone == null || userTimeZone.isEmpty()) {
            return "UTC"; // default to UTC
        }
        return userTimeZone;
    }

    // load the email
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        // Retrieve the user from the database
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert UserEntity to UserDetails (mapping roles to SimpleGrantedAuthority)
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))  // Assuming UserEntity has roles
                .collect(Collectors.toList());

        // Return UserDetails object (with email, password, and authorities)
        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    public AuthPayloadEntity login(String email, String password) {
        try {
            // Authenticate user using Spring Security's authentication manager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            // If authentication is successful, extract user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Extract roles from authenticated user
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();  // Convert roles to a list of strings

            // If the user has no assigned roles, default to "CUSTOMER"
            if (roles.isEmpty()) {
                roles = List.of("ROLE_CUSTOMER");
            }

            // Generate JWT token with email and roles
            String token = jwtUtilService.generateToken(email, roles);

            // Return structured response with token, message, and roles
            return new AuthPayloadEntity(token, "Login successful", roles);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during login: " + e.getMessage());
        }
    }

    public List<UserEntity> getUserByFilter(SearchUsersInput searchUsersInput) {
        Specification<UserEntity> spec = Specification.where(UserSpecifications.hasUsername(searchUsersInput.getUsername()))
                .and(UserSpecifications.hasEmail(searchUsersInput.getEmail()))
                .and(UserSpecifications.hasRoles(searchUsersInput.getRoles()));

        return userRepository.findAll(spec);
    }


    @Override
    public UserEntity getUserById(GetUserByIdInput getUserByIdInput) throws UserNotFoundException {
        if (getUserByIdInput == null || getUserByIdInput.getId() == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        Long id = getUserByIdInput.getId(); // Extract ID from input object

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
    }


    @Override
    public UserEntity createUser(CreateUserInput createUserInput, String userTimeZone) throws UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone);

        // Check if email is already in use
        if (userRepository.findByEmail(createUserInput.getEmail()).isPresent()) {
            throw new UserFoundException("Email is already in use.");
        }

        // Check if username is already in use
        if (userRepository.findUserByUsername(createUserInput.getName()).isPresent()) {
            throw new UserFoundException("Username is already in use.");
        }

        // Check if passwords match
        if (!createUserInput.getPassword().equals(createUserInput.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Set up the time zone
        ZoneId zoneId = ZoneId.of(userTimeZone);

        // Create a new user entity
        UserEntity user = new UserEntity();
        user.setUsername(createUserInput.getName());
        user.setPassword(passwordEncoder.encode(createUserInput.getPassword()));
        user.setEmail(createUserInput.getEmail());
        user.setCreatedAt(OffsetDateTime.now(zoneId));
        user.setIsPresent(true);  // Ensure a valid boolean value

        // Log before saving
        logger.info("Creating user: Name - " + createUserInput.getName() + ", Email - " + createUserInput.getEmail());

        // Set up roles
        Set<RoleEntity> roles = new HashSet<>();

        // Create or get the CUSTOMER role
        RoleEntity userRole = roleRepository.findByRole(Roles.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new RoleEntity(Roles.CUSTOMER)));

        // Add the role to the user
        roles.add(userRole);
        user.setRoles(roles);

        // Log role assignment
        logger.info("Assigned role '{}' to user '{}'", userRole.getRole().name(), user.getUsername());

        // Save the user and log the result
        UserEntity savedUser = userRepository.save(user);

        // Log after saving
        logger.info("User created: Name - " + savedUser.getUsername() + ", Email - " + savedUser.getEmail());

        // Log authenticated user roles (if needed)
        logAuthenticatedUserRoles();
        return savedUser;  // Return the saved user
    }

    @Override
    public UserEntity updateUserCredentials(UpdateUserCredentialsInput updateUserCredentialsInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone);

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (userRepository.findUserByUsername(updateUserCredentialsInput.getUser()).isPresent()
                && !existingUser.getUsername().equals(updateUserCredentialsInput.getUser())) {
            throw new UserFoundException("Username is already in use.");
        }

        if (updateUserCredentialsInput.getUpdatedPassword() != null &&
                updateUserCredentialsInput.getConfirmPassword() != null &&
                !updateUserCredentialsInput.getUpdatedPassword().trim().equals(updateUserCredentialsInput.getConfirmPassword().trim())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }


        // Set the updated fields efficiently
        Optional.ofNullable(updateUserCredentialsInput.getUser())
                .ifPresent(existingUser::setUsername);

        Optional.ofNullable(updateUserCredentialsInput.getEmail())
                .ifPresent(existingUser::setEmail);

        // Handle password update if provided and hash it before saving
        Optional.ofNullable(updateUserCredentialsInput.getUpdatedPassword())
                .ifPresent(password -> existingUser.setPassword(passwordEncoder.encode(password))); // Hash the password

        RoleEntity customerRole = roleRepository.findByRole(Roles.CUSTOMER)
                        .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        // Assign the role to customer
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(customerRole);
        existingUser.setRoles(roles);

        // Log user update
        logger.info("Updating user: ID - " + id + ", Name - " + updateUserCredentialsInput.getUser());

        logAuthenticatedUserRoles();

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity deleteUser(DeleteUserInput deleteUserInput) throws UserNotFoundException {
        // Check if the User exists in the repository by the provided ID
        UserEntity deletedUser = userRepository.findById(deleteUserInput.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + deleteUserInput.getId()));

        // Log user deletion
        logger.info("Deleting user: ID - " + deleteUserInput.getId() + ", Name - " + deletedUser.getUsername());

        // Delete the user from the repository
        userRepository.deleteById(deleteUserInput.getId());

        logAuthenticatedUserRoles();

        // Return the deleted user entity (this can be useful if you want to confirm the user was deleted or for logging purposes)
        return deletedUser;
    }

    // Booleans
    // Method to check if an email already exists in the database
    public boolean existsByEmail(String email) {
        // Check if a user with the provided email exists
        return userRepository.existsByEmail(email);
    }
}
