package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.EmailNotFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;
import com.W3yneRagsac.SnapShop.service.classes.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class UserResolver {

    private static final Logger logger = LoggerFactory.getLogger(UserResolver.class);

    @Autowired
    private UserService userService;

    @QueryMapping
    @PreAuthorize("hasRole('W3YNER$gsA3_admin')")
    public UserEntity getUserByName(@Argument("input") GetByNameDTO getByNameDTO) throws UserNotFoundException {
        if (getByNameDTO == null || getByNameDTO.getName() == null || getByNameDTO.getName().isEmpty()) {
            throw new IllegalStateException("Input object or name cannot be null or empty");
        }

        logger.info("Fetching user by name: {}", getByNameDTO.getName());
        return userService.findUserByName(getByNameDTO.getName())
                .orElseThrow(() -> new UserNotFoundException("User with name: " + getByNameDTO.getName() + " not found."));
    }

    @QueryMapping
    @PreAuthorize("hasRole('W3YNER$gsA3_admin')")
    public UserEntity getUserByEmail(@Argument("input") @Valid GetByEmailDTO getByEmailDTO) throws EmailNotFoundException {
        if (getByEmailDTO == null || getByEmailDTO.getEmail() == null || getByEmailDTO.getEmail().isEmpty()) {
            throw new IllegalStateException("Input email cannot be null or empty");
        }

        String email = getByEmailDTO.getEmail();
        logger.info("Fetching user by email: {}", email);
        return userService.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User with email: " + email + " not found."));
    }

    @MutationMapping
    public UserEntity createUser(@Argument("input") @Valid CreateUserDTO userInput, @Argument String userTimeZone) throws UserFoundException {
        if (userInput == null || userInput.getName() == null || userInput.getName().isEmpty()) {
            throw new IllegalStateException("Input name cannot be null or empty");
        }
        logger.info("Creating user with email: {}", userInput.getEmail());
        return userService.createUser(userInput, userTimeZone);
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public UserEntity updateUser(
            @Argument("input") @Valid UpdateUserDTO updateUserDTO,
            @Argument("userTimeZone") String userTimeZone) throws UserNotFoundException, UserFoundException {

        Long id = updateUserDTO.getId();

        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        logger.info("Updating user with ID: {}", id);
        return userService.updateUser(updateUserDTO, id, userTimeZone);
    }

    @MutationMapping
    @PreAuthorize("hasRole('W3YNER$gsA3_admin')")
    public UserEntity updateEmail(
            @Argument("input") @Valid UpdateEmailDTO updateEmailDTO,
            @Argument String userTimeZone) throws UserNotFoundException, UserFoundException {

        Long id = updateEmailDTO.getId();

        if (id == null) {
            throw new IllegalStateException("ID must not be null");
        }

        logger.info("Updating user with ID: {}", id);
        return userService.updateEmail(updateEmailDTO, id, userTimeZone);
    }

    //    TODO: IMPLEMENT AUTHENTICATION FOR THIS METHOD TO WORK

//    public UserEntity updatePassword(UpdatePasswordDTO updatePasswordDTO, String userTimeZone) throws UserNotFoundException {
//        UserEntity existingUser = getUserByName(); // This would get the currently logged-in user
//
//        if (existingUser == null) {
//            throw new UserNotFoundException("User not found.");
//        }
//
//        // Update the password
//        existingUser.setPassword(updatePasswordDTO.getUpdatedPassword());  // Update password from DTO
//        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone))); // Use the userTimeZone (as String)
//
//        return userService.save(existingUser); // Save the updated user
//    }

    @MutationMapping
    @PreAuthorize("hasRole('W3YNER$gsA3_admin')")
    public UserEntity deleteUser(
            @Argument("input") @Valid DeleteUserDTO deleteUserDTO) throws UserNotFoundException {

        if (deleteUserDTO == null || deleteUserDTO.getId() == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        logger.info("Deleting user with ID: {}", deleteUserDTO.getId());
        return userService.deleteUser(deleteUserDTO);
    }
}
