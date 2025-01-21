package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;
import com.W3yneRagsac.SnapShop.repository.UserRepository;
import com.W3yneRagsac.SnapShop.service.interfaces.IUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
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
    public UserEntity createUser(CreateUserDTO createUserDTO, String userTimeZone) throws UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone); // Use helper function

        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new UserFoundException("Email is already in use.");
        }

        if (userRepository.findUserByName(createUserDTO.getName()).isPresent()) {
            throw new UserFoundException("Username is already in use.");
        }

        if (!createUserDTO.getPassword().equals(createUserDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        ZoneId zoneId = ZoneId.of(userTimeZone);
        UserEntity user = new UserEntity();
        user.setName(createUserDTO.getName());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setEmail(createUserDTO.getEmail());
        user.setCreatedAt(OffsetDateTime.now(zoneId));
        user.setIsPresent(true);  // Set isPresent to a valid boolean value

        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UpdateUserDTO updateUserDTO, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone);

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (userRepository.findUserByName(updateUserDTO.getUser()).isPresent()
                && !existingUser.getName().equals(updateUserDTO.getUser())) {
            throw new UserFoundException("Username is already in use.");
        }

        existingUser.setName(updateUserDTO.getUser());
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone)));

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity updateEmail(UpdateEmailDTO updateEmailDTO, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone); // Use helper function

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (userRepository.findByEmail(updateEmailDTO.getEmail()).isPresent()
                && !existingUser.getEmail().equals(updateEmailDTO.getEmail())) {
            throw new UserFoundException("Email is already in use.");
        }

        existingUser.setEmail(updateEmailDTO.getEmail());
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone))); // Use provided timezone

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity updatePassword(UpdatePasswordDTO updatePasswordDTO, Long id, String userTimeZone) throws UserNotFoundException {
        userTimeZone = validateAndGetTimezone(userTimeZone); // Use helper function

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!updatePasswordDTO.getUpdatedPassword().equals(updatePasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        existingUser.setPassword(passwordEncoder.encode(updatePasswordDTO.getUpdatedPassword()));
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.of(userTimeZone))); // Use provided timezone

        return userRepository.save(existingUser);
    }

    @Override
    public UserEntity deleteUser(DeleteUserDTO deleteUserDTO) throws UserNotFoundException {
        // Check if the User exists in the repository by the provided ID
        UserEntity deletedUser = userRepository.findById(deleteUserDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + deleteUserDTO.getId()));

        // Delete the user from the repository
        userRepository.deleteById(deleteUserDTO.getId());

        // Return the deleted user entity (this can be useful if you want to confirm the user was deleted or for logging purposes)
        return deletedUser;
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
