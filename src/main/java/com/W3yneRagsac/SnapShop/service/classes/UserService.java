package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.RoleEntity;
import com.W3yneRagsac.SnapShop.model.UserEntity;
import com.W3yneRagsac.SnapShop.model.enums.Roles;
import com.W3yneRagsac.SnapShop.repository.RoleRepository;
import com.W3yneRagsac.SnapShop.repository.UserRepository;
import com.W3yneRagsac.SnapShop.service.interfaces.IUserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Logging of the roles
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
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
    }

    // Helper function to ensure a valid timezone or default to UTC
    private String validateAndGetTimezone(String userTimeZone) {
        if (userTimeZone == null || userTimeZone.isEmpty()) {
            return "UTC"; // default to UTC
        }
        return userTimeZone;
    }

    @Override
    public UserEntity createUser(CreateUserInput createUserInput, String userTimeZone) throws UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone);

        if (userRepository.findByEmail(createUserInput.getEmail()).isPresent()) {
            throw new UserFoundException("Email is already in use.");
        }

        if (userRepository.findUserByName(createUserInput.getName()).isPresent()) {
            throw new UserFoundException("Username is already in use.");
        }

        if (!createUserInput.getPassword().equals(createUserInput.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        ZoneId zoneId = ZoneId.of(userTimeZone);
        UserEntity user = new UserEntity();
        user.setName(createUserInput.getName());
        user.setPassword(passwordEncoder.encode(createUserInput.getPassword()));
        user.setEmail(createUserInput.getEmail());
        user.setCreatedAt(OffsetDateTime.now(zoneId));
        user.setIsPresent(true);  // Ensure a valid boolean value

        // Log before saving
        logger.info("Creating user: Name - " + createUserInput.getName() + ", Email - " + createUserInput.getEmail());

        UserEntity savedUser = userRepository.save(user);

        // Log after saving
        logger.info("User created: Name - " + savedUser.getName() + ", Email - " + savedUser.getEmail());

        logAuthenticatedUserRoles();

        return savedUser;
    }

    @Override
    public UserEntity updateUser(UpdateUserInput updateUserInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone);

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (userRepository.findUserByName(updateUserInput.getUser()).isPresent()
                && !existingUser.getName().equals(updateUserInput.getUser())) {
            throw new UserFoundException("Username is already in use.");
        }
        RoleEntity customerRole = roleRepository.findByRole(Roles.CUSTOMER);

        existingUser.setName(updateUserInput.getUser());
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone)));

        // Assign the role to customer
        Set<RoleEntity> roles = new HashSet<>(); // create an array
        // add the role as a customer
        roles.add(customerRole);
        existingUser.setRoles(roles);

        // Log user update
        logger.info("Updating user: ID - " + id + ", Name - " + updateUserInput.getUser());

        logAuthenticatedUserRoles();

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity updateEmail(UpdateEmailInput updateEmailInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone); // Use helper function

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (userRepository.findByEmail(updateEmailInput.getEmail()).isPresent()
                && !existingUser.getEmail().equals(updateEmailInput.getEmail())) {
            throw new UserFoundException("Email is already in use.");
        }

        existingUser.setEmail(updateEmailInput.getEmail());
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone))); // Use provided timezone

        // Log email update
        logger.info("Updating email for user: ID - " + id + ", New Email - " + updateEmailInput.getEmail());

        logAuthenticatedUserRoles();

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity updatePassword(UpdatePasswordInput updatePasswordInput, Long id, String userTimeZone) throws UserNotFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone); // Use helper function

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!updatePasswordInput.getUpdatedPassword().equals(updatePasswordInput.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        existingUser.setPassword(passwordEncoder.encode(updatePasswordInput.getUpdatedPassword()));
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone))); // Use provided timezone

        // Log password update
        logger.info("Updating password for user: ID - " + id);

        logAuthenticatedUserRoles();

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity deleteUser(DeleteUserInput deleteUserInput) throws UserNotFoundException {
        // Check if the User exists in the repository by the provided ID
        UserEntity deletedUser = userRepository.findById(deleteUserInput.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + deleteUserInput.getId()));

        // Log user deletion
        logger.info("Deleting user: ID - " + deleteUserInput.getId() + ", Name - " + deletedUser.getName());

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

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findUserByName(String name) {
        return userRepository.findUserByName(name);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }
}
