package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_CUSTOMER')")
    public UserEntity getUserById(@Argument("input") @Valid GetUserByIdInput getUserByIdInput) throws UserNotFoundException {
        if(getUserByIdInput.getId() == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        return userService.getUserById(getUserByIdInput);
    }

    @SchemaMapping(typeName = "Query")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_CUSTOMER')")
    public UserEntity searchUsers(@Argument("filter") @Valid SearchUsersInput userFilterInput) {
        try {
            List<UserEntity> users = userService.getUserByFilter(userFilterInput);

            if (users == null || users.isEmpty()) {
                throw new RuntimeException("No users match the provided filter.");
            }

            if (users.size() > 1) {

                return users.getFirst();
            }

            return users.getFirst();
        } catch (Exception e) {
            // Log the exception or handle it based on your logging setup
            logger.error("Error occurred while searching for users with filter: {}", userFilterInput, e);
            throw new RuntimeException("An error occurred while searching for users: " + e.getMessage());
        }
    }



    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('ROLE_GUEST') or !isAuthenticated()")
    public UserEntity createUser(@Argument("input") @Valid CreateUserInput userInput, @Argument String userTimeZone) throws UserFoundException {
        try {
            return userService.createUser(userInput, userTimeZone);
        } catch (UserFoundException | IllegalArgumentException e) {
            throw new GraphQLException("Error creating user: " + e.getMessage());
        }
    }


    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_VENDOR')")
    public UserEntity updateUserCredentials(
            @Argument("input") @Valid UpdateUserCredentialsInput updateUserCredentialsInput,
            @Argument("userTimeZone") String userTimeZone,
            Principal principal
    ) throws UserNotFoundException, UserFoundException {

        // Ensure the principal has the required role
        checkRole(principal, "ROLE_CUSTOMER");

        // Retrieve the user ID from the input and check for null
        Long id = updateUserCredentialsInput.getId();
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        // Log the update action
        logger.info("Updating user with ID: {}", id);

        // Create GetUserByIdInput using the retrieved ID
        GetUserByIdInput getUserByIdInput = new GetUserByIdInput();
        getUserByIdInput.setId(id);

        // Retrieve the user entity by ID from the user service
        UserEntity user = userService.getUserById(getUserByIdInput);

        // Update the user credentials and return the updated user
        return userService.updateUserCredentials(updateUserCredentialsInput, id, userTimeZone);
    }






    @SchemaMapping(typeName = "Mutation")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_VENDOR')")
    public UserEntity deleteUser(@Argument("input") @Valid DeleteUserInput deleteUserInput, Principal principal) throws UserNotFoundException {
        checkRole(principal, "ROLE_CUSTOMER");

        if (deleteUserInput == null || deleteUserInput.getId() == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        logger.info("Deleting user with ID: {}", deleteUserInput.getId());
        return userService.deleteUser(deleteUserInput);
    }
}
