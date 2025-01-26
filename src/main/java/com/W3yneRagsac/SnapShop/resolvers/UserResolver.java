package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.EmailNotFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;
import com.W3yneRagsac.SnapShop.service.classes.UserService;
import graphql.GraphQLException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Controller
public class UserResolver {

    private static final Logger logger = LoggerFactory.getLogger(UserResolver.class);


    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    @Autowired
    public UserResolver(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    // Verify the user's role
    private void checkRole(Principal principal, String role) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            boolean hasRole = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(role));
            if (!hasRole) {
                throw new AccessDeniedException("User does not have the '" + role + "' role.");
            }
        }
    }

    private void validateNonEmptyString(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(fieldName + " cannot be null or empty");
        }
    }

    @SchemaMapping(typeName = "Query")
    @PreAuthorize("hasAnyRole('GUEST', 'CUSTOMER', 'VENDOR')")
    public UserEntity getUserByName(@Argument("input") GetUserByNameInput getUserByNameInput) throws UserNotFoundException {
        validateNonEmptyString(getUserByNameInput.getName(), "Name");
        logger.info("Fetching user by name: {}", getUserByNameInput.getName());
        return userService.findUserByName(getUserByNameInput.getName())
                .orElseThrow(() -> new UserNotFoundException("User with name: " + getUserByNameInput.getName() + " not found."));
    }

    @SchemaMapping(typeName = "Query")
    @PreAuthorize("hasAnyRole('GUEST', 'CUSTOMER', 'VENDOR')")
    public UserEntity getUserByEmail(@Argument("input") @Valid GetUserByEmailInput getUserByEmailInput) throws EmailNotFoundException {
        validateNonEmptyString(getUserByEmailInput.getEmail(), "Email");
        logger.info("Fetching user by email: {}", getUserByEmailInput.getEmail());
        return userService.findByEmail(getUserByEmailInput.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("User with email: " + getUserByEmailInput.getEmail() + " not found."));
    }

    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('GUEST')")
    public UserEntity createUser(@Argument("input") @Valid CreateUserInput userInput, @Argument String userTimeZone) throws UserFoundException {
        try {
            return userService.createUser(userInput, userTimeZone);
        } catch (UserFoundException | IllegalArgumentException e) {
            throw new GraphQLException("Error creating user: " + e.getMessage());
        }
    }


    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    public UserEntity updateUser(@Argument("input") @Valid UpdateUserInput updateUserInput,
                                 @Argument("userTimeZone") String userTimeZone, Principal principal) throws UserNotFoundException, UserFoundException {
        checkRole(principal, "ROLE_CUSTOMER");

        Long id = updateUserInput.getId();
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        logger.info("Updating user with ID: {}", id);

        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

        return userService.updateUser(updateUserInput, id, userTimeZone);
    }

    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    public UserEntity updateEmail(@Argument("input") @Valid UpdateEmailInput updateEmailInput,
                                  @Argument String userTimeZone, Principal principal) throws UserNotFoundException, UserFoundException {
        checkRole(principal, "ROLE_CUSTOMER");

        Long id = updateEmailInput.getId();
        if (id == null) {
            throw new IllegalStateException("ID must not be null");
        }

        logger.info("Updating user email with ID: {}", id);
        return userService.updateEmail(updateEmailInput, id, userTimeZone);
    }

    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    public UserEntity updatePassword(UpdatePasswordInput updatePasswordInput, Long id,
                                     String userTimeZone,
                                     Principal principal) throws UserNotFoundException {
        checkRole(principal, "ROLE_CUSTOMER");

        // Verify time zone
        ZoneId timeZone = ZoneId.of(userTimeZone);

        // Find and update user password
        UserEntity existingUser = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!updatePasswordInput.getUpdatedPassword().equals(updatePasswordInput.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String updatedPassword = updatePasswordInput.getUpdatedPassword();
        if (updatedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }

        if (updatedPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        existingUser.setPassword(passwordEncoder.encode(updatedPassword));
        existingUser.setUpdatedAt(OffsetDateTime.now(timeZone));

        return userService.updatePassword(updatePasswordInput, id, userTimeZone);
    }

    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    public UserEntity deleteUser(@Argument("input") @Valid DeleteUserInput deleteUserInput, Principal principal) throws UserNotFoundException {
        checkRole(principal, "ROLE_CUSTOMER");

        if (deleteUserInput == null || deleteUserInput.getId() == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        logger.info("Deleting user with ID: {}", deleteUserInput.getId());
        return userService.deleteUser(deleteUserInput);
    }
}
